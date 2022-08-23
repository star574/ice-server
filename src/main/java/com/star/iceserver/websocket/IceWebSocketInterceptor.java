package com.star.iceserver.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * TODO:
 *
 * @Description:
 * @BelongsPackage: com.star.iceserver.websocket
 * @Author: lsh
 * @CreateTime: 2022-08-23  16:28
 */
public class IceWebSocketInterceptor extends HttpSessionHandshakeInterceptor {

	private static Logger logger = LoggerFactory.getLogger(IceWebSocketInterceptor.class);

	/**
	 * TODO:
	 *
	 * @description: 连接之前
	 * @author: lsh
	 * @date: 2022-08-23 16:48
	 * @param: [request, response, wsHandler, attributes]
	 * @return: boolean
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		HttpHeaders headers = request.getHeaders();
		String token = headers.get("Sec-WebSocket-Protocol").get(0);
		logger.warn("token={}", token);
		String id = null;
		String auth = null;
		try {
			id = token.split("-")[0];
			auth = token.split("-")[1];
		} catch (Exception e) {
		}
		// 鉴权
		if (!StringUtils.hasLength(auth) || !auth.equals("654321")) {
			logger.warn("鉴权失败");
			return false;
		}
		// 在session的getAttributes可以拿到
		attributes.put("id", id);
		response.getHeaders().set("Sec-WebSocket-Protocol", token);
		return true;
	}

	/**
	 * TODO:
	 *
	 * @description: 连接之后
	 * @author: lsh
	 * @date: 2022-08-23 16:48
	 * @param: [request, response, wsHandler, ex]
	 * @return: void
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
		super.afterHandshake(request, response, wsHandler, ex);
	}
}
