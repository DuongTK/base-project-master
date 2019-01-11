package sapo.vn.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sapo.vn.product.model.Product;
import sapo.vn.product.service.ProductService;

import java.util.List;

@RestController
public class MainController {

    @Autowired
    private ProductService productService;

//    @Autowired
//    private VersionService versionService;

    @RequestMapping(value = "/products",method = RequestMethod.GET)
    public List<Product> getAllProduct(){
        return productService.findAll();
    }

//    @RequestMapping(value = "/versions",method = RequestMethod.GET)
//    public List<Version> getAllVersion(){
//        return versionService.findAll();
//    }


}
