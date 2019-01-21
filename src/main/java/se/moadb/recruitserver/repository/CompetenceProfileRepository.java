package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.moadb.recruitserver.domain.CompetenceProfile;

public interface CompetenceProfileRepository extends JpaRepository<CompetenceProfile, Integer> {
}
