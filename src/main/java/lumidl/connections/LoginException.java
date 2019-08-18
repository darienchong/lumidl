package lumidl.connections;

/**
 * Custom wrapper for situations arising during logging in.
 * @author dongyu
 *
 */
public class LoginException extends RuntimeException {
	private static final long serialVersionUID = -1279793046321074471L;

	public LoginException(String message) {
		super(message);
	}

	public LoginException(Throwable cause) {
		super(cause);
	}

	public LoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
