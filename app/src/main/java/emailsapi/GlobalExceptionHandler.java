package emailsapi;

import java.io.IOException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import emailsapi.Email.Error.EmailCouldNotBeCreated;
import emailsapi.Email.Error.EmailNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

   @ExceptionHandler(EmailNotFoundException.class)
   public void handleEmailNotFoundException(HttpServletResponse response) throws IOException {
       response.sendError(HttpServletResponse.SC_NOT_FOUND);
   }

   @ExceptionHandler(EmailCouldNotBeCreated.class)
   public void handleEmailCouldNotBeCreated(HttpServletResponse response) throws IOException {
       response.sendError(HttpServletResponse.SC_BAD_REQUEST);
   }
}
