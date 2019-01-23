package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.moadb.recruitserver.domain.Competence;

import java.util.List;

public interface CompetenceRepository extends JpaRepository<Competence, Integer> {

    @Override
    List<Competence> findAll();

    Competence findByName(String name);

}
