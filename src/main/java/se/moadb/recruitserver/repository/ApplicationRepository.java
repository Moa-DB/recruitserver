package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.moadb.recruitserver.domain.*;

import java.sql.Date;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findAllByPersonLike(Person person);
    List<Application> findAllByAvailabilitiesIn(List<Availability> availabilities);
    List<Application> findAllByCompetenceProfilesIn(List<CompetenceProfile> competenceProfiles);
    List<Application> findAllByDate(Date date);
}