package exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionResolver
{
  @ExceptionHandler({PictothemoError.class})
  public ResponseEntity<Error> globalError(PictothemoError e)
  {
    return new ResponseEntity<Error>(e.getError(), e.getError().getStatus());
  }
}
