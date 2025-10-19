package org.animotion.animotionbackend.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WebSocketReply {
    private String correlationId;
    private Status status;
    private Object payload;
    private String error;

    public enum Status {
        SUCCESS, ERROR
    }

    public static WebSocketReply success(String correlationId, Object payload) {
        return new WebSocketReply(correlationId, Status.SUCCESS, payload, null);
    }

    public static WebSocketReply error(String correlationId, String errorMessage) {
        return new WebSocketReply(correlationId, Status.ERROR, null, errorMessage);
    }
}