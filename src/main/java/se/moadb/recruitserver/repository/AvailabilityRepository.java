package se.moadb.recruitserver.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Availability;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    @Override
    List<Availability> findAll();

    @Override
    List<Availability> findAllById(Iterable<Integer> iterable);

    @Override
    <S extends Availability> List<S> saveAll(Iterable<S> iterable);

    @Override
    <S extends Availability> S save(S s);

    @Override
    Optional<Availability> findById(Integer integer);

    Availability findById(Long id);
    List<Availability> findAllByFromDateBetween(Date fromDate, Date toDate);
}
