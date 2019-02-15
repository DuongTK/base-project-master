package duong.cache.hd.controller;

import duong.cache.hd.base.constant.ApiResult;
import duong.cache.hd.exception.ResourceNotFoundException;
import duong.cache.hd.model.Product;
import duong.cache.hd.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import duong.cache.hd.service.ProductService;
import duong.cache.hd.service.VersionService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/admin")
public class VersionController {

    @Autowired
    private ProductService productService;

    @Autowired
    private VersionService versionService;

    @RequestMapping(value = "/products/{product_id}/versions",method = RequestMethod.GET)
    public List<Version> getAllVersions(@PathVariable(value = "product_id") String productId){
        return versionService.findAllByProductID(productId);
    }

    @RequestMapping(value = "/products/{product_id}/versions/{version_id}",method = RequestMethod.GET)
    public Version getVersionByID(@PathVariable(value = "product_id") String productId,
                                  @PathVariable(value = "version_id") String versionId){
        return versionService.findByIdAndProductId(productId,versionId).orElseThrow(()-> new ResourceNotFoundException("Not found Resourcse"));
    }
Logger logger = LoggerFactory.getLogger(VersionController.class);
    @RequestMapping(value = "/products/{product_id}/versions",method = RequestMethod.POST)
    public ApiResult createVersion(@PathVariable(value = "product_id") String productId,
                                   @Valid @RequestBody Version version){
        Optional<Product> product = productService.findById(productId);
        if (product.isPresent()){
            List<Version> versions = versionService.findAllByProductID(productId);
            for( Version v : versions){
                if (version.compare(v)){
                    return new ApiResult(409,"version already exist !");
                }
            }
            version.setProduct(product.get());
            versionService.save(version);
            return new ApiResult(200,"Add new version successful");
        }
        return new ApiResult(404,"productId "+productId+" not found");
    }

    @RequestMapping(value = "/products/{product_id}/versions/{version_id}",method = RequestMethod.PUT)
    public ApiResult updateVersion(@PathVariable(value = "product_id") String productId,
                                 @PathVariable(value = "version_id") String versionId,
                                 @Valid @RequestBody Version version_update){
        if (!productService.existsById(productId)){
            return new ApiResult(404,"productId " + productId + " not found");
        }
        Optional<Version> ver= versionService.findById(Integer.parseInt(versionId));
        if (ver.isPresent()){
            List<Version> versions = versionService.findAllByProductID(productId);
            for( Version v : versions){
                if (version_update.compare(v)){
                    return new ApiResult(409,"version already exist !");
                }
            }

            Version version = ver.get();
            version.setName(version_update.getName());
            version.setProperties(version_update.getProperties());
            version.setImage(version_update.getImage());
            version.setBarCode(version_update.getBarCode());
            version.setSkuCode(version_update.getSkuCode());
            version.setPrice(version_update.getPrice());
            versionService.save(version);
            return new ApiResult(200,"Update version successful");
        }
        return new ApiResult(404,"versionId "+versionId+" not found");
    }

    @RequestMapping(value = "/products/{product_id}/versions/{version_id}",method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteVersion(@PathVariable (value = "product_id") String productId,
                                           @PathVariable (value = "version_id") String versionId) {
        return versionService.findByIdAndProductId(productId, versionId).map(v -> {
            versionService.delete(v);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Version not found with id " + versionId + " and productId " + productId));
    }
}
