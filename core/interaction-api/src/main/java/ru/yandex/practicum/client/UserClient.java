package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

@FeignClient(name = "user-service", path = "/internal/api/users")
public interface UserClient {

    @GetMapping("/{userId}")
    UserShortDto getById(@PathVariable Long userId) throws NotFoundException;

    @GetMapping
    List<UserShortDto> getByIds(@RequestParam(name = "id") List<Long> ids,
                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                @RequestParam(required = false, defaultValue = "10") Integer size);

}
