package sapo.vn.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sapo.vn.product.exception.ResourceNotFoundException;
import sapo.vn.product.model.Version;
import sapo.vn.product.service.ProductService;
import sapo.vn.product.service.VersionService;

import javax.validation.Valid;
import java.util.List;

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
        return versionService.findByIdAndProductId(productId,versionId).get();
    }

    @RequestMapping(value = "/products/{product_id}/versions",method = RequestMethod.POST)
    public Version createVersion(@PathVariable(value = "product_id") String productId,
                                 @Valid @RequestBody Version version){
        return productService.findById(productId).map(product -> {
            version.setProduct(product);
            return versionService.save(version);
        }).orElseThrow(() -> new ResourceNotFoundException("productId " + productId + " not found"));
    }

    @RequestMapping(value = "/products/{product_id}/versions/{version_id}",method = RequestMethod.PUT)
    public Version updateVersion(@PathVariable(value = "product_id") String productId,
                                 @PathVariable(value = "version_id") String versionId,
                                 @Valid @RequestBody Version version_update){
        if (!productService.existsById(productId)){
            throw new ResourceNotFoundException("productId " + productId + " not found");
        }
        return versionService.findById(versionId).map(version -> {
            version.setName(version_update.getName());
            version.setProperties(version_update.getProperties());
            version.setImage(version_update.getImage());
            version.setBarCode(version_update.getBarCode());
            version.setSkuCode(version_update.getSkuCode());
            version.setPrice(version_update.getPrice());
            return versionService.save(version);
        }).orElseThrow(() -> new ResourceNotFoundException("versionId " + versionId + "not found"));
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
