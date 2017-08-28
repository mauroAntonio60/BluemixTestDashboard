/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package wasdev.sample.store;


import java.sql.Statement;

import java.util.Collection;
import java.util.Set;

import com.google.gson.JsonObject;

import redis.clients.jedis.Jedis;

import wasdev.sample.Visitor;

public class RedisVisitorStore implements VisitorStore {

	private static Jedis jedis = null;
	// redis://admin:UEIKYSDUFFWEUEGN@sl-us-south-1-portal.5.dblayer.com:20606
	// redis-cli -h sl-us-south-1-portal.5.dblayer.com -p 20606 -a
	// UEIKYSDUFFWEUEGN

	public RedisVisitorStore() {
		jedis = createClient();

		if (jedis != null) {
			
		}
	}

	public Jedis getConnection() {
		return jedis;
	}

	private static Jedis createClient() {
		String url1;

		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the
			// credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for redis.
			JsonObject redisCredentials = VCAPHelper.getCloudCredentials("redis");
			if (redisCredentials == null) {
				System.out.println("No Redis service bound to this application");
				return null;
			}

			/**
			 * { "db_type": "redis", "maps": [], "name":
			 * "bmx_re_17aug28t0235_c45_8633", "uri_cli": "redis-cli -h
			 * bluemix-sandbox-dal-9-portal.6.dblayer.com -p 29557 -a
			 * UFMVTSJEVIFFQPKQ", "deployment_id": "59a38175f067f70018016249",
			 * "uri":
			 * "redis://admin:UFMVTSJEVIFFQPKQ@bluemix-sandbox-dal-9-portal.6.dblayer.com:29557"
			 * }
			 */

			System.out.println(redisCredentials);
			String url = redisCredentials.get("uri").getAsString();
			System.out.println("got redis credentials from VCAP: " + url);

			// url manipulation - getting a User, the password, a link
			// "uri":
			// "redis://admin:UFMVTSJEVIFFQPKQ@bluemix-sandbox-dal-9-portal.6.dblayer.com:29557"

			url1 = url.substring(8); // removing "redis://"
			// admin:UFMVTSJEVIFFQPKQ@bluemix-sandbox-dal-9-portal.6.dblayer.com:29557

			/**
			 * JedisPool pool = new JedisPool(new JedisPoolConfig(), urlHost,
			 * Integer.parseInt(port), Protocol.DEFAULT_TIMEOUT, password);
			 */
		} else {
			System.out.println("Running locally. Looking for credentials in redis.properties");
			url1 = VCAPHelper.getLocalProperties("redis.properties").getProperty("redis_url");

			if (url1 == null || url1.length() == 0) {
				System.out.println("To use a database, set the redis url in src/main/resources/redis.properties");
				return null;
			}
		}

		try {
			// search for : and @ to obtain user and password
			// admin:UFMVTSJEVIFFQPKQ@
			int indexEndUser = url1.indexOf(":");
			int indexEndPassword = url1.indexOf("@");
			// user password url port
			// admin:UFMVTSJEVIFFQPKQ@bluemix-sandbox-dal-9-portal.6.dblayer.com:29557
			String user = url1.substring(0, indexEndUser);
			String password = url1.substring(indexEndUser + 1, indexEndPassword);
			String urlHostPort = url1.substring(indexEndPassword + 1);
			int indexEndHost = urlHostPort.indexOf(":");
			String urlHost = urlHostPort.substring(0, indexEndHost);
			String port = urlHostPort.substring(indexEndHost + 1, urlHostPort.length());

			System.out.println(user);
			System.out.println(password);
			System.out.println(urlHost);
			System.out.println(port);

			jedis = new Jedis(urlHost, Integer.parseInt(port));
			jedis.auth(password);
			System.out.println("Connected to Redis");

			return jedis;
		} catch (Exception e) {
			System.out.println("Unable to connect to database");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	/**
	 * not used for Redis
	 */
	public Object getDB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Visitor> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Visitor get(String id) {

		// System.out.println("starting reading : " + name);
		Visitor vi = new Visitor();
		vi.set_id(id);

		try {
			vi.setName(jedis.get("name"+id));
			// System.out.println("select done ");
		} catch (Exception sqle) {
			System.out.println("Could not read");
		}
		return vi;
	}

	@Override
	public Visitor persist(Visitor vi) {

		try {
			jedis.set("name"+vi.get_id(), vi.getName());
		} catch (Exception e) {
			System.out.println("Could not persist");
			e.printStackTrace();
		}
		return vi;
	}

	@Override
	public Visitor update(String id, Visitor vi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Visitor push(Visitor vi) {
		jedis.lpush("queue#tasks", vi.getName());
		return vi;
	}

	@Override
	public Visitor pop(String id) {
		Visitor vi = new Visitor();
		vi.set_id(id);
		vi.setName(jedis.rpop("queue#tasks"));
		return vi;
	}

	@Override
	public Visitor testSets(Visitor vi) {
		jedis.sadd("nicknames", "nickname#1");
		jedis.sadd("nicknames", "nickname#2");
		jedis.sadd("nicknames", "nickname#1");
		jedis.sadd("nicknames", vi.getName());
		 
		Set<String> nicknames = jedis.smembers("nicknames");
		
		boolean exists = jedis.sismember("nicknames", vi.getName());
		vi.setName(vi.getName() + " size set " + nicknames.size() + " + it is there " + exists );
		return vi;
	}

	

}
