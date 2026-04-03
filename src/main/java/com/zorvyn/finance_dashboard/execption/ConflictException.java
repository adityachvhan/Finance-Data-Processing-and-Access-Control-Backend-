package com.zorvyn.finance_dashboard.execption;

public class ConflictException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConflictException(String message) {
		super(message);
	}
}
