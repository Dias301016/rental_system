package kz.project.RentalSystem.repositories;

import javassist.compiler.ast.Keyword;
import kz.project.RentalSystem.entities.Keywords;
import kz.project.RentalSystem.entities.Products;
import kz.project.RentalSystem.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Products,Long> {
        List<Products> findAllByPriceIsGreaterThan(int price);
        List<Products> findAllByCategory_Id(Long id);
        List<Products> findAllByKeywords(Keywords keyword);
        List<Products> findAllByAuthor(Users user);


}
