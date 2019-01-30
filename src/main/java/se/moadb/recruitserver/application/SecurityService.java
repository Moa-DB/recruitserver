package se.moadb.recruitserver.application;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import se.moadb.recruitserver.domain.Role;
import se.moadb.recruitserver.domain.User;
import se.moadb.recruitserver.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class SecurityService implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleService roleService;

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public final User loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("user: " + username + " tried to log in");
        final User user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        detailsChecker.check(user);
        return user;
    }

    /**
     * Get a User by username.
     * @param username
     * @return The User entity
     */
    public User getUser(String username) {
        return repository.findByUsername(username);
    }
    public User saveUser(String username, String password, String role) {
            Set<Role> authorities = new HashSet<>(Arrays.asList(
                    roleService.findByName(role)
            ));
            User user = new User(username, passwordEncoder.encode(password), authorities);
            repository.save(user);
            return user;
    }
}
