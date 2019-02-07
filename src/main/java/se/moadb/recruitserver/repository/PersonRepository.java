package se.moadb.recruitserver.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Person;
import se.moadb.recruitserver.domain.User;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    /**
     * Find a person by his/her id
     * @param id
     * @return A Person entity
     */
    Person findById(Long id);

    /**
     * Find a person by his/her name
     * @param name
     * @return A Person entity
     */
    Person findByName(String name);

    /**
     * Find a person by his/her User entity
     * @param user
     * @return A Person entity
     */
    Person findByUser(User user);

    @Override
    <S extends Person> S save(S s);

    @Override
    List<Person> findAll();
}
