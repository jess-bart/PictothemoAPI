package controller;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dao.PictureDao;
import entity.Comment;
import entity.Picture;
import entity.SimpleResponse;
import exception.PictothemoError;
import pojo.PictureVote;

@RestController
@RequestMapping({"/picture"})
public class PictureController
{
  private PictureDao dao;
  @Autowired
  private MessageSource messageSource;
  
  public PictureController()
  {
    dao = new PictureDao();
  }
  
  @PostConstruct
  public void init() {
    dao.setMessageSource(messageSource);
  }
  
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, value={"/{date}"})
  public ResponseEntity<Picture> getPictureById(HttpServletRequest request, @PathVariable long date) throws PictothemoError {
    Picture picture = dao.getPictureById(null, date);
    return new ResponseEntity<Picture> (picture, HttpStatus.OK);
  }


  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public ResponseEntity<List<Picture>> getPictures(@DateTimeFormat(iso=DateTimeFormat.ISO.DATE) @RequestParam(value="startingDate", required=false) Date startingDate, @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) @RequestParam(value="endingDate", required=false) Date endingDate, @RequestParam(value="user", required=false) String user, @RequestParam(value="theme", required=false) String theme, @RequestParam(value="potd", required=false) Boolean potd, @RequestParam(value="voteCount", required=false) Integer vote)
    throws PictothemoError
  {
    List<Picture> pictures = dao.getPictures(startingDate, endingDate, user, theme, potd, vote, null);
    return new ResponseEntity<List<Picture>>(pictures, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.POST}, value={"/vote/{id}/{vote}"})
  public ResponseEntity<PictureVote> voteForPicture(HttpServletRequest request, @PathVariable long id, @PathVariable boolean vote) throws PictothemoError {
    PictureVote result = dao.voteForPicture(request, id, vote);
    return new ResponseEntity<PictureVote>(result, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.DELETE}, value={"/{id}"})
  public ResponseEntity<SimpleResponse> deletePicture(HttpServletRequest request, @PathVariable long id) throws PictothemoError {
    dao.deletePicture(request, id);
    SimpleResponse response = new SimpleResponse(true);
    return new ResponseEntity<SimpleResponse>(response, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.GET}, value={"/content/{id}"})
  public ResponseEntity<SimpleResponse> downloadPicture(HttpServletResponse response, @PathVariable long id) throws PictothemoError {
    dao.downloadPicture(response, id);
    SimpleResponse simpleResponse = new SimpleResponse(true);
    return new ResponseEntity<SimpleResponse>(simpleResponse, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.GET}, value={"/content/date/{date}"})
  public ResponseEntity<SimpleResponse> downloadPicture(HttpServletResponse response, @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) @PathVariable Date date) throws PictothemoError {
    boolean result = dao.downloadPicture(response, date);
    SimpleResponse simpleResponse = new SimpleResponse(result);
    return new ResponseEntity<SimpleResponse>(simpleResponse, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.POST}, value={"/content"})
  public ResponseEntity<SimpleResponse> uploadPicture(HttpServletRequest request, HttpServletResponse response, @RequestParam("picture") MultipartFile file) throws PictothemoError {
    dao.uploadPicture(request, response, file);
    SimpleResponse simpleResponse = new SimpleResponse(true);
    return new ResponseEntity<SimpleResponse>(simpleResponse, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.POST}, value={"/comment/{id}"})
  public ResponseEntity<Comment> addComment(HttpServletRequest request, @PathVariable long id, @RequestParam("text") String text) throws PictothemoError {
    Comment result = dao.addComment(request, id, text);
    return new ResponseEntity<Comment>(result, HttpStatus.OK);
  }
  
  @RequestMapping(method={RequestMethod.DELETE}, value={"/comment/{id}"})
  public ResponseEntity<SimpleResponse> removeComment(HttpServletRequest request, @PathVariable long id) throws PictothemoError {
    dao.removeComment(request, id);
    SimpleResponse simpleResponse = new SimpleResponse(true);
    return new ResponseEntity<SimpleResponse>(simpleResponse, HttpStatus.OK);
  }
}