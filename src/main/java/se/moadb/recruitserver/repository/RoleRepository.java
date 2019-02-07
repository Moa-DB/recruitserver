package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.Role;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByName(String role);

    @Override
    List<Role> findAll();

    @Override
    <S extends Role> S save(S s);

    @Override
    Optional<Role> findById(String s);
}
