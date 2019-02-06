package se.moadb.recruitserver.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.*;
import org.json.*;


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
   private String username;

   private Application secondApplication;
   private Application thirdApplication;
   private Application fourthApplication;
   private Map<String, Object> emptyRequest;
   private Map<String, Object> nameRequest;
   private Map<String, Object> competenceRequest;
   private Map<String, Object> timePeriodRequest;
   private Map<String, Object> applicationDateRequest;
   private Date applicationDate1;
   private Date applicationDate2;
   private Date applicationDate3;
   private Date fromDate;
   private Date toDate;
   private List<Application> allApplications;
   private List<Application> nameApplications;
   private List<Application> competenceApplications;
   private List<Application> applicationDate1Applicaions;
   private List<Application> timePeriodApplications;
   private List<Availability> timePeriodAvailabilities;

   @Before
   public void setup() {
      username = "username";

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
      app = new Application(p, competenceProfiles, availabilities, new Status("UNHANDLED"));
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




      ArrayList<CompetenceProfile> cplist = new ArrayList<>();
      cplist.add(cp1);
      ArrayList<CompetenceProfile> cplist2 = new ArrayList<>();
      cplist2.add(cp1);
      cplist2.add(cp2);
      ArrayList<CompetenceProfile> cplist3 = new ArrayList<>();
      cplist3.add(cp2);

      Person p2 = new Person("Greta", "Borg", "19820501-3244", "greta@strand.se", new User());
      p2.setId(2);

      ArrayList<Availability> alistX = new ArrayList<>();
      Date fromX = Date.valueOf("2014-02-23");
      Date toX = Date.valueOf("2014-05-25");
      Availability aX = new Availability(fromX, toX);
      aX.setId(5);
      alistX.add(aX);
      Date fromX2 = Date.valueOf("2014-07-10");
      Date toX2 = Date.valueOf("2014-08-10");
      Availability aX2 = new Availability(fromX2, toX2);
      aX2.setId(6);
      alistX.add(aX2);

      applicationDate1 = Date.valueOf("2011-11-11");

      fromDate = Date.valueOf("2012-01-02");
      toDate = Date.valueOf("2015-07-22");

      Application firstApplication = new Application(p, cplist2, alistX, new Status("UNHANDLED"), applicationDate1);
      firstApplication.setId(9);
      Application secondApplication = new Application(p2, cplist2, alistX, new Status("UNHANDLED"), applicationDate1);
      secondApplication.setId(10);


      allApplications = new ArrayList<>();
      allApplications.add(firstApplication);
      allApplications.add(secondApplication);

      /* applications between "2012-01-02" and "2015-07-22" */
      timePeriodApplications = new ArrayList<>();
      timePeriodApplications.add(firstApplication);

      /* applications with date "2014-02-07" */
      applicationDate1Applicaions = new ArrayList<>();
      applicationDate1Applicaions.add(firstApplication);

      /* add all applications with "Per" */
      nameApplications = new ArrayList<>();
      nameApplications.add(firstApplication);

      /* applications with competence "Karuselldrift" */
      competenceApplications = new ArrayList<>();
      competenceApplications.add(firstApplication);


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
   public void whenAcceptApplication_shouldReturnApplication() throws Exception {
      long id = 1;
      Application a = new Application(p, competenceProfiles, availabilities, new Status("ACCEPTED"));
      a.setId(1);
      Mockito.when(applicationService.accept(id)).thenReturn(a);

      RequestBuilder rb = MockMvcRequestBuilders.put("/applications/1/accept").accept(MediaType.APPLICATION_JSON);
      MvcResult res = mvc.perform(rb).andReturn();
      String expected = "{ \"id\": 1, \"person\": { \"id\": 4, \"name\": \"Per\", \"surname\": \"Strand\", \"ssn\": \"19671212-1211\", \"email\": \"per@strand.kth.se\" }, \"competenceProfiles\": [ { \"id\": 7, \"competence\": { \"name\": \"Korvgrillning\" }, \"yearsOfExperience\": 3.5 }, { \"id\": 8, \"competence\": { \"name\": \"Karuselldrift\" }, \"yearsOfExperience\": 2 } ], \"availabilities\": [ { \"id\": 5, \"fromDate\": \"2014-02-23\", \"toDate\": \"2014-05-25\" }, { \"id\": 6, \"fromDate\": \"2014-07-10\", \"toDate\": \"2014-08-10\" } ], \"status\": { \"name\": \"ACCEPTED\" } }";
      JSONAssert.assertEquals(expected, res.getResponse().getContentAsString(), false);
   }
   @Test
   public void whenRejectApplication_shouldReturnApplication() throws Exception {
      long id = 1;
      Application a = new Application(p, competenceProfiles, availabilities, new Status("REJECTED"));
      a.setId(1);
      Mockito.when(applicationService.reject(id)).thenReturn(a);

      RequestBuilder rb = MockMvcRequestBuilders.put("/applications/1/reject").accept(MediaType.APPLICATION_JSON);
      MvcResult res = mvc.perform(rb).andReturn();
      String expected = "{ \"id\": 1, \"person\": { \"id\": 4, \"name\": \"Per\", \"surname\": \"Strand\", \"ssn\": \"19671212-1211\", \"email\": \"per@strand.kth.se\" }, \"competenceProfiles\": [ { \"id\": 7, \"competence\": { \"name\": \"Korvgrillning\" }, \"yearsOfExperience\": 3.5 }, { \"id\": 8, \"competence\": { \"name\": \"Karuselldrift\" }, \"yearsOfExperience\": 2 } ], \"availabilities\": [ { \"id\": 5, \"fromDate\": \"2014-02-23\", \"toDate\": \"2014-05-25\" }, { \"id\": 6, \"fromDate\": \"2014-07-10\", \"toDate\": \"2014-08-10\" } ], \"status\": { \"name\": \"REJECTED\" } }";
      JSONAssert.assertEquals(expected, res.getResponse().getContentAsString(), false);
      //Assert.assertEquals(expected, res.getResponse().getContentAsString());
   }
   @Test
   public void whenUnhandleApplication_shouldReturnApplication() throws Exception {
      long id = 1;
      Application a = new Application(p, competenceProfiles, availabilities, new Status("UNHANDLED"));
      a.setId(1);
      Mockito.when(applicationService.unhandle(id)).thenReturn(a);

      RequestBuilder rb = MockMvcRequestBuilders.put("/applications/1/unhandle").accept(MediaType.APPLICATION_JSON);
      MvcResult res = mvc.perform(rb).andReturn();
      String expected = "{ \"id\": 1, \"person\": { \"id\": 4, \"name\": \"Per\", \"surname\": \"Strand\", \"ssn\": \"19671212-1211\", \"email\": \"per@strand.kth.se\" }, \"competenceProfiles\": [ { \"id\": 7, \"competence\": { \"name\": \"Korvgrillning\" }, \"yearsOfExperience\": 3.5 }, { \"id\": 8, \"competence\": { \"name\": \"Karuselldrift\" }, \"yearsOfExperience\": 2 } ], \"availabilities\": [ { \"id\": 5, \"fromDate\": \"2014-02-23\", \"toDate\": \"2014-05-25\" }, { \"id\": 6, \"fromDate\": \"2014-07-10\", \"toDate\": \"2014-08-10\" } ], \"status\": { \"name\": \"UNHANDLED\" } }";
      JSONAssert.assertEquals(expected, res.getResponse().getContentAsString(), false);
      //Assert.assertEquals(expected, res.getResponse().getContentAsString());
   }
   @Test
   public void whenMakeApplication_shouldReturnApplication() throws Exception {
      long id = 1;
      Application a = new Application(p, competenceProfiles, availabilities, new Status("UNHANDLED"));
      a.setId(1);

      Principal mockPrincipal = Mockito.mock(Principal.class);
      Mockito.when(mockPrincipal.getName()).thenReturn(username);
      Mockito.when(applicationService.saveApplication(any(ApplicationPostRequest.class), any(String.class))).thenReturn(a);

      RequestBuilder rb = MockMvcRequestBuilders.post("/applications")
            .principal(mockPrincipal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(apr));
      MvcResult res = mvc.perform(rb).andReturn();
      String expected = "{\"id\":1,\"person\":{\"id\":4,\"name\":\"Per\",\"surname\":\"Strand\",\"ssn\":\"19671212-1211\",\"email\":\"per@strand.kth.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-23\",\"toDate\":\"2014-05-25\"},{\"id\":6,\"fromDate\":\"2014-07-10\",\"toDate\":\"2014-08-10\"}],\"status\":{\"name\":\"UNHANDLED\"}}";
      String result = res.getResponse().getContentAsString();
      JSONAssert.assertEquals(expected, result, false);
   }
   @Test
   public void whenGetApplicationsByName_shouldReturnApplicationsContainingName() throws Exception {
      Mockito.when(applicationService.getApplications(nameRequest)).thenReturn(nameApplications);

      RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applications/filter")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(nameRequest));

      String result = mvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
      String expected = "[{\"id\":9,\"person\":{\"id\":4,\"name\":\"Per\",\"surname\":\"Strand\",\"ssn\":\"19671212-1211\",\"email\":\"per@strand.kth.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2011-11-10\"}]";
      JSONAssert.assertEquals(expected, result,false);
   }
   @Test
   public void whenGetApplicaionsWithoutParameters_shouldReturnAllApplications() throws Exception {
      Mockito.when(applicationService.getApplications(emptyRequest)).thenReturn(allApplications);

      RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applications/filter")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(emptyRequest));
      String result = mvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
      String expected = "[{\"id\":9,\"person\":{\"id\":4,\"name\":\"Per\",\"surname\":\"Strand\",\"ssn\":\"19671212-1211\",\"email\":\"per@strand.kth.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2011-11-10\"},{\"id\":10,\"person\":{\"id\":2,\"name\":\"Greta\",\"surname\":\"Borg\",\"ssn\":\"19820501-3244\",\"email\":\"greta@strand.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2011-11-10\"}]";
      JSONAssert.assertEquals(expected, result,false);
   }
}