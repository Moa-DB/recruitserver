package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.moadb.recruitserver.domain.*;

import se.moadb.recruitserver.presentation.ApplicationPostRequest;
import se.moadb.recruitserver.presentation.ApplicationStatusPutRequest;
import se.moadb.recruitserver.presentation.AvailabilityInPostRequest;
import se.moadb.recruitserver.presentation.CompetenceInPostRequest;
import se.moadb.recruitserver.repository.*;

import javax.persistence.OptimisticLockException;
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
    * A JSON body should be sent containing:
    * - "status" : "ACCEPTED" | "REJECTED" | "UNHANDLED"
    * This key should contain what the client application thinks the current (pre-change) status of the application is.
    * If this status does not correspond to what is currently in the database, this means someone else is handling the application and the transaction will be aborted.
    * @param id, referring to the application
    * @param oldStatusToCompareWith, the current status that the client thinks this application has
    * @return The accepted application.
    * @throws EntityDoesNotExistException, when accepting a non-existing application.
    * @throws ConcurrentAccessException, when accessing application simultaneously with another user
    */
   public Application accept(long id, String oldStatusToCompareWith) throws EntityDoesNotExistException, ConcurrentAccessException {
      return changeApplicationStatus(id, Status.ACCEPTED, oldStatusToCompareWith);
   }
   /**
    * Reject an application.
    * A JSON body should be sent containing:
    * - "status" : "ACCEPTED" | "REJECTED" | "UNHANDLED"
    * This key should contain what the client application thinks the current (pre-change) status of the application is.
    * If this status does not correspond to what is currently in the database, this means someone else is handling the application and the transaction will be aborted.
    * @param id, referring to the application
    * @param oldStatusToCompareWith, the current status that the client thinks this application has
    * @return The rejected application.
    * @throws EntityDoesNotExistException, when rejecting a non-existing application.
    * @throws ConcurrentAccessException, when accessing application simultaneously with another user
    */
   public Application reject(long id, String oldStatusToCompareWith) throws EntityDoesNotExistException, ConcurrentAccessException {
      return changeApplicationStatus(id, Status.REJECTED, oldStatusToCompareWith);
   }
   /**
    * Unhandle an application.
    * A JSON body should be sent containing:
    * - "status" : "ACCEPTED" | "REJECTED" | "UNHANDLED"
    * This key should contain what the client application thinks the current (pre-change) status of the application is.
    * If this status does not correspond to what is currently in the database, this means someone else is handling the application and the transaction will be aborted.
    * @param id, referring to the application
    * @param oldStatusToCompareWith, the current status that the client thinks this application has
    * @return The unhandled application.
    * @throws EntityDoesNotExistException, when unhandling a non-existing application.
    * @throws ConcurrentAccessException, when accessing application simultaneously with another user
    */
   public Application unhandle(long id, String oldStatusToCompareWith) throws EntityDoesNotExistException, ConcurrentAccessException {
      return changeApplicationStatus(id, Status.UNHANDLED, oldStatusToCompareWith);
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


      List<Application> applications = applicationRepository.findAll();

      String fromTime = request.entrySet().stream()
              .filter(e -> e.getKey().equals("from_time"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();
      String toTime = request.entrySet().stream()
              .filter(e -> e.getKey().equals("to_time"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();
      String name = request.entrySet().stream()
              .filter(e -> e.getKey().equals("name"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();
      String competence = request.entrySet().stream()
              .filter(e -> e.getKey().equals("competence"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();
      String applicationDate = request.entrySet().stream()
              .filter(e -> e.getKey().equals("application_date"))
              .map(Map.Entry::getValue).findFirst().orElse("").toString();

      if (!fromTime.equals("") && !toTime.equals("")){
         Date fDate = Date.valueOf(fromTime);
         Date tDate = Date.valueOf(toTime);
         List<Availability> availabilities = availabilityRepository.findAllByFromDateBetween(fDate, tDate);
         List<Application> fromToApplications = applicationRepository.findAllByAvailabilitiesIn(availabilities);
         applications.removeIf(app -> !fromToApplications.contains(app));
      }

      if (!name.equals("")){
         Person person = personRepository.findByName(name);
         List<Application> nameApplications = applicationRepository.findAllByPersonLike(person);
         applications.removeIf(app -> !nameApplications.contains(app));
      }

      if (!competence.equals("")){
         Competence comp = competenceRepository.findByName(competence);
         List<CompetenceProfile> competenceProfiles = competenceProfileRepository.findByCompetence(comp);
         List<Application> competenceApplications = applicationRepository.findAllByCompetenceProfilesIn(competenceProfiles);
         applications.removeIf(app -> !competenceApplications.contains(app));
      }

      if (!applicationDate.equals("")){
         Date applDate = Date.valueOf(applicationDate);
         List<Application> dateApplications = applicationRepository.findAllByDate(applDate);
         applications.removeIf(app -> !dateApplications.contains(app));
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
      Date today = getCurrentDate();
      return new Application(person.get(), competenceProfiles, availabilities, status, today);
   }
   private Date getCurrentDate() {
      return new Date(Calendar.getInstance().getTime().getTime());
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
   private Application changeApplicationStatus(long applicationId, String statusName, String oldStatusToCompareWith) throws EntityDoesNotExistException, ConcurrentAccessException {
      Status status = statusRepository.findByName(statusName);

      try {
         Application application = applicationRepository.findById(applicationId).get();
         checkForConflict(application, oldStatusToCompareWith); //íf app was changed by someone else while we were browsing, inform user
         application.setStatus(status);
         return applicationRepository.save(application);
      } catch (NoSuchElementException e) {
         throw new EntityDoesNotExistException("application", applicationId);
      } catch (OptimisticLockException e) {
         throw new ConcurrentAccessException("application", "status");
      }

   }
   //if another user accesses and changes status of application before we send our PUT request, warn user and abort
   private void checkForConflict(Application application, String oldStatusToCompareWith) throws ConcurrentAccessException {
      String currentStatus = application.getStatus().getName();
      if (!currentStatus.equals(oldStatusToCompareWith)) {
         throw new ConcurrentAccessException();
      }
   }
}