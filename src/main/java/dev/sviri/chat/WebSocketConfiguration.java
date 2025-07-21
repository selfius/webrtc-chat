package dev.sviri.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final SignalWebSocketHandler signalWebSocketHandler;

    public WebSocketConfiguration(SignalWebSocketHandler signalWebSocketHandler) {
        this.signalWebSocketHandler = signalWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalWebSocketHandler, "signal").setAllowedOrigins("https://chat.sviri.dev");
    }


}
