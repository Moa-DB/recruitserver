package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.moadb.recruitserver.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByName(String role);
}
