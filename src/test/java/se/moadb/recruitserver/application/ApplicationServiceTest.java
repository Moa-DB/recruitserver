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
import se.moadb.recruitserver.repository.ApplicationRepository;
import se.moadb.recruitserver.repository.StatusRepository;

import java.sql.Date;
import java.util.ArrayList;
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

   private Application unhandledapp;
   private Application acceptedapp;
   private Application rejectedapp;
   @Before
   public void setUp() {
      Person p = new Person("Per", "Strand", "19671212-1211", "per@strand.kth.se", new User());
      Competence c1 = new Competence("Korvgrillning");
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
}