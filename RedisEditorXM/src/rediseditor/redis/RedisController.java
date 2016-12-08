package rediseditor.redis;

import java.util.ArrayList;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import rediseditor.gui.DialogBoxes;

/**
 * Redis controller class that handles communication between UI and Redis
 * 
 * Uses Jedis 2.9 API to more easily communicate between Java and Redis
 * without having to re-implement every method used.
 * 
 * Handles things like fetching keys & values, as well as updating and
 * clearin data within Redis
 * 
 * @author theooswanditosw164
 */
public class RedisController {
	
	//Instance of jedis that will be used to communicate with Redis
	private static Jedis jedis_instance;
	
	/**
	 * Constructor of the controller instance, creates and jedis instance 
	 * according to address parameter passed into constructor
	 * 
	 * @param address	that holds where redis location is ("localhost") for local and 
	 * 					string needed for remote connection if needed
	 */
	public RedisController(String address){
		try {
			jedis_instance = new Jedis(address);
			jedis_instance.ping();
			
		} catch (JedisConnectionException e){
			//Error displayed to user if could not connect to Redis with the given
			//address and program closes
			
			if (e.getMessage().equals("java.net.UnknownHostException: " + address)){
				DialogBoxes.displayErrorMessage("Could not connect to: " + address);
			}
			System.exit(1);
		}
	}
	
	/**
	 * Method that calls KEYS * on Redis server and GET [KEY] for each
	 * key to return all key-value pairs held in Redis
	 * 
	 * @returns an ArrayList of String arrays holding key-vlaue pair data
	 * 			Each String array holds {key, value}
	 */
	public ArrayList<String[]> getKeyValuePairData(){
		Set<String> list_of_keys = jedis_instance.keys("*");
		ArrayList<String[]> array_to_return = new ArrayList<String[]>();
		
		for(String key: list_of_keys){
			array_to_return.add(new String[]{key, jedis_instance.get(key)});
		}
		return array_to_return;
	}
	
	/**
	 * Method that gets all the keys held in Redis
	 * 
	 * @returns a set of all keys in Redis
	 */
	public Set<String> getKeys(){
		return jedis_instance.keys("*");
	}
	
//	/**
//	 * Method that accepts a key and deletes its key-pair from Redis after user
//	 * approves the deletion
//	 * 
//	 * @param key to delete
//	 * @return	boolean depending if delete was done or not
//	 */
//	public boolean deletePrompt(Object key){
//		if (DialogBoxes.displayWarningPrompt("Are you sure you want to delete ["+key.toString()+"] ?")){
//			delete(key);
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * Method that deletes a key-value pair without prompt from Redis
	 * 
	 * @param key to delete
	 */
	public void delete(Object key){
		jedis_instance.del(key.toString());
	}
	
	/**
	 * Method that adds a key-value pair into Redis
	 * 
	 * @param key	to be entered
	 * @param value to be assigned to the key parameter
	 */
	public void add(Object key, Object value){
		jedis_instance.set(key.toString(), value.toString());
	}
	
	/**
	 * Method that deletes all key-value pairs from Redis
	 */
	public void flushall(){
		jedis_instance.flushAll();
	}
}
