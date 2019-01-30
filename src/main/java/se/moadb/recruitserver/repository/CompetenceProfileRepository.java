package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.moadb.recruitserver.domain.Competence;
import se.moadb.recruitserver.domain.CompetenceProfile;

import java.util.List;

public interface CompetenceProfileRepository extends JpaRepository<CompetenceProfile, Integer> {

    CompetenceProfile findById(Long id);
    List<CompetenceProfile> findByCompetence(Competence competence);
}
