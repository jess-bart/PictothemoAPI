package dao;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.Theme;
import entity.User;
import enumeration.ErrorCode;
import exception.PictothemoError;

public class ThemeDao
  extends BaseDao
{
  public ThemeDao() {}
  
  public List<Theme> getCandidatesThemes(Calendar date) throws PictothemoError
  {
    List<Theme> result = null;
    Session session = sessionFactory.openSession();

    try {
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Theme> query = builder.createQuery(Theme.class);
      Root<Theme> themeRoot = query.from(Theme.class);
      themeRoot.fetch("names");
      
      Calendar themeDate = (Calendar)date.clone();
      date.add(5, -1);
      Predicate tomorrowPredicate = builder.and(builder.equal(themeRoot.get("candidateDate"), date.getTime()), builder.equal(themeRoot.get("won"), Boolean.valueOf(true)));
      Predicate predicate = builder.or(tomorrowPredicate, builder.equal(themeRoot.get("candidateDate"), themeDate.getTime()));
      
      query.where(predicate);
      query.distinct(true);
      query.orderBy(new Order[] { builder.asc(themeRoot.get("candidateDate")) });
      result = session.createQuery(query).getResultList();
    } catch (NoResultException e) {
      throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
    } finally {
      session.close();
    }
    
    return result;
  }
  
  public Theme getLosingTheme(Calendar themeDate) throws PictothemoError
  {
    Session session = sessionFactory.openSession();
    Theme result = null;
    try {
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Theme> query = builder.createQuery(Theme.class);
      Root<Theme> themeRoot = query.from(Theme.class);
      themeRoot.fetch("names");
      
      query.where(new Predicate[] { builder.equal(themeRoot.get("candidateDate"), themeDate.getTime()), 
        builder.equal(themeRoot.get("won"), Boolean.valueOf(false)) });
      query.distinct(true);
      result = (Theme)session.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
    } finally {
      session.close();
    }
    
    return result;
  }
  
  public void setNextTheme() throws PictothemoError {
    Calendar themeDate = Calendar.getInstance();
    themeDate.add(5, 1);
    
    List<Theme> result = null;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try
    {
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Theme> query = builder.createQuery(Theme.class);
      Root<Theme> themeRoot = query.from(Theme.class);
      themeRoot.fetch("names");
      
      query.where(builder.equal(themeRoot.get("candidateDate"), themeDate.getTime()));
      query.distinct(true);
      
      result = session.createQuery(query).getResultList();
      
      int i = 0;
      int themeIndex = 0;
      int voteCount = 0;
      Theme theme = null;
      
      for (Iterator<Theme> it = result.iterator(); it.hasNext();) {
        theme = (Theme)it.next();
        
        if(theme.isWon())
        	return;
        
        if (theme.votes().size() > voteCount) {
          themeIndex = i;
          voteCount = theme.votes().size();
        }
        i++;
      }
      
      ((Theme)result.get(themeIndex)).setWon(true);
      session.saveOrUpdate(result.get(themeIndex));
      tx.commit();
    } catch (NoResultException e) {
      throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
    } finally {
      session.close();
    }
  }
  
  public Theme getThemeByDate(Date date) throws PictothemoError
  {
    Theme result = null;
    Session session = sessionFactory.openSession();
    try
    {
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Theme> query = builder.createQuery(Theme.class);
      Root<Theme> themeRoot = query.from(Theme.class);
      themeRoot.fetch("names");
      
      query.where(new Predicate[] { builder.equal(themeRoot.get("candidateDate"), date), builder.equal(themeRoot.get("won"), Boolean.valueOf(true)) });
      query.distinct(true);
      result = (Theme)session.createQuery(query).setMaxResults(1).getSingleResult();
    } catch (NoResultException e) {
      throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
    } finally {
      session.close();
    }
    
    return result;
  }
  
  public Theme getThemeById(HttpServletRequest request, long id) throws PictothemoError {
    Theme result = null;
    Session session = sessionFactory.openSession();
    try
    {
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Theme> query = builder.createQuery(Theme.class);
      Root<Theme> themeRoot = query.from(Theme.class);
      themeRoot.fetch("names");
      
      query.where(builder.equal(themeRoot.get("id"), Long.valueOf(id)));
      query.distinct(true);
      result = (Theme)session.createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      e.printStackTrace();
      throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
    } finally {
      session.close();
    }
    
    return result;
  }
  
  public void voteForTheme(HttpServletRequest request, long themeId) throws PictothemoError
  {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      UserDao dao = new UserDao();
      dao.setMessageSource(messageSource);
      User user = dao.checkToken(request);
      
      String queryString = "DELETE u From UserThemeVote u JOIN Theme t ON u.theme_id = t.id WHERE t.candidate_date = (SELECT candidate_date FROM Theme th WHERE th.id = :theme) AND user_id = :user";
     
      javax.persistence.Query query = session.createNativeQuery(queryString);
      query.setParameter("theme", Long.valueOf(themeId));
      query.setParameter("user", Long.valueOf(user.getId()));
      query.executeUpdate();
      
      queryString = "INSERT INTO UserThemeVote VALUES(:user, :theme)";
      query = session.createNativeQuery(queryString);
      query.setParameter("user", Long.valueOf(user.getId()));
      query.setParameter("theme", Long.valueOf(themeId));
      query.executeUpdate();
      tx.commit();
    }
    catch (PictothemoError e) {
      tx.rollback();
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      tx.rollback();
    }
    finally {
      session.close();
    }
  }
}