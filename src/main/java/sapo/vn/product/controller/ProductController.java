package sapo.vn.product.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sapo.vn.product.exception.ResourceNotFoundException;
import sapo.vn.product.model.Product;
import sapo.vn.product.model.Version;
import sapo.vn.product.service.ProductService;
import sapo.vn.product.service.VersionService;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/admin")
public class ProductController {

    private static Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private VersionService versionService;

    @RequestMapping(value = "/products",method = RequestMethod.GET)
    public Page<Product> getProducts(Pageable pageable) {
        return productService.findAll(pageable);
    }

    @RequestMapping(value = "/products/{product_id}",method = RequestMethod.GET)
    public Product getProductByID(@PathVariable(value = "product_id") String productId){
        return productService.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Not found Resourcse"));
    }

    @RequestMapping(value = "/products",method = RequestMethod.POST)
    public Product createProduct(@Valid @RequestBody Product product) {
        product.getVersions().stream().map(version -> {
            version.setProduct(product);
            return version;
        }).collect(Collectors.toList());
        return productService.save(product);
    }

    @RequestMapping(value = "/products/{product_id}",method = RequestMethod.PUT)
    public Product updateProduct(
            @PathVariable(value = "product_id")  String productId,
            @Valid @RequestBody Product product_update) {

        return productService.findById(productId).map(product -> {
            product.setName(product_update.getName());
            product.setDescription(product_update.getDescription());
            product.setImage(product_update.getImage());
            product.setType(product_update.getType());
            product.setBrand(product_update.getBrand());
            product.setTags(product_update.getTags());

            if (product_update.getVersions() != null){
            List<Version> versionsNew = product_update.getVersions().stream().map(version -> {
                    version.setProduct(product);
                    return version;
                }).collect(Collectors.toList());
            List<Version> versionsInDB = versionService.findAllByProductID(productId);
            versionService.deleteAll(versionsInDB);
            product.setVersions(versionsNew);
            }
            return productService.save(product);

        }).orElseGet(() -> {
            //nếu product_id chưa tồn tại thì thêm product mới với product_id
            product_update.setId(productId);
            product_update.getVersions().stream().map(version -> {
                version.setProduct(product_update);
                return  version;
            }).collect(Collectors.toList());
            return productService.save(product_update);
        });
    }

    @RequestMapping(value = "/products/{product_id}",method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "product_id") String productId){
        return productService.findById(productId).map(product -> {
            productService.delete(product);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("productID " + productId + " not found"));
    }



//    @RequestMapping(value = "/products",method = RequestMethod.GET)
//    public Page<Product> getProductsBySize(
//            @RequestParam(value = "page",required = false,defaultValue = "0") int page,
//            @RequestParam(value = "size",required = false,defaultValue = "10") int size,
//            @RequestParam(value = "sort",required = false,defaultValue = "ASC") String sort
//            ){
//        Sort sortable = null;
//        if (sort.equals("ASC")) {
//            sortable = Sort.by("id").ascending();
//        }
//        if (sort.equals("DESC")) {
//            sortable = Sort.by("id").descending();
//        }
//        Pageable pageable = PageRequest.of(page,size,sortable);
//        return productService.findAll(pageable);
//    }

}
