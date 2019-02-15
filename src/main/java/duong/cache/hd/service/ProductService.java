package duong.cache.hd.service;

import duong.cache.hd.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
