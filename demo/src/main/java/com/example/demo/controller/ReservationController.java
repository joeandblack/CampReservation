package com.example.demo.controller;

import com.example.demo.entity.Camp;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.service.CampService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Operations: create, update, cancel
 * Register:
 *   user: email, full-name
 *   arrival/departure date
 *   return booking identifier
 * Constrains:
 *   Single campsite
 *   Reserved days: 1 to 3
 *   Maximum one month, default one month
 *   Check-in/Check-out time: 12:00am
 * Other requirement:
 *   Error handling
 *   concurrent requests handling
 *
 * Note: Close-Open implementation of the dates: (startDate/endDate)
 *   [arrival-date, departure-date)
 * For example: arrival 2021-10-01, departure 2021-10-04, meaning:
 *   - check-in at 01 and check-out at 04
 *   - stay 3 days: 01, 02, and 03
 *   - leaving at 4th day, 2021-10-04, which is open for others to reserve without conflict
 * This assume it's the same as in Hotel reservations.
 */
@RestController
@RequestMapping(path="/")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final CampService campService;
    private final UserService userService;
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(CampService campService, UserService userService, ReservationService reservationService) {
        this.campService = campService;
        this.userService = userService;
        this.reservationService = reservationService;
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestParam String username,
                                           @RequestParam String firstName,
                                           @RequestParam String lastName,
                                           @RequestParam String email) {
        return ResponseEntity.ok(userService.register(username, firstName, lastName, email));
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }



    @GetMapping(value="/reservation", consumes="application/json", produces="application/json")
    public ResponseEntity<List<Reservation>> getReservations () {
        log.debug("getAllReservations: ");
        List<Reservation> list = reservationService.getAllReservations();
        return ResponseEntity.ok(list);
    }

    /**
     * Also used to check the reservation status
     * @param reservationId
     * @return
     */
    @GetMapping(value="reservation/{reservationId}", consumes="application/json", produces="application/json")
    public ResponseEntity<Reservation> getReservationByReservationId (@PathVariable String reservationId) {
        log.debug("getReservationsByReservationId: ");
        Reservation res = reservationService.getReservationByReservationId(reservationId);
        return ResponseEntity.ok(res);
    }

    @GetMapping(value="reservation/{userId}", consumes="application/json", produces="application/json")
    public ResponseEntity<List<Reservation>> getUserReservations (@PathVariable String userId) {
        log.debug("getReservations: ");
        List<Reservation> list = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/reservation")
    public ResponseEntity<Reservation> makeReservation(@RequestParam String username,
                                                       @RequestParam String firstName,
                                                       @RequestParam String lastName,
                                                       @RequestParam String email,
                                                       @RequestParam Long startDate,
                                                       @RequestParam Long endDate,
                                                       @RequestParam Integer numReserves) {

        Camp camp = campService.getOneCamp(startDate, endDate, numReserves);
        User user = userService.register(username, firstName, lastName, email);
        return makeReservation(camp.getCampId(), user.getUsername(), startDate, endDate, numReserves);
    }

    /**
     *
     * @param username
     * @param startDate
     * @param endDate
     * @param numReserves
     * @return Reservation, containing Booking Identifier (reservationId)
     */
    @PostMapping("reservation/{campId}/{username}")
    public ResponseEntity<Reservation> makeReservation(@PathVariable String campId,
                                                       @PathVariable String username,
                                                       @RequestParam Long startDate,
                                                       @RequestParam Long endDate,
                                                       @RequestParam Integer numReserves) {

        // add parameters validator
        Camp camp = campService.getSelectedCamp(campId, startDate, endDate, numReserves);
        User user = userService.getUserByUsername(username);
        Reservation res = reservationService.makeReservation(user, camp, startDate, endDate, numReserves);
        return ResponseEntity.ok(res);
    }

    @PutMapping(value="/reservation/{reservationId}", consumes="application/json", produces="application/json")
    public ResponseEntity<String> updateReservation (@PathVariable String reservationId, @RequestBody Reservation reservation) {
        log.info("updateReservation: reservationId="+reservationId);
        reservation.setReservationId(reservationId);
        reservationService.update(reservation);
        return ResponseEntity.ok("Reservation is modified");
    }

    @DeleteMapping(value="/reservation/{reservationId}", consumes="application/json", produces="application/json")
    public ResponseEntity<String> cancelReservation (@PathVariable String reservationId) {
        log.info("cancelReservation: reservationId="+reservationId);
        reservationService.cancel(reservationId, false);
        return ResponseEntity.ok("Reservation is cancelled");
    }

}
