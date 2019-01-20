package sapo.vn.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sapo.vn.product.model.Product;
import sapo.vn.product.model.Version;
import sapo.vn.product.repository.VersionRepository;
import sapo.vn.product.service.VersionService;

import java.util.List;
import java.util.Optional;

@Service
public class VersionServiceImpl implements VersionService {
    @Autowired
    private VersionRepository versionRepository;

    @Override
    public List<Version> findAllByProductID(String productId) {
        return versionRepository.findAllByProductID(productId);
    }

    @Override
    public Version save(Version version){
        return versionRepository.save(version);
    }

    @Override
    public Optional<Version> findById(int id) {
        return versionRepository.findById(id);
    }

    @Override
    public Optional<Version> findByIdAndProductId(String productId, String versionId) {
        return versionRepository.findByIdAndProductId(productId,versionId);
    }

    @Override
    public void delete(Version version) {
        versionRepository.delete(version);
    }

    @Override
    public void deleteAll(List<Version> versions) {
        versionRepository.deleteAll(versions);
    }

}
