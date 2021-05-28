package kz.project.RentalSystem.services;

import kz.project.RentalSystem.entities.Categories;
import kz.project.RentalSystem.entities.Keywords;
import kz.project.RentalSystem.entities.Products;

import java.util.List;

public interface ProductService {
    Products addProduct(Products product);
    List<Products> getAllProducts();
    Products getProduct(Long id);
    void deleteProduct(Products product);
    Products saveProduct(Products product);

    List<Categories> getAllCategories();
    Categories addCategory(Categories category);
    Categories saveCategory(Categories category);
    Categories getCategory(Long id);
    void deleteCategory(Categories category);

    List<Keywords> getAllKeywords();
    Keywords addKeyword(Keywords keyword);
    Keywords saveKeyword(Keywords keyword);
    Keywords getKeyword(Long id);
    void deleteKeyword(Keywords keyword);
}
