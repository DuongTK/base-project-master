package sapo.vn.product.service;

import sapo.vn.product.model.Version;
import java.util.List;
import java.util.Optional;

public interface VersionService {
    List<Version> findAllByProductID(String productId);
    Version save(Version version);
    Optional<Version> findById(String id);
    Optional<Version> findByIdAndProductId(String productId,String versionId);
    void delete(Version version);
}
