package tech.ailef.dbadmin.external.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

public class ValidationErrorsContainer {
	private Map<String, List<ConstraintViolation<?>>> errors = new HashMap<>();
	
	public ValidationErrorsContainer(ConstraintViolationException e) {
		e.getConstraintViolations().forEach(c -> {
			errors.putIfAbsent(c.getPropertyPath().toString(), new ArrayList<>());
			errors.get(c.getPropertyPath().toString()).add(c);
		});
	}
	
	public List<ConstraintViolation<?>> forField(String name) {
		return errors.getOrDefault(name, new ArrayList<>());
	}
	
	public boolean hasErrors(String name) {
		return forField(name).size() > 0;
	}
	
	public boolean isEmpty() {
		return errors.isEmpty();
	}

	@Override
	public String toString() {
		return "ValidationErrorsContainer [errors=" + errors + "]";
	}
	
	
}
