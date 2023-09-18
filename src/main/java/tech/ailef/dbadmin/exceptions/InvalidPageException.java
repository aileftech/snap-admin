package tech.ailef.dbadmin.exceptions;

/**
 * Thrown during the computation of pagination if the requested
 * page number is not valid within the current request (e.g. it is greater
 * than the maximum available page). Used internally to redirect the
 * user to a default page. 
 */
public class InvalidPageException extends DbAdminException {
	private static final long serialVersionUID = -8891734807568233099L;
	
	public InvalidPageException() {
	}
	
	public InvalidPageException(String msg) {
		super(msg);
	}

}
