package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.moadb.recruitserver.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    Person findByOldId(Long id);

}
