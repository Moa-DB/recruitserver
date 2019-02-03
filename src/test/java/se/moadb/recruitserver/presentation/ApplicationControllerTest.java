package se.moadb.recruitserver.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import se.moadb.recruitserver.application.ApplicationService;
import se.moadb.recruitserver.domain.*;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;


@RunWith(SpringRunner.class)
@WebMvcTest(value = ApplicationController.class, secure = false)
public class ApplicationControllerTest {

   @Autowired
   private MockMvc mvc;
   @Autowired
   ObjectMapper objectMapper;

   @MockBean
   private ApplicationService applicationService;

   private Application app;
   private Person p;
   private Competence c1;
   private Competence c2;
   private CompetenceProfile cp1;
   private CompetenceProfile cp2;
   private Collection<CompetenceProfile> competenceProfiles;
   private Collection<Availability> availabilities;
   private Status status;
   private ApplicationPostRequest apr;
   private ApplicationStatusPutRequest applicationStatusPutRequest;
   private String username;
   private Date appDate;

   @Before
   public void setup() {
      username = "username";
      appDate = Date.valueOf("2013-05-05");

      c1 = new Competence("Korvgrillning");
      c2 = new Competence("Karuselldrift");
      cp1 = new CompetenceProfile(c1, 3.5);
      cp1.setId(7);
      cp2 = new CompetenceProfile(c2, 2);
      cp2.setId(8);
      ArrayList<CompetenceProfile> list = new ArrayList<>();
      list.add(cp1);
      list.add(cp2);
      competenceProfiles = list;

      ArrayList<Availability> alist = new ArrayList<>();
      Date from1 = Date.valueOf("2014-02-24");
      Date to1 = Date.valueOf("2014-05-26");
      Availability a1 = new Availability(from1, to1);
      a1.setId(5);
      alist.add(a1);
      Date from2 = Date.valueOf("2014-07-11");
      Date to2 = Date.valueOf("2014-08-11");
      Availability a2 = new Availability(from2, to2);
      a2.setId(6);
      alist.add(a2);
      availabilities = alist;

      p = new Person("Per", "Strand", "19671212-1211", "per@strand.kth.se", new User());
      p.setId(4);
      app = new Application(p, competenceProfiles, availabilities, new Status("UNHANDLED"), appDate);
      app.setId(9);

      CompetenceInPostRequest cipr = new CompetenceInPostRequest("Korvgrillning", 3.5);
      CompetenceInPostRequest cipr2 = new CompetenceInPostRequest("Karuselldrift", 2.0);
      AvailabilityInPostRequest aipr = new AvailabilityInPostRequest(from1, to1);
      AvailabilityInPostRequest aipr2 = new AvailabilityInPostRequest(from2, to2);
      List<CompetenceInPostRequest> lc = new ArrayList<>();
      List<AvailabilityInPostRequest> la = new ArrayList<>();
      lc.add(cipr);
      lc.add(cipr2);
      la.add(aipr);
      la.add(aipr2);
      apr = new ApplicationPostRequest(lc, la);

      applicationStatusPutRequest = new ApplicationStatusPutRequest(Status.ACCEPTED);
   }

   @Test
   public void whenAcceptApplication_shouldReturnApplication() throws Exception {
      long id = 1;
      Application a = new Application(p, competenceProfiles, availabilities, new Status("ACCEPTED"), appDate);
      a.setId(1);
      Mockito.when(applicationService.accept(id, Status.ACCEPTED)).thenReturn(a);

      applicationStatusPutRequest.setStatus(Status.ACCEPTED);
      RequestBuilder rb = MockMvcRequestBuilders.put("/applications/1/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(applicationStatusPutRequest));
      MvcResult res = mvc.perform(rb).andReturn();
      String expected = "{ \"id\": 1, \"person\": { \"id\": 4, \"name\": \"Per\", \"surname\": \"Strand\", \"ssn\": \"19671212-1211\", \"email\": \"per@strand.kth.se\" }, \"competenceProfiles\": [ { \"id\": 7, \"competence\": { \"name\": \"Korvgrillning\" }, \"yearsOfExperience\": 3.5 }, { \"id\": 8, \"competence\": { \"name\": \"Karuselldrift\" }, \"yearsOfExperience\": 2 } ], \"availabilities\": [ { \"id\": 5, \"fromDate\": \"2014-02-23\", \"toDate\": \"2014-05-25\" }, { \"id\": 6, \"fromDate\": \"2014-07-10\", \"toDate\": \"2014-08-10\" } ], \"status\": { \"name\": \"ACCEPTED\" },\"date\":\"2013-05-04\" }";
      JSONAssert.assertEquals(expected, res.getResponse().getContentAsString(), true);
   }
   @Test
   public void whenRejectApplication_shouldReturnApplication() throws Exception {
      long id = 1;
      Application a = new Application(p, competenceProfiles, availabilities, new Status("REJECTED"), appDate);
      a.setId(1);
      Mockito.when(applicationService.reject(id, Status.REJECTED)).thenReturn(a);

      applicationStatusPutRequest.setStatus(Status.REJECTED);
      RequestBuilder rb = MockMvcRequestBuilders.put("/applications/1/reject")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(applicationStatusPutRequest));
      MvcResult res = mvc.perform(rb).andReturn();
      String expected = "{ \"id\": 1, \"person\": { \"id\": 4, \"name\": \"Per\", \"surname\": \"Strand\", \"ssn\": \"19671212-1211\", \"email\": \"per@strand.kth.se\" }, \"competenceProfiles\": [ { \"id\": 7, \"competence\": { \"name\": \"Korvgrillning\" }, \"yearsOfExperience\": 3.5 }, { \"id\": 8, \"competence\": { \"name\": \"Karuselldrift\" }, \"yearsOfExperience\": 2 } ], \"availabilities\": [ { \"id\": 5, \"fromDate\": \"2014-02-23\", \"toDate\": \"2014-05-25\" }, { \"id\": 6, \"fromDate\": \"2014-07-10\", \"toDate\": \"2014-08-10\" } ], \"status\": { \"name\": \"REJECTED\" },\"date\":\"2013-05-04\"} }";
      JSONAssert.assertEquals(expected, res.getResponse().getContentAsString(), true);
      //Assert.assertEquals(expected, res.getResponse().getContentAsString());
   }
   @Test
   public void whenUnhandleApplication_shouldReturnApplication() throws Exception {
      long id = 1;
      Application a = new Application(p, competenceProfiles, availabilities, new Status("UNHANDLED"), appDate);
      a.setId(1);
      Mockito.when(applicationService.unhandle(id, Status.UNHANDLED)).thenReturn(a);

      applicationStatusPutRequest.setStatus(Status.UNHANDLED);
      RequestBuilder rb = MockMvcRequestBuilders.put("/applications/1/unhandle")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(applicationStatusPutRequest));
      MvcResult res = mvc.perform(rb).andReturn();
      String expected = "{ \"id\": 1, \"person\": { \"id\": 4, \"name\": \"Per\", \"surname\": \"Strand\", \"ssn\": \"19671212-1211\", \"email\": \"per@strand.kth.se\" }, \"competenceProfiles\": [ { \"id\": 7, \"competence\": { \"name\": \"Korvgrillning\" }, \"yearsOfExperience\": 3.5 }, { \"id\": 8, \"competence\": { \"name\": \"Karuselldrift\" }, \"yearsOfExperience\": 2 } ], \"availabilities\": [ { \"id\": 5, \"fromDate\": \"2014-02-23\", \"toDate\": \"2014-05-25\" }, { \"id\": 6, \"fromDate\": \"2014-07-10\", \"toDate\": \"2014-08-10\" } ], \"status\": { \"name\": \"UNHANDLED\" },\"date\":\"2013-05-04\"} }";
      JSONAssert.assertEquals(expected, res.getResponse().getContentAsString(), true);
      //Assert.assertEquals(expected, res.getResponse().getContentAsString());
   }
   @Test
   public void whenMakeApplication_shouldReturnApplication() throws Exception {
      long id = 1;
      Application a = new Application(p, competenceProfiles, availabilities, new Status("UNHANDLED"), appDate);
      a.setId(1);

      Principal mockPrincipal = Mockito.mock(Principal.class);
      Mockito.when(mockPrincipal.getName()).thenReturn(username);
      Mockito.when(applicationService.saveApplication(any(ApplicationPostRequest.class), any(String.class))).thenReturn(a);

      RequestBuilder rb = MockMvcRequestBuilders.post("/applications")
            .principal(mockPrincipal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(apr));
      MvcResult res = mvc.perform(rb).andReturn();
      String expected = "{\"id\":1,\"person\":{\"id\":4,\"name\":\"Per\",\"surname\":\"Strand\",\"ssn\":\"19671212-1211\",\"email\":\"per@strand.kth.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-23\",\"toDate\":\"2014-05-25\"},{\"id\":6,\"fromDate\":\"2014-07-10\",\"toDate\":\"2014-08-10\"}],\"status\":{\"name\":\"UNHANDLED\"}, \"date\":\"2013-05-04\"}";
      String result = res.getResponse().getContentAsString();
      JSONAssert.assertEquals(expected, result, true);
   }
}