package ru.yandex.practicum.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Location;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLatAndLon(Float lat, Float lon);

    @Query("select l from Location l where distance(l.lat, l.lon, :latitude, :longitude) <= :radius")
    List<Location> findAllByRadius(
            @Param("latitude") Float latitude,
            @Param("longitude") Float longitude,
            @Param("radius") Float radius
    );
}