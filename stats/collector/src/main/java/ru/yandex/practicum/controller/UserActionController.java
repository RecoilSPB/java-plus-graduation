package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.yandex.practicum.mapper.UserActionMapper;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.service.ActionService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final ActionService actionService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("ActionController call collectUserAction for request = {}", request);
            UserAction userAction = UserActionMapper.toEntity(request);
            actionService.collectUserAction(userAction);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            // отправляем ошибку клиенту
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

}