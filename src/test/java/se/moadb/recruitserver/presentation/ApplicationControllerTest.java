package se.moadb.recruitserver.presentation;

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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;


@RunWith(SpringRunner.class)
@WebMvcTest(value = ApplicationController.class, secure = false)
public class ApplicationControllerTest {

   @Autowired
   private MockMvc mvc;

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

   @Before
   public void setup() {

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
      Date from = Date.valueOf("2014-02-24");
      Date to = Date.valueOf("2014-05-26");
      Availability a1 = new Availability(from, to);
      a1.setId(5);
      alist.add(a1);
      from = Date.valueOf("2014-07-11");
      to = Date.valueOf("2014-08-11");
      Availability a2 = new Availability(from, to);
      a2.setId(6);
      alist.add(a2);
      availabilities = alist;

      p = new Person("Per", "Strand", "19671212-1211", "per@strand.kth.se", new User());
      p.setId(4);
      app = new Application(p, competenceProfiles, availabilities, new Status("UNHANDLED"));
      app.setId(9);
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

}