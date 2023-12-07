package emailsapi.Email;

public abstract class Email {
    protected long id;
    protected long account;
    private String message;
    private String sender;
    private String recipient;
    private boolean isTrashed;

    public Email(long id, String message, String sender, String recipient, boolean isTrashed) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
        this.isTrashed = isTrashed;
    }

    public abstract EmailState getState();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        throw new UnsupportedOperationException("Email sender cannot be changed");
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String from) {
        throw new UnsupportedOperationException("Email sender cannot be changed");
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String to) {
        throw new UnsupportedOperationException("Email recipient cannot be changed");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        throw new UnsupportedOperationException("Email sender cannot be changed");
    }

    public boolean isTrashed() {
        return isTrashed;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Email other = (Email) obj;

        return id == other.id && message.equals(other.message) && sender.equals(other.sender) && recipient.equals(other.recipient) && isTrashed == other.isTrashed;
    }
}
