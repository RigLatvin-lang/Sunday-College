package aur.diploma.backend.parent.repository;

import aur.diploma.backend.parent.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    @Query("SELECT p FROM Parent p JOIN FETCH p.user JOIN FETCH p.student WHERE p.user.id = :userId")
    Optional<Parent> findByUserId(@Param("userId") Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT p FROM Parent p JOIN FETCH p.user JOIN FETCH p.student")
    List<Parent> findAllWithDetails();
}
