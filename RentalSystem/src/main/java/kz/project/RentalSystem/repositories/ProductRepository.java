package kz.project.RentalSystem.repositories;

import kz.project.RentalSystem.entities.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Products,Long> {
        List<Products> findAllByPriceIsGreaterThan(int price);
}
