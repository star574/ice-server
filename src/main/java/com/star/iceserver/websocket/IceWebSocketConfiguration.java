package com.star.iceserver.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * TODO:
 *
 * @Description:
 * @BelongsPackage: com.star.iceserver.websocket
 * @Author: lsh
 * @CreateTime: 2022-08-23  16:28
 */
@Configuration
@EnableWebSocket
public class IceWebSocketConfiguration implements WebSocketConfigurer {

	/**
	 * 注入ServerEndpointExporter
	 * 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocketendpoint
	 */

	/**
	 * websocket 请求拦截器
	 */
	@Bean
	public IceWebSocketInterceptor webSocketInterceptor() {
		return new IceWebSocketInterceptor();
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new IceWebSocketHandler(), "/websocket") //处理器
				.addInterceptors(webSocketInterceptor()) //拦截器
				.setAllowedOrigins("*"); // 允许来源
	}
}
