package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.yandex.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findByInitiatorId(Long initiatorId, Pageable page);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    Page<Event> findAll(Pageable page);

    boolean existsByCategoryId(Long catId);

    List<Event> findAllByLocationId(Long locationId);
}
