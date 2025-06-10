package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Requests;
import ru.yandex.practicum.model.Response;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Requests, Integer> {
    @Query(value = "SELECT new ru.yandex.practicum.model.Response(" +
            "application, uri, COUNT(ip) as total) " +
            "FROM Requests " +
            "WHERE moment between :start AND :end " +
            "GROUP BY application, uri " +
            "ORDER BY total DESC")
    List<Response> findAll(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = "SELECT new ru.yandex.practicum.model.Response(" +
            "application, uri, COUNT(DISTINCT ip) as total) " +
            "FROM Requests " +
            "WHERE moment between :start AND :end " +
            "GROUP BY application, uri " +
            "ORDER BY total DESC")
    List<Response> findAllUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = "SELECT new ru.yandex.practicum.model.Response(" +
            "application, uri, COUNT(ip) as total) " +
            "FROM Requests " +
            "WHERE moment between :start AND :end " +
            "AND uri in ( :uris) " +
            "GROUP BY application, uri " +
            "ORDER BY total DESC")
    List<Response> findUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query(value = "SELECT new ru.yandex.practicum.model.Response(" +
            "application, uri, COUNT(DISTINCT ip) as total) " +
            "FROM Requests " +
            "WHERE moment between :start AND :end " +
            "AND uri in ( :uris) " +
            "GROUP BY application, uri " +
            "ORDER BY total DESC")
    List<Response> findUrisUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}
