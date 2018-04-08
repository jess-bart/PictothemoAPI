package pojo;

import entity.Picture;

public class PictureVote
{
  private int positives;
  private int negatives;
  
  public PictureVote(Picture picture)
  {
    this.positives = picture.getPositives();
    this.negatives = picture.getNegatives();
  }
  
  public int getPositives()
  {
    return this.positives;
  }
  
  public void setPositives(int positives)
  {
    this.positives = positives;
  }
  
  public int getNegatives()
  {
    return this.negatives;
  }
  
  public void setNegatives(int negatives)
  {
    this.negatives = negatives;
  }
}
