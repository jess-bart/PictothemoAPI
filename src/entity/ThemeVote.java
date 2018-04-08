package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="UserThemeVote")
public class ThemeVote
  implements Serializable
{
  private static final long serialVersionUID = -239151159896450475L;
  @Id
  @Column(name="user_id")
  private long userId;
  @Id
  @Column(name="theme_id")
  public boolean themeId;
  
  public long getUserId()
  {
    return this.userId;
  }
  
  public void setUserId(long userId)
  {
    this.userId = userId;
  }
  
  public boolean isThemeId()
  {
    return this.themeId;
  }
  
  public void setThemeId(boolean themeId)
  {
    this.themeId = themeId;
  }
}
