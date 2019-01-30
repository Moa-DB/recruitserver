package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.*;
import se.moadb.recruitserver.repository.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Service that handles logic concerning the /applications API.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@Service
public class ApplicationService  {
   @Autowired
   private ApplicationRepository applicationRepository;
   @Autowired
   private StatusRepository statusRepository;
   @Autowired
   private PersonRepository personRepository;
   @Autowired
   private CompetenceRepository competenceRepository;
   @Autowired
   private AvailabilityRepository availabilityRepository;

   /**
    * Accept an application.
    * @param id, referring to the application
    * @return The accepted application.
    */
   public Application accept(long id) {

      Status accepted = statusRepository.findByName("ACCEPTED");

      try {
         Application application = applicationRepository.findById(id).get();
         application.setStatus(accepted);
         return applicationRepository.save(application);
      } catch (NoSuchElementException e) {
         throw new EntityDoesNotExistException("application", id);
      }
   }
   /**
    * Reject an application.
    * @param id, referring to the application
    * @return The rejected application.
    */
   public Application reject(long id) {

      Status rejected = statusRepository.findByName("REJECTED");

      try {
         Application application = applicationRepository.findById(id).get();
         application.setStatus(rejected);
         return applicationRepository.save(application);
      } catch (NoSuchElementException e) {
         throw new EntityDoesNotExistException("application", id);
      }
   }
   /**
    * Unhandle an application.
    * @param id, referring to the application
    * @return The unhandled application.
    */
   public Application unhandle(long id) {
      Status unhandled = statusRepository.findByName("UNHANDLED");

      try {
         Application application = applicationRepository.findById(id).get();
         application.setStatus(unhandled);
         return applicationRepository.save(application);
      } catch (NoSuchElementException e) {
         throw new EntityDoesNotExistException("application", id);
      }
   }
   /**
    * Finds applications
    * No attached JSON data means you get all applications.
    * JSON can be used to filter data and should be structured as follows:
    * {
    *    name : string,
    *    application_date : date,
    *    competence : string,
    *    from_time : date,
    *    to_time : date
    * }
    * @return the requested applications
    */
   public List<Application> getApplications(Map<String, Object> request) {

      System.out.println(request.toString()); //TODO remove this printout

//      List<Application> applications = new ArrayList<>();
      List<Application> applications = applicationRepository.findAll();
      Person person = new Person(); // save person ID here


      /* get from_time if present, else empty string */
      String fromTime = request.entrySet().stream()
              .filter(e -> e.getKey().equals("from_time"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();

      /* get to_time if present, else empty string */
      String toTime = request.entrySet().stream()
              .filter(e -> e.getKey().equals("to_time"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();

      /* get name if present, else empty string */
      String name = request.entrySet().stream()
              .filter(e -> e.getKey().equals("name"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();

      /* get name if present, else empty string */
      String competence = request.entrySet().stream()
              .filter(e -> e.getKey().equals("competence"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();

      /* get application_date if present, else empty string */
      String applicationDate = request.entrySet().stream()
              .filter(e -> e.getKey().equals("application_date"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();

      //TODO remove this printout
      System.out.println("from time: " + fromTime + "\n" +
              "to time: " + toTime + "\n" +
              "name: " + name + "\n" +
              "competence: " + competence + "\n" +
              "application date: " + applicationDate);

      /* remove all that are earlier than from_time */
      if (!fromTime.equals("") && !toTime.equals("")){

         /* convert String dates to Date dates */
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         Date fromDate = null;
         Date toDate = null;
         try {
            fromDate = format.parse (fromTime);
            toDate = format.parse(toTime);
         } catch (ParseException e) {
            e.printStackTrace();
         }

         //TODO remove this printout
         System.out.println("from date: " + fromDate + "\n" +
                 "to date: " + toDate);

         /* find all availabilities that match the dates */
         List<Availability> availabilities = availabilityRepository.findAllByFromDateBetween(fromDate, toDate);

         /* get a list with the matching applications */
         List<Application> fromToApplications = applicationRepository.findAllByAvailabilitiesIn(availabilities);

         /* go through each application and remove those that doesn't match from to dates */
         applications.forEach(application -> {
            fromToApplications.forEach(applicationsFT -> {
               if (!applications.contains(applicationsFT)){
                  applications.remove(application);
               }
            });
         });

      }

      return applications;
   }
}
