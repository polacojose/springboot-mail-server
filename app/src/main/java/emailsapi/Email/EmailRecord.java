package emailsapi.Email;

public record EmailRecord(long id, String message, String sender, String recipient){
     public EmailRecord (Email email) {
        this(email.getId(), email.getMessage(), email.getSender(), email.getRecipient());
    }
};
