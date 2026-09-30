package com.example.notification_service.Kafka;

import com.example.notification_service.Events.UserRegisteredEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventConsumer {

    @KafkaListener(
            topics = "user-events",
            groupId = "notification-service"
    )
    public void consume(UserRegisteredEvent event) {

        System.out.println(
                "Received UserRegisteredEvent: " +
                        event.getUserId() +
                        " | " +
                        event.getEmail()
        );

        // Notification logic will go here later
    }
}