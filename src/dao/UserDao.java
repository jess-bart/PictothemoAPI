package dao;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;

import entity.Picture;
import entity.User;
import enumeration.ErrorCode;
import exception.PictothemoError;

public class UserDao
  extends BaseDao
{
  private final String FLAG_SALT = "SALTED";
  private static final String SALT = "*iz|Uah[dT_clv@pGinI9@;XV7El~w(sC1a?[-sk0v^Xsq2Fh*:7{zWbXRdKz*Cy";
  public UserDao() {}
  
  public entity.User getUserByPseudo(HttpServletRequest request, String pseudo) throws PictothemoError {
    entity.User result = null;
    Session session = sessionFactory.openSession();
    try
    {
      String queryString = getQueryString() + "WHERE u.pseudo = :pseudo";
      
      TypedQuery<entity.User> query = session.createQuery(queryString, entity.User.class).setCacheable(true);
      query.setParameter("pseudo", pseudo);
      result = (entity.User)query.getSingleResult();
      
      TrophyDao trophyDao = new TrophyDao();
      trophyDao.setMessageSource(messageSource);
      result.setTrophies(trophyDao.getTrophies(session, result.getId()));
    } catch (NoResultException e) {
      throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
    } finally {
      session.close();
    }
    
    return result;
  }
  
  public entity.User getUserById(long id) throws PictothemoError {
    entity.User result = null;
    Session session = sessionFactory.openSession();
    try
    {
      String queryString = getQueryString() + "WHERE u.id = :id";
      
      TypedQuery<entity.User> query = session.createQuery(queryString, entity.User.class).setCacheable(true);
      query.setParameter("id", Long.valueOf(id));
      result = (entity.User)query.getSingleResult();
      
      TrophyDao trophyDao = new TrophyDao();
      trophyDao.setMessageSource(messageSource);
      result.setTrophies(trophyDao.getTrophies(session, result.getId()));
    } catch (NoResultException e) {
      throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
    } finally {
      session.close();
    }
    
    return result;
  }
  
  public entity.User checkToken(HttpServletRequest request) throws PictothemoError {
    entity.User user = null;
    Session session = sessionFactory.openSession();
    String token = request.getHeader("Authorization");
    if ((token == null) || (token.length() == 0)) {
      throw new PictothemoError(ErrorCode.MISSING_PARAMETER, getMessage("error.missing_parameter"), HttpStatus.UNAUTHORIZED);
    }
    try {
    	String queryString = "Select u FROM User u WHERE u.accessToken = :token";
      
      TypedQuery<entity.User> query = session.createQuery(queryString, entity.User.class);
      query.setParameter("token", token);
      user = (entity.User)query.getSingleResult();
      
      if (user.getExpiresToken().before(Calendar.getInstance().getTime()))
        throw new PictothemoError(ErrorCode.EXPIRED_TOKEN, getMessage("error.expired_token"), HttpStatus.FORBIDDEN);
    } catch (NoResultException e) {
      throw new PictothemoError(ErrorCode.AUTH_FAILED, getMessage("error.auth_fail"), HttpStatus.FORBIDDEN);
    } finally {
      session.close();
    }

    return user;
  }
  
  public pojo.User authenticate(HttpServletRequest request) throws PictothemoError {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    
    boolean useSalt = (request.getParameter("flags") != null) && (request.getParameter("flags").indexOf(FLAG_SALT) > -1);
    
    if ((request.getHeader("pseudo") == null) || (request.getHeader("password") == null))
      throw new PictothemoError(ErrorCode.MISSING_PARAMETER, getMessage("error.missing_parameter"));
    
    String pseudo = request.getHeader("pseudo");
    String password = request.getHeader("password");
    
    pojo.User result = null;
    try {
      String queryString = "SELECT u FROM User u ";
      
      if (useSalt) {
        password = convertToSha512(password + SALT);
        queryString = queryString + "WHERE pseudo = :pseudo AND password = :password ";
      } else {
    	 queryString = queryString + "WHERE pseudo = :pseudo AND password = UPPER(SHA2(CONCAT(UPPER(MD5(CONCAT(:password, salt))), :salt), 512)) ";
      }
      
      TypedQuery<entity.User> query = session.createQuery(queryString, entity.User.class);
      query.setParameter("pseudo", pseudo);
      query.setParameter("password", password);
      if (!useSalt)
      	query.setParameter("salt", SALT);
      
      entity.User user = (entity.User)query.getSingleResult();
      String accessToken = UUID.randomUUID().toString();
      user.setAccessToken(accessToken);
      
      Calendar tomorrow = Calendar.getInstance();
      tomorrow.add(5, 1);
      user.setExpiresToken(tomorrow.getTime());
      
      session.update(user);
      tx.commit();
      result = new pojo.User(user);
    } catch (NoResultException e) {
      tx.rollback();
      throw new PictothemoError(ErrorCode.AUTH_FAILED, getMessage("error.auth_fail"), HttpStatus.FORBIDDEN);
    } finally {
      session.close();
    }
    
    return result;
  }
  
  public entity.User createUser(String pseudo, String password) throws PictothemoError {
    entity.User user = new entity.User();
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      user.setPseudo(pseudo);
      
      String accessToken = UUID.randomUUID().toString();
      String salt = UUID.randomUUID().toString();
       
      String computedPassword = convertToMD5(password + salt).toUpperCase();
      computedPassword = convertToSha512(computedPassword + SALT).toUpperCase();
      user.setPassword(computedPassword);
      user.setAccessToken(accessToken);
      user.setSalt(salt);
      user.setExpiresToken(getExpiresToken());
      
      session.save(user);
      session.getTransaction().commit();
    }
    catch (ConstraintViolationException e) {
      throw new PictothemoError(ErrorCode.ENTITY_ALREADY_EXISTS, getMessage("error.user_already_exists"));
    } finally {
      session.close();
    }
    
    return user;
  }
  
  public entity.User updateUser(HttpServletRequest request, entity.User user) throws PictothemoError {
    entity.User dbUser = checkToken(request);
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    
    try
    {
      dbUser.setProfilId(user.getProfilId());
      
      session.update(dbUser);
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      throw e;
    } finally {
      session.close();
    }
    
    return dbUser;
  }
  
  public void deleteUser(HttpServletRequest request, String password) throws PictothemoError{
  	Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      User user = this.checkToken(request);
      
      String computedPassword = convertToMD5(password + user.getSalt()).toUpperCase();
      computedPassword = convertToSha512(computedPassword + SALT).toUpperCase();
      
      if(!user.getPassword().equals(computedPassword))
      	throw new PictothemoError(ErrorCode.AUTH_FAILED, getMessage("error.auth_fail"), HttpStatus.FORBIDDEN);
      
      session.remove(user);
      tx.commit();
    }
  	finally {
	    session.close();
	  }
  }
  
  private Date getExpiresToken() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(5, 1);
    return new Date(calendar.getTimeInMillis());
  }
  
  private String convertToSha512(String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      byte[] bytes = md.digest(password.getBytes("UTF-8"));
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++)
        sb.append(Integer.toString((bytes[i] & 0xFF) + 256, 16).substring(1));
      return sb.toString();
    }
    catch (NoSuchAlgorithmException|UnsupportedEncodingException e) {}
    return null;
  }
  
  private String convertToMD5(String password)
  {
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hash = digest.digest(password.getBytes("UTF-8"));
      return DatatypeConverter.printHexBinary(hash);
    } catch (Exception ex) {}
    return null;
  }
  
  private String getQueryString()
  {
    return "SELECT u FROM User u ";
  }
}