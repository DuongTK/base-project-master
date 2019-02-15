package duong.cache.hd.service;

import duong.cache.hd.model.Version;

import java.util.List;
import java.util.Optional;

public interface VersionService {
    List<Version> findAllByProductID(String productId);
    Version save(Version version);
    Optional<Version> findById(int id);
    Optional<Version> findByIdAndProductId(String productId,String versionId);
    void delete(Version version);
    void deleteAll(List<Version> versions);
}
