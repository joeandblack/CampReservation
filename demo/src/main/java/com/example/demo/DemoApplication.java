package com.example.demo;

import com.example.demo.entity.Camp;
import com.example.demo.entity.User;
import com.example.demo.persist.CampRepository;
import com.example.demo.persist.ReservationRepository;
import com.example.demo.persist.ReservationDateRepository;
import com.example.demo.persist.UserRepository;
import com.example.demo.service.CampService;
import com.example.demo.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	private static final int MAX_CAMPS = 10;  // make this configurable
	private static final int MAX_USERS = 1000;// make this configurable

	static {
		Utils.DATEFORMAT.setTimeZone(TimeZone.getTimeZone("Pacific/Honolulu"));
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner campRunner (CampRepository campRepo) {
		log.info("campRunner: ");
		return args -> {
			// assume there are limited camps
			for(int i=1; i<=MAX_CAMPS; i++) {
				String campId = String.format("camp%02d", i);
				campRepo.save(new Camp(campId));
			}
			campRepo.findAll().forEach(System.out::println);
		};
	}

	@Bean
	CommandLineRunner userRunner (UserRepository userRepo) {
		return args -> {
			// assume there are enough users
			for(int i=1; i<=MAX_USERS; i++) {
				String username = String.format("user%02d", i);
				String email = String.format("%s@email.com", username);
				userRepo.save(new User(username, email));
			}
			//userRepo.findAll().forEach(System.out::println);
		};
	}

	//Temporary for testing
	@Bean
	CommandLineRunner reservationRunner (UserRepository userRepo, ReservationService reService, CampService campService) {
		return args -> {
			Iterator<User> userIterator = userRepo.findAll().iterator(); // assume user is not repeated and unlimited

			// simulates random reserved
			Calendar cal = Calendar.getInstance();
			for (int i=1; i<=10; i++) {  // next 15 days
				int numReserves = new Random().nextInt(10) + 1;
				int days = new Random().nextInt(3) + 1;  // 1 to 3 days
				doReserve(userIterator.next(), cal, days, numReserves, reService, campService);
				cal.add(Calendar.DATE, 1);
			}

//			// more reservations
//			Calendar c = Calendar.getInstance();
//			doReserve(userIterator.next(), c,3, 5, reService, campService);
//			c.add(Calendar.DATE, 1);
//			doReserve(userIterator.next(), c,1, 2, reService, campService);
//			c.add(Calendar.DATE, 3);
//			doReserve(userIterator.next(), c,2, 1, reService, campService);
//			c.add(Calendar.DATE, 2);
//			doReserve(userIterator.next(), c,3, 4, reService, campService);
//			c.add(Calendar.DATE, 1);
//			doReserve(userIterator.next(), c,2, 4, reService, campService);

		};
	}

	private void doReserve(User user, Calendar cal, int days, int numReserves, ReservationService reService, CampService campService) {
		long startDate = cal.getTimeInMillis();
		Calendar copy = Calendar.getInstance();
		copy.setTimeInMillis(startDate);
		copy.add(Calendar.DATE, days);
		long endDate = copy.getTimeInMillis();
		Camp camp = campService.getOneCamp(startDate, endDate, numReserves);
		log.debug("getOneCam=>"+camp);
		reService.makeReservation(user, camp, startDate, endDate, numReserves);
	}

	//Temporary for testing
	@Bean
	CommandLineRunner searchRunner (ReservationRepository resRepo, ReservationDateRepository rcRepo, CampService campService) {
		return args -> {
			log.debug("====== search by reservedDate ====");

			resRepo.findAll().forEach(System.out::println);
			rcRepo.findAll().forEach(System.out::println);

			campService.updateData();

			log.debug("print 5 days: ");
			campService.getAvailableDates(5);

			log.debug("====== search end ====");
		};
	}


}
