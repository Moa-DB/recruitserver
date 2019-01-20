package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.moadb.recruitserver.domain.Availability;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {
}
