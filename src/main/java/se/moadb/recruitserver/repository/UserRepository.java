package se.moadb.recruitserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.moadb.recruitserver.domain.User;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String userName);

    @Override
    List<User> findAll();

    @Override
    <S extends User> S save(S s);

    @Override
    Optional<User> findById(Integer integer);
}
