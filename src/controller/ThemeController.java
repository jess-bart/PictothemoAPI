package controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dao.ThemeDao;
import entity.SimpleResponse;
import entity.Theme;
import exception.PictothemoError;



@RestController
@RequestMapping({"/theme"})
public class ThemeController
{
  private ThemeDao dao;
  @Autowired
  private MessageSource messageSource;
  
  public ThemeController()
  {
    dao = new ThemeDao();
  }
  
  @PostConstruct
  public void init() {
    dao.setMessageSource(messageSource);
  }
  
  @RequestMapping(method={RequestMethod.GET}, value={"/{date}"})
  public ResponseEntity<List<Theme>> getCandidatesThemes(HttpServletRequest request, @PathVariable @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Date date) throws PictothemoError {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    List<Theme> themes = dao.getCandidatesThemes(cal);
    return new ResponseEntity<List<Theme>>(themes, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.POST}, value={"/{id}"})
  public ResponseEntity<SimpleResponse> voteForTheme(HttpServletRequest request, @PathVariable long id) throws PictothemoError {
    dao.voteForTheme(request, id);
    SimpleResponse response = new SimpleResponse(true);
    return new ResponseEntity<SimpleResponse>(response, HttpStatus.OK);
  }
}