package com.zorvyn.finance_dashboard.execption;

public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String resource, Long id) {
		super(resource + " not found with id: " + id);
	}

}
