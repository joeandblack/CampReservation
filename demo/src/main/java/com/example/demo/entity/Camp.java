package com.example.demo.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name = "camps")
public class Camp {

    private static final int MAX_CAPACITY_PER_DAY = 10; // max people accepted per day

    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "campId", unique = true, nullable = false, length = 40)
    private String campId; // use integer for easy to read

    private int capacity = MAX_CAPACITY_PER_DAY; // max allowed

    private int totalReserved;

    public Camp(){}
    public Camp(String campId) {
        this.campId=campId;
    }

    public String getCampId() {
        return campId;
    }

    public void setCampId(String campId) {
        this.campId = campId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getTotalReserved() {
        return totalReserved;
    }

    public void setTotalReserved(int totalReserved) {
        this.totalReserved = totalReserved;
    }

    @Override
    public String toString() {
        return "Camp{" +
                "campId='" + campId + '\'' +
                ", capacity=" + capacity +
                ", totalReserved=" + totalReserved +
                '}';
    }
}
