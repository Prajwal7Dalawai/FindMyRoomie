package com.example.auth_service.Kafka;

import com.example.auth_service.Event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegisteredEventProducer {
    private static final String TOPIC = "user-events";
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    public void publish(UserRegisteredEvent event) {

        System.out.println(
                "Publishing UserRegisteredEvent: "
                        + event.getUserId()
                        + " | "
                        + event.getEmail()
        );

        kafkaTemplate.send(
                TOPIC,
                event.getUserId().toString(),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                System.out.println(
                        "Kafka publish failed: "
                                + exception.getMessage()
                );
            } else {
                System.out.println(
                        "Kafka publish successful. Topic: "
                                + result.getRecordMetadata().topic()
                                + ", Partition: "
                                + result.getRecordMetadata().partition()
                                + ", Offset: "
                                + result.getRecordMetadata().offset()
                );
            }
        });
    }
}
