package com.example.demo.persist;

import com.example.demo.entity.Camp;
import com.example.demo.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface CampRepository extends JpaRepository<Camp, String> {

    @Query(nativeQuery = true, value =
            "SELECT c.camp_id, c.capacity, COALESCE(Reserved.Total,0) Tot FROM\n" +
                    "    (SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r\n" +
                    "    JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
                    "    GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
                    "    HAVING rd.reservation_date = ?1 AND r.status = ?2\n" +
                    "    ORDER BY r.camp_id, rd.reservation_date) Reserved\n" +
                    "RIGHT OUTER JOIN camps c ON c.camp_id=Reserved.camp_id ;\n"
    )
    List<Object[]> findCampReservesByDate(long date, String status);

    @Query(nativeQuery = true, value =
            "SELECT rd.reservation_date, sum(r.num_reserves) Total FROM reservations r\n" +
                    "JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
                    "GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
                    "HAVING r.camp_id= ?1 AND rd.reservation_date >= ?2 AND rd.reservation_date < ?3 AND r.status=?4\n" +
                    "ORDER BY r.camp_id, rd.reservation_date")
    List<Object[]> findCampDateReserveNums(String campId, long startDate, long endDate, String status);

    @Query(nativeQuery = true, value =
            "SELECT r.camp_id, rd.reservation_date, SUM(r.num_reserves) Total FROM reservations r\n" +
                    "JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
                    "GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
                    "HAVING rd.reservation_date >= ?1 AND rd.reservation_date < ?2 AND r.status=?3\n" +
                    "ORDER BY r.camp_id, rd.reservation_date;\n"
    )
    List<Object[]> findCampDateReserveSums(long startDate, long endDate, String status);

    @Query(nativeQuery = true, value =
            "SELECT MAX(Total) FROM (\n" +
                    " SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r\n" +
                    " JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
                    " GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
                    " HAVING r.camp_id=?1 AND rd.reservation_date >=?2 AND rd.reservation_date < ?3 AND r.status=?4 \n" +
                    " ) NumReserves;\n"
    )
    Optional<Number> findMaxNumReserves(String campId, long startDate, long endDate, String confirmed);


    @Query(nativeQuery = true, value =
        "SELECT * FROM (\n" +
                "    SELECT c.*, COALESCE(MaxNums.MaxTotal,0) MinMax FROM\n" +
                "        (SELECT NumReserves.camp_id, MAX(NumReserves.Total) MaxTotal FROM (\n" +
                "             SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r\n" +
                "             JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
                "             GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
                "             HAVING r.status=?1 AND rd.reservation_date >= ?2 AND rd.reservation_date < ?3\n" +
                "             ) NumReserves\n" +
                "             GROUP BY NumReserves.camp_id\n" +
                "         ) MaxNums\n" +
                "     RIGHT OUTER JOIN camps c ON MaxNums.camp_id=c.camp_id\n" +
                "     ORDER BY MinMax\n" +
                " ) CampView\n" +
                " WHERE capacity >= (COALESCE(MinMax,0) + ?4)\n"
    )
    Optional<List<Camp>> findAvailableCamps(String status, Long startDate, Long endDate, int numReserves);

    @Query(nativeQuery = true, value =
        "SELECT c.* FROM\n" +
                "    (SELECT NumReserves.camp_id, MAX(NumReserves.Total) MaxTotal FROM (\n" +
                "         SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r\n" +
                "         JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id\n" +
                "         GROUP BY r.camp_id, r.status, rd.reservation_date\n" +
                "         HAVING rd.reservation_date >= ?2 AND rd.reservation_date < ?3 AND r.status=?5\n" +
                "         ) NumReserves\n" +
                "         GROUP BY NumReserves.camp_id\n" +
                "     ) MaxNums\n" +
                " RIGHT OUTER JOIN camps c ON MaxNums.camp_id=c.camp_id\n" +
                " WHERE c.camp_id=?1 AND c.capacity >= (COALESCE(MaxNums.MaxTotal,0) + ?4);\n"
    )
    //Optional<Camp> isAvailable(String campId, long startDate, long endDate, int numReserves, String status);
    Optional<Camp> findSelectedCamp(String campId, long startDate, long endDate, int numReserves, String status);
}
