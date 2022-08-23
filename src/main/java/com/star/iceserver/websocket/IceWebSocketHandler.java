package com.star.iceserver.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionManager;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO:
 *
 * @Description:
 * @BelongsPackage: com.star.iceserver.websocket
 * @Author: lsh
 * @CreateTime: 2022-08-23  16:27
 */
public class IceWebSocketHandler extends AbstractWebSocketHandler {

	public static Logger logger = LoggerFactory.getLogger(IceWebSocketHandler.class);

	/**
	 * TODO:
	 *
	 * @description: 连接成功 加入session
	 * @author: lsh
	 * @date: 2022-08-23 17:13
	 * @param: [session]
	 * @return: void
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.warn("连接成功");
		IceSessionManage.putSession(session);
		super.afterConnectionEstablished(session);
	}

	/**
	 * TODO:
	 *
	 * @description: 断开连接，移除session
	 * @author: lsh
	 * @date: 2022-08-23 17:22
	 * @param: [session, status]
	 * @return: void
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		IceSessionManage.delSession(getId(session));
		super.afterConnectionClosed(session, status);
	}

	/**
	 * TODO:
	 *
	 * @description: 发生错误
	 * @author: lsh
	 * @date: 2022-08-23 17:23
	 * @param: [session, exception]
	 * @return: void
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		IceSessionManage.delSession(getId(session));
		super.handleTransportError(session, exception);
	}


	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		String id = getId(session);
		logger.warn("{}准备发送消息", id);
		ConcurrentHashMap<String, WebSocketSession> sessionPool = IceSessionManage.SESSION_POOL;
		sessionPool.remove(id);
		sessionPool.forEach((K, V) -> {
			try {
				V.sendMessage(message);
				logger.warn("{}已发送", id);
			} catch (IOException e) {
				logger.warn("消息转发失败!");
			}
		});
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

	}


	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

	}


	private String getId(WebSocketSession session) {
		return (String) session.getAttributes().get("id");
	}

}
