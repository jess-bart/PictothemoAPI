package controller;

import dao.TrophyDao;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping({"/trophy"})
public class TrophyController
{
  private TrophyDao dao;
  @Autowired
  private MessageSource messageSource;
  
  @PostConstruct
  public void init()
  {
    dao.setMessageSource(messageSource);
  }
  
  public TrophyController() {
    dao = new TrophyDao();
  }
}