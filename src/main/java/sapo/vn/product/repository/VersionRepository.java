package sapo.vn.product.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sapo.vn.product.model.Version;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRepository extends CrudRepository<Version,Integer> {

    @Query(value = "select * from version where version.product_id = :product_id",nativeQuery = true)
    List<Version> findAllByProductID(@Param(value = "product_id") String productId);

    @Query(value = "select * from version where version.product_id = :product_id and version.id = :version_id",nativeQuery = true)
    Optional<Version> findByIdAndProductId(@Param(value = "product_id") String productId,@Param(value = "version_id") String versionId);

}
