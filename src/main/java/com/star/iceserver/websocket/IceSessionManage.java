package com.star.iceserver.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

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
		logger.debug("{}--连接成功--在线人数--{}", id, SESSION_POOL.size());
	}

	public static void delSession(String id) {
		SESSION_POOL.remove(id);
	}
}
