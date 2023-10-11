package tech.ailef.dbadmin.external.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DbAdminNotFoundException extends ResponseStatusException {
	private static final long serialVersionUID = 4090093290330473479L;

	public DbAdminNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
	
	@Override
	public String getMessage() {
		return getReason();
	}

}
