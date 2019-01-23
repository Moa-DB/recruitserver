package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.moadb.recruitserver.domain.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, String> {
    Status findByName(String name);
}
