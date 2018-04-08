package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class Translation
  implements Serializable
{
  private static final long serialVersionUID = -6216704909713037946L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private int id;
  @NaturalId
  private String code;
  private String locale;
  private String value;
  
  public String getCode()
  {
    return this.code;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public String getLocale()
  {
    return this.locale;
  }
  
  public void setLocale(String locale)
  {
    this.locale = locale;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
}
