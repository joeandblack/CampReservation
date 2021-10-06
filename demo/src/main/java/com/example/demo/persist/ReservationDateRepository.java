package com.example.demo.persist;

import com.example.demo.entity.Camp;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RepositoryRestResource
public interface ReservationDateRepository extends JpaRepository<ReservationDates, String> {

//    @Query(nativeQuery = true, value =
//        "SELECT sum(r.num_reserves) TotalNums FROM reservations r\n" +
//                    "JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
//                    "GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
//                    "HAVING r.camp_id = ?1 AND r.status=?2 AND rd.reservation_date = ?3 ")
//    Optional<Number> findTotalNumReserves(String campId, String status, long date);


//    @Query(nativeQuery = true, value =
//            "SELECT rd.reservation_date, sum(r.num_reserves) Total FROM reservations r\n" +
//                    "JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
//                    "GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
//                    "HAVING r.camp_id= ?1 AND rd.reservation_date >= ?2 AND rd.reservation_date < ?3 AND r.status='" + Reservation.CONFIRMED+ "'\n" +
//                    "ORDER BY r.camp_id, rd.reservation_date")
//    List<Object[]> findCampDateReserveNums(String campId, long startDate, long endDate);

//    @Query(nativeQuery = true, value =
//            "SELECT MAX(Total) FROM (\n" +
//                    " SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r\n" +
//                    " JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
//                    " GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
//                    " HAVING r.camp_id=?1 AND rd.reservation_date >=?2 AND rd.reservation_date < ?3 AND r.status=?4 \n" +
//                    " ) NumReserves;\n")
//    Optional<Number> findMaxNumReserves(String campId, long startDate, long endDate, String confirmed);
}

