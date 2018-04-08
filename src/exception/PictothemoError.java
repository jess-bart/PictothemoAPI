package exception;

import enumeration.ErrorCode;
import org.springframework.http.HttpStatus;

public class PictothemoError
  extends Exception
{
  private static final long serialVersionUID = -6264247860033896628L;
  private Error error;
  
  public PictothemoError(ErrorCode code, String message)
  {
    this.error = new Error(code, message);
  }
  
  public PictothemoError(ErrorCode code, String message, HttpStatus status)
  {
    this.error = new Error(code, message, status);
  }
  
  public Error getError()
  {
    return this.error;
  }
  
  public void setError(Error error)
  {
    this.error = error;
  }
}
