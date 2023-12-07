package emailsapi.Email;

/**
 * @author Jose A. Polaco Jr. <polacojose@gmail.com>
 *
 * Defines an interface for creating, retrieving, updating and deleting emails and sending emails
 */
public interface EmailStore {

    /**
     * Creates an email
     * @param email The email to be created
     * @return The created email
     */
    Email CreateEmail(Email email);


    /**
     * Updates an email
     * @param email The email to be updated
     * @return True if the email was updated
     */
    boolean UpdateEmail(Email email);


    /**
     * Retrieves an email
     * @param id The id of the email
     * @return The email
     */
    Email GetEmail(long id);


    /**
     * Gets a paginated list of emails
     * @param lastId The id of the email to start from (exclusive) 
     * @return The list of emails
     */
    Email[] GetAllEmails(long lastId);

    /**
     * Trashes an email in a recoverable state
     * @param id The id of the email
     * @return True if the email was trashed
     */
    boolean TrashEmail(long id);

    /**
     * Recovers an email from the trash
     * @param id The id of the email
     * @return True if the email was recovered
     */
    boolean UnTrashEmail(long id);

    /**
     * Sends an email
     * @param id The id of the email
     * @return True if the email was sent
     */
    boolean SendEmail(long id);
}
