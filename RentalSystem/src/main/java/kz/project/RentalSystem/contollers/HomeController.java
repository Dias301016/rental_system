package kz.project.RentalSystem.contollers;

import kz.project.RentalSystem.entities.Categories;
import kz.project.RentalSystem.entities.Keywords;
import kz.project.RentalSystem.entities.Products;
import kz.project.RentalSystem.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;


    @GetMapping(value = "/")
    public String home(Model model) {
        List<Products> products = productService.getAllProducts();
        model.addAttribute("producty", products);
        List<Categories> categories = productService.getAllCategories();
        return "home";

    }

    @GetMapping(path = "/addproduct")
    public String addProduct(Model model) {

        List<Categories> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);

        return "/addproduct";
    }

    @PostMapping(value = "/addproduct")
    public String addProduct(@RequestParam(name = "category_id",defaultValue = "0") Long id,
        @RequestParam(name = "name", defaultValue = "No item") String name,
        @RequestParam(name = "description", defaultValue = "NO DESCR") String description,
        @RequestParam(name = "price", defaultValue = "0") int price)
        {
            Categories ctg = productService.getCategory(id);

            if(ctg!=null) {
                Products product = new Products();
                product.setName(name);
                product.setPrice(price);
                product.setDescription(description);
                product.setCategory(ctg);
                productService.addProduct(product);

            }

        return "redirect:/";
        }

    @GetMapping(value = "/details/{idshka}")
    public String details(Model model, @PathVariable(name = "idshka") Long id){
        Products product = productService.getProduct(id);
        model.addAttribute("product",product);
        List<Keywords> keywords = productService.getAllKeywords();
        keywords.removeAll(product.getKeywords());
        model.addAttribute("keyword",keywords);
        List<Categories> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        return "details";
    }

    @GetMapping(value = "/addcategory")
    public String addCategory(Model model){
        List<Categories> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
            return "/addcategory";

    }
    @PostMapping(value = "/addcategory")
    public String addCategory(@RequestParam(name = "name",defaultValue = "No item") String name){

       Categories ctg = new Categories();
       ctg.setName(name);
       productService.addCategory(ctg);
        return "redirect:/addcategory";

    }
    @GetMapping(value = "/categorydetails/{idshka2}")
    public String categoryDetails(Model model, @PathVariable(name = "idshka2") Long id){
        Categories category = productService.getCategory(id);
        model.addAttribute("category", category);
        return "categorydetails";
    }


    @PostMapping(value = "/savecategory")
    public String saveCategory (
            @RequestParam(name = "id",defaultValue = "0") Long id,
            @RequestParam(name = "name",defaultValue = "No item") String name)

    {
        Categories ctg = productService.getCategory(id);

            if(ctg!=null) {
              ctg.setName(name);
                productService.saveCategory(ctg);
            }
        return "redirect:/addcategory";
    }


    @PostMapping(value = "/saveproduct")
    public String saveProduct (
                                @RequestParam(name = "id",defaultValue = "0") Long id,
                                @RequestParam(name = "name",defaultValue = "No item") String name,
                                @RequestParam(name = "description",defaultValue = "NO DESCR") String description,
                                @RequestParam(name = "price",defaultValue = "0") int price,
                                @RequestParam(name = "category_id",defaultValue = "0") Long categoryId)
    {

        Products product = productService.getProduct(id);
        if(product!=null){
            Categories ctg = productService.getCategory(categoryId);

            if(ctg!=null) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(ctg);
            productService.saveProduct(product);
        }
        }
        return "redirect:/";
    }
    @PostMapping(value ="/deleteproduct")
    public String deleteProduct(@RequestParam(name = "id",defaultValue = "0") Long id){

        Products product = productService.getProduct(id);
        if(product!=null){
            productService.deleteProduct(product);
        }
        return "redirect:/";


    }
    @PostMapping(value = "/unassignkeyword")
    public String unAssignKeyword(@RequestParam(name="products_id") Long productId,
                                @RequestParam(name="keywords_id") Long keywordId){

        Keywords keyword = productService.getKeyword(keywordId);
        if(keyword!=null){
            Products product = productService.getProduct(productId);
            if(product!=null){

                List<Keywords> keywords = product.getKeywords();
                if(keywords==null){
                    keywords = new ArrayList<>();
                }
                keywords.remove(keyword);
                productService.saveProduct(product);

                return "redirect:/details/"+productId+"#keywordDiv";

            }




        }

        return "redirect:/";

    }
    @PostMapping(value = "/assignkeyword")
    public String assignKeyword(@RequestParam(name="products_id") Long productId,
                                @RequestParam(name="keywords_id") Long keywordId){

                Keywords keyword = productService.getKeyword(keywordId);
                if(keyword!=null){
                    Products product = productService.getProduct(productId);
                    if(product!=null){

                    List<Keywords> keywords = product.getKeywords();
                    if(keywords==null){
                        keywords = new ArrayList<>();
                    }
                    keywords.add(keyword);
                    productService.saveProduct(product);

                    return "redirect:/details/"+productId+"#keywordDiv";

                    }




                }

        return "redirect:/";

        }



    }

