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
import se.moadb.recruitserver.application.PersonService;
import se.moadb.recruitserver.application.SecurityService;
import se.moadb.recruitserver.domain.Person;
import se.moadb.recruitserver.domain.Role;
import se.moadb.recruitserver.domain.User;
import se.moadb.recruitserver.repository.UserRepository;

import java.security.Principal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AuthenticationController.class, secure = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private PersonService personService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private Set<Role> authorities;
    private Role role;
    private Person person;
    private RegistrationPostRequest registrationPostRequest;

    @Before
    public void setUp() {
        role = new Role("testRole");
        authorities = new HashSet<>(Arrays.asList(
                role
        ));
        user = new User("test", "secret", authorities);
        person = new Person("Per", "Strand", "19671212-1211", "per@strand.kth.se", user);
        person.setId(0);
        registrationPostRequest = new RegistrationPostRequest(user.getUsername(), user.getPassword(), person.getName(),
                person.getSurname(), person.getEmail(), role.getName(), person.getSsn());
    }

    @Test
    public void whenRegisterUserAndPerson_shouldReturnPerson() throws Exception {
        Mockito.when(personService.savePersonAndUser(any(RegistrationPostRequest.class))).thenReturn(person);

        RequestBuilder rb = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationPostRequest));
        MvcResult res = mvc.perform(rb).andReturn();
        String expected = "{\"id\":0,\"name\":\"Per\",\"surname\":\"Strand\",\"ssn\":\"19671212-1211\",\"email\":\"per@strand.kth.se\"}";
        String result = res.getResponse().getContentAsString();

        JSONAssert.assertEquals(expected, result, false);
    }

    @Test
    public void whenSuccessLogin_shouldReturnUser() throws Exception {

        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(user.getUsername());
        Mockito.when(securityService.getUser(user.getUsername())).thenReturn(user);

        RequestBuilder rb = MockMvcRequestBuilders.get("/login/success")
                .principal(mockPrincipal);
        MvcResult res = mvc.perform(rb).andReturn();
        String expected = "{\"username\":\"test\",\"roles\":[{\"name\":\"testRole\",\"authority\":\"testRole\"}],\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"authorities\":[{\"name\":\"testRole\",\"authority\":\"testRole\"}],\"enabled\":true}";
        String result = res.getResponse().getContentAsString();

        JSONAssert.assertEquals(expected, result, false);
    }
}
