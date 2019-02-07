package se.moadb.recruitserver.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Competence;
import se.moadb.recruitserver.domain.CompetenceProfile;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
@Repository
public interface CompetenceProfileRepository extends JpaRepository<CompetenceProfile, Integer> {

    CompetenceProfile findById(Long id);
    List<CompetenceProfile> findByCompetence(Competence competence);

    @Override
    List<CompetenceProfile> findAll();

    @Override
    <S extends CompetenceProfile> S save(S s);

    @Override
    Optional<CompetenceProfile> findById(Integer integer);
}
