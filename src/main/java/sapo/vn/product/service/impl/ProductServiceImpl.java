package sapo.vn.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sapo.vn.product.model.Product;
import sapo.vn.product.repository.ProductRepository;
import sapo.vn.product.service.ProductService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

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
