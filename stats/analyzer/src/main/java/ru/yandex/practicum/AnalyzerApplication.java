package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.processor.EventSimilarityProcessor;
import ru.yandex.practicum.processor.UserActionProcessor;

@EnableDiscoveryClient
@SpringBootApplication
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);

        final UserActionProcessor userActionProcessor =
                context.getBean(UserActionProcessor.class);
        final EventSimilarityProcessor eventSimilarityProcessor =
                context.getBean(EventSimilarityProcessor.class);

        Thread hubEventsThread = new Thread(userActionProcessor);
        hubEventsThread.setName("UserActionHandlerThread");
        hubEventsThread.start();

        eventSimilarityProcessor.run();
    }
}
