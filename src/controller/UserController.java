package controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dao.UserDao;
import exception.PictothemoError;

@RestController
@RequestMapping({"/user"})
public class UserController
{
  private UserDao dao;
  @Autowired
  private MessageSource messageSource;
  
  public UserController()
  {
    this.dao = new UserDao();
  }
  
  @PostConstruct
  public void init()
  {
    this.dao.setMessageSource(this.messageSource);
  }
  
  @RequestMapping(method={RequestMethod.POST}, value={"/authentication"})
  public ResponseEntity<pojo.User> authentificate(HttpServletRequest request)
    throws PictothemoError
  {
    pojo.User user = this.dao.authenticate(request);
    return new ResponseEntity<pojo.User>(user, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.GET}, value={"/id/{id}"})
  public ResponseEntity<entity.User> getUserById(HttpServletRequest request, @PathVariable long id) throws PictothemoError
  {
    entity.User user = null;
    user = this.dao.getUserById(id);
    user.clearSecurityField();
    return new ResponseEntity<entity.User>(user, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.GET}, value={"/pseudo/{pseudo}"})
  public ResponseEntity<entity.User> getUserByPseudo(HttpServletRequest request, @PathVariable String pseudo) throws PictothemoError
  {
    entity.User user = this.dao.getUserByPseudo(request, pseudo);
    user.clearSecurityField();
    return new ResponseEntity<entity.User> (user, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.POST})
  public ResponseEntity<entity.User> createUser(HttpServletRequest request, @RequestHeader String pseudo, @RequestHeader String password)
    throws PictothemoError
  {
    entity.User user = this.dao.createUser(pseudo, password);
    return new ResponseEntity<entity.User>(user, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.PUT}, consumes={"application/json"})
  public ResponseEntity<entity.User> updateUser(HttpServletRequest request, @RequestBody entity.User user)
    throws PictothemoError
  {
    this.dao.updateUser(request, user);
    return new ResponseEntity<entity.User>(user, HttpStatus.OK);
  }
}
