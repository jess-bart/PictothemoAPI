

package dao;

import entity.Trophy;
import enumeration.ErrorCode;
import exception.PictothemoError;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.hibernate.Session;

public class TrophyDao
  extends BaseDao
{
  public TrophyDao() {}
  
  public List<Trophy> getTrophies(Session session, long userId) throws PictothemoError
  {
    String queryString = "SELECT t.id, tr_t.value AS title, tr_d.value As description, (CASE WHEN (u.user_id IS NOT NULL) THEN 1 ELSE 0 END) AS validated FROM Trophy t LEFT JOIN UserTrophy u ON t.id = u.trophy_id AND u.user_id = :user LEFT JOIN Translation tr_t ON t.title = tr_t.code And tr_t.locale = :locale LEFT JOIN Translation tr_d ON t.description = tr_d.code And tr_d.locale = :locale ORDER BY t.id";
    
    List<Trophy> trophies = null;
    try
    {
      TypedQuery<Trophy> query = session.createNativeQuery(queryString, Trophy.class);
      query.setParameter("user", Long.valueOf(userId));
      query.setParameter("locale", TranslationDao.getCountryLocale());
      
      trophies = query.getResultList();
    } catch (NoResultException e) {
      throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
    }
    
    return trophies;
  }
}