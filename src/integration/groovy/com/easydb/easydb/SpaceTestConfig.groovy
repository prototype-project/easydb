package com.easydb.easydb

import com.easydb.easydb.domain.MainSpace
import com.easydb.easydb.domain.Space
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class SpaceTestConfig {

    @Primary
    @Bean
    Space inMemorySpace() {
        return new MainSpace('testSpace', new InMemoryBucketRepository())
    }
}
