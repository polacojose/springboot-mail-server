package emailsapi.Email.Error;

public class EmailCouldNotBeCreated extends RuntimeException {
    public EmailCouldNotBeCreated() {
        super("Could not create email");
    }
}
