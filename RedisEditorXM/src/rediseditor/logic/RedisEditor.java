package rediseditor.logic;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import rediseditor.gui.UiInitialise;
import rediseditor.redis.RedisController;

public class RedisEditor extends JPanel {

	protected static JFrame frame;
	protected static RedisController controller;

	private JButton refresh_button;
	private JButton save_button;
	private JButton addrow_button;
	private JButton deleterow_button;
	private JButton close_button;

	protected JTable table;
	protected DefaultTableModel table_model;
	protected JScrollPane scroll_pane;

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

	private static void createGUI(){
		frame = new JFrame("RedisEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setPreferredSize(new Dimension(1200, 500));
		frame.add(new RedisEditor());
		frame.pack();
		frame.setVisible(true);
	}

	public RedisEditor() {
		controller = new RedisController("localhost"); //TODO delete later don't always want localhost

		initialise_button_components();
		scroll_pane = new JScrollPane(new JTable()); //Initialise scrollpane so remove doesn't throw NPE
		
		//Initialise container for button cluster on the left
		Box left_button_cluster = Box.createVerticalBox();

		//Setup refresh table button at the top with 50 pixel padding below
		left_button_cluster.add(refresh_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 50));

		//Setup save and cancel buttons for when user is editing cell
		left_button_cluster.add(save_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 50));

		left_button_cluster.add(addrow_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 10));
		left_button_cluster.add(deleterow_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 70));

		left_button_cluster.add(close_button);


		setLayout(new BorderLayout());
		add(left_button_cluster, BorderLayout.WEST);
		refreshTable();
	}

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

	private void refreshButtonLogic(){
		refreshTable();
	}

	private void saveButtonLogic(){
		if (table.isEditing()){
			table.getCellEditor().stopCellEditing();
		}
		new RedisPublishWorker(this).execute();
	}


	private void addRowButtonLogic(){
		table_model.addRow(new Object[]{"", ""});
	}

	private void deleteRowButtonLogic(){
		int index_selected = table.getSelectedRow();
		if (index_selected != -1){
			String key_to_remove = (String) table.getValueAt(table.getSelectedRow(), 0);
			if ( controller.deletePrompt(key_to_remove) ){
				refreshTable();
			}
		} 		
	}

	private void closeButtonLogic(){
		System.exit(0);
	}

	private void refreshTable(){
		this.remove(scroll_pane);
		new RedisFetcherWorker(this).execute();
	}
}
