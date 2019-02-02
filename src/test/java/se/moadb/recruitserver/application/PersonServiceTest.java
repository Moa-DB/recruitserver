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
import se.moadb.recruitserver.domain.Person;
import se.moadb.recruitserver.domain.Role;
import se.moadb.recruitserver.domain.User;
import se.moadb.recruitserver.presentation.RegistrationPostRequest;
import se.moadb.recruitserver.repository.PersonRepository;
import se.moadb.recruitserver.repository.RoleRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceTest {

    @Autowired
    PersonService personService;

    @MockBean
    PersonRepository personRepository;

    @MockBean
    SecurityService securityService;

    @MockBean
    RoleRepository roleRepository;

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
    public void whenSavePersonAndUser_shouldReturnPerson(){
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(role);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

    @Test(expected = EntityDoesNotExistException.class)
    public void whenSavePersonAndUserWithUnknownRole_shouldThrowException(){
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(null);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

    @Test(expected = InvalidPostRequestException.class)
    public void whenSavePersonAndUserWithoutDateOfBirth_shouldThrowException(){
        registrationPostRequest.setDateOfBirth("");
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(role);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

    @Test(expected = InvalidPostRequestException.class)
    public void whenSavePersonAndUserWithoutEmail_shouldThrowException(){
        registrationPostRequest.setEmail("");
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(role);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

    @Test(expected = InvalidPostRequestException.class)
    public void whenSavePersonAndUserWithoutFirstName_shouldThrowException(){
        registrationPostRequest.setFirstName("");
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(role);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

    @Test(expected = InvalidPostRequestException.class)
    public void whenSavePersonAndUserWithoutLastName_shouldThrowException(){
        registrationPostRequest.setLastName("");
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(role);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

    @Test(expected = InvalidPostRequestException.class)
    public void whenSavePersonAndUserWithoutPassword_shouldThrowException(){
        registrationPostRequest.setPassword("");
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(role);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

    @Test(expected = InvalidPostRequestException.class)
    public void whenSavePersonAndUserWithoutRole_shouldThrowException(){
        registrationPostRequest.setRole("");
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(role);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

    @Test(expected = InvalidPostRequestException.class)
    public void whenSavePersonAndUserWithoutUsername_shouldThrowException(){
        registrationPostRequest.setUsername("");
        Mockito.when(securityService.saveUser(user.getUsername(), user.getPassword(), role.getName())).thenReturn(user);
        Mockito.when(personRepository.save(any(Person.class))).thenReturn(person);
        Mockito.when(roleRepository.findByName(any(String.class))).thenReturn(role);

        Assert.assertEquals(person, personService.savePersonAndUser(registrationPostRequest));
    }

}
