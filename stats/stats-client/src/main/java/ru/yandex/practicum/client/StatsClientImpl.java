package ru.yandex.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.practicum.dto.StatsRequestDto;
import ru.yandex.practicum.dto.StatsRequestParamsDto;
import ru.yandex.practicum.dto.StatsResponseDto;
import ru.yandex.practicum.utils.JsonFormatPattern;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author PopovN
 * @created 09.06.2025 14:13
 */

@Slf4j
@Component
public class StatsClientImpl implements StatsClient{
    private final RestTemplate rest;
    private final DiscoveryClient discoveryClient;
    private final RetryTemplate retryTemplate;
    private final String statsServiceId;

    @Autowired
    public StatsClientImpl(DiscoveryClient discoveryClient,
                       @Value("${discovery.services.stats-server-id}") String statsServiceId,
                       RestTemplateBuilder restTemplateBuilder) {
        this.discoveryClient = discoveryClient;
        this.statsServiceId = statsServiceId;
        this.rest = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(""))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
        this.retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
    }

    @Override
    public Collection<StatsResponseDto> getAllStats(StatsRequestParamsDto statsRequestParamsDto) {
        if (!checkValidRequestParamsDto(statsRequestParamsDto)) {
            log.error("Get stats was not successful because of incorrect parameters {}", statsRequestParamsDto);
            return List.of();
        }

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", statsRequestParamsDto.getStart().format(JsonFormatPattern.DATE_TIME_FORMATTER))
                .queryParam("end", statsRequestParamsDto.getEnd().format(JsonFormatPattern.DATE_TIME_FORMATTER));

        if (statsRequestParamsDto.getUris() != null && !statsRequestParamsDto.getUris().isEmpty()) {
            uriComponentsBuilder.queryParam("uris", statsRequestParamsDto.getUris());
        }
        if (statsRequestParamsDto.getUnique() != null) {
            uriComponentsBuilder.queryParam("unique", statsRequestParamsDto.getUnique());
        }
        String uri = uriComponentsBuilder.build(false)
                .encode()
                .toUriString();

        HttpEntity<String> requestEntity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<StatsResponseDto[]> statServerResponse;
        try {
            statServerResponse = rest.exchange(makeUri(uri), HttpMethod.GET, requestEntity, StatsResponseDto[].class);
        } catch (HttpStatusCodeException e) {
            log.error("Get stats was not successful with code {} and message {}", e.getStatusCode(), e.getMessage(), e);
            return List.of();
        } catch (Exception e) {
            log.error("Get stats was not successful with exception {} and message {}", e.getClass().getName(), e.getMessage(), e);
            return List.of();
        }
        statServerResponse.getBody();
        return List.of(Objects.requireNonNull(statServerResponse.getBody()));
    }

    private boolean checkValidRequestParamsDto(StatsRequestParamsDto statsRequestParamsDto) {
        if (statsRequestParamsDto.getStart() == null || statsRequestParamsDto.getEnd() == null
                || statsRequestParamsDto.getStart().isAfter(statsRequestParamsDto.getEnd())) {
            return false;
        }

        if (statsRequestParamsDto.getUris() != null && statsRequestParamsDto.getUris().isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public void postStats(StatsRequestDto statsRequestDto) {
        HttpEntity<StatsRequestDto> requestEntity = new HttpEntity<>(statsRequestDto, defaultHeaders());
        try {
            rest.exchange(makeUri("/hit"), HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            log.error("Hit stats was not successful with code {} and message {}", e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Hit stats was not successful with exception {} and message {}", e.getClass().getName(), e.getMessage(), e);
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private URI makeUri(String path) {
        ServiceInstance instance = retryTemplate.execute(cxt -> getInstance(statsServiceId));
        log.info("Host() = {} Port() = {}", instance.getHost(), instance.getPort());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    private ServiceInstance getInstance(String serviceId) {
        try {
            return discoveryClient
                    .getInstances(serviceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + serviceId,
                    exception
            );
        }
    }
}
