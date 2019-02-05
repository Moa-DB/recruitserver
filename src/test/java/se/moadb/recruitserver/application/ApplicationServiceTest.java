package se.moadb.recruitserver.application;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import se.moadb.recruitserver.domain.*;
import se.moadb.recruitserver.presentation.ApplicationPostRequest;
import se.moadb.recruitserver.presentation.AvailabilityInPostRequest;
import se.moadb.recruitserver.presentation.CompetenceInPostRequest;
import se.moadb.recruitserver.repository.*;

import java.sql.Date;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationServiceTest {

   @Autowired
   private ApplicationService applicationService;

   @MockBean
   private ApplicationRepository applicationRepository;
   @MockBean
   private StatusRepository statusRepository;
   @MockBean
   private CompetenceRepository competenceRepository;
   @MockBean
   private PersonRepository personRepository;
   @MockBean
   private SecurityService securityService;
   @MockBean
   private CompetenceProfileRepository competenceProfileRepository;
   @MockBean
   private AvailabilityRepository availabilityRepository;

   private Application unhandledapp;
   private Application acceptedapp;
   private Application rejectedapp;
   private ApplicationPostRequest apr;
   private User user;
   private String username;
   private Person p;
   private Competence c1;
   private List<CompetenceProfile> competenceProfiles;
   private Map<String, Object> emptyRequest;
   private Map<String, Object> nameRequest;
   private Map<String, Object> competenceRequest;
   private Map<String, Object> timePeriodRequest;
   private Map<String, Object> applicationDateRequest;
   private Date applicationDate1;
   private Date fromDate;
   private Date toDate;
   private List<Application> allApplications;
   private List<Application> nameApplications;
   private List<Application> competenceApplications;
   private List<Application> applicationDate1Applicaions;
   private List<Application> timePeriodApplications;
   private List<Availability> timePeriodAvailabilities;

   @Before
   public void setUp() {
      p = new Person("Per", "Strand", "19671212-1211", "per@strand.kth.se", new User());
      Person p2 = new Person("Greta", "Borg", "19820501-3244", "greta@strand.se", new User());
      Person p3 = new Person("Bob", "Ross", "19640814-3277", "bob@ross.se", new User());
      c1 = new Competence("Korvgrillning");
      Competence c2 = new Competence("Karuselldrift");
      CompetenceProfile cp1 = new CompetenceProfile(c1, 3.5);
      CompetenceProfile cp2 = new CompetenceProfile(c2,5);
      cp1.setId(7);
      cp2.setId(8);
      ArrayList<CompetenceProfile> cplist = new ArrayList<>();
      cplist.add(cp1);
      ArrayList<CompetenceProfile> cplist2 = new ArrayList<>();
      cplist2.add(cp1);
      cplist2.add(cp2);
      ArrayList<CompetenceProfile> cplist3 = new ArrayList<>();
      cplist3.add(cp2);

      /* list of competences */
      competenceProfiles = new ArrayList<>();
      competenceProfiles.add(cp1); // add a competence profile to the list

      ArrayList<Availability> alist = new ArrayList<>();
      Date from = Date.valueOf("2014-02-24");
      Date to = Date.valueOf("2014-05-26");
      Availability a1 = new Availability(from, to);
      a1.setId(5);
      alist.add(a1);

      ArrayList<Availability> alist2 = new ArrayList<>();
      Date from2 = Date.valueOf("2014-07-10");
      Date to2 = Date.valueOf("2014-08-10");
      Availability a2 = new Availability(from2, to2);
      a2.setId(6);
      alist2.add(a2);

      ArrayList<Availability> alist3 = new ArrayList<>();
      Date from3 = Date.valueOf("2001-01-24");
      Date to3 = Date.valueOf("2012-01-18");
      Availability a3 = new Availability(from3, to3);
      a3.setId(11);
      alist2.add(a3);

      timePeriodAvailabilities = new ArrayList<>();
      timePeriodAvailabilities.add(a1);
      timePeriodAvailabilities.add(a2);

      unhandledapp = new Application(p, cplist, alist, new Status("UNHANDLED"));
      unhandledapp.setId(1);
      acceptedapp = new Application(p, cplist, alist, new Status("ACCEPTED"));
      rejectedapp = new Application(p, cplist, alist, new Status("REJECTED"));

      CompetenceInPostRequest cipr = new CompetenceInPostRequest("Korvgrillning", 5.0);
      AvailabilityInPostRequest aipr = new AvailabilityInPostRequest(from, to);
      List<CompetenceInPostRequest> lc = new ArrayList<>();
      List<AvailabilityInPostRequest> la = new ArrayList<>();
      lc.add(cipr);
      la.add(aipr);
      apr = new ApplicationPostRequest(lc, la);

      username = "username";
      user = new User(username, "pass", new ArrayList<>());

      applicationDate1 = Date.valueOf("2014-02-07");
      Date applicationDate2 = Date.valueOf("2012-02-07");
      Date applicationDate3 = Date.valueOf("2014-09-22");

      fromDate = Date.valueOf("2012-01-02");
      toDate = Date.valueOf("2015-07-22");

      Application firstApplication = new Application(p, cplist2, alist, new Status("UNHANDLED"), applicationDate1);
      Application secondApplication = new Application(p2, cplist, alist2, new Status("UNHANDLED"), applicationDate2);
      Application thirdApplication = new Application(p, cplist2, alist3, new Status("UNHANDLED"), applicationDate3);
      Application fourthApplication = new Application(p3, cplist3, alist, new Status("UNHANDLED"), applicationDate1);

      allApplications = new ArrayList<>();
      allApplications.add(firstApplication);
      allApplications.add(secondApplication);
      allApplications.add(thirdApplication);
      allApplications.add(fourthApplication);

      /* applications bewteen "2012-01-02" and "2015-07-22" */
      timePeriodApplications = new ArrayList<>();
      timePeriodApplications.add(firstApplication);
      timePeriodApplications.add(secondApplication);
      timePeriodApplications.add(fourthApplication);

      /* applications with date "2014-02-07" */
      applicationDate1Applicaions = new ArrayList<>();
      applicationDate1Applicaions.add(firstApplication);
      applicationDate1Applicaions.add(fourthApplication);

      /* add all applications with "Per" */
      nameApplications = new ArrayList<>();
      nameApplications.add(firstApplication);
      nameApplications.add(secondApplication);

      /* applications with competence "Karuselldrift" */
      competenceApplications = new ArrayList<>();
      competenceApplications.add(firstApplication);
      competenceApplications.add(thirdApplication);
      competenceApplications.add(fourthApplication);

      /* requests */
      emptyRequest = new HashMap<String, Object>() {{
      }};

      nameRequest = new HashMap<>();
      nameRequest.put("name", "Per");

      competenceRequest = new HashMap<>();
      competenceRequest.put("competence", "Korvgrillning");

      applicationDateRequest = new HashMap<>();
      applicationDateRequest.put("application_date", "2014-02-07");

      timePeriodRequest = new HashMap<>();
      timePeriodRequest.put("from_time", "2012-01-02");
      timePeriodRequest.put("to_time", "2015-07-22");

   }

   @Test
   public void unhandle_shouldReturnUnhandledApp() throws Exception {
      Application accepted = acceptedapp;
      Application expected = unhandledapp;
      expected.setId(1);

      Mockito.when(statusRepository.findByName("UNHANDLED")).thenReturn(new Status("UNHANDLED"));
      Mockito.when(applicationRepository.findById((long) 1)).thenReturn(java.util.Optional.ofNullable(accepted));
      Mockito.when(applicationRepository.save(any(Application.class))).thenReturn(unhandledapp);
      Application result = applicationService.unhandle(1);
      Assert.assertEquals(expected, result);

   }
   @Test(expected = EntityDoesNotExistException.class)
   public void unhandle_wrongIdShouldThrowException() throws Exception {
      Mockito.when(statusRepository.findByName("UNHANDLED")).thenReturn(new Status("UNHANDLED"));
      Mockito.when(applicationRepository.findById((long) 10)).thenReturn(Optional.ofNullable(null));

      applicationService.accept(10);
   }
   @Test
   public void accept_shouldReturnAcceptedApp() throws Exception {
      Application unhandled = unhandledapp;
      Application expected = acceptedapp;
      expected.setId(1);

      Mockito.when(statusRepository.findByName("ACCEPTED")).thenReturn(new Status("ACCEPTED"));
      Mockito.when(applicationRepository.findById((long) 1)).thenReturn(java.util.Optional.ofNullable(unhandled));
      Mockito.when(applicationRepository.save(any(Application.class))).thenReturn(acceptedapp);
      Application result = applicationService.accept(1);
      Assert.assertEquals(expected, result);
   }
   @Test(expected = EntityDoesNotExistException.class)
   public void accept_wrongIdShouldThrowException() throws Exception {
      Mockito.when(statusRepository.findByName("ACCEPTED")).thenReturn(new Status("ACCEPTED"));
      Mockito.when(applicationRepository.findById((long) 10)).thenReturn(Optional.ofNullable(null));

      applicationService.accept(10);
   }
   @Test
   public void reject_shouldReturnRejectedApp() throws Exception {
      Application unhandled = unhandledapp;
      Application expected = rejectedapp;
      expected.setId(1);

      Mockito.when(statusRepository.findByName("REJECTED")).thenReturn(new Status("REJECTED"));
      Mockito.when(applicationRepository.findById((long) 1)).thenReturn(java.util.Optional.ofNullable(unhandled));
      Mockito.when(applicationRepository.save(any(Application.class))).thenReturn(rejectedapp);
      Application result = applicationService.accept(1);
      Assert.assertEquals(expected, result);
   }
   @Test(expected = EntityDoesNotExistException.class)
   public void reject_wrongIdShouldThrowException() throws Exception {
      Mockito.when(statusRepository.findByName("REJECTED")).thenReturn(new Status("REJECTED"));
      Mockito.when(applicationRepository.findById((long) 10)).thenReturn(Optional.ofNullable(null));

      applicationService.accept(10);
   }
   @Test
   public void saveapplication_shouldReturnSavedApplication() throws Exception {
      Application expected = unhandledapp;

      Mockito.when(securityService.getUser(username)).thenReturn(user);
      Mockito.when(personRepository.findByUser(user)).thenReturn(p);
      Mockito.when(competenceRepository.findByName("Korvgrillning")).thenReturn(c1);
      Mockito.when(statusRepository.findByName("UNHANDLED")).thenReturn(new Status("UNHANDLED"));
      Mockito.when(applicationRepository.save(any(Application.class))).thenReturn(unhandledapp);

      Application result = applicationService.saveApplication(apr, username);
      Assert.assertEquals(expected, result);
   }
   @Test(expected = InvalidPostRequestException.class)
   public void makePostRequestWithoutAvailable_shouldThrowException() throws Exception {
      ApplicationPostRequest a = apr;
      a.setAvailable(new ArrayList<>());
      applicationService.saveApplication(apr, username);
   }
   @Test(expected = InvalidPostRequestException.class)
   public void makePostRequestWithoutCompetences_shouldThrowException() throws Exception {
      ApplicationPostRequest a = apr;
      a.setCompetences(new ArrayList<>());
      applicationService.saveApplication(apr, username);
   }
   @Test(expected = InvalidPostRequestException.class)
   public void makePostRequestWithoutCompetenceKey_shouldThrowException() throws Exception {
      ApplicationPostRequest a = apr;
      a.getCompetences().get(0).setCompetence(null);
      applicationService.saveApplication(apr, username);
   }
   @Test(expected = InvalidPostRequestException.class)
   public void makePostRequestWithoutYearsOfExperienceKey_shouldThrowException() throws Exception {
      Mockito.when(personRepository.findByUser(any(User.class))).thenReturn(new Person());
      Mockito.when(competenceRepository.findByName(any(String.class))).thenReturn(new Competence());
      ApplicationPostRequest a = apr;
      a.getCompetences().get(0).setYears_of_experience(null);
      applicationService.saveApplication(apr, username);
   }
   @Test(expected = InvalidPostRequestException.class)
   public void makePostRequestWithoutFromKey_shouldThrowException() throws Exception {
      Mockito.when(personRepository.findByUser(any(User.class))).thenReturn(new Person());
      Mockito.when(competenceRepository.findByName(any(String.class))).thenReturn(new Competence());
      ApplicationPostRequest a = apr;
      a.getAvailable().get(0).setFrom(null);
      applicationService.saveApplication(apr, username);
   }
   @Test(expected = InvalidPostRequestException.class)
   public void makePostRequestWithoutToKey_shouldThrowException() throws Exception {
      Mockito.when(personRepository.findByUser(any(User.class))).thenReturn(new Person());
      Mockito.when(competenceRepository.findByName(any(String.class))).thenReturn(new Competence());
      ApplicationPostRequest a = apr;
      a.getAvailable().get(0).setTo(null);
      applicationService.saveApplication(apr, username);
   }
   @Test(expected = EntityDoesNotExistException.class)
   public void makePostRequestWithInvalidCompetence_shouldThrowException() throws Exception {
      ApplicationPostRequest a = apr;
      Mockito.when(competenceRepository.findByName("Korvgrillning")).thenReturn(null);
      applicationService.saveApplication(apr, username);
   }
   @Test(expected = RuntimeException.class)
   public void makePostRequestCannotFindPerson_shouldThrowException() throws Exception {
      ApplicationPostRequest a = apr;
      Mockito.when(securityService.getUser(username)).thenReturn(user);
      Mockito.when(personRepository.findByUser(user)).thenReturn(null);
      applicationService.saveApplication(apr, username);
   }
   @Test
   public void whenSearchingWithEmptyBody_shouldReturnAllApplications() {
      Mockito.when(applicationRepository.findAll()).thenReturn(allApplications);
      List<Application> result = applicationService.getApplications(emptyRequest);
      List<Application> expected = allApplications;
      Assert.assertEquals(expected, result);
   }
   @Test
   public void whenSearchingForName_shouldReturnApplicationsContainingName(){
      Mockito.when(applicationRepository.findAll()).thenReturn(allApplications);
      Mockito.when(personRepository.findByName(p.getName())).thenReturn(p);
      Mockito.when(applicationRepository.findAllByPersonLike(p)).thenReturn(nameApplications);
      List<Application> result = applicationService.getApplications(nameRequest);
      List<Application> expected = nameApplications;
      Assert.assertEquals(expected, result);
   }
   @Test
   public void whenSearchingForCompetence_shouldReturnApplicationsContaingingCompetence(){
      Mockito.when(applicationRepository.findAll()).thenReturn(allApplications);
      Mockito.when(competenceRepository.findByName(c1.getName())).thenReturn(c1);
      Mockito.when(competenceProfileRepository.findByCompetence(c1)).thenReturn(competenceProfiles);
      Mockito.when(applicationRepository.findAllByCompetenceProfilesIn(competenceProfiles)).thenReturn(competenceApplications);
      List<Application> result = applicationService.getApplications(competenceRequest);
      List<Application> expected = competenceApplications;
      Assert.assertEquals(expected, result);
   }
   @Test
   public void whenSearchingForApplicationDate_shouldReturnApplicationsContainingApplicationDate(){
      Mockito.when(applicationRepository.findAll()).thenReturn(allApplications);
      Mockito.when(applicationRepository.findAllByDate(applicationDate1)).thenReturn(applicationDate1Applicaions);
      List<Application> result = applicationService.getApplications(applicationDateRequest);
      List<Application> expected = applicationDate1Applicaions;
      Assert.assertEquals(expected, result);
   }
   @Test
   public void whenSearchingForATimePeriod_shouldReturnApplicationsBetweenFromDateAndToDate(){
      Mockito.when(applicationRepository.findAll()).thenReturn(allApplications);
      Mockito.when(availabilityRepository.findAllByFromDateBetween(fromDate, toDate)).thenReturn(timePeriodAvailabilities);
      Mockito.when(applicationRepository.findAllByAvailabilitiesIn(timePeriodAvailabilities)).thenReturn(timePeriodApplications);
      List<Application> result = applicationService.getApplications(timePeriodRequest);
      List<Application> expected = timePeriodApplications;
      Assert.assertEquals(expected, result);
   }
}