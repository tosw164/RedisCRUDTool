package rediseditor.redis;

import java.util.ArrayList;
import java.util.Set;

import javax.print.attribute.standard.DialogTypeSelection;
import javax.swing.JOptionPane;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import rediseditor.gui.DialogBoxes;

public class RedisController {
	private static RedisController controller_instance;
	private static Jedis jedis_instance;
	
	public static RedisController getInstance(String address){
		if (address.equals("")){
			controller_instance = new RedisController("localhost");
		}
		return controller_instance;
	}
	
	private RedisController(String address){
		newInstance(address);
	}
	
	public static void newInstance(String connection_address){
		try {
			jedis_instance = new Jedis(connection_address);
			jedis_instance.ping();
		} catch (JedisConnectionException e){
			if (e.getMessage().equals("java.net.UnknownHostException: " + connection_address)){
				System.out.println("sadface");
				DialogBoxes.displayErrorMessage("Could not connect to: " + connection_address);
			}
			System.exit(1);
		}
		System.out.println("Connection to server sucessfully");
	}
	
	public void ping() {
		System.out.println("Server ping results: " + jedis_instance.ping());
	}
	
	public ArrayList<String[]> getKeyValuePairData(){
		Set<String> list_of_keys = jedis_instance.keys("*");
		ArrayList<String[]> array_to_return = new ArrayList<String[]>();
		
		for(String key: list_of_keys){
			array_to_return.add(new String[]{key, jedis_instance.get(key)});
			System.out.println(key + "\t" + jedis_instance.get(key));
		}
		return array_to_return;
	}
	
	public boolean delete(String key){
		if (DialogBoxes.displayWarningPrompt("Are you sure you want to delete ["+key+"] ?")){
			jedis_instance.del(key);
			return true;
		}
		return false;
	}
	
	public void add(String key, String value){
		jedis_instance.set(key, value);
	}
	
}
