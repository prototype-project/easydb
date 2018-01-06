package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceDefinition;
import com.easydb.easydb.domain.space.SpaceFactory;

public class MainSpaceFactory implements SpaceFactory {
    private final BucketRepository bucketRepository;

    public MainSpaceFactory(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    @Override
    public Space buildSpace(SpaceDefinition spaceDefinition) {
        return new SpaceService(
                spaceDefinition.getSpaceName(),
                bucketRepository);
    }
}
