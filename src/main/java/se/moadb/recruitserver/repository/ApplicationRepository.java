package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findById(Long aLong);
    Application save(Application application);
    List<Application> findAll();
    List<Application> findAllByPersonLike(Person person);
    List<Application> findAllByAvailabilitiesIn(List<Availability> availabilities);
    List<Application> findAllByCompetenceProfilesIn(List<CompetenceProfile> competenceProfiles);
    List<Application> findAllByDate(Date date);
}