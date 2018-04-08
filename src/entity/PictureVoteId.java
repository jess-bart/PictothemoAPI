package entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PictureVoteId
  implements Serializable
{
  private static final long serialVersionUID = 790410109662388404L;
  @Column(name="user_id", updatable=false)
  private Long userId;
  @Column(name="picture_id", updatable=false)
  private Long pictureId;
  
  public PictureVoteId() {}
  
  public PictureVoteId(Long userId, Long pictureId)
  {
    this.userId = userId;
    this.pictureId = pictureId;
  }
  
  public Long getUserId()
  {
    return this.userId;
  }
  
  public Long getPictureId()
  {
    return this.pictureId;
  }
  
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PictureVoteId)) {
      return false;
    }
    PictureVoteId vote = (PictureVoteId)o;
    return (Objects.equals(getPictureId(), vote.getPictureId())) && 
      (Objects.equals(getUserId(), vote.getUserId()));
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { getPictureId(), getUserId() });
  }
}
