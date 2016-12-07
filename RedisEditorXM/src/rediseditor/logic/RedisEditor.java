package rediseditor.logic;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import rediseditor.gui.UiInitialise;
import rediseditor.redis.RedisController;

/**
 * Class that contains main method
 * 
 * Creates all UI elements needed for the program to function
 * as well as their logic
 * 
 * @author theooswanditosw164
 * 
 * Credits to the Jedis team for creating API allowing for
 * simple communication with Redis
 * https://github.com/xetorthio/jedis
 */
public class RedisEditor extends JPanel {

	//Initialisation of fields used
	protected static JFrame frame; //Frame containing Panel
	protected static RedisController controller; //Controller instance that allows communiction with Redis

	private JButton refresh_button;		//Button that refreshes contents of Table with current Redis contents
	private JButton save_button;		//Button that commits all changes (contents of Table) to Redis
	private JButton addrow_button;		//Button that creates blank row to allow addition of new key-value pairs
	private JButton deleterow_button;	//Button that deletes a key-value pair after user confirmation
	private JButton close_button;		//Button that closes application

	protected JTable table;					//Table containing information
	protected DefaultTableModel table_model;//Model that table is based off
	protected JScrollPane scroll_pane;		//Panel that contains table & model that creates scrollbar as needed

	/**
	 * Main method that program is initialised from
	 * @param args	(not used)
	 */
	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					createGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * First method main() calls.
	 * Initialise the JFrame that all UI elements will be placed on.
	 * Sets up dimensions, and various properties of the JFrame
	 */
	private static void createGUI(){
		frame = new JFrame("RedisEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setPreferredSize(new Dimension(1200, 500));
		frame.add(new RedisEditor());
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Constructor for the JPanel. Performs UI placement onto JPanel
	 * 
	 * Initialises controller TODO currently localhost since used for testing purposes
	 * Creates button components and binds their respective logic functions to them
	 * Padding also created so buttons aren't all directly adjacent to each other
	 */
	public RedisEditor() {
		controller = new RedisController("localhost"); //TODO delete later don't always want localhost

		initialise_button_components();
		scroll_pane = new JScrollPane(new JTable()); //Initialise scrollpane so remove doesn't throw NPE
		
		Box left_button_cluster = Box.createVerticalBox();

		left_button_cluster.add(refresh_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 50));

		left_button_cluster.add(save_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 50));

		left_button_cluster.add(addrow_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 10));
		left_button_cluster.add(deleterow_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 70));

		left_button_cluster.add(close_button);

		setLayout(new BorderLayout()); 
		add(left_button_cluster, BorderLayout.WEST); //Adds button cluster to left side of window
		refreshTable(); //Generates and commits table of Redis contents to right side of window
	}

	/**
	 * Creates button components and sets class feilds based on their properties
	 * Also assigns each button to the method that holds logic for each
	 */
	private void initialise_button_components(){
		refresh_button = UiInitialise.createButton("Refresh");
		refresh_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshButtonLogic();
			}
		});

		save_button = UiInitialise.createButton("Save All");
		save_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveButtonLogic();
			}
		});

		addrow_button = UiInitialise.createButton("Add New Row");
		addrow_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRowButtonLogic();
			}
		});

		deleterow_button = UiInitialise.createButton("Delete Selected");
		deleterow_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteRowButtonLogic();
			}
		});

		close_button = UiInitialise.createButton("close");
		close_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeButtonLogic();
			}
		});
	}

	/**
	 * Handles logic for the refresh button
	 * Calls the multithreaded method to replace current table with contents of Redis
	 * Unsaved changes will not be commited and lost when pressing refresh
	 */
	private void refreshButtonLogic(){
		refreshTable();
	}

	/**
	 * Handles logic for the save button
	 * Makes sure that no cell is being edited right now (commits changes
	 * if they are still being made when button is pressed) and then calls
	 * multithreaded worker to publish contents of table to Redis
	 */
	private void saveButtonLogic(){
		if (table.isEditing()){
			table.getCellEditor().stopCellEditing();
		}
		new RedisPublishWorker(this).execute();
	}

	/**
	 * Handles logic for the add new row button
	 * Adds a blank row to the table that lets user add new key-value pair
	 */
	private void addRowButtonLogic(){
		table_model.addRow(new Object[]{"", ""});
	}

	/**
	 * Handles logic for the delete selected row button
	 * First checks if row is actually selected
	 * Then Prompts user to confirm deleting that row
	 * Then deletes from Redis and refreshes table
	 */
	private void deleteRowButtonLogic(){
		int index_selected = table.getSelectedRow();
		if (index_selected != -1){
			String key_to_remove = (String) table.getValueAt(table.getSelectedRow(), 0);
			if ( controller.deletePrompt(key_to_remove) ){
				refreshTable();
			}
		} 		
	}

	/**
	 * Handles logic for the close button
	 * Quits program
	 */
	private void closeButtonLogic(){
		System.exit(0);
	}

	/**
	 * Handles refreshing the table of Redis contents
	 * Removes current scroll pane from the panel
	 * Then calls multithreaded worker to fetch information from Redis to update table
	 */
	private void refreshTable(){
		this.remove(scroll_pane);
		new RedisFetcherWorker(this).execute();
	}
}
