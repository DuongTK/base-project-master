package sapo.vn.product.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sapo.vn.product.model.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product,String> {
}
