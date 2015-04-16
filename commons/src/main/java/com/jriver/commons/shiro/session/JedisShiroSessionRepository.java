package com.jriver.commons.shiro.session;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.jriver.commons.shiro.SerializeUtil;

/**
 * redis save shiro session class
 * 
 * @author river
 */
public class JedisShiroSessionRepository implements ShiroSessionRepository
{

	private static final Logger	LOG						= LoggerFactory
																.getLogger(JedisShiroSessionRepository.class);

	private static final String	REDIS_SHIRO_SESSION		= "shiro-session:";
	private static final int	SESSION_VAL_TIME_SPAN	= 18000;

	private ShardedJedisPool	shardedJedisPool;

	@Override
	public void saveSession(Session session)
	{
		if (session == null || session.getId() == null) throw new NullPointerException(
				"session is empty");
		ShardedJedis jedis = null;
		try
		{
			byte[] key = SerializeUtil.serialize(buildRedisSessionKey(session
					.getId()));
			byte[] value = SerializeUtil.serialize(session);
			long sessionTimeOut = session.getTimeout() / 1000;
			Long expireTime = sessionTimeOut + SESSION_VAL_TIME_SPAN + (5 * 60);

			jedis = shardedJedisPool.getResource();
			jedis.set(key, value);
			jedis.expire(key, expireTime.intValue());
		}
		catch (Exception e)
		{
			LOG.error("save shiro session error!", e);
		}
		finally
		{
			if (null != jedis)
			{
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	@Override
	public void deleteSession(Serializable id)
	{
		if (id == null)
		{
			throw new NullPointerException("session id is empty");
		}
		ShardedJedis jedis = null;
		try
		{
			jedis = shardedJedisPool.getResource();
			jedis.del(buildRedisSessionKey(id));
		}
		catch (Exception e)
		{
			LOG.error("delete shiro session error!", e);
		}
		finally
		{
			if (null != jedis)
			{
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	@Override
	public Session getSession(Serializable id)
	{
		if (id == null) throw new NullPointerException("session id is empty");
		Session session = null;
		ShardedJedis jedis = null;
		try
		{
			jedis = shardedJedisPool.getResource();
			byte[] value = jedis.get(SerializeUtil
					.serialize(buildRedisSessionKey(id)));
			session = SerializeUtil.deserialize(value, Session.class);
		}
		catch (Exception e)
		{
			LOG.error("get shiro session error!", e);
		}
		finally
		{
			if (null != jedis)
			{
				shardedJedisPool.returnResource(jedis);
			}
		}
		return session;
	}

	@Override
	public Collection<Session> getAllSessions()
	{
		// TODO
		return null;
	}

	private String buildRedisSessionKey(Serializable sessionId)
	{
		return REDIS_SHIRO_SESSION + sessionId;
	}

	public ShardedJedisPool getShardedJedisPool()
	{
		return shardedJedisPool;
	}

	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool)
	{
		this.shardedJedisPool = shardedJedisPool;
	}
}
