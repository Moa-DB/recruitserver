package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.moadb.recruitserver.domain.*;

import se.moadb.recruitserver.presentation.ApplicationPostRequest;
import se.moadb.recruitserver.presentation.AvailabilityInPostRequest;
import se.moadb.recruitserver.presentation.CompetenceInPostRequest;
import se.moadb.recruitserver.repository.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Service that handles logic concerning the /applications API.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@Service
public class ApplicationService  {
   @Autowired
   private SecurityService securityService;
   @Autowired
   private ApplicationRepository applicationRepository;
   @Autowired
   private StatusRepository statusRepository;
   @Autowired
   private AvailabilityRepository availabilityRepository;
   @Autowired
   private CompetenceRepository competenceRepository;
   @Autowired
   private PersonRepository personRepository;
   @Autowired
   private CompetenceProfileRepository competenceProfileRepository;

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
         java.util.Date fromDate = null;
         java.util.Date toDate = null;
         try {
            fromDate = format.parse (fromTime);
            toDate = format.parse(toTime);
         } catch (ParseException e) {
            e.printStackTrace();
         }

         java.sql.Date sqlStartDate = new java.sql.Date(fromDate.getTime());
         java.sql.Date sqlEndDate = new java.sql.Date(toDate.getTime());

         //TODO remove this printout
         System.out.println("from date: " + sqlStartDate + "\n" +
                 "to date: " + sqlEndDate);

         /* it doesn't seem to matter if using util.Date or sql.Date get the same availabilities */
         /* find all availabilities that match the dates */
         List<Availability> availabilities = availabilityRepository.findAllByFromDateBetween(sqlStartDate, sqlEndDate);

         /* get a list with the matching applications */
         List<Application> fromToApplications = applicationRepository.findAllByAvailabilitiesIn(availabilities);

         //TODO remove this printout
         System.out.println("all applications");
         applications.forEach(application -> {
            System.out.println(application.getId());
         });
         System.out.println("from to applications:");
         fromToApplications.forEach(application -> {
            System.out.println(application.getId());
         });


         /* remove applications that doesn't match the from to criteria */
         Iterator<Application> iterator = applications.iterator();
         while(iterator.hasNext()){
            Application app = iterator.next();
            if (!fromToApplications.contains(app)) {
               System.out.println("Removing " + app.getId());
               iterator.remove();
            }
         }

         //TODO remove this printout
         /* check whats left in applications */
         applications.forEach(application -> {
            System.out.println("application " + application.getId());
         });

      }

      /* remove all that doesn't contain name */
      if (!name.equals("")){

         /* get the person and then find the matching applications */
         Person person = personRepository.findByName(name);
         List<Application> nameApplications = applicationRepository.findAllByPerson(person);


         //TODO remove this printout
         nameApplications.forEach(application -> {
            System.out.println("application " + application.getId());
         });

         /* remove applications that doesn't match the name criteria */
         Iterator<Application> iterator = applications.iterator();
         while(iterator.hasNext()){
            Application app = iterator.next();
            if (!nameApplications.contains(app)) {
               System.out.println("Removing " + app.getId());
               iterator.remove();
            }
         }


         //TODO remove this printout
         /* check whats left in applications */
         applications.forEach(application -> {
            System.out.println("application " + application.getId());
         });

      }

      /* remove all that doesn't contain competence */
      if (!competence.equals("")){
         /* find the competence  */
         Competence comp = competenceRepository.findByName(competence);

         /* find competence profiles */
         List<CompetenceProfile> competenceProfiles = competenceProfileRepository.findByCompetence(comp);

         /* get matching applications */
         List<Application> competenceApplications = applicationRepository.findAllByCompetenceProfilesIn(competenceProfiles);

         //TODO remove printout
         competenceApplications.forEach(c -> {
            System.out.println("competence application id: " + c.getId());
         });

         /* remove applications that doesn't match the competence criteria */
         Iterator<Application> iterator = applications.iterator();
         while(iterator.hasNext()){
            Application app = iterator.next();
            if (!competenceApplications.contains(app)) {
               System.out.println("Removing " + app.getId());
               iterator.remove();
            }
         }

      }

      /* remove all that doesn't contain application_date */
      if (!applicationDate.equals("")){

         /* convert String dates to Date dates */
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         java.util.Date appDate = null;
         try {
            appDate = format.parse (applicationDate);
         } catch (ParseException e) {
            e.printStackTrace();
         }

         java.sql.Date sqlAppDate = new java.sql.Date(appDate.getTime());

         List<Application> dateApplications = applicationRepository.findAllByDate(sqlAppDate);

         //TODO remove printout
         dateApplications.forEach(d -> {
            System.out.println("application date: " + d.getId());
         });

         /* remove applications that doesn't match the competence criteria */
         //TODO how to do this right

//         Iterator<Application> iterator = applications.iterator();
//         while(iterator.hasNext()){
//            Application app = iterator.next();
//            if (!dateApplications.contains(app)) {
//               System.out.println("Removing " + app.getId());
//               iterator.remove();
//            }
//         }


      }



      return applications;
   }

   /**
    * Saves a new Application from an application POST request.
    * @param applicationPostRequest, holding POSTed data
    * @param username, the currently logged in user's username
    * @throws EntityDoesNotExistException, if the posted "competence" does not exist
    * @throws InvalidPostRequestException, if the POST request is invalid
    * @throws RuntimeException, if you cannot fetch a Person from the currently logged in User (application fault)
    * @return The newly saved Application
    */
   public Application saveApplication(ApplicationPostRequest applicationPostRequest, String username) throws InvalidPostRequestException, EntityDoesNotExistException, RuntimeException {
      validatePostRequest(applicationPostRequest);
      User currentUser = securityService.getUser(username);
      Application application = convertIntoEntity(applicationPostRequest, currentUser);
      return applicationRepository.save(application);
   }
   private Application convertIntoEntity(ApplicationPostRequest applicationPostRequest, User currentUser) throws RuntimeException {
      Optional<Person> person = Optional.ofNullable(personRepository.findByUser(currentUser));
      if (!person.isPresent()) {
         throw new RuntimeException("Could not find a person connected to the currently logged in User");
      }

      List<CompetenceProfile> competenceProfiles = new ArrayList<>();
      for (CompetenceInPostRequest cpr : applicationPostRequest.getCompetences()) {
         Competence competence = competenceRepository.findByName(cpr.getCompetence());
         double yearsOfExperience = cpr.getYears_of_experience();
         CompetenceProfile competenceProfile = new CompetenceProfile(competence, yearsOfExperience);
         competenceProfiles.add(competenceProfile);
      }
      List<Availability> availabilities = new ArrayList<>();
      for (AvailabilityInPostRequest apr : applicationPostRequest.getAvailable()) {
         Availability availability = new Availability(apr.getFrom(), apr.getTo());
         availabilities.add(availability);
      }
      Status status = statusRepository.findByName("UNHANDLED");
      return new Application(person.get(), competenceProfiles, availabilities, status);
   }
   private void validatePostRequest(ApplicationPostRequest applicationPostRequest) throws InvalidPostRequestException, EntityDoesNotExistException {
      //check that no mandatory key or list is not present
      if (applicationPostRequest.getAvailable().size() < 1) {
         throw new InvalidPostRequestException("available");
      }
      if (applicationPostRequest.getCompetences().size() < 1) {
         throw new InvalidPostRequestException("competences");
      }
      for (CompetenceInPostRequest cp : applicationPostRequest.getCompetences()) {
         Optional<String> competence = Optional.ofNullable(cp.getCompetence());
         Optional<Double> years = Optional.ofNullable(cp.getYears_of_experience());
         if (!competence.isPresent()) {
            throw new InvalidPostRequestException("competence", "competences");
         } else {
            //check if this competence actually exists
            Optional<Competence> comp = Optional.ofNullable(competenceRepository.findByName(competence.get()));
            if (!comp.isPresent()) {
               throw new EntityDoesNotExistException("competence", competence.get());
            }
         }
         if (!years.isPresent()) {
            throw new InvalidPostRequestException("years_of_experience", "competences");
         }
      }
      for (AvailabilityInPostRequest ap : applicationPostRequest.getAvailable()) {
         Optional<Date> from = Optional.ofNullable(ap.getFrom());
         Optional<Date> to = Optional.ofNullable(ap.getTo());
         if (!from.isPresent()) {
            throw new InvalidPostRequestException("from", "available");
         }
         if (!to.isPresent()) {
            throw new InvalidPostRequestException("to", "available");
         }
      }
   }
}
