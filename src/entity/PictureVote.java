package entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class PictureVote
{
  @EmbeddedId
  private PictureVoteId id;
  private boolean positive;
  @OneToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="user_id", insertable=false, updatable=false)
  @MapsId("userId")
  private User user;
  
  public PictureVote(PictureVoteId id)
  {
    this.id = id;
  }
  
  public PictureVote() {}
  
  public pojo.User getUser()
  {
  	try {
  		pojo.User pictureUser = new pojo.User();
      pictureUser.setId(this.user.getId());
      pictureUser.setProfilId(this.user.getProfilId());
      pictureUser.setPseudo(this.user.getPseudo());
      return pictureUser;
	  }catch(Exception e) {
		  return null;
	  }
  }
  
  public void setUser(User user)
  {
    this.user = user;
  }
  
  public boolean isPositive()
  {
    return this.positive;
  }
  
  public void setPositive(boolean positive)
  {
    this.positive = positive;
  }
}
