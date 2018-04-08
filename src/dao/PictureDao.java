package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import entity.Comment;
import entity.Picture;
import entity.Theme;
import entity.User;
import enumeration.ErrorCode;
import exception.PictothemoError;
import pojo.PictureVote;


public class PictureDao extends BaseDao
{
  private String uploadPath;
  private final String[] ACCEPTED_EXTENSION = { "png", "jpg", "jpeg", "bmp" };
  
  public PictureDao() {
    uploadPath = System.getProperty("user.home") + File.separator + "Upload";
  }
  
  public Picture getPictureById(Session sess, long id)
  {
    Picture picture = null;
    Session session = null;
    
    if (sess == null) {
      session = sessionFactory.openSession();
    } else {
      session = sess;
    }
    try {
      String queryString = getQueryString() + "WHERE p.id = :id";
      TypedQuery<Picture> query = session.createQuery(queryString, Picture.class);
      query.setParameter("id", Long.valueOf(id));
      picture = (Picture)query.getSingleResult();
    } finally {
      if (sess == null) {
        session.close();
      }
    }
    return picture;
  }
  
  public Picture getPictureByDate(Date date)
  {
    Picture picture = null;
    Session session = sessionFactory.openSession();
    try
    {
      String queryString = "SELECT DISTINCT p FROM Picture p LEFT JOIN FETCH p.theme theme WHERE p.theme.candidateDate = :date AND potd = true";
      TypedQuery<Picture> query = session.createQuery(queryString, Picture.class);
      query.setParameter("date", date);
      query.setMaxResults(1);
      picture = (Picture)query.getSingleResult();
    }
    catch (NoResultException localNoResultException) {}finally {
      session.close();
    }
    
    return picture;
  }
  
  public List<Picture> getPictures(Date startingDate, Date endingDate, String user, String theme, Boolean potd, Integer vote, String order)
  {
    List<Picture> pictures = null;
    Session session = sessionFactory.openSession();
    try
    {
      String queryString = "SELECT DISTINCT p FROM Picture p LEFT OUTER JOIN Translation t ON p.theme.name = t.code AND t.locale=:locale LEFT OUTER JOIN FETCH p.user LEFT OUTER JOIN FETCH p.comments comments LEFT OUTER JOIN FETCH comments.user LEFT OUTER JOIN FETCH p.votes v LEFT OUTER JOIN FETCH v.user LEFT OUTER JOIN FETCH p.theme theme LEFT OUTER JOIN FETCH theme.names WHERE 1=1 ";

      if ((startingDate != null) && (endingDate != null))
        queryString = queryString + " AND p.theme.candidateDate BETWEEN :starting_date AND :ending_date";
      
      if (user != null)
        queryString = queryString + " AND p.user.pseudo LIKE :user";
      
      if (potd != null)
        queryString = queryString + " AND p.potd = :potd";
      
      if (theme != null)
        queryString = queryString + " AND p.theme.value LIKE :theme";
      
      if (vote != null)
        queryString = queryString + " AND (p.positives.size - p.negatives.size) >= :vote";
      
      if (order != null)
        queryString = queryString + " ORDER BY :order DESC";
      
      TypedQuery<Picture> query = session.createQuery(queryString, Picture.class);
      if ((startingDate != null) && (endingDate != null)) {
        query.setParameter("starting_date", startingDate);
        query.setParameter("ending_date", endingDate);
      }
      
      if (user != null)
        query.setParameter("user", "%" + user + "%");
      
      if (potd != null)
        query.setParameter("potd", potd);
      
      if (theme != null)
        query.setParameter("theme", "%" + theme + "%");
      
      if (vote != null) 
        query.setParameter("vote", vote);
      
      if (order != null) 
        query.setParameter("order", order);
      
      query.setParameter("locale", TranslationDao.getCountryLocale());
      
      pictures = query.getResultList();
    } finally {
      session.close();
    }
    
    return pictures;
  }
  
  public void deletePicture(HttpServletRequest request, long id) throws PictothemoError{
  	Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      UserDao dao = new UserDao();
      dao.setMessageSource(messageSource);
      dao.checkToken(request);
      
      Picture picture = new Picture();
      picture.setId(id);
      
      session.remove(picture);
      tx.commit();
    }
  	finally {
	    session.close();
	  }
  }
  
  public PictureVote voteForPicture(HttpServletRequest request, long id, boolean vote) throws PictothemoError {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      UserDao dao = new UserDao();
      dao.setMessageSource(messageSource);
      User user = dao.checkToken(request);
      
      String queryString = "INSERT INTO PictureVote(user_id, picture_id, positive) VALUES(:user, :picture, :positive) ON DUPLICATE KEY UPDATE positive = :positive";
      

      Query query = session.createNativeQuery(queryString);
      query.setParameter("user", Long.valueOf(user.getId()));
      query.setParameter("picture", Long.valueOf(id));
      query.setParameter("positive", Boolean.valueOf(vote));
      query.executeUpdate();
      PictureVote result = new PictureVote(getPictureById(session, id));
      
      tx.commit();
      
      return result;
    }
    finally {
      session.close();
    }
  }
  
  public void uploadPicture(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws PictothemoError
  {
    UserDao userDao = new UserDao();
    userDao.setMessageSource(messageSource);
    User user = userDao.checkToken(request);
    
    int extIndex = file.getOriginalFilename().lastIndexOf('.');
    if ((extIndex == -1) || (isExtensionAllowed(file.getOriginalFilename().substring(extIndex))))
      throw new PictothemoError(ErrorCode.UNSUPPORTED_FORMAT, getMessage("error.format_not_allowed"));
    if (file.getSize() > 41943040L) {
      throw new PictothemoError(ErrorCode.FILE_TOO_BIG, getMessage("error.file_too_big"));
    }
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try
    {
      Calendar tomorrow = Calendar.getInstance();
      tomorrow.add(5, 1);
      tomorrow.set(11, 0);
      tomorrow.set(12, 0);
      tomorrow.set(13, 0);
      tomorrow.set(14, 0);
      
      System.out.println(Calendar.DATE);
		System.out.println(Calendar.DAY_OF_MONTH);
		System.out.println(Calendar.DAY_OF_WEEK);
		System.out.println(Calendar.DAY_OF_WEEK_IN_MONTH);
		System.out.println(Calendar.DAY_OF_YEAR);
		System.out.println(Calendar.MINUTE);
		System.out.println(Calendar.HOUR);
		System.out.println(Calendar.SECOND);
      
      List<Picture> picturesToRemove = getUserCandidatePicture(tomorrow.getTime(), user.getId());
      
      if (picturesToRemove.size() > 0) {
        cleanUserPictures(picturesToRemove, user.getId(), tomorrow);
      }
      ThemeDao themeDao = new ThemeDao();
      themeDao.setMessageSource(messageSource);
      Theme theme = themeDao.getThemeByDate(tomorrow.getTime());
      
      Picture picture = new Picture();
      picture.setUser(user);
      picture.setTheme(theme);
      
      session.persist(picture);
      
      String fileName = picture.getId() + file.getOriginalFilename().substring(extIndex);
      file.transferTo(new File(uploadPath, fileName));
      
      tx.commit();
    } catch (IOException|IllegalStateException e) {
      tx.rollback();
      throw new PictothemoError(ErrorCode.UPLOAD_ERROR, getMessage("error.unknown"));
    } finally {
      session.close();
    }
  }
  
  public void downloadPicture(HttpServletResponse response, long id) throws PictothemoError {
    if (id < 0L) {
      return;
    }
    File upload = new File(uploadPath);
    File[] files = upload.listFiles();
    
    String pictureId = String.valueOf(id);
    String filename = null;
    
    if (files != null) {
      for (File file : files) {
        filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
        
        if (filename.equals(pictureId)) {
          try {
            InputStream in = new FileInputStream(file);
            response.setContentType(Files.probeContentType(Paths.get(file.getAbsolutePath(), new String[0])));
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            response.setHeader("Content-Length", String.valueOf(file.length()));
            FileCopyUtils.copy(in, response.getOutputStream());
          } catch (IOException e) {
            throw new PictothemoError(ErrorCode.RESOURCE_NOT_AVAILABLE, getMessage("error.ressource_not_available"));
          }
        }
      }
    }
    


    throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
  }
  
  public boolean downloadPicture(HttpServletResponse response, Date date) throws PictothemoError {
    Picture picture = getPictureByDate(date);
    
    if (picture != null) {
      downloadPicture(response, picture.getId());
    }
    return picture != null;
  }
  
  private boolean isExtensionAllowed(String ext) {
    for (String extension : ACCEPTED_EXTENSION) {
      if (extension.toLowerCase().equalsIgnoreCase(ext)) {
        return true;
      }
    }
    return false;
  }
  
  public void removeComment(HttpServletRequest request, long id) throws PictothemoError {
    UserDao userDao = new UserDao();
    userDao.setMessageSource(messageSource);
    User user = userDao.checkToken(request);
    
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try
    {
      String queryString = "DELETE FROM Comment WHERE user_id = :user AND picture_id  = :picture";
      
      Query query = session.createQuery(queryString);
      query.setParameter("user", Long.valueOf(user.getId()));
      query.setParameter("picture", Long.valueOf(id));
      
      int i = query.executeUpdate();
      if (i == 0) {
        throw new PictothemoError(ErrorCode.ENTITY_NOT_FOUND, getMessage("error.entity_not_found"));
      }
      tx.commit();
    } catch (PictothemoError e) {
      tx.rollback();
      throw e;
    }
    catch (Exception e) {
      tx.rollback();
    } finally {
      session.close();
    }
  }
  
  public Comment addComment(HttpServletRequest request, long id, String text) throws PictothemoError {
    UserDao userDao = new UserDao();
    userDao.setMessageSource(messageSource);
    User user = userDao.checkToken(request);
    
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try
    {
      Comment comment = new Comment();
      comment.setUser(user);
      comment.setPictureId(id);
      comment.setText(text);
      session.save(comment);
      tx.commit();
      
      return comment;
    }
    catch (ConstraintViolationException e) {
      throw new PictothemoError(ErrorCode.ENTITY_ALREADY_EXISTS, getMessage("error.entity_already_exists"));
    }
    finally {
      session.close();
    }
  }
  
  private List<Picture> getUserCandidatePicture(Date candidateDate, long user) {
    Session session = sessionFactory.openSession();
    try
    {
      String queryString = "SELECT DISTINCT p FROM Picture p LEFT OUTER JOIN FETCH p.user LEFT OUTER JOIN FETCH p.theme WHERE p.user.id = :user AND p.theme.candidateDate = :candidate_date";

      TypedQuery<Picture> query = session.createQuery(queryString, Picture.class);
      query.setParameter("candidate_date", candidateDate, TemporalType.DATE);
      query.setParameter("user", Long.valueOf(user));
      
      return query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      session.close();
    }
  }
  
  @Async
  private void cleanUserPictures(List<Picture> pictures, long user, Calendar date) {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      String queryString = "DELETE p FROM Picture p LEFT JOIN Theme t ON p.theme_id = t.id AND t.won = 1 WHERE t.candidate_date = :candidate_date AND p.user_id = :user";
      
      Query query = session.createNativeQuery(queryString);
      query.setParameter("candidate_date", date.getTime());
      query.setParameter("user", Long.valueOf(user));
      query.executeUpdate();
      
      tx.commit();
      
      File directory = new File(uploadPath);
      File[] files = directory.listFiles();
      
      String[] filename = null;
      Picture comparison = new Picture();
      
      for (int i = files.length - 1; i >= 0; i--) {
        filename = files[i].getName().split("\\.");
        if ((filename != null) && (filename.length > 0)) {
          comparison.setId(Long.valueOf(filename[0]).longValue());
          
          if (pictures.contains(comparison)) {
            files[i].delete();
          }
        }
      }
    } catch (Exception e) {
      tx.rollback();
    }
    finally {
      session.close();
    }
  }
  
  private String getQueryString() {
    return "SELECT DISTINCT p FROM Picture p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.comments comments LEFT JOIN FETCH comments.user LEFT JOIN FETCH p.votes votesLEFT JOIN FETCH p.theme theme LEFT JOIN FETCH theme.names ";
  }
}