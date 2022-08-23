package com.star.iceserver.websocket;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO:
 *
 * @Description:
 * @BelongsPackage: com.star.iceserver.websocket
 * @Author: lsh
 * @CreateTime: 2022-08-23  16:50
 */
public class IceSessionManage {

	public static Logger logger = LoggerFactory.getLogger(IceSessionManage.class);

	public static final ConcurrentHashMap<String, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();

	public static void putSession(WebSocketSession session) {
		String id = (String) session.getAttributes().get("id");
		SESSION_POOL.put(id, session);
		logger.warn("{}--连接成功--在线人数--{}", id, SESSION_POOL.size());
	}

	public static void delSession(String id) {
		SESSION_POOL.remove(id);
	}

	public static void sendTextMessage(String toId, Object content) {
		WebSocketSession webSocketSession = SESSION_POOL.get(toId);
		if (webSocketSession != null) {
			logger.warn("消息发送成功 toId={}", toId);
			try {
				webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(content)));
				return;
			} catch (IOException e) {
				logger.warn("消息发送失败 toId={}", toId);
				throw new RuntimeException(e);
			}
		}
		logger.warn("消息发送失败 toId={} 不存在!", toId);
	}

	public static void sendBinaryMessage(String toId, byte[] content) {
		WebSocketSession webSocketSession = SESSION_POOL.get(toId);
		if (webSocketSession != null) {
			logger.warn("消息发送成功 toId={}", toId);
			try {
				webSocketSession.sendMessage(new BinaryMessage(content));
				return;
			} catch (IOException e) {
				logger.warn("消息发送失败 toId={}", toId);
				throw new RuntimeException(e);
			}
		}
		logger.warn("消息发送失败 toId={} 不存在!", toId);
	}

	public static void sendMessage(String toId, WebSocketMessage webSocketMessage) {
		WebSocketSession webSocketSession = SESSION_POOL.get(toId);
		if (webSocketSession != null) {
			logger.warn("消息发送成功 toId={}", toId);
			try {
				webSocketSession.sendMessage(webSocketMessage);
				return;
			} catch (IOException e) {
				logger.warn("消息发送失败 toId={}", toId);
				throw new RuntimeException(e);
			}
		}
		logger.warn("消息发送失败 toId={} 不存在!", toId);
	}
}
