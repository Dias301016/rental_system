package kz.project.RentalSystem.contollers;

import kz.project.RentalSystem.config.WebMvcConfig;
import kz.project.RentalSystem.entities.Categories;
import kz.project.RentalSystem.entities.Keywords;
import kz.project.RentalSystem.entities.Products;
import kz.project.RentalSystem.entities.Users;
import kz.project.RentalSystem.services.ProductService;
import kz.project.RentalSystem.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Value("${file.avatar.viewPath}")
    private String viewPath;
    @Value("${file.avatar.uploadPath}")
    private String uploadPath;
    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;
    @Value("${file.product.viewPath2}")
    private String viewPath2;
    @Value("${file.product.uploadPath2}")
    private String uploadPath2;
    @Value("${file.product.defaultPicture2}")
    private String defaultPicture2;


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
        List<Keywords> keywords = productService.getAllKeywords();
        for(Products n : list) {
            for (Keywords keyword : keywords) {

                if (n.getName().contains(search) || n.getDescription().contains(search) || n.getCategory().getName().contains(search) || n.getAuthor().getEmail().contains(search)) {
                    set.add(n);
                }   else if (keyword.getName().contains(search)){
                    List<Products> products1 = productService.getAllByKeyword(keyword);
                    for(Products a :products1){
                        set.add(a);

                    }
                }
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
     @PreAuthorize("isAnonymous()")
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
    @PreAuthorize("isAuthenticated()")
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

    @PostMapping(value = "/uploadproduct")
    @PreAuthorize("isAuthenticated()")
    public String uploadAvatar(@RequestParam(name = "product_picture")MultipartFile file,
                               @RequestParam(name = "id")Long id){

        if(file.getContentType().equals("image/jpeg")||file.getContentType().equals("image/png")) {

            try {
                Products currentProduct = productService.getProduct(id);
                String picName = DigestUtils.sha1Hex("picture_of_"+currentProduct.getId()+"!");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath2+picName+".jpg");
                Files.write(path, bytes);
                currentProduct.setProductPicture(picName);
                productService.saveProduct(currentProduct);
                return "redirect:/details/"+id;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/";

    }
    @PostMapping(value = "/deleteproductpicture")
    @PreAuthorize("isAuthenticated()")
    public String deleteProductPicture(@RequestParam(name="id") Long id){
        Products currentProduct = productService.getProduct(id);
        if(currentProduct!=null){
            currentProduct.setProductPicture("<null>");
            productService.saveProduct(currentProduct);
        }
        return "redirect:/details/"+id;
    }
    @GetMapping(value = "/viewproduct/{url}",produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewProductPicture(@PathVariable(name="url") String url) throws IOException {
        String pictureUrl = viewPath2+defaultPicture2;
        if(url!=null&&!url.equals("null")){
            pictureUrl=viewPath2+url+".jpg";

        }
        InputStream in;
        try{
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in= resource.getInputStream();
        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewPath2+defaultPicture2);
            in =resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
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
    @PostMapping(value = "/deletecategory")
    public String deleteCategory(
            @RequestParam(name = "id",defaultValue = "0") Long id)

    {
        Categories ctg = productService.getCategory(id);
        List<Products> products = productService.getAllByCategories(id);

        Categories def = productService.getCategory(3L);

        if(ctg!=null&&id!=3L) {

            productService.deleteCategory(ctg);
            for (Products product:products){
                product.setCategory(def);
                productService.saveProduct(product);
            }
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
    @PostMapping(value = "/deletekeyword")
    public String deleteKeyword(
            @RequestParam(name = "id",defaultValue = "0") Long id)

    {
        Keywords kwd = productService.getKeyword(id);

        if(kwd!=null) {

           productService.deleteKeyword(kwd);
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

    @PostMapping(value = "/uploadavatar")
    public String uploadAvatar(@RequestParam(name = "user_avatar")MultipartFile file){

        if(file.getContentType().equals("image/jpeg")||file.getContentType().equals("image/png")) {

            try {
                Users currentUser = getUserData();
                String picName = DigestUtils.sha1Hex("avatar_of_"+currentUser.getId()+"!");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath +picName +".jpg");
                Files.write(path, bytes);
                currentUser.setUserAvatar(picName);
                userService.saveUser(currentUser);
                return "redirect:/profile?success";

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            return "redirect:/";

        }
        @GetMapping(value = "/viewphoto/{url}",produces = {MediaType.IMAGE_JPEG_VALUE})
        public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name="url") String url) throws IOException {
        String pictureUrl = viewPath+defaultPicture;
        if(url!=null&&!url.equals("null")){
            pictureUrl=viewPath+url+".jpg";

        }
            InputStream in;
        try{
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in= resource.getInputStream();
        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
            in =resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
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

