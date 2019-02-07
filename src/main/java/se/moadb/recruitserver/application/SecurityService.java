package se.moadb.recruitserver.application;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Role;
import se.moadb.recruitserver.domain.User;
import se.moadb.recruitserver.repository.RoleRepository;
import se.moadb.recruitserver.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service that handles logic concerning User and authentication.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@Service
public class SecurityService implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    /**
     * Used by Spring to validate user when logging in.
     * @param username, username of the user that shall be loaded from the database
     * @return User with the given username
     * @throws EntityDoesNotExistException, if the username is not found in the database
     */
    @Override
    public final User loadUserByUsername(String username) throws EntityDoesNotExistException {
        System.out.println("user: " + username + " tried to log in");
        final User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityDoesNotExistException("user", username);
        }
        detailsChecker.check(user);
        return user;
    }

    /**
     * Get a User by username.
     * @param username, username of the the user to be searched for
     * @return The User entity
     */
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Saves a new user to the database.
     * @param username, username of the user that shall be saved
     * @param password, password of the user that shall be saved
     * @param role, string with name of ONE role that the user shall be granted
     * @throws EntityAlreadyExistException, if the username already exists in the database
     * @return the newly created user
     */
    public User saveUser(String username, String password, String role) throws EntityAlreadyExistException, EntityDoesNotExistException {
        if(userRepository.findByUsername(username) != null){
            throw new EntityAlreadyExistException("user", username);
        }
        if(roleRepository.findByName(role) == null){
            throw new EntityDoesNotExistException("role", role);
        }
        Set<Role> authorities = new HashSet<>(Arrays.asList(
                roleRepository.findByName(role)
        ));
        User user = new User(username, passwordEncoder.encode(password), authorities);
        return userRepository.save(user);
    }
}
