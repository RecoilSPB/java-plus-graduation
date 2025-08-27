package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;
import ru.yandex.practicum.mapper.UserActionMapper;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.service.ActionService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ActionController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final ActionService actionService;
    private final UserActionMapper userActionMapper;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("ActionController call collectUserAction for request = {}", request);
        UserAction userAction = userActionMapper.toEntity(request);
        actionService.collectUserAction(userAction);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

}