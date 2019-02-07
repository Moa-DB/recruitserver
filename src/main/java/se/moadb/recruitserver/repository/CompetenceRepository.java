package se.moadb.recruitserver.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Competence;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Integer> {

    @Override
    List<Competence> findAll();

    Competence findByName(String name);

    @Override
    <S extends Competence> S save(S s);

    @Override
    Optional<Competence> findById(Integer integer);

}
