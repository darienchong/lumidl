package lumidl.connection;

/**
 * Custom exception to wrap authentication errors.
 * @author dongyu
 *
 */
public class AuthenticationException extends RuntimeException {
	private static final long serialVersionUID = 1470404041568132024L;
	
	public AuthenticationException(String string) {
		super(string);
	}
}
