package emailsapi.Email;

public class EmailDraft extends Email {

    public EmailDraft(long id, String message, String sender, String recipient, boolean isTrashed) {
        super(id,  message,  sender,  recipient, isTrashed);
    }

   
    @Override
    public EmailState getState() {
        return EmailState.DRAFT;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
    }

    @Override
    public void setSender(String from) {
        super.setSender(from);
    }

    @Override
    public void setRecipient(String to) {
        super.setRecipient(to);
    }
}
