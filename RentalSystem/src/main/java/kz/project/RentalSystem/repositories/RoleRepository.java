package kz.project.RentalSystem.repositories;

import kz.project.RentalSystem.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Roles,Long> {

    Roles findByRole(String role);
}

