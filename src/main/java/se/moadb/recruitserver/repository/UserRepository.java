package se.moadb.recruitserver.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.moadb.recruitserver.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {


    User findByUsername(String userName);
}
