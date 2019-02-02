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
import se.moadb.recruitserver.domain.Role;
import se.moadb.recruitserver.domain.User;
import se.moadb.recruitserver.repository.RoleRepository;
import se.moadb.recruitserver.repository.UserRepository;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityServiceTest {

    @Autowired
    SecurityService securityService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    RoleRepository roleRepository;

    private User user;
    private Set<Role> authorities;
    private Role role;

    @Before
    public void setUp() {
        role = new Role("testRole");
        authorities = new HashSet<>(Arrays.asList(
                role
        ));
        user = new User("test", "secret", authorities);
    }

    @Test
    public void whenGetUser_shouldReturnUser(){
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Assert.assertEquals(user, securityService.getUser(user.getUsername()));
    }

    @Test
    public void whenGetUserWithUnknownUsername_shouldReturnNull(){
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        Assert.assertNull(securityService.getUser(user.getUsername()));
    }

    @Test
    public void whenLoadUserByUsername_shouldReturnUser(){
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Assert.assertEquals(user, securityService.getUser(user.getUsername()));
    }

    @Test(expected = EntityDoesNotExistException.class)
    public void LoadUserByUnknownUsername_shouldThrowException() throws Exception {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        securityService.loadUserByUsername(user.getUsername());
    }

    @Test
    public void whenSaveUser_shouldReturnUser(){
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        Mockito.when(roleRepository.findByName(role.getName())).thenReturn(role);

        User result = securityService.saveUser(user.getUsername(), user.getPassword(), role.getName());
        Assert.assertEquals(user, result);
    }

    @Test(expected = EntityAlreadyExistException.class)
    public void whenSaveExistingUser_shouldThrowException(){
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        securityService.saveUser(user.getUsername(), user.getPassword(), role.getName());
    }

    @Test(expected = EntityDoesNotExistException.class)
    public void whenSaveUserWithUnknownRole_shouldThrowException(){
        Mockito.when(roleRepository.findByName(role.getName())).thenReturn(null);
        securityService.saveUser(user.getUsername(), user.getPassword(), role.getName());
    }



}
