package com.example.demo.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "reservation_dates", uniqueConstraints={
        @UniqueConstraint(columnNames = {"reservation_id", "reservation_date"})
})
public class ReservationDates implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "reservation_date_id", unique = true, nullable = false, length = 40)
    private String reservationDateId;

    @Column(name = "reservation_date" , nullable = false)
    private Long reservationDate;

    @Column(name = "reservation_id" , insertable = false , updatable = false)
    private String reservationId;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    public ReservationDates(){}

    public String getReservationDateId() {
        return reservationDateId;
    }

    public void setReservationDateId(String reservationDateId) {
        this.reservationDateId = reservationDateId;
    }

    public Long getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Long reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public String toString() {
        return "ReservationDates{" +
                "reservationDateId='" + reservationDateId + '\'' +
                ", reservationDate=" + reservationDate +
                ", reservationId='" + reservationId + '\'' +
                ", reservation=" + reservation +
                '}';
    }
}
