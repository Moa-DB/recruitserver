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
import se.moadb.recruitserver.repository.ApplicationRepository;
import se.moadb.recruitserver.repository.CompetenceRepository;
import se.moadb.recruitserver.repository.PersonRepository;
import se.moadb.recruitserver.repository.StatusRepository;

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

   private Application unhandledapp;
   private Application acceptedapp;
   private Application rejectedapp;
   private ApplicationPostRequest apr;
   private User user;
   private String username;
   private Person p;
   private Person p2;
   private Competence c1;
   private Competence c2;
   private Application firstApplication;
   private Application secondApplication;
   private Application thirdApplication;
   private Map<String, Object> emptyRequest;
   private Map<String, Object> nameRequest;


   private List<Application> allApplications;
   private List<Application> nameApplications;

   @Before
   public void setUp() {
      p = new Person("Per", "Strand", "19671212-1211", "per@strand.kth.se", new User());
      p2 = new Person("Greta", "Borg", "19820501-3244", "greta@strand.se", new User());
      c1 = new Competence("Korvgrillning");
      c2 = new Competence("Karuselldrift");
      CompetenceProfile cp1 = new CompetenceProfile(c1, 3.5);
      CompetenceProfile cp2 = new CompetenceProfile(c2,5);
      cp1.setId(7);
      cp2.setId(8);
      ArrayList<CompetenceProfile> cplist = new ArrayList<>();
      cplist.add(cp1);
      ArrayList<CompetenceProfile> cplist2 = new ArrayList<>();
      cplist2.add(cp1);
      cplist2.add(cp2);

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

      firstApplication = new Application(p, cplist2, alist, new Status("UNHANDLED"));
      secondApplication = new Application(p2, cplist, alist2, new Status("UNHANDLED"));
      thirdApplication = new Application(p, cplist2, alist3, new Status("UNHANDLED"));

      allApplications = new ArrayList<>();
      allApplications.add(firstApplication);
      allApplications.add(secondApplication);
      allApplications.add(thirdApplication);

      /* add all applications with "Per" */
      nameApplications = new ArrayList<>();
      nameApplications.add(firstApplication);
      nameApplications.add(secondApplication);

      emptyRequest = new HashMap<String, Object>() {{
      }};
//      nameRequest = new HashMap<String, Object>() {{
//          put("name", "Per");
//      }};
      nameRequest = new HashMap<>();
      nameRequest.put("name", "Per");


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
   public void whenSearchingWithNoAttachedJSONData_shouldReturnAllApplications() {
      Mockito.when(applicationRepository.findAll()).thenReturn(allApplications);
      List<Application> result = applicationService.getApplications(emptyRequest);
      System.out.println("result " + result);
      List<Application> expected = allApplications;
      System.out.println("expected " + expected);
      Assert.assertEquals(expected, result);
   }
   @Test
   public void whenSearchingWithName_shouldReturnApplicationsWithName(){
      Mockito.when(applicationRepository.findAllByPerson(any(Person.class))).thenReturn(nameApplications);
      List<Application> result = applicationService.getApplications(nameRequest);
      List<Application> expected = nameApplications;
      Assert.assertEquals(expected, result);
   }
}