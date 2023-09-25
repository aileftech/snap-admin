package tech.ailef.dbadmin.external.exceptions;

public class DbAdminException extends RuntimeException {
	private static final long serialVersionUID = 8120227031645804467L;

	public DbAdminException() {
	}
	
	public DbAdminException(Throwable e) {
		super(e);
	}
	
	public DbAdminException(String msg) {
		super(msg);
	}
}
