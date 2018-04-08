package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class User
{
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(updatable=false)
  private long id;
  @Column(unique=true, length=100, updatable=false)
  private String pseudo;
  private String password;
  @Column(updatable=false)
  private String salt;
  @Temporal(TemporalType.DATE)
  @Column(name="registration_date", updatable=false)
  private Date registrationDate;
  @Column(name="access_token")
  private String accessToken;
  @Column(name="expires_token")
  private Date expiresToken;
  @Column(name="profil_id")
  private int profilId;
  @Transient
  private List<Trophy> trophies;
  
  public User()
  {
    this.registrationDate = new Date();
  }
  
  public User(String pseudo, String password)
  {
    this();
    this.pseudo = pseudo;
    this.password = password;
  }
  
  public long getId()
  {
    return this.id;
  }
  
  public void setId(long id)
  {
    this.id = id;
  }
  
  public String getPseudo()
  {
    return this.pseudo;
  }
  
  public void setPseudo(String pseudo)
  {
    this.pseudo = pseudo;
  }
  
  @JsonIgnore
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public String getSalt()
  {
    return this.salt;
  }
  
  public void setSalt(String salt)
  {
    this.salt = salt;
  }
  
  public Date getRegistrationDate()
  {
    return this.registrationDate;
  }
  
  public void setRegistrationDate(Date registrationDate)
  {
    this.registrationDate = registrationDate;
  }
  
  public String getAccessToken()
  {
    return this.accessToken;
  }
  
  public void setAccessToken(String accessToken)
  {
    this.accessToken = accessToken;
  }
  
  public Date getExpiresToken()
  {
    return this.expiresToken;
  }
  
  public void setExpiresToken(Date expiresToken)
  {
    this.expiresToken = expiresToken;
  }
  
  public int getProfilId()
  {
    return this.profilId;
  }
  
  public void setProfilId(int profilId)
  {
    this.profilId = profilId;
  }
  
  public List<Trophy> getTrophies()
  {
    return this.trophies;
  }
  
  public void setTrophies(List<Trophy> trophies)
  {
    this.trophies = trophies;
  }
  
  public void clearSecurityField()
  {
    this.salt = null;
    this.accessToken = null;
    this.expiresToken = null;
  }
}
