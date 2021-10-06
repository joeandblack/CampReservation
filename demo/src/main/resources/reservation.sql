drop table reservation_dates;
drop table reservations;
drop table camps;
drop table users;

CREATE TABLE users (
  user_id VARCHAR(40) NOT NULL,
  username VARCHAR(80) NOT NULL,
  email VARCHAR(80),
  PRIMARY KEY users_pk (user_id),
  UNIQUE KEY users_uk1 (username)
);

CREATE TABLE camps (
  camp_id VARCHAR(40) NOT NULL,
  capacity INT NOT NULL,
  PRIMARY KEY camps_pk(camp_id)
);

CREATE TABLE reservations (
  reservation_id VARCHAR(40) NOT NULL,
  user_id VARCHAR(40) NOT NULL,
  camp_id VARCHAR(40) NOT NULL,
  startDate BIGINT NOT NULL,
  endDate BIGINT NOT NULL,
  num_reserves INT NOT NULL,
  status VARCHAR(20),
  FOREIGN KEY reservations_fk1 (user_id) REFERENCES users (user_id),
  FOREIGN KEY reservations_fk2 (camp_id) REFERENCES camps (camp_id),
  PRIMARY KEY reservations_pk (reservation_id)
);

CREATE TABLE reservation_dates (
  reservation_date_id VARCHAR(40) NOT NULL,
  reservation_id VARCHAR(40) NOT NULL,
  reservation_date BIGINT NOT NULL,
  FOREIGN KEY reservation_dates_fk1 (reservation_id) REFERENCES reservations (reservation_id),
  UNIQUE  KEY reservation_dates_uk1 (reservation_id, reservation_date),
  PRIMARY KEY reservation_dates_pk (reservation_date_id)
);


INSERT INTO users VALUES ('1', 'user01', 'user01@mail.com');
INSERT INTO users VALUES ('2', 'user02', 'user02@mail.com');
INSERT INTO users VALUES ('3', 'user03', 'user03@mail.com');
INSERT INTO users VALUES ('4', 'user04', 'user04@mail.com');
INSERT INTO users VALUES ('5', 'user05', 'user05@mail.com');
INSERT INTO users VALUES ('6', 'user06', 'user06@mail.com');
INSERT INTO users VALUES ('7', 'user07', 'user07@mail.com');
INSERT INTO users VALUES ('8', 'user08', 'user08@mail.com');
INSERT INTO users VALUES ('9', 'user09', 'user09@mail.com');
INSERT INTO users VALUES ('10','user10', 'user10@mail.com');

INSERT INTO camps VALUES ('101', 10);
INSERT INTO camps VALUES ('102', 20);
INSERT INTO camps VALUES ('103', 15);
INSERT INTO camps VALUES ('104', 35);
INSERT INTO camps VALUES ('105', 40);

INSERT INTO reservations VALUES('001', '1', '101', UNIX_TIMESTAMP('2021-10-01')*1000, UNIX_TIMESTAMP('2021-10-04')*1000, 4, 'CONFIRMED');
INSERT INTO reservation_dates VALUE(uuid(), '001', UNIX_TIMESTAMP('2021-10-01')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '001', UNIX_TIMESTAMP('2021-10-02')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '001', UNIX_TIMESTAMP('2021-10-03')*1000);

INSERT INTO reservations VALUES('002', '2', '102', UNIX_TIMESTAMP('2021-10-01')*1000, UNIX_TIMESTAMP('2021-10-03')*1000, 3, 'CONFIRMED');
INSERT INTO reservation_dates VALUE(uuid(), '002', UNIX_TIMESTAMP('2021-10-01')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '002', UNIX_TIMESTAMP('2021-10-02')*1000);

INSERT INTO reservations VALUES('003', '3', '101', UNIX_TIMESTAMP('2021-10-05')*1000, UNIX_TIMESTAMP('2021-10-08')*1000, 2, 'CONFIRMED');
INSERT INTO reservation_dates VALUE(uuid(), '003', UNIX_TIMESTAMP('2021-10-05')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '003', UNIX_TIMESTAMP('2021-10-06')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '003', UNIX_TIMESTAMP('2021-10-07')*1000);

INSERT INTO reservations VALUES('004', '4', '101', UNIX_TIMESTAMP('2021-10-03')*1000, UNIX_TIMESTAMP('2021-10-06')*1000, 1, 'CONFIRMED');
INSERT INTO reservation_dates VALUE(uuid(), '004', UNIX_TIMESTAMP('2021-10-03')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '004', UNIX_TIMESTAMP('2021-10-04')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '004', UNIX_TIMESTAMP('2021-10-05')*1000);

INSERT INTO reservations VALUES('005', '5', '102', UNIX_TIMESTAMP('2021-10-04')*1000, UNIX_TIMESTAMP('2021-10-05')*1000, 4, 'CONFIRMED');
INSERT INTO reservation_dates VALUE(uuid(), '005', UNIX_TIMESTAMP('2021-10-04')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '005', UNIX_TIMESTAMP('2021-10-05')*1000);

INSERT INTO reservations VALUES('006', '6', '101', UNIX_TIMESTAMP('2021-10-01')*1000, UNIX_TIMESTAMP('2021-10-02')*1000, 3, 'CONFIRMED');
INSERT INTO reservation_dates VALUE(uuid(), '006', UNIX_TIMESTAMP('2021-10-01')*1000);

INSERT INTO reservations VALUES('007', '7', '101', UNIX_TIMESTAMP('2021-10-01')*1000, UNIX_TIMESTAMP('2021-10-03')*1000, 3, 'CONFIRMED');
INSERT INTO reservation_dates VALUE(uuid(), '007', UNIX_TIMESTAMP('2021-10-01')*1000);
INSERT INTO reservation_dates VALUE(uuid(), '007', UNIX_TIMESTAMP('2021-10-02')*1000);

commit;



-- query camp, date, nums
SELECT c.*, COALESCE(Reserved.Total,0) Tot FROM
    (SELECT r.camp_id, from_unixtime(rd.reservation_date/1000) Dat, sum(r.num_reserves) Total FROM reservations r
    JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id
    GROUP BY r.camp_id, r.status, rd.reservation_date
    HAVING rd.reservation_date >= unix_timestamp('2021-10-01')*1000 AND rd.reservation_date < unix_timestamp('2021-10-04')*1000 AND r.status='CONFIRMED'
    ORDER BY r.camp_id, rd.reservation_date) Reserved
RIGHT OUTER JOIN camps c ON c.camp_id=Reserved.camp_id ;


 SELECT r.camp_id, r.status, rd.reservation_date, sum(r.num_reserves) Total FROM reservations r
 JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id
 GROUP BY r.camp_id, r.status, rd.reservation_date
 HAVING r.camp_id= '101' AND r.status='CONFIRMED' AND rd.reservation_date >= unix_timestamp('2021-10-01')*1000 AND rd.reservation_date < unix_timestamp('2021-10-04')*1000
 ORDER BY r.camp_id, rd.reservation_date ;

SELECT MAX(Total) FROM (
 SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r
 JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id
 GROUP BY r.camp_id, r.status, rd.reservation_date
 HAVING r.status='CONFIRMED' AND rd.reservation_date >= unix_timestamp('2021-10-01')*1000 AND rd.reservation_date < unix_timestamp('2021-10-04')*1000
 ) NumReserves;


SELECT c.*, COALESCE(MaxNums.MaxTotal,0) MinMax FROM
    (SELECT NumReserves.camp_id, MAX(NumReserves.Total) MaxTotal FROM (
         SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r
         JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id
         GROUP BY r.camp_id, r.status, rd.reservation_date
         HAVING r.status='CONFIRMED' AND rd.reservation_date >= unix_timestamp('2021-10-01')*1000 AND rd.reservation_date < unix_timestamp('2021-10-04')*1000
         ) NumReserves
         GROUP BY NumReserves.camp_id
     ) MaxNums
 RIGHT OUTER JOIN camps c ON MaxNums.camp_id=c.camp_id
 ORDER BY MinMax
 ;

-- find one camp
SELECT camp_id FROM (
    SELECT c.*, COALESCE(MaxNums.MaxTotal,0) MinMax FROM
        (SELECT NumReserves.camp_id, MAX(NumReserves.Total) MaxTotal FROM (
             SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r
             JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id
             GROUP BY r.camp_id, r.status, rd.reservation_date
             HAVING r.status='CONFIRMED' AND rd.reservation_date >= unix_timestamp('2021-10-01')*1000 AND rd.reservation_date < unix_timestamp('2021-10-04')*1000
             ) NumReserves
             GROUP BY NumReserves.camp_id
         ) MaxNums
     RIGHT OUTER JOIN camps c ON MaxNums.camp_id=c.camp_id
     ORDER BY MinMax
 ) CampView
 WHERE capacity > MinMax + 4
 LIMIT 1;


-- is camp available
SELECT c.* FROM
    (SELECT NumReserves.camp_id, MAX(NumReserves.Total) MaxTotal FROM (
         SELECT r.camp_id, sum(r.num_reserves) Total FROM reservations r
         JOIN reservation_dates rd ON rd.reservation_id=r.reservation_id
         GROUP BY r.camp_id, r.status, rd.reservation_date
         HAVING rd.reservation_date >= unix_timestamp('2021-10-01')*1000 AND rd.reservation_date < unix_timestamp('2021-10-04')*1000 AND r.status='CONFIRMED'
         ) NumReserves
         GROUP BY NumReserves.camp_id
     ) MaxNums
 RIGHT OUTER JOIN camps c ON MaxNums.camp_id=c.camp_id
 WHERE c.camp_id='104' AND c.capacity > (COALESCE(MaxNums.MaxTotal,0) + 4);

