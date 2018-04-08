package pojo;

import java.util.Date;

public class User
{
  private long id;
  private String pseudo;
  private String password;
  private String salt;
  private Date registrationDate;
  private String accessToken;
  private Date expiresToken;
  private int profilId;
  
  public User() {}
  
  public User(entity.User user)
  {
    this.id = user.getId();
    this.pseudo = user.getPseudo();
    this.password = user.getPassword();
    this.profilId = user.getProfilId();
    this.salt = user.getSalt();
    this.registrationDate = user.getRegistrationDate();
    this.accessToken = user.getAccessToken();
    this.expiresToken = user.getExpiresToken();
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
  
  public int getProfilId()
  {
    return this.profilId;
  }
  
  public void setProfilId(int profilId)
  {
    this.profilId = profilId;
  }
  
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
}
