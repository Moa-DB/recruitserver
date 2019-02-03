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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
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
   private Competence c1;
   @Before
   public void setUp() {
      p = new Person("Per", "Strand", "19671212-1211", "per@strand.kth.se", new User());
      c1 = new Competence("Korvgrillning");
      CompetenceProfile cp1 = new CompetenceProfile(c1, 3.5);
      cp1.setId(7);
      ArrayList<CompetenceProfile> cplist = new ArrayList<>();
      cplist.add(cp1);
      ArrayList<Availability> alist = new ArrayList<>();
      Date from = Date.valueOf("2014-02-24");
      Date to = Date.valueOf("2014-05-26");
      Availability a1 = new Availability(from, to);
      a1.setId(5);
      alist.add(a1);

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
}