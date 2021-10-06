package com.example.demo.controller;

import com.example.demo.ReservationException;
import com.example.demo.entity.Camp;
import com.example.demo.service.CampService;
import com.example.demo.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/reservation")
public class CampController {

    private static final Logger log = LoggerFactory.getLogger(CampController.class);

    private final ReservationService reservationService;
    private final CampService campService;

    @Autowired
    public CampController(ReservationService reservationService, CampService campService) {
        this.campService = campService;
        this.reservationService = reservationService;
    }

    /**
     * Assume the start-date is current date
     * @param range
     * @return
     */
    @GetMapping(value="/query", consumes="application/json", produces="application/json")
    public ResponseEntity<Map<String,List<Camp>>> getAvailableDates (@RequestParam(required = false, defaultValue = "30") Integer range) {
        log.debug("getAvailableDates: range="+range);

        // validate range value
        if (range <= 0 || range > 30)
            throw new ReservationException("Minimum 1 day and maximum 30 days");

        Map<String,List<Camp>> map = campService.getAvailableDates(range);
        return ResponseEntity.ok(map);
    }

}
