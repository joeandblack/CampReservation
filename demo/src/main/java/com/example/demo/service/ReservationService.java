package com.example.demo.service;

import com.example.demo.ReservationException;
import com.example.demo.Utils;
import com.example.demo.entity.Camp;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationDates;
import com.example.demo.entity.User;
import com.example.demo.persist.ReservationDateRepository;
import com.example.demo.persist.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final CampService campService;
    private final ReservationDateRepository reservationDateRepository;

    // Simulates a real persist message queue here
    // prevent multiple requests coming at the same time
    private final static BlockingQueue<Reservation> bookingQueue = new LinkedBlockingQueue<>(100);
    private final ExecutorService executor;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              CampService campService,
                              ReservationDateRepository reservationDateRepository) {
        this.reservationRepository = reservationRepository;
        this.campService = campService;
        this.reservationDateRepository = reservationDateRepository;

        this.executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    log.debug("bookingQueue waiting...");
                    try {
                        Reservation res = bookingQueue.take();
                        if (res != null)
                            doReserve(res);
                    } catch (InterruptedException e) {
                        log.error("bookingQueue execute error: ", e);
                    }
                }
            }
        });
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationByReservationId(String reservationId) {
        Optional<Reservation> res = reservationRepository.findByReservationId(reservationId);
        if (!res.isPresent())
            throw new ReservationException("Cannot find reservation: "+reservationId);
        return res.get();
    }

    public List<Reservation> getReservationsByUserId(String userId) {
        return reservationRepository.findByUserId(userId);
    }


    /**
     * Make reservation, it's not completed yet. The status is 'INIT', and will change to 'confirmed' or 'failed' afterwards.
     * FE can check the status by using the Get api.
     * @param user
     * @param camp
     * @param startDate
     * @param endDate
     * @param numReserves
     * @return
     */
    public Reservation makeReservation(User user, Camp camp, long startDate, long endDate, int numReserves) {
        log.debug("create: user="+user.getUsername()+", "+startDate+", endDate="+endDate+", numReserves="+numReserves);
        Reservation res = new Reservation();
        res.setUserId(user.getUserId());
        res.setUser(user);
        res.setCampId(camp.getCampId());
        res.setCamp(camp);
        res.setStartDate(Utils.truncate(startDate));
        res.setEndDate(Utils.truncate(endDate));
        res.setNumReserves(numReserves);
        res.setStatus(Reservation.INIT);
        reservationRepository.save(res); // not final
        try {
            bookingQueue.offer(res, 3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ReservationException("Sorry, service is busy...");
        }
        return res;
    }

    @Transactional
    public List<ReservationDates> doReserve(Reservation res) {
        log.debug("doReserve: res="+res);

        if (!campService.isAvailable(res.getCamp(), res.getStartDate(), res.getEndDate(), res.getNumReserves())) {
            res.setStatus(Reservation.FAILED);
            reservationRepository.save(res); // update status
            log.debug("reservation failed!");
            return null;
        }

        List<Long> dates = Utils.getDateList(res.getStartDate(), res.getEndDate());
        List<ReservationDates> list = new ArrayList<>();
        for(Long date: dates) {
            ReservationDates rd = new ReservationDates();
            rd.setReservationId(res.getReservationId());
            rd.setReservation(res);
            rd.setReservationDate(date);
            ReservationDates saved = reservationDateRepository.save(rd);
            list.add(saved);
        }
        res.setStatus(Reservation.CONFIRMED);
        reservationRepository.save(res); // update status
        log.debug("reservation is confirmed");
        return list;
    }

    /**
     * Reservation can be either soft-canceled or hard-canceled
     * @param reservationId
     */
    public void cancel(String reservationId, boolean hardCancel) {
        if (hardCancel) {
            reservationRepository.deleteById(reservationId);
            // cascade delete
        }
        else {
            Optional<Reservation> res = reservationRepository.findById(reservationId);
            if (res.isPresent()) {
                res.get().setStatus(Reservation.CANCELED);
                reservationRepository.save(res.get());
            }
            else {
                throw new ReservationException("Cannot find reservation: "+reservationId);
            }
        }
    }

    public void update(Reservation reservation) {
        Optional<Reservation> old = reservationRepository.findByReservationId(reservation.getReservationId());
        if (!old.isPresent())
            throw new ReservationException("Invalid reservation ID: "+reservation.getReservationId());
        reservationRepository.save(reservation);
        // update reservationDates
    }

}

