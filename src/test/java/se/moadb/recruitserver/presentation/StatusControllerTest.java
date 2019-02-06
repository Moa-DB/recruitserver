package se.moadb.recruitserver.presentation;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import se.moadb.recruitserver.application.StatusService;
import se.moadb.recruitserver.domain.Status;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(value = StatusController.class, secure = false)
public class StatusControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private StatusService statusService;

    @Test
    public void whenGetStatuses_shouldReturnStatuses() throws Exception {
        List<Status> statuses = new ArrayList<>();
        statuses.add(new Status("ACCEPTED"));
        statuses.add(new Status("REJECTED"));
        statuses.add(new Status("UNHANDLED"));

        Mockito.when(statusService.getAllStatuses()).thenReturn(statuses);

        RequestBuilder rb = get("/statuses").accept(MediaType.APPLICATION_JSON);
        MvcResult res = mvc.perform(rb).andReturn();
        String expected = "[{\"name\":\"ACCEPTED\"},{\"name\":\"REJECTED\"},{\"name\":\"UNHANDLED\"}]";
        JSONAssert.assertEquals(expected, res.getResponse().getContentAsString(), false);
    }
}
