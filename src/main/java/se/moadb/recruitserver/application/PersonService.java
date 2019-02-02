package se.moadb.recruitserver.application;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import se.moadb.recruitserver.domain.Person;
import se.moadb.recruitserver.domain.Role;
import se.moadb.recruitserver.domain.User;
import se.moadb.recruitserver.presentation.RegistrationPostRequest;
import se.moadb.recruitserver.repository.PersonRepository;
import se.moadb.recruitserver.repository.RoleRepository;

import java.util.Optional;

/**
 * Service that handles logic concerning persons.
 */
@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    SecurityService userDetailsService;

    @Autowired
    RoleRepository roleRepository;

    /**
     * Creates a new user and a new person.
     *
     * @param registrationPostRequest, holds all data needed to create a new user and person
     * @return the newly created user.
     */
    public Person savePersonAndUser(RegistrationPostRequest registrationPostRequest){
        validatePostRequest(registrationPostRequest);
        User user = userDetailsService.saveUser(registrationPostRequest.getUsername(), registrationPostRequest.getPassword(),
                registrationPostRequest.getRole());
        Person person = new Person(registrationPostRequest.getFirstName(), registrationPostRequest.getLastName(),
                registrationPostRequest.getDateOfBirth(), registrationPostRequest.getEmail(), user);
        return personRepository.save(person);
    }

    private void validatePostRequest(RegistrationPostRequest registrationPostRequest) throws InvalidPostRequestException, EntityDoesNotExistException {
        //check that no mandatory key or list is not present
        if (registrationPostRequest.getDateOfBirth().equals("") ) {
            throw new InvalidPostRequestException("date of birth");
        }
        if (registrationPostRequest.getEmail().equals("")) {
            throw new InvalidPostRequestException("email");
        }
        if (registrationPostRequest.getFirstName().equals("")) {
            throw new InvalidPostRequestException("first name");
        }
        if (registrationPostRequest.getLastName().equals("")) {
            throw new InvalidPostRequestException("last name");
        }
        if (registrationPostRequest.getPassword().equals("")) {
            throw new InvalidPostRequestException("password");
        }
        if (registrationPostRequest.getRole().equals("")) {
            throw new InvalidPostRequestException("role");
        }
        if (registrationPostRequest.getUsername().equals("")) {
            throw new InvalidPostRequestException("username");
        }

        Optional<Role> role = Optional.ofNullable(roleRepository.findByName(registrationPostRequest.getRole()));
        if (!role.isPresent()) {
            throw new EntityDoesNotExistException("role", registrationPostRequest.getRole());
        }
    }
}
