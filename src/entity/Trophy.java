package entity;

import dao.TranslationDao;
import java.io.Serializable;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
public class Trophy
  implements Serializable
{
  private static final long serialVersionUID = -6469633659332507135L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private int id;
  @Column(name="title")
  private String title;
  @Column(name="description")
  private String description;
  @Column(name="validated", insertable=false, updatable=false)
  private boolean validated;
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public void setValidated(boolean validated)
  {
    this.validated = validated;
  }
  
  public boolean getValidated()
  {
    return this.validated;
  }
}
