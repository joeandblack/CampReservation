package com.example.demo.persist;

import com.example.demo.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface ReservationRepository extends JpaRepository<Reservation,String> {

    public Optional<Reservation> findByReservationId(String reservationId);

    public List<Reservation> findByUserId(String userId);

}

