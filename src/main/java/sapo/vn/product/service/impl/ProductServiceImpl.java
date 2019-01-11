package sapo.vn.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sapo.vn.product.model.Product;
import sapo.vn.product.repository.ProductRepository;
import sapo.vn.product.service.ProductService;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return (List<Product>)productRepository.findAll();

    }


}
