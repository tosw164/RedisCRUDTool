package rediseditor.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingWorker;

import rediseditor.gui.DialogBoxes;

/**
 * Class that handles logic of publishing the contents of the JTable to Redis
 * Does this on a separate thread so it doens't cause the GUI to hang if there
 * is a lot of data to process and export to Redis.
 * 
 * Extends Swingworker<Void, String>
 * doInBackground() returns a Void type
 * Process/Publish accepts/passes a List<String> of chunks to process
 * 
 * @author theooswanditosw164
 */
final class RedisPublishWorker extends SwingWorker<Void, String> {
	
	private final RedisEditor redisEditor; //Instance of the parent so that it can access various fields

	/**
	 * Constructor for the class, accepts and sets the @param redisEditor instance to the
	 * class that will be creating instances of this object
	 */
	RedisPublishWorker(RedisEditor redisEditor) {
		this.redisEditor = redisEditor;
	}

	/**
	 * Overridden doInBackground() method, contains all of the logic that does 
	 * the export from the JTable into Redis
	 */
	@Override
	protected Void doInBackground() throws Exception {
		
		//Initialise instances of lists used to hold and validate JTable data
		ArrayList<String[]> table_contents = new ArrayList<String[]>(); //Will hold filtered data to be moved to Redis
		Set<String> keys_visited = new HashSet<String>(); //Set used to check for duplicate keys. Holds all processed key names
		
		//Go through each row in the JTable
		for (int row = 0; row < this.redisEditor.table_model.getRowCount(); row++){

			//Variable to hold currently processed keys and values
			String current_key = this.redisEditor.table.getValueAt(row, 0).toString();
			String current_value = this.redisEditor.table.getValueAt(row, 1).toString();

			//Check if current processed keys already visited (duplicate key found)
			if (keys_visited.contains(current_key)){
				
				//Call publish (print error message) and no nothing (no export)
				publish("something");
				return null;
				
			//If current processed key not already visited (not a duplicate key) and not nothing
			} else if (!current_key.equals("")){
				
				//Therefore empty keys filtered out (doesn't matter if value not empty)
				//Add key-value pair to table_contents and add key to visited list
				keys_visited.add(current_key);
				table_contents.add(new String[]{ current_key, current_value });
			}
		}

		//Remove all data from Redis
		RedisEditor.controller.flushall();
		
		//Port all data contained in table_contents after filter into Redis
		for (String[] entry : table_contents){
			RedisEditor.controller.add(entry[0], entry[1]);
		}
		return null;
	}

	/**
	 * Overridden process() method used in this case to intercept processing of 
	 * the JTable data when duplicate key found. Will display error message before
	 * just going back to UI, without committing any changes
	 */
	@Override
	protected void process(List<String> chunks) {
		DialogBoxes.displayErrorMessage("Duplicate key found");
	}
}