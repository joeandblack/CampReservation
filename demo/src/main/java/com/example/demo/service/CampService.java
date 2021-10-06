package com.example.demo.service;

import com.example.demo.ReservationException;
import com.example.demo.Utils;
import com.example.demo.entity.Camp;
import com.example.demo.entity.Reservation;
import com.example.demo.persist.CampRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CampService {

    private static final Logger log = LoggerFactory.getLogger(CampService.class);

    public final static int MAX_RESERVATION_DAYS = 30; // 1 monthes

    private final CampRepository campRepository;
    private final ScheduledExecutorService executor;

    public boolean isAvailable(Camp camp, long startDate, long endDate, int numReserves) {
//        Optional<Number> maxNumReserves = campRepository.findMaxNumReserves(campId, startDate, endDate, Reservation.CONFIRMED);
//        return !maxNumReserves.isPresent() || maxNumReserves.get().intValue() <= allowedNums;
        Optional<Camp> campOpt = campRepository.findSelectedCamp(camp.getCampId(), startDate, endDate, numReserves, Reservation.CONFIRMED);
        return campOpt.isPresent();
    }

    // Cache data for fast query response. Update from database every 5 min.
    private final SortedMap<Long,List<Camp>> dateMap = new TreeMap<>();

    private class Refresher implements Runnable {
        @Override
        public void run() {
            log.info("refresher running...");
            updateData();
        }
    }

    @Autowired
    public CampService(CampRepository campRepository) {
        this.campRepository = campRepository;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new Refresher(), 30, 300, TimeUnit.SECONDS);
    }

    // convert the date from Long to String type
    public SortedMap<String,List<Camp>> getAvailableDates(int dateRange) {

        SortedMap<String,List<Camp>> map = new TreeMap<>();
//        cachedCampDates.forEach((k,v)->{
//            map.put(k, v.subList(0, dateRange));
//        });
        for(Long key: dateMap.keySet()) {
            List<Camp> camps = dateMap.get(key);
            Iterator<Camp> itr = camps.iterator();
            List<Camp> newValues = new ArrayList<>();
            int count = dateRange;
            IN:
            while(count-- > 0) {
                if (!itr.hasNext())
                    break IN;
                Camp camp = itr.next();
                newValues.add(camp);
            }
            map.put(Utils.DATEFORMAT.format(new Date(key)), newValues);
        }

        // for debug
        map.forEach((k,v)->{
            log.debug("Date: "+k);
            v.forEach(e->{
                log.debug("\t\t"+e.toString());
            });
        });

        return Collections.unmodifiableSortedMap(map);
    }

     public void updateData() {
         Calendar cal = Calendar.getInstance();
        int dateRange = MAX_RESERVATION_DAYS;
        log.debug("updateDate: cal="+cal.getTime()+", dateRange="+dateRange);
        List<Long> dates = Utils.getDateList(cal, dateRange);
        for(Long date: dates) {
            log.debug("updateData - date: "+Utils.DATEFORMAT.format(new Date(date)));
            List<Camp> camps = new ArrayList<>();
            List<Object[]> results = campRepository.findCampReservesByDate(date, Reservation.CONFIRMED);
            for (Object[] obj : results) {
                String campId = (String) obj[0];
                Number max = (Number) obj[1];
                Number total = (Number) obj[2];
                log.debug("date="+date+", campId=" + campId + ", max=" + max + ", total=" + total);
                Camp camp = new Camp(campId);
                camp.setCapacity(max.intValue());
                camp.setTotalReserved(total.intValue());
                camps.add(camp);
            }
            dateMap.put(date, camps);
        }
    }

    public Camp getSelectedCamp(String campId, long startDate, long endDate, int numReserves) {
        //Camp camp = campRepository.getById(campId);
        Optional<Camp> camp = campRepository.findSelectedCamp(campId, startDate, endDate, numReserves, Reservation.CONFIRMED);
        if (!camp.isPresent())
            throw new ReservationException("The selected camp is not available for the given date!");
        return camp.get();
    }


    public Camp getOneCamp(long startDate, long endDate, int numReserves) {
        Optional<List<Camp>> camps = campRepository.findAvailableCamps(Reservation.CONFIRMED, startDate, endDate, numReserves);
        if (!camps.isPresent())
            throw new ReservationException("No camp available for the given date!");
        for(int i=0; i<camps.get().size(); i++) {
            log.debug("found camp: "+camps.get().get(i));
        }
        int index = new Random().nextInt(camps.get().size());
        return camps.get().get(index);
    }


}