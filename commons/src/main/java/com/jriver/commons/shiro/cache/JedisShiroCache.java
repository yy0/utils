package com.jriver.commons.shiro.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.jriver.commons.shiro.SerializeUtil;

/**
 * redis shiro cache class
 * 
 * @author river
 */
public class JedisShiroCache<K, V> implements Cache<K, V>
{
	private static final Logger		LOG					= LoggerFactory
																.getLogger(JedisShiroCache.class);

	private static final String		REDIS_SHIRO_CACHE	= "shiro-cache:";

	private final ShardedJedisPool	shardedJedisPool;

	private String					name;

	public JedisShiroCache(String name, ShardedJedisPool jedisManager)
	{
		this.name = name;
		this.shardedJedisPool = jedisManager;
	}

	/**
	 * 自定义relm中的授权/认证的类名加上授权/认证英文名字
	 */
	public String getName()
	{
		if (name == null) return "";
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) throws CacheException
	{
		byte[] byteKey = SerializeUtil.serialize(buildCacheKey(key));
		byte[] byteValue = new byte[0];
		ShardedJedis jedis = null;
		try
		{
			jedis = shardedJedisPool.getResource();
			byteValue = jedis.get(byteKey);
		}
		catch (Exception e)
		{
			LOG.error("get shiro cache error! key:{}", key, e);
		}
		finally
		{
			if (null != jedis)
			{
				shardedJedisPool.returnResource(jedis);
			}
		}
		return (V) SerializeUtil.deserialize(byteValue);
	}

	@Override
	public V put(K key, V value) throws CacheException
	{
		V previos = get(key);
		ShardedJedis jedis = null;
		try
		{
			jedis = shardedJedisPool.getResource();
			jedis.set(SerializeUtil.serialize(buildCacheKey(key)),
					SerializeUtil.serialize(value));
		}
		catch (Exception e)
		{
			LOG.error("put shiro cache error! key:{}, value:{}", new Object[] {
					key, value });
			LOG.error("", e);
		}
		finally
		{
			if (null != jedis)
			{
				shardedJedisPool.returnResource(jedis);
			}
		}
		return previos;
	}

	@Override
	public V remove(K key) throws CacheException
	{
		V previos = get(key);
		ShardedJedis jedis = null;
		try
		{
			jedis = shardedJedisPool.getResource();
			jedis.del(buildCacheKey(key));
		}
		catch (Exception e)
		{
			LOG.error("remove shiro cache error! key:{}", key);
			LOG.error("", e);
		}
		finally
		{
			if (null != jedis)
			{
				shardedJedisPool.returnResource(jedis);
			}
		}
		return previos;
	}

	@Override
	public void clear() throws CacheException
	{
		// TODO
	}

	@Override
	public int size()
	{
		if (keys() == null) return 0;
		return keys().size();
	}

	@Override
	public Set<K> keys()
	{
		// TODO
		return null;
	}

	@Override
	public Collection<V> values()
	{
		// TODO
		return null;
	}

	private String buildCacheKey(Object key)
	{
		return REDIS_SHIRO_CACHE + getName() + ":" + key;
	}

}
