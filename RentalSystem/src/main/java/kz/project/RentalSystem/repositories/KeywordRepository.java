package kz.project.RentalSystem.repositories;

import kz.project.RentalSystem.entities.Keywords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface KeywordRepository extends JpaRepository<Keywords,Long> {

}
