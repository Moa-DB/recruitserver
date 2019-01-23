package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.moadb.recruitserver.domain.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {
}