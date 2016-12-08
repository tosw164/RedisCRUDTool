package rediseditor.logic;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


/**
 * Class that handles logic to getting all key-value pairs from Redis and creating
 * a JScrollPane containing a JTable paired with a TableModel that contains the 
 * key-value pairs. 
 * Does this on another thread so the GUI doens't hang when processing a lot of data.
 * The class once done will add this JScrollPane to the JPanel to display the table.
 * 
 * Extends Swingworker<Void, Object[]>
 * doInBackground() returns a Void type
 * Process/Publish accepts/passes a List<Object[]> of chunks to process
 * 	-This packaged Object array contains the JTable object, TableModel object and 
 *   JScrollPane object that will be added to JPanel
 * 
 * @author theooswanditosw164
 */
final class RedisFetcherWorker extends SwingWorker<Void, Object[]> {
	
	private final RedisEditor redisEditor;//Instance of the parent so that it can access various fields

	/**
	 * Constructor for the class, accepts and sets the @param redisEditor instance to the
	 * class that will be creating instances of this object
	 */
	RedisFetcherWorker(RedisEditor redisEditor) {
		this.redisEditor = redisEditor;
	}

	/**
	 * Overridden doInBackground() method, contains all of the logic that does 
	 * the importing of data and creation of Object[] package.
	 */
	@Override
	protected Void doInBackground() throws Exception {
		
		String[] table_headings = {"Key", "Value"}; //Column headers for table
		int row_count = 0; //Set initial number of rows

		//Create the model of the worker based on variables above
		TableModel worker_model = new DefaultTableModel(table_headings, row_count){
			
			//Sets all cells to be editable, can double click to edit contents
			public boolean isCellEditable(int row, int column){
				return true;
			}

			//Sets all data type of columns to string
			public Class getColumnClass(int column){
				switch (column) {
				default:
					return String.class;
				}
			}
		};

		//Create the JTable object based on model defined above
		JTable worker_table = new JTable(worker_model);

		//Get contents of Redis using RedisController and adds it to the table model
		for (String[] s: RedisEditor.controller.getKeyValuePairData()){
			((DefaultTableModel) worker_model).addRow(s);
		}

		//Sets the model of table
		worker_table.setModel(worker_model);
		
		//Creates preference width of each column so that contents of each can be visisted (TODO hardcoded)
		worker_table.getColumnModel().getColumn(0).setPreferredWidth(300);
		worker_table.getColumnModel().getColumn(1).setPreferredWidth(900);


		//For ordering by clicking column headers
		TableRowSorter<TableModel> worker_sorter = new TableRowSorter<TableModel>(worker_model);
		worker_table.setRowSorter(worker_sorter);
		ArrayList<RowSorter.SortKey> key = new ArrayList<RowSorter.SortKey>();
		key.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
		worker_sorter.setSortKeys(key);
		worker_sorter.sort();

		//Stops reordering of columns by dragging them around
		worker_table.getTableHeader().setReorderingAllowed(false);

		//Centre alignment for the cells http://stackoverflow.com/a/7433758
		DefaultTableCellRenderer alignment_renderer = new DefaultTableCellRenderer();
		alignment_renderer.setHorizontalAlignment(JLabel.CENTER);
		worker_table.setDefaultRenderer(String.class, alignment_renderer);
		worker_table.setDefaultRenderer(Integer.class, alignment_renderer);

		//adds scroll pane to table to panel
		JScrollPane worker_scrollpane = new JScrollPane(worker_table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		worker_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		worker_scrollpane.setVisible(true);
		worker_scrollpane.setMinimumSize(new Dimension(550, 400));
		
		//Package the Object array and publish it so process() can intercept and process objects
		Object[] component_package = new Object[]{worker_table, worker_model, worker_scrollpane};
		publish(component_package);
		return null;
	}

	/**
	 * Overridden process() method used in this case to accept 3 objects (JTable, TableModel, JScrollPane)
	 * which are set in the main program, used to refresh contents of the JTable displayed to user of
	 * the contents of Redis.
	 */
	@Override
	protected void process(List<Object[]> chunks) {
		
		//Sets various fields of RedisEditor to ones passed by doInBackground() and refreshing the frame
		//once these fields have been set and JScrollPane added to JPanel
		this.redisEditor.table = (JTable) chunks.get(chunks.size() -1)[0];
		this.redisEditor.table_model = (DefaultTableModel) chunks.get(chunks.size() - 1)[1];
		this.redisEditor.scroll_pane = (JScrollPane) chunks.get(chunks.size() - 1)[2];
		this.redisEditor.add(this.redisEditor.scroll_pane, BorderLayout.CENTER);
		RedisEditor.frame.pack();
	}
}