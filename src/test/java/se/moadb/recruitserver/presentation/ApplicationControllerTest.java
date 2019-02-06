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
import java.util.*;


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

   private Person p;
   private Collection<CompetenceProfile> competenceProfiles;
   private Collection<Availability> availabilities;
   private ApplicationPostRequest apr;
   private ApplicationStatusPutRequest applicationStatusPutRequest;
   private String username;
   private Date appDate;


   private Map<String, Object> emptyRequest;
   private Map<String, Object> nameRequest;
   private Map<String, Object> competenceRequest;
   private Map<String, Object> timePeriodRequest;
   private Map<String, Object> applicationDateRequest;
   private List<Application> allApplications;
   private List<Application> nameApplications;
   private List<Application> competenceApplications;
   private List<Application> applicationDateApplicaions;
   private List<Application> timePeriodApplications;

   @Before
   public void setup() {
      username = "username";
      appDate = Date.valueOf("2013-05-05");

      Competence c1 = new Competence("Korvgrillning");
      Competence c2 = new Competence("Karuselldrift");
      CompetenceProfile cp1 = new CompetenceProfile(c1, 3.5);
      cp1.setId(7);
      CompetenceProfile cp2 = new CompetenceProfile(c2, 2);
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

      Date applicationDate1 = Date.valueOf("2011-11-11");
      Date applicationDate2 = Date.valueOf("2012-12-13");


      p = new Person("Per", "Strand", "19671212-1211", "per@strand.kth.se", new User());
      p.setId(4);
      Application app = new Application(p, competenceProfiles, availabilities, new Status("UNHANDLED"), applicationDate1);
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


      Application unhandledapp = new Application(p, cplist, alist, new Status("UNHANDLED"), appDate);

      unhandledapp.setId(1);
      Application acceptedapp = new Application(p, cplist, alist, new Status("ACCEPTED"), appDate);
      Application rejectedapp = new Application(p, cplist, alist, new Status("REJECTED"), appDate);


      Application firstApplication = new Application(p, cplist2, alistX, new Status("UNHANDLED"), applicationDate1);
      firstApplication.setId(9);
      Application secondApplication = new Application(p2, cplist2, alistX, new Status("UNHANDLED"), applicationDate2);
      secondApplication.setId(10);


      allApplications = new ArrayList<>();
      allApplications.add(firstApplication);
      allApplications.add(secondApplication);

      /* applications between "2012-01-02" and "2015-07-22" */
      timePeriodApplications = new ArrayList<>();
      timePeriodApplications.add(firstApplication);
      timePeriodApplications.add(secondApplication);

      /* applications with date "2014-02-07" */
      applicationDateApplicaions = new ArrayList<>();
      applicationDateApplicaions.add(secondApplication);

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
      applicationDateRequest.put("application_date", "2012-12-12");

      timePeriodRequest = new HashMap<>();
      timePeriodRequest.put("from_time", "2012-01-02");
      timePeriodRequest.put("to_time", "2015-07-22");

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
   public void whenGetApplicationsWithoutParameters_shouldReturnAllApplications() throws Exception {
      Mockito.when(applicationService.getApplications(emptyRequest)).thenReturn(allApplications);

      RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applications/filter")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(emptyRequest));
      String result = mvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
      String expected = "[{\"id\":9,\"person\":{\"id\":4,\"name\":\"Per\",\"surname\":\"Strand\",\"ssn\":\"19671212-1211\",\"email\":\"per@strand.kth.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2011-11-10\"},{\"id\":10,\"person\":{\"id\":2,\"name\":\"Greta\",\"surname\":\"Borg\",\"ssn\":\"19820501-3244\",\"email\":\"greta@strand.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2012-12-12\"}]";
      JSONAssert.assertEquals(expected, result,false);
   }
   @Test
   public void whenGetApplicationByApplicationDate_shouldReturnApplicationsCreatedOnDate() throws Exception {
      Mockito.when(applicationService.getApplications(applicationDateRequest)).thenReturn(applicationDateApplicaions);

      RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applications/filter")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(applicationDateRequest));
      String result = mvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
      String expected = "[{\"id\":10,\"person\":{\"id\":2,\"name\":\"Greta\",\"surname\":\"Borg\",\"ssn\":\"19820501-3244\",\"email\":\"greta@strand.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2012-12-12\"}]";
      JSONAssert.assertEquals(expected, result,false);
   }
   @Test
   public void whenGetApplicationByCompetence_shouldReturnApplicationsContainingCompetence() throws Exception {
      Mockito.when(applicationService.getApplications(competenceRequest)).thenReturn(competenceApplications);

      RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applications/filter")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(competenceRequest));
      String result = mvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
      String expected = "[{\"id\":9,\"person\":{\"id\":4,\"name\":\"Per\",\"surname\":\"Strand\",\"ssn\":\"19671212-1211\",\"email\":\"per@strand.kth.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2011-11-10\"}]";
      JSONAssert.assertEquals(expected, result,false);
   }
   @Test
   public void whenGetApplicationBetweenDates_shouldReturnApplicationsBetweenDates() throws Exception {
      Mockito.when(applicationService.getApplications(timePeriodRequest)).thenReturn(timePeriodApplications);

      RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applications/filter")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(timePeriodRequest));
      String result = mvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
      String expected = "[{\"id\":9,\"person\":{\"id\":4,\"name\":\"Per\",\"surname\":\"Strand\",\"ssn\":\"19671212-1211\",\"email\":\"per@strand.kth.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2011-11-10\"},{\"id\":10,\"person\":{\"id\":2,\"name\":\"Greta\",\"surname\":\"Borg\",\"ssn\":\"19820501-3244\",\"email\":\"greta@strand.se\"},\"competenceProfiles\":[{\"id\":7,\"competence\":{\"name\":\"Korvgrillning\"},\"yearsOfExperience\":3.5},{\"id\":8,\"competence\":{\"name\":\"Karuselldrift\"},\"yearsOfExperience\":2.0}],\"availabilities\":[{\"id\":5,\"fromDate\":\"2014-02-22\",\"toDate\":\"2014-05-24\"},{\"id\":6,\"fromDate\":\"2014-07-09\",\"toDate\":\"2014-08-09\"}],\"status\":{\"name\":\"UNHANDLED\"},\"date\":\"2012-12-12\"}]";
      JSONAssert.assertEquals(expected, result,false);
   }

}