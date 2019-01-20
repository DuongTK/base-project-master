package sapo.vn.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sapo.vn.product.model.Product;
import sapo.vn.product.model.ProductDTO;
import sapo.vn.product.model.Version;
import sapo.vn.product.repository.ProductRepository;
import sapo.vn.product.repository.VersionRepository;
import sapo.vn.product.service.ProductService;
import sapo.vn.product.utilities.RandomString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

//    @Autowired
//    protected VersionRepository versionRepository;

//    @Override
//    public Product save(ProductDTO productDTO) {
//        Product product = new Product();
//        Version version = new Version();
//
//        String product_id = RandomString.generateString(9);
//        product.setId(product_id);
//        product.setName(productDTO.getName());
//        product.setDescription(productDTO.getDescription());
//        product.setImage(productDTO.getImage());
//        product.setType(productDTO.getType());
//        product.setBrand(productDTO.getBrand());
//        product.setTags(productDTO.getTags());
//
//        version.setId(productDTO.getVersionId());
//        version.setName(productDTO.getNameVersion());
//        version.setProperties(productDTO.getProperties());
//        version.setImage(productDTO.getImage());
//        version.setSkuCode(productDTO.getSkuCode());
//        version.setBarCode(productDTO.getBarCode());
//        version.setPrice(productDTO.getPrice());
//        version.setProduct(product);
//
//        List<Version> versions = new ArrayList<>();
//        versions.add(version);
//        product.setVersions(versions);
//        productRepository.save(product);
//        versionRepository.save(version);
//        return product;
//    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return (List<Product>)productRepository.findAll();

    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public void delete(Product product){
        productRepository.delete(product);
    }

    @Override
    public boolean existsById(String id) {
        return productRepository.existsById(id);
    }


}
