package entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Picture
{
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private long id;
  private boolean potd;
  @Column(name="theme_id", insertable=false, updatable=false)
  private boolean themeId;
  @Column(name="user_id", insertable=false, updatable=false)
  private boolean userId;
  
  
  @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
  @OneToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="user_id")
  private User user;
  
  @OneToOne
  @JoinColumn(name="theme_id")
  private Theme theme;
  
  @OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval=true)
  @JoinColumn(name="picture_id", updatable=false)
  private Set<Comment> comments;
  @OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  @JoinColumns({@JoinColumn(name="picture_id", referencedColumnName="id")})
  @Where(clause="positive = 1")
  private Set<PictureVote> positives;
  @OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  @JoinColumns({@JoinColumn(name="picture_id", referencedColumnName="id")})
  private Set<PictureVote> votes;
  @OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  @JoinColumns({@JoinColumn(name="picture_id", referencedColumnName="id")})
  @Where(clause="positive = 0")
  private Set<PictureVote> negatives;
  
  public Picture()
  {
    this.positives = new HashSet<PictureVote>();
    this.negatives = new HashSet<PictureVote>();
  }
  
  public long getId()
  {
    return this.id;
  }
  
  public void setId(long id)
  {
    this.id = id;
  }
  
  public boolean isPotd()
  {
    return this.potd;
  }
  
  public void setPotd(boolean potd)
  {
    this.potd = potd;
  }
  
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
  
  public Theme getTheme()
  {
    return this.theme;
  }
  
  public void setTheme(Theme theme)
  {
    this.theme = theme;
  }
  
  public void setUser(User user)
  {
    this.user = user;
  }
  
  public Set<Comment> getComments()
  {
    return this.comments;
  }
  
  public void setComments(Set<Comment> comments)
  {
    this.comments = comments;
  }
  
  public Set<PictureVote> getVotes()
  {
  	return this.votes;
  }
  
  public void setVotes(Set<PictureVote> votes)
  {
    this.votes = votes;
  }
  
  public int getPositives()
  {
    return getVotesCount(true);
  }
  
  public int getNegatives()
  {
    return getVotesCount(false);
  }
  
  private int getVotesCount(boolean positive)
  {
    int result = 0;
    for (PictureVote vote : this.votes) {
      if (vote.isPositive() == positive) {
        result++;
      }
    }
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if ((obj instanceof Picture))
    {
      Picture other = (Picture)obj;
      return getId() == other.getId();
    }
    return false;
  }
}
