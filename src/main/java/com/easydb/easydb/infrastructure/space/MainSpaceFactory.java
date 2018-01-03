package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceDefinitionQueryDto;
import com.easydb.easydb.domain.space.SpaceFactory;

public class MainSpaceFactory implements SpaceFactory {
    private final UUIDProvider uuidProvider;
    private final BucketRepository bucketRepository;

    public MainSpaceFactory(UUIDProvider uuidProvider, BucketRepository bucketRepository) {
        this.uuidProvider = uuidProvider;
        this.bucketRepository = bucketRepository;
    }

    @Override
    public Space buildSpace(SpaceDefinitionQueryDto spaceDefinition) {
        return new MainSpace(
                spaceDefinition.getSpaceName(),
                bucketRepository,
                uuidProvider);
    }
}
