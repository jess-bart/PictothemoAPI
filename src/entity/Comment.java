package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(uniqueConstraints={@javax.persistence.UniqueConstraint(columnNames={"picture_id", "user_id"})})
public class Comment
  implements Serializable
{
  private static final long serialVersionUID = 1575779547581917278L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private int id;
  private String text;
  @Column(name="picture_id")
  private long pictureId;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="publish_date")
  private Date publishDate;
  @ManyToOne(fetch=FetchType.LAZY, cascade={javax.persistence.CascadeType.MERGE})
  @JoinColumn(name="user_id")
  private User user;
  
  public Comment()
  {
    this.publishDate = new Date();
  }
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public String getText()
  {
    return this.text;
  }
  
  public void setText(String text)
  {
    this.text = text;
  }
  
  public String getPublishDate()
  {
    return this.publishDate.toString();
  }
  
  public void setPublishDate(Date publishDate)
  {
    this.publishDate = publishDate;
  }
  
  public pojo.User getUser()
  {
  	try {
  		pojo.User commentUser = new pojo.User();
      commentUser.setId(this.user.getId());
      commentUser.setProfilId(this.user.getProfilId());
      commentUser.setPseudo(this.user.getPseudo());
      return commentUser;
	  }catch(Exception e) {
		  return null;
	  }
  }
  
  public void setUser(User user)
  {
    this.user = user;
  }
  
  public void setPictureId(long pictureId)
  {
    this.pictureId = pictureId;
  }
}
