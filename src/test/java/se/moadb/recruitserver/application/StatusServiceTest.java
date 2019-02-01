package se.moadb.recruitserver.application;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import se.moadb.recruitserver.domain.Status;
import se.moadb.recruitserver.repository.StatusRepository;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatusServiceTest {

    @Autowired
    StatusService statusService;

    @MockBean
    private StatusRepository statusRepository;

    @Test
    public void whenGetStatuses_shouldReturnStatuses(){
        List<Status> statuses = new ArrayList<>();
        statuses.add(new Status("ACCEPTED"));
        statuses.add(new Status("REJECTED"));
        statuses.add(new Status("UNHANDLED"));

        Mockito.when(statusRepository.findAll()).thenReturn(statuses);
        Assert.assertEquals(statuses, statusService.getAllStatuses());
    }
}
