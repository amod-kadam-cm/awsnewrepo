package com.cloudmanthan.aws.awsworkshop.elasticache;

import redis.clients.jedis.Jedis;

public class DemoUsingElastiCache {

	public static void main(String[] args) {
		
		//String JEDIS_SERVER_HOST = "demo-ecc-redis-001.5wnrpe.0001.aps1.cache.amazonaws.com";
		// Connect via NAT
		String JEDIS_SERVER_HOST ="13.126.72.203";
		int JEDIS_SERVER_PORT = 6379;
		Jedis jedis = new Jedis(JEDIS_SERVER_HOST,JEDIS_SERVER_PORT);
		
		jedis.connect();
		
		jedis.set("events/city/rome", "32,15,223,828");
		String cachedResponse = jedis.get("events/city/rome");
		
		

	}

}
