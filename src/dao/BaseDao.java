package dao;

import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class BaseDao
{
  protected MessageSource messageSource;
  protected SessionFactory sessionFactory;
  
  public BaseDao()
  {
    sessionFactory = util.HibernateUtil.getSessionFactory();
  }
  
  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }
  
  protected String getMessage(String code) {
    return getMessage(code, null);
  }
  
  protected String getMessage(String code, String[] args) {
    return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
  }
}