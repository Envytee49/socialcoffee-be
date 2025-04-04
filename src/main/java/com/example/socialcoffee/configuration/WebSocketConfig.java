//package com.example.socialcoffee.configuration;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic", "/queue"); // Add /queue for private messaging
//        config.setApplicationDestinationPrefixes("/app");
//        config.setUserDestinationPrefix("/user"); // Enable /user/queue/...
//    }
//
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-notify").setAllowedOrigins("*").withSockJS(); // WebSocket endpoint
//    }
//}
//
