package influence.exceptions;

@SuppressWarnings("serial")
public class NotSameTypeException extends RuntimeException {

	public NotSameTypeException(String message) {
		super(message);
	}
	
    public NotSameTypeException (Throwable cause) {
        super (cause);
    }

    public NotSameTypeException (String message, Throwable cause) {
        super (message, cause);
    }
}
