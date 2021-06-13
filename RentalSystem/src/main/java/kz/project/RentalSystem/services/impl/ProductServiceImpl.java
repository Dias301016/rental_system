package kz.project.RentalSystem.services.impl;

import kz.project.RentalSystem.entities.Categories;
import kz.project.RentalSystem.entities.Keywords;
import kz.project.RentalSystem.entities.Products;
import kz.project.RentalSystem.entities.Users;
import kz.project.RentalSystem.repositories.CategoryRepository;
import kz.project.RentalSystem.repositories.KeywordRepository;
import kz.project.RentalSystem.repositories.ProductRepository;
import kz.project.RentalSystem.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class  ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Override
    public Products addProduct(Products product) {
        return productRepository.save(product);
    }

    @Override
    public Products getProduct(Long id) {
        return productRepository.getOne(id);
    }

    @Override
    public List<Products> getAllProducts(){ return productRepository.findAllByPriceIsGreaterThan(0); }

    @Override
    public void deleteProduct(Products product) { productRepository.delete(product); }

    @Override
    public Products saveProduct(Products product) {        return productRepository.save(product); }

    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Categories addCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public Categories saveCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public Categories getCategory(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public List<Products> getAllByKeyword(Keywords keyword) {
        return productRepository.findAllByKeywords(keyword);
    }

    @Override
    public List<Products> getAllByAuthor(Users user) {
        return productRepository.findAllByAuthor(user);
    }

    @Override
    public List<Products> getAllByCategories(Long id) {     return productRepository.findAllByCategory_Id(id); }

    @Override
    public void deleteCategory(Categories category) {
        categoryRepository.delete(category);
    }

    @Override
    public List<Keywords> getAllKeywords() {
        return keywordRepository.findAll();
    }

    @Override
    public Keywords addKeyword(Keywords keyword) {
        return keywordRepository.save(keyword);
    }

    @Override
    public Keywords saveKeyword(Keywords keyword) {
        return keywordRepository.save(keyword);
    }

    @Override
    public Keywords getKeyword(Long id) {
       return keywordRepository.getOne(id);
    }

    @Override
    public void deleteKeyword(Keywords keyword) {
        keywordRepository.delete(keyword);
    }


}
