package com.searchpharmacy.pharmacy.cache

import com.searchpharmacy.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations

class RedisTemplateTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private RedisTemplate redisTemplate

    def "RedisTemplate String operations"() {
        given:
        def valueOperations = redisTemplate.opsForValue()
        def key = "StringKey"
        def value = "hello"

        when:
        valueOperations.set(key, value)

        then:
        def result = valueOperations.get(key)
        result == value
    }

    def "RedisTemplate set operations"() {
        given:
        def setOperations = redisTemplate.opsForSet()
        def key = "SetKey"

        when:
        setOperations.add(key, "h", "e", "l", "l", "o")

        then:
        def size = setOperations.size(key)
        size == 4
    }

    def "RedisTemplate hash operations"() {
        given:
        def hashOperations = redisTemplate.opsForHash()
        def key = "HashKey"

        when:
        hashOperations.put(key, "SubKey", "Value")

        then:
        def result = hashOperations.get(key, "SubKey")
        result == "Value"

        def entries = hashOperations.entries(key)
        entries.keySet().contains("SubKey")
        entries.values().contains("Value")

        def size = hashOperations.size(key)
        size == entries.size()
    }
}
