package com.searchpharmacy

import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification

@SpringBootTest
class AbstractIntegrationContainerBaseTest extends Specification {
    // mariadb는 application.yml의 testcontainer로 만들지만 redis는 따로 띄워야함
    static final GenericContainer MY_REDIS_CONTAINER

    static {
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6")
                .withExposedPorts(6379)

        MY_REDIS_CONTAINER.start()

        System.setProperty("spring.data.redis.host", MY_REDIS_CONTAINER.getHost())
        System.setProperty("spring.data.redis.port", MY_REDIS_CONTAINER.getMappedPort(6379).toString())
    }
}
