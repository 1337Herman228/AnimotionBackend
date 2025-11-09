package org.animotion.animotionbackend.config;

import org.animotion.animotionbackend.config.security.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // --- Префиксы для сообщений ОТ клиента К серверу ---
        // Все сообщения от клиентов, адресованные серверу, должны начинаться с /app
        // Например: /app/project/move-card
        registry.setApplicationDestinationPrefixes("/app");

        // --- Префиксы для сообщений ОТ сервера К клиенту (рассылка) ---
        // Включаем простой брокер сообщений, который будет рассылать сообщения
        // клиентам, подписанным на темы, начинающиеся с /topic.
        // Например, /topic/project/123
        registry.enableSimpleBroker("/topic/", "/queue/");


        // --- ДОБАВЬТЕ ЭТУ СТРОКУ ---
        // Указывает префикс для сообщений, адресованных конкретному пользователю.
        // Spring автоматически преобразует /user/queue/replies в уникальный
        // адрес для сессии пользователя, например /queue/replies-user123xyz
        registry.setUserDestinationPrefix("/user");
    }
}