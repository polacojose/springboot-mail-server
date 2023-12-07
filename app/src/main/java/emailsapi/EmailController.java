package emailsapi;

import java.sql.SQLException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emailsapi.Email.Email;
import emailsapi.Email.EmailDraft;
import emailsapi.Email.EmailSQLiteStore;
import emailsapi.Email.Error.EmailCouldNotBeCreated;
import emailsapi.Email.Error.EmailNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class EmailController {

    @GetMapping("api/email")
    public Email[] getAllEmails(@RequestParam(defaultValue = "0") long lastid) {
        try {
            EmailSQLiteStore store = new EmailSQLiteStore("email");
            Email[] emails = store.GetAllEmails(lastid);
            return emails;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Email[0];
    }

    @GetMapping("api/email/{id}")
    public Email getEmail(@PathVariable long id) {
        try {
            EmailSQLiteStore store = new EmailSQLiteStore("email");
            Email email = store.GetEmail(id);
            if(email != null) {
                return email;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new EmailNotFoundException(id);
    }

    @PutMapping("api/email")
    public Email createEmail(@RequestBody EmailDraft requestEmail, HttpServletResponse response) {
        try {
            EmailSQLiteStore store = new EmailSQLiteStore("email");
            Email email = store.CreateEmail(requestEmail);
            if(email != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                return email;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new EmailCouldNotBeCreated();
    }

    @DeleteMapping("api/email/{id}")
    public String trashEmail(@PathVariable long id, HttpServletResponse response) {
        try {
            EmailSQLiteStore store = new EmailSQLiteStore("email");
            if (store.TrashEmail(id)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return "Email trashed";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new EmailNotFoundException(id);
    }

    @PatchMapping("api/email")
    public EmailDraft updateEmail(@RequestBody EmailDraft email) {
        try {
            EmailSQLiteStore store = new EmailSQLiteStore("email");
            if(store.UpdateEmail(email)){
                return email;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new EmailNotFoundException(email.getId());
    }

    @PostMapping("api/sendemail/{id}")
    public boolean sendEmail(@PathVariable long id) {
        try {
            EmailSQLiteStore store = new EmailSQLiteStore("email");
            if(store.SendEmail(id)){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new EmailNotFoundException(id);
    }
}












