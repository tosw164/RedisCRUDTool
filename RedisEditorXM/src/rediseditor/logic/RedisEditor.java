package rediseditor.logic;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rediseditor.gui.DialogBoxes;
import rediseditor.gui.UiInitialise;
import rediseditor.redis.RedisController;

public class RedisEditor extends JPanel {

	private static JFrame frame;
	private static RedisController controller;

	private JButton refresh_button;
	private JButton save_button;
	private JButton addrow_button;
	private JButton deleterow_button;
	private JButton close_button;

	private JTable table;
	private DefaultTableModel table_model;
	private JScrollPane scroll_pane;

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

	public static void createGUI(){
		frame = new JFrame("RedisEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setPreferredSize(new Dimension(1200, 500));
		frame.add(new RedisEditor());
		frame.pack();
		frame.setVisible(true);
	}

	public RedisEditor() {
		controller = RedisController.getInstance(""); //TODO delete later don't always want localhost

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
		new SwingWorker<Void, String>() {

			@Override
			protected Void doInBackground() throws Exception {
				ArrayList<String[]> table_contents = new ArrayList<String[]>();
				Set<String> keys_visited = new HashSet<String>();
				for (int row = 0; row < table_model.getRowCount(); row++){

					String current_key = table.getValueAt(row, 0).toString();
					String current_value = table.getValueAt(row, 1).toString();

					if (keys_visited.contains(current_key)){
						publish("something");
						return null;
					} else if (!current_key.equals("")){
						keys_visited.add(current_key);
						table_contents.add(new String[]{ current_key, current_value });
					}
				}

				controller.flushall();
				for (String[] entry : table_contents){
					controller.add(entry[0], entry[1]);
				}
				return null;
			}

			@Override
			protected void process(List<String> chunks) {
				DialogBoxes.displayErrorMessage("Duplicate key found");
			}	

		}.execute();
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
		
		new SwingWorker<Void, Object[]>(){

			@Override
			protected Void doInBackground() throws Exception {
				String[] table_headings = {"Key", "Value"};
				int row_count = 0;

				TableModel worker_model = new DefaultTableModel(table_headings, row_count){
					public boolean isCellEditable(int row, int column){
						return true;
					}

					public Class getColumnClass(int column){
						switch (column) {
						default:
							return String.class;
						}
					}
				};

				JTable worker_table = new JTable(worker_model);

				for (String[] s: controller.getKeyValuePairData()){
					((DefaultTableModel) worker_model).addRow(s);
				}

				worker_table.setModel(worker_model);
				worker_table.getColumnModel().getColumn(0).setPreferredWidth(300);
				worker_table.getColumnModel().getColumn(1).setPreferredWidth(900);


				//For ordering
				TableRowSorter<TableModel> worker_sorter = new TableRowSorter<TableModel>(worker_model);
				worker_table.setRowSorter(worker_sorter);
				ArrayList<RowSorter.SortKey> key = new ArrayList<RowSorter.SortKey>();
				key.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
				worker_sorter.setSortKeys(key);
				worker_sorter.sort();

				worker_table.getTableHeader().setReorderingAllowed(false);

				//Alignment for the cells http://stackoverflow.com/a/7433758
				DefaultTableCellRenderer alignment_renderer = new DefaultTableCellRenderer();
				alignment_renderer.setHorizontalAlignment(JLabel.CENTER);
				worker_table.setDefaultRenderer(String.class, alignment_renderer);
				worker_table.setDefaultRenderer(Integer.class, alignment_renderer);

				//adds scroll pane to table to panel
				JScrollPane worker_scrollpane = new JScrollPane(worker_table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				worker_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				worker_scrollpane.setVisible(true);
				worker_scrollpane.setMinimumSize(new Dimension(550, 400));
				
				Object[] component_package = new Object[]{worker_table, worker_model, worker_scrollpane};
				publish(component_package);
				return null;
			}

			@Override
			protected void process(List<Object[]> chunks) {
				table = (JTable) chunks.get(chunks.size() -1)[0];
				table_model = (DefaultTableModel) chunks.get(chunks.size() - 1)[1];
				scroll_pane = (JScrollPane) chunks.get(chunks.size() - 1)[2];
				add(scroll_pane, BorderLayout.CENTER);
				frame.pack();
			}
		}.execute();
	}
}
