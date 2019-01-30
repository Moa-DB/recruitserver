package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.moadb.recruitserver.domain.Availability;

import java.util.Date;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    Availability findById(Long id);
    List<Availability> findAllByFromDateBetween(Date fromDate, Date toDate);
}
