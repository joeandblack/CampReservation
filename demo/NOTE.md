# CompsiteReservation
Campsite Reservation System

Operations: create, update, cancel
Register:
  user: email, full-name
  arrival/departure date
  return booking identifier
Constrains:
  Single campsite
  Reserved days: 1 to 3
  Maximum one month, default one month
  Check-in/Check-out time: 12:00am
Other requirement:
  Error handling
  concurrent requests handling

Note: Close-Open implementation of the dates: (startDate/endDate)
  [arrival-date, departure-date)
For example: arrival 2021-10-01, departure 2021-10-04, meaning:
  - check-in at 01 and check-out at 04
  - stay 3 days: 01, 02, and 03
  - leaving at 4th day, 2021-10-04, which is open for others to reserve without conflict
This assume it's the same as in Hotel reservations.



Database Design: 
User: not in the requirement, added because it's a basic element for any real apps. It can be extended to add security feature later on.
Camp: not in the requirement, added because it's easy for futher extension. The single camp situation is just a special case.
Reservation:
  Status: INIT when submit the request; CONFIRM after checking the constraints.
ReservationDate:

JPQ, use standard SQL, can apply to any RDBMS.
src/main/resources/reservation.sql

Cache data for fast query response.

Queueing the reservation request to prevent conflict.
Trade-off is the process is in serial - it's hard to persisting data in parallel anyway.

