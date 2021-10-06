package com.example.demo.controller;

import com.example.demo.ReservationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandler {
	
	private final static Logger log = LoggerFactory.getLogger(ErrorHandler.class);

	public class ReservationErrorResponse {
		private HttpStatus status;
		private String message;

		public ReservationErrorResponse(HttpStatus status, String message) {
			this.status = status;
			this.message = message;
		}

		public HttpStatus getStatus() {
			return status;
		}

		public void setStatus(HttpStatus status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	@ExceptionHandler(ReservationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ReservationErrorResponse> handleReservationException(ReservationException ex) {
		String errMsg = String.format("%s", ex.getMessage());
		log.error(errMsg, ex);
		ReservationErrorResponse error = new ReservationErrorResponse(HttpStatus.BAD_REQUEST, errMsg);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	@ResponseBody
	public ResponseEntity<ReservationErrorResponse> handleException(Exception ex) {
		String errMsg = String.format("%s", ex.getMessage());
		log.error(errMsg, ex);
		ReservationErrorResponse error = new ReservationErrorResponse(HttpStatus.EXPECTATION_FAILED, errMsg);
		return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
	}


}
