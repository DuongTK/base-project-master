package sapo.vn.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sapo.vn.product.model.Product;
import sapo.vn.product.model.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {

//    Product save(ProductDTO productDTO);
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    void delete(Product product);
    boolean existsById(String id);

}
