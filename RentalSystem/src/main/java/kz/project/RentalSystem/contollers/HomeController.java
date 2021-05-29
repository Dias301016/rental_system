package kz.project.RentalSystem.contollers;

import kz.project.RentalSystem.config.WebMvcConfig;
import kz.project.RentalSystem.entities.Categories;
import kz.project.RentalSystem.entities.Keywords;
import kz.project.RentalSystem.entities.Products;
import kz.project.RentalSystem.entities.Users;
import kz.project.RentalSystem.services.ProductService;
import kz.project.RentalSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.*;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping(value = "/")
    public String home(Model model) {
        model.addAttribute("currentUser",getUserData());
        List<Products> products = productService.getAllProducts();
        model.addAttribute("producty", products);
        List<Categories> categories = productService.getAllCategories();
        return "home";

    }

    @GetMapping(path = "/addproduct")
    @PreAuthorize("isAuthenticated()")
    public String addProduct(Model model) {
        model.addAttribute("currentUser",getUserData());

        List<Categories> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);

        return "/addproduct";
    }

    @PostMapping("/search")
    public String search(String search, Model model){
        model.addAttribute("currentUser",getUserData());
        HashSet<Products> set = new HashSet<>();
        List<Products> list = productService.getAllProducts();
        list.sort(Comparator.comparing(Products::getPostDate));
        for(Products n : list) {
            if(n.getName().contains(search) || n.getDescription().contains(search) || n.getCategory().getName().contains(search) || n.getAuthor().getFName().contains(search)) {
                set.add(n);
            }
        }
        model.addAttribute("search",set);
        return "home";
    }

    @PostMapping(value = "/addproduct")
    public String addProduct(@RequestParam(name = "category_id",defaultValue = "0") Long id,
            @RequestParam(name = "author_id",defaultValue = "0") Long authorId,
        @RequestParam(name = "name", defaultValue = "No item") String name,
        @RequestParam(name = "description", defaultValue = "NO DESCR") String description,
        @RequestParam(name = "price", defaultValue = "0") int price)
        {
            Categories ctg = productService.getCategory(id);
            Users usr = userService.getUser(authorId);
            if(ctg!=null) {
                Date data = new Date();
                Products product = new Products();
                product.setName(name);
                product.setPrice(price);
                product.setDescription(description);
                product.setCategory(ctg);
                product.setAuthor(usr);
                product.setPostDate(data);
                productService.addProduct(product);

            }

        return "redirect:/";
        }

        private Users getUserData(){

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(!(authentication instanceof AnonymousAuthenticationToken)){
                User secUser = (User)authentication.getPrincipal();
                Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
            }
            return null;

        }


     @GetMapping(value = "/register")
     public String register(Model model){

        model.addAttribute("currentUser",getUserData());
        return "register";

     }

     @PostMapping(value = "/register")
     public String toRegister(@RequestParam(name ="user_email") String email,
                              @RequestParam(name = "user_password") String password,
                              @RequestParam(name ="re_user_password") String rePassword,
                              @RequestParam(name = "user_first_name") String firstName)
    {

        if(password.equals(rePassword)){
            Users newUser = new Users();
            newUser.setFName(firstName);
            newUser.setPassword(password);
            newUser.setEmail(email);

            if (userService.createUser(newUser)!=null){
                return "redirect:/register?success";


            }

        }
        return "redirect:/register?error";
     }

    @GetMapping(value = "/details/{idshka}")
    public String details(Model model, @PathVariable(name = "idshka") Long id){
        Products product = productService.getProduct(id);
        model.addAttribute("product",product);
        model.addAttribute("currentUser",getUserData());
        List<Keywords> keywords = productService.getAllKeywords();
        keywords.removeAll(product.getKeywords());
        model.addAttribute("keyword",keywords);
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users",users);
        List<Categories> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        return "details";
    }

    @GetMapping(value = "/addcategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String addCategory(Model model){
        model.addAttribute("currentUser",getUserData());
        List<Categories> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
            return "/addcategory";

    }
    @PostMapping(value = "/addcategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String addCategory(@RequestParam(name = "name",defaultValue = "No item") String name){

       Categories ctg = new Categories();
       ctg.setName(name);
       productService.addCategory(ctg);
        return "redirect:/addcategory";

    }
    @GetMapping(value = "/categorydetails/{idshka2}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String categoryDetails(Model model, @PathVariable(name = "idshka2") Long id){
        Categories category = productService.getCategory(id);
        model.addAttribute("category", category);
        model.addAttribute("currentUser",getUserData());
        return "categorydetails";
    }

    @GetMapping(value = "/addkeyword")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String addKeyword(Model model){
        model.addAttribute("currentUser",getUserData());
        List<Keywords> keywords = productService.getAllKeywords();
        model.addAttribute("keywords", keywords);
        return "addkeyword";

    }
    @PostMapping(value = "/addkeyword")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String addKeyword(@RequestParam(name = "name",defaultValue = "No item") String name){

        Keywords kwd = new Keywords();
        kwd.setName(name);
        productService.addKeyword(kwd);
        return "redirect:/addkeyword";

    }
    @GetMapping(value = "/keyworddetails/{idshka2}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String keywordDetails(Model model, @PathVariable(name = "idshka2") Long id){
        Keywords keyword = productService.getKeyword(id);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentUser",getUserData());
        return "keyworddetails";
    }

    @GetMapping(value = "/saveproduct/{idshka}")
    @PreAuthorize("isAuthenticated()")
    public String saveProduct(Model model, @PathVariable(name = "idshka") Long id){
        Products product = productService.getProduct(id);
        model.addAttribute("product",product);
        model.addAttribute("currentUser",getUserData());
        List<Keywords> keywords = productService.getAllKeywords();
        keywords.removeAll(product.getKeywords());
        model.addAttribute("keyword",keywords);
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users",users);
        List<Categories> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        return "saveproduct";
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
    @PostMapping(value = "/savekeyword")
    public String saveKeyword(
            @RequestParam(name = "id",defaultValue = "0") Long id,
            @RequestParam(name = "name",defaultValue = "No item") String name)

    {
        Keywords kwd = productService.getKeyword(id);

        if(kwd!=null) {
            kwd.setName(name);
            productService.saveKeyword(kwd);
        }
        return "redirect:/addkeyword";
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

                return "redirect:/saveproduct/"+productId+"#keywordDiv";

            }




        }

        return "redirect:/";

    }
    @GetMapping(value = "/403")
    public String deniedPage(Model model){
        model.addAttribute("currentUser",getUserData());
        return "403";

    }
    @GetMapping(value = "/login")
    public String loginPage(Model model){
        model.addAttribute("currentUser",getUserData());
        return "login";

    }



    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model){
        model.addAttribute("currentUser",getUserData());
        return "profile";

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

                    return "redirect:/saveproduct/"+productId+"#keywordDiv";

                    }




                }

        return "redirect:/";

        }



    }

