package se.moadb.recruitserver.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.moadb.recruitserver.domain.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {

    Role findByName(String role);
}
