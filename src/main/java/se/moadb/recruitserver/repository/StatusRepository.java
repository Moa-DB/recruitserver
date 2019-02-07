package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Status;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
@Repository
public interface StatusRepository extends JpaRepository<Status, String> {
    Status findByName(String name);

    @Override
    List<Status> findAll();

    @Override
    <S extends Status> S save(S s);

    @Override
    Optional<Status> findById(String s);
}
