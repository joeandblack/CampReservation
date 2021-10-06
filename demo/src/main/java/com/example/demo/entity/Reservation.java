package com.example.demo.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {

    // reservation status
    public static final String INIT = "INIT";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String FAILED = "FAILED";
    public static final String CANCELED = "CANCELED";

    // booking identifier
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "reservation_id", unique = true, nullable = false, length = 40)
    private String reservationId;

    @Column(name = "user_id" , insertable = false , updatable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "camp_id" , insertable = false , updatable = false)
    private String campId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "camp_id")
    private Camp camp;

    @Column(name = "num_reserves", nullable = false)
    private int numReserves;

    private long startDate;
    private long endDate;
    @Column(name = "status")
    private String status; // reservation status


    //    @Basic(optional = false)
//    @Column(name = "LastTouched", insertable = false, updatable = false)
//    @Temporal(TemporalType.TIMESTAMP)
    private final Timestamp createdTime;
    private Timestamp modifiedTime;

    public Reservation() {
        //this.reservationId = UUID.randomUUID().toString();
        this.status = INIT;
        this.createdTime = new Timestamp(System.currentTimeMillis());
    }

    public Reservation(int numReserves) {
        this();
        this.numReserves = numReserves;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCampId() {
        return campId;
    }

    public void setCampId(String campId) {
        this.campId = campId;
    }

    public Camp getCamp() {
        return camp;
    }

    public void setCamp(Camp camp) {
        this.camp = camp;
    }

    public int getNumReserves() {
        return numReserves;
    }

    public void setNumReserves(int numReserves) {
        this.numReserves = numReserves;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", userId='" + userId + '\'' +
                ", user=" + user +
                ", campId='" + campId + '\'' +
                ", camp=" + camp +
                ", numReserves=" + numReserves +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}
