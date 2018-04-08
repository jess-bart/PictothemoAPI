package dao;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.context.i18n.LocaleContextHolder;

import entity.Translation;

public class TranslationDao extends BaseDao
{
  public static TranslationDao dao;
  public static String[] supportedLocale = { "FR" };
  public static final String DEFAULT_LOCALE = "EN";
  
  public TranslationDao() {}
  
  public static String getTranslation(Map<String, Translation> translations) { 
	String locale = getCountryLocale();
    if (translations.containsKey(locale))
      return ((Translation)translations.get(locale)).getValue();
    if (translations.containsKey("EN"))
      return ((Translation)translations.get("EN")).getValue();
    return null;
  }
 
  public static String getCountryLocale() { 
	String locale = LocaleContextHolder.getLocale().getCountry().toUpperCase();
    return java.util.Arrays.asList(supportedLocale).contains(locale) ? locale : "EN";
  }
  
  public List<Translation> getTranslation(String code) {
    List<Translation> result = null;
    Session session = sessionFactory.openSession();
    try
    {
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Translation> criteria = builder.createQuery(Translation.class);
      Root<Translation> translationRoot = criteria.from(Translation.class);
      criteria.where(builder.equal(translationRoot.get("code"), code));
      criteria.orderBy(new Order[] { builder.asc(translationRoot.get("locale")) });
      result = session.createQuery(criteria).getResultList();
    } finally {
      session.close();
    }
    
    return result;
  }
}