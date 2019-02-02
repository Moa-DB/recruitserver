package se.moadb.recruitserver.presentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import se.moadb.recruitserver.application.PersonService;
import se.moadb.recruitserver.application.SecurityService;
import se.moadb.recruitserver.domain.Person;
import se.moadb.recruitserver.domain.User;

import java.security.Principal;

/**
 * Entry point for REST requests concerning authentication such as registration.
 * Implemented methods:
 * - POST /registration, create a new user and person.
 */
@RestController
public class AuthenticationController {

    @Autowired
    SecurityService securityService;

    @Autowired
    PersonService personService;

    /**
     * Register a new user and person.
     * JSON data should be structured as follows:
     * {
     *    dateOfBirth : String,
     *    email : String,
     *    firstName : String,
     *    lastName : String,
     *    password : String,
     *    role : String,
     *    username : String,
     * }
     */
    @PostMapping("/registration")
    public Person registerUserAndPerson(@RequestBody RegistrationPostRequest registrationPostRequest) {
        Person person = personService.savePersonAndUser(registrationPostRequest);
        return person;
    }

    /**
     * Redirected to on successful login
     * @param model
     * @param principal, of the logged in user
     * @return user details of the logged in user.
     */
    @GetMapping("/login/success")
    public User successLogin(ModelMap model, Principal principal){
        return securityService.getUser(principal.getName());
    }
}
