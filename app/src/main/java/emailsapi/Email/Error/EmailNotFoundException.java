package emailsapi.Email.Error;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(long id) {
        super("Could not find email with id: " + id);
    }
}
