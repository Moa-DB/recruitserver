package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.moadb.recruitserver.domain.Competence;

public interface CompetenceRepository extends JpaRepository<Competence, Integer> {

    Competence findByOldId(Long id);

}
