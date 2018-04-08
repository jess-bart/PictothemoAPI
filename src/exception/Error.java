package exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enumeration.ErrorCode;
import org.springframework.http.HttpStatus;

public class Error
{
  private ErrorCode code;
  private String message;
  @JsonIgnore
  private HttpStatus status;
  
  public Error(ErrorCode code, String message)
  {
    this.code = code;
    this.message = message;
    this.status = HttpStatus.BAD_REQUEST;
  }
  
  public Error(ErrorCode code, String message, HttpStatus status)
  {
    this.code = code;
    this.message = message;
    this.status = status;
  }
  
  public String getCode()
  {
    return this.code.name();
  }
  
  public void setCode(ErrorCode code)
  {
    this.code = code;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
  
  public HttpStatus getStatus()
  {
    return this.status;
  }
  
  public void setStatus(HttpStatus status)
  {
    this.status = status;
  }
}
