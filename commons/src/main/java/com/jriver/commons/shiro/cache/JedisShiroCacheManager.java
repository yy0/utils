package com.jriver.commons.shiro.cache;

import org.apache.shiro.cache.Cache;

import redis.clients.jedis.ShardedJedisPool;

/**
 * 这里的name是指自定义relm中的授权/认证的类名加上授权/认证英文名字
 * 
 * @author river
 */
public class JedisShiroCacheManager implements ShiroCacheManager
{

	private ShardedJedisPool	shardedJedisPool;

	@Override
	public <K, V> Cache<K, V> getCache(String name)
	{
		return new JedisShiroCache<K, V>(name, shardedJedisPool);
	}

	@Override
	public void destroy()
	{
		shardedJedisPool.getResource().disconnect();
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
