package com.intellitransit.repository;

import com.intellitransit.entity.GtfsFeedMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GtfsFeedMetadataRepository extends JpaRepository<GtfsFeedMetadata, Long> {
    Optional<GtfsFeedMetadata> findByBatchId(String batchId);
}
