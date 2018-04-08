package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dao.TranslationDao;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class Theme
  implements Serializable
{
  private static final long serialVersionUID = -2391511598509850475L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private long id;
  private String name;
  public boolean won;
  @Temporal(TemporalType.DATE)
  @Column(name="candidate_date", updatable=false)
  private Date candidateDate;
  @OneToMany(fetch=FetchType.LAZY)
  @JoinColumn(name="code", referencedColumnName="name")
  @MapKey(name="locale")
  private Map<String, Translation> names;
  @OneToMany(fetch=FetchType.LAZY, cascade={javax.persistence.CascadeType.PERSIST})
  @JoinColumns({@JoinColumn(name="theme_id", referencedColumnName="id")})
  private Set<ThemeVote> votes;
  
  public long getId()
  {
    return this.id;
  }
  
  public void setId(long id)
  {
    this.id = id;
  }
  
  @JsonIgnore
  public String getCodeName()
  {
    return this.name;
  }
  
  public String getName()
  {
    return TranslationDao.getTranslation(this.names);
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public boolean isWon()
  {
    return this.won;
  }
  
  public void setWon(boolean won)
  {
    this.won = won;
  }
  
  public Date getCandidateDate()
  {
    return this.candidateDate;
  }
  
  public void setCandidateDate(Date candidateDate)
  {
    this.candidateDate = candidateDate;
  }
  
  public Set<ThemeVote> votes()
  {
    return this.votes;
  }
  
  public void setVotes(Set<ThemeVote> votes)
  {
    this.votes = votes;
  }
}
