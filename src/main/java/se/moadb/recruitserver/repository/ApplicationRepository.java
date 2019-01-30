package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.moadb.recruitserver.domain.Application;
import se.moadb.recruitserver.domain.Availability;
import se.moadb.recruitserver.domain.CompetenceProfile;
import se.moadb.recruitserver.domain.Person;

import java.util.Date;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findAllByPerson(Person person);
    List<Application> findAllByAvailabilitiesIn(List<Availability> availabilities);
}