package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.dto.request.EventRequestStatus;
import ru.yandex.practicum.model.EventRequest;
import ru.yandex.practicum.model.EventRequestCount;

import java.util.List;

public interface RequestRepository extends JpaRepository<EventRequest, Long>,
        QuerydslPredicateExecutor<EventRequest> {

    List<EventRequest> findByRequesterId(Long userId);

    List<EventRequest> findByEventId(Long eventId);

    Long countByEventIdAndStatusIn(Long eventId, List<EventRequestStatus> status);

    List<EventRequest> findAllByEventIdAndStatus(Long eventId, EventRequestStatus status);

    @Query("SELECT new ru.yandex.practicum.model.EventRequestCount(pr.eventId, count(pr.id)) " +
            "FROM EventRequest pr WHERE pr.eventId in :ids and status = 'CONFIRMED' GROUP BY pr.eventId")
    List<EventRequestCount> getCountConfirmed(@Param("ids") List<Long> ids);
}
