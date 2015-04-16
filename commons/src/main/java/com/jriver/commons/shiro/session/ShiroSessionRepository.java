package com.jriver.commons.shiro.session;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;

/**
 * custom shiro session manager interface
 * 
 * @author river
 */
public interface ShiroSessionRepository
{

	void saveSession(Session session);

	void deleteSession(Serializable sessionId);

	Session getSession(Serializable sessionId);

	Collection<Session> getAllSessions();
}
