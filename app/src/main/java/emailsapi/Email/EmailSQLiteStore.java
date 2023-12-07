package emailsapi.Email;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import emailsapi.Email.Error.EmailNotFoundException;

/**
 * @author Jose A. Polaco Jr. <polacojose@gmail.com>
 * Defines a SQLite based implementation of the EmailStore
 */
public class EmailSQLiteStore implements EmailStore {

    private final Connection conn;

    /**
     * Connects or initializes and connects a new SQLite store
     * @param name The file name of the database
     * @return The new SQLite store
     */
    public EmailSQLiteStore(String name) throws SQLException {
        this.conn = connectDB(name);
        createTable();
    }

    private void createTable() throws SQLException {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS email (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	message text NOT NULL,\n"
                + "	sender text NOT NULL,\n"
                + "	recipient text NOT NULL,\n"
                + "	state text NOT NULL,\n"
                + "     trashed boolean NOT NULL,\n"
                + "     trashedTime timestamp\n"
                + ");";
        
        Statement stmt = this.conn.createStatement();
        // create a new table
        stmt.execute(sql);
    }

    private Connection connectDB(String name) throws SQLException {
        String url = "jdbc:sqlite:db/" + name + ".db";

        Connection conn = DriverManager.getConnection(url);

        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("The driver name is " + meta.getDriverName());
        System.out.println("Database connected.");

        return conn;
    }

    @Override
    public Email CreateEmail(Email email) {
        try {

            Statement stmt = this.conn.createStatement();
            stmt.execute("INSERT INTO email (message, sender, recipient, state, trashed) VALUES ('"
                    + email.getMessage()
                    + "', '"
                    + email.getSender()
                    + "', '"
                    + email.getRecipient()
                    + "', '"
                    + email.getState()
                    + "', '"
                    + email.isTrashed()
                    + "');");

            stmt = this.conn.createStatement();
            stmt.execute("SELECT last_insert_rowid();");
            ResultSet rs = stmt.getResultSet();
            if (rs.next()) {
                email.setId(rs.getLong(1));
                return email;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                this.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public boolean TrashEmail(long id) {
        try {
            Statement stmt = this.conn.createStatement();
            stmt.execute("UPDATE email SET trashed = 1, trashedTime = CURRENT_TIMESTAMP " 
            + "WHERE id = " + id + " AND NOT trashed;");
            stmt = this.conn.createStatement();
            stmt.execute("SELECT changes();");
            ResultSet rs = stmt.getResultSet();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean UnTrashEmail(long id) {
        try {
            Statement stmt = this.conn.createStatement();
            stmt.execute("UPDATE email SET trashed = 0 " 
            + "WHERE id = " + id + " AND trashed;");
            stmt = this.conn.createStatement();
            stmt.execute("SELECT changes();");
            ResultSet rs = stmt.getResultSet();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Email GetEmail(long id) {
        try {
            Statement stmt = this.conn.createStatement();
            boolean result = stmt.execute("SELECT * FROM email WHERE id = " + id + " AND NOT trashed;");

            if (result) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    return extractEmail(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                this.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        throw new EmailNotFoundException(id);
    }

    @Override
    public boolean UpdateEmail(Email email) {
        try {

            Statement stmt = this.conn.createStatement();
            stmt.execute("UPDATE email SET message = '"
                    + email.getMessage()
                    + "', sender = '"
                    + email.getSender()
                    + "', recipient = '"
                    + email.getRecipient()
                    + "', state = '"
                    + email.getState()
                    + "' WHERE id = "
                    + email.getId() 
                    + " AND state = 'DRAFT'"
                    + " AND NOT trashed;");
            
            stmt = this.conn.createStatement();
            stmt.execute("SELECT changes();");
            ResultSet rs = stmt.getResultSet();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                this.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public Email[] GetAllEmails(long lastId) {

        List<Email> emails = new ArrayList<>();
        try {
            Statement stmt = this.conn.createStatement();
            boolean result = stmt.execute("SELECT * FROM email WHERE NOT trashed AND id > " + lastId + " ORDER BY id LIMIT 10;");
            if (result) {
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    emails.add(extractEmail(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                this.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return emails.toArray(new Email[0]);
    }

    private Email extractEmail(ResultSet rs) throws SQLException {
        EmailState state = EmailState.valueOf(rs.getString(5));
        switch(state) {
            case SENT:
            return new EmailSent(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(6));
            case RECEIVED:
            return new EmailReceived(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(6));
            default:
            return new EmailDraft(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(6));
        }
    }

    @Override
    public boolean SendEmail(long id) {
        try {

            Statement stmt = this.conn.createStatement();
            boolean result = stmt.execute("SELECT * FROM email WHERE id = " + id + " AND NOT trashed;");

            Email email = null;
            if (result) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    email = extractEmail(rs);
                }
            }

            if (email == null){
                return false;
            }

            if (email.getClass() != EmailDraft.class) {
                return false;
            }

            EmailSent sent = new EmailSent(email.getId(), email.getMessage(), email.getSender(), email.getRecipient(), false);

            stmt = this.conn.createStatement();
            stmt.execute("UPDATE email SET message = '"
                    + sent.getMessage()
                    + "', sender = '"
                    + sent.getSender()
                    + "', recipient = '"
                    + sent.getRecipient()
                    + "', state = '"
                    + sent.getState()
                    + "' WHERE id = "
                    + sent.getId() 
                    + " AND state = 'DRAFT'"
                    + " AND NOT trashed;");
            
            stmt = this.conn.createStatement();
            stmt.execute("SELECT changes();");
            ResultSet rs = stmt.getResultSet();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                this.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
