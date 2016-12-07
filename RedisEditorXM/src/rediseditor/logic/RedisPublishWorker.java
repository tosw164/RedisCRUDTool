package rediseditor.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingWorker;

import rediseditor.gui.DialogBoxes;

final class RedisPublishWorker extends SwingWorker<Void, String> {
	/**
	 * 
	 */
	private final RedisEditor redisEditor;

	/**
	 * @param redisEditor
	 */
	RedisPublishWorker(RedisEditor redisEditor) {
		this.redisEditor = redisEditor;
	}

	@Override
	protected Void doInBackground() throws Exception {
		ArrayList<String[]> table_contents = new ArrayList<String[]>();
		Set<String> keys_visited = new HashSet<String>();
		for (int row = 0; row < this.redisEditor.table_model.getRowCount(); row++){

			String current_key = this.redisEditor.table.getValueAt(row, 0).toString();
			String current_value = this.redisEditor.table.getValueAt(row, 1).toString();

			if (keys_visited.contains(current_key)){
				publish("something");
				return null;
			} else if (!current_key.equals("")){
				keys_visited.add(current_key);
				table_contents.add(new String[]{ current_key, current_value });
			}
		}

		RedisEditor.controller.flushall();
		for (String[] entry : table_contents){
			RedisEditor.controller.add(entry[0], entry[1]);
		}
		return null;
	}

	@Override
	protected void process(List<String> chunks) {
		DialogBoxes.displayErrorMessage("Duplicate key found");
	}
}