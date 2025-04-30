package com.instagram.profileservice.config.kafka;

import com.instagram.dto.kafka.PostCreatedEventDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Configuration
    public static class PostConsumerConfig {

        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapServers;

        @Bean
        public ConsumerFactory<String, PostCreatedEventDto> postConsumerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "profile-service");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
            props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

            return new DefaultKafkaConsumerFactory<>(
                    props,
                    new StringDeserializer(),
                    new JsonDeserializer<>(PostCreatedEventDto.class)
            );
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, PostCreatedEventDto> kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, PostCreatedEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(postConsumerFactory());
            return factory;
        }
    }

    @Configuration
    public static class FollowsConsumerConfig {

        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapServers;

        @Bean
        public ConsumerFactory<String, Long> followsConsumerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "profile-service");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
            props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
            props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

            return new DefaultKafkaConsumerFactory<>(
                    props,
                    new StringDeserializer(),
                    new LongDeserializer()
            );
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, Long> followsKafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(followsConsumerFactory());
            return factory;
        }
    }
}
