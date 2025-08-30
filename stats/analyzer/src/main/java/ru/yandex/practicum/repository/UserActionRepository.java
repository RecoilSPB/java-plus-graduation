package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.RecommendedEvent;
import ru.yandex.practicum.model.UserAction;

import java.util.List;
import java.util.Optional;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    Optional<UserAction> findByUserIdAndEventId(Long userId, Long eventId);

    List<UserAction> findAllByUserId(Long userId);

    @Query("SELECT new ru.yandex.practicum.model.RecommendedEvent(ua.eventId, sum(ua.weight)) " +
            " FROM UserAction ua " +
            "WHERE ua.eventId in :ids " +
            "GROUP BY ua.eventId")
    List<RecommendedEvent> getSumWeightForEvents(@Param("ids") List<Long> ids);

    @Query("SELECT ua FROM UserAction ua WHERE ua.userId = :id ORDER BY ua.created DESC LIMIT :limit")
    List<UserAction> findByUserIdOrderByCreatedDescLimitedTo(@Param("id") Long userId, @Param("limit") long limit);
}