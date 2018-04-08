package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class SimpleResponse
{
  @JsonProperty
  private boolean success;
  
  public SimpleResponse(boolean success)
  {
    this.success = success;
  }
}
