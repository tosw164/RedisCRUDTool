package rediseditor.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rediseditor.redis.RedisController;

public class MainEditorClass extends JPanel {

	private JTable table;
	private JScrollPane scroll_pane;
	private JTextField value_field;
	private JTextField key_field;
	private TableRowSorter<TableModel> sorter;
	private static RedisController controller;
	private static JFrame frame;


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
		frame.setPreferredSize(new Dimension(1000, 800));
		frame.add(new MainEditorClass());
		frame.pack();
		frame.setVisible(true);
	}
	
	private void yesnobuttonpress(boolean save){
		if (table.isEditing()){
			if (save){
				triggerSet(true);
			} else {
				triggerSet(false);
			}
		}
	}

	public MainEditorClass() {
		controller = RedisController.getInstance(""); //TODO delete later

		Box left_button_cluster = Box.createVerticalBox();
		
		createPadding(left_button_cluster, 1, 50);

		left_button_cluster.add(setupSaveButton());
		
		JButton cancel_button = new JButton("cancel");
		cancel_button.setMaximumSize(new Dimension(300,30));
		cancel_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("cancel");
				yesnobuttonpress(false);
			}
		});
		left_button_cluster.add(cancel_button);
		
		//TODO should probably create dedicated refresh table button here
		
		createPadding(left_button_cluster, 1, 50);
		
		left_button_cluster.add(setupRefreshButtton());
		
		createPadding(left_button_cluster, 1, 30);
		
		left_button_cluster.add(new JLabel("Key:"));
		left_button_cluster.add(setupKeyTextField());
		
		createPadding(left_button_cluster, 1, 5);
		
		left_button_cluster.add(new JLabel("Value:"));
		left_button_cluster.add(setupValueTextField());
		
		createPadding(left_button_cluster, 1, 5);

		Box add_clear_cluster = Box.createHorizontalBox();
		add_clear_cluster.add(setupAddButton());
		createPadding(add_clear_cluster, 5, 1);
		add_clear_cluster.add(setupClearButton());

		left_button_cluster.add(add_clear_cluster);

		createPadding(left_button_cluster, 1, 30);

		left_button_cluster.add(setupDeleteButton());

		createPadding(left_button_cluster, 1, 70);

		left_button_cluster.add(setupCloseButton());


		setLayout(new BorderLayout());
		add(left_button_cluster, BorderLayout.WEST);
		add(setupKeyValueList(), BorderLayout.CENTER);
	}
	
	private void refreshTable(){
		this.remove(scroll_pane);
		add(setupKeyValueList(), BorderLayout.CENTER);
		frame.pack();
	}
	
	private void createPadding(Box source, int width, int height){
		source.add(Box.createRigidArea(new Dimension(width, height)));
	}

	private JButton setupSaveButton(){
		// TODO haven't done.
		JButton save_button = new JButton("save");
		save_button.setMaximumSize(new Dimension(300,30));
		save_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("save");
				yesnobuttonpress(true);
			}
		});

		return save_button;
	}

	private JButton setupRefreshButtton(){
		JButton refresh_button = new JButton("Refresh Table");
		refresh_button.setMaximumSize(new Dimension(300,30));
		refresh_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable();
			}
		});

		return refresh_button;
	}

	private Component setupKeyTextField() {
		key_field = new JTextField();
		key_field.setMaximumSize(new Dimension(300, 30));
		return key_field;
	}

	private Component setupValueTextField() {
		value_field = new JTextField();
		value_field.setMaximumSize(new Dimension(300, 30));
		return value_field;
	}

	private JButton setupAddButton(){
		JButton add_button = new JButton("Add");
		add_button.setMaximumSize(new Dimension(100, 30));
		add_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addButtonLogic();
				clearTextFields();
				refreshTable();
			}
		});

		return add_button;
	}
	
	private void addButtonLogic(){
		String key_entered = key_field.getText();
		String value_entered = value_field.getText();
		
		Set<String> list_of_keys = controller.getKeys();
		boolean key_already_present = false;
		for(String key: list_of_keys){
			if (key_entered.equals(key)){
				key_already_present = true;
			}
		}
		if (key_already_present){
			boolean to_overwrite = DialogBoxes.displayWarningPrompt("Key already exists, do you want to overwrite?");
			if (to_overwrite){
				controller.add(key_entered, value_entered);
			}
		} else { //Key not already present in database
			if (DialogBoxes.displayConfirmationPrompt("Are you sure you want to add this key value pair?")){
				controller.add(key_entered, value_entered);
			}
		}
	}
	
	private void clearTextFields(){
		key_field.setText("");
		value_field.setText("");
	}

	private Component setupClearButton() {
		JButton clear_button = new JButton("Clear");
		clear_button.setMaximumSize(new Dimension(100, 30));
		clear_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearTextFields();
			}
		});

		return clear_button;
	}

	private JButton setupDeleteButton(){
		JButton delete_button = new JButton("Delete Selected");
		delete_button.setMaximumSize(new Dimension(300,30));
		delete_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int index_selected = table.getSelectedRow();
				if (index_selected != -1){
					String key_to_remove = (String) table.getValueAt(table.getSelectedRow(), 0);
					if ( controller.delete(key_to_remove) ){
						refreshTable();
					}
				} 				
			}
		});

		return delete_button;
	}


	private JButton setupCloseButton(){
		JButton close_button = new JButton("close");
		close_button.setMaximumSize(new Dimension(300,30));
		close_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		return close_button;
	}
	
	private TableModel table_model;

	private JScrollPane setupKeyValueList(){
		String[] table_headings = {"Key", "Value"};
		int row_count = 0;

		table_model = new DefaultTableModel(table_headings, row_count){
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

		table = new JTable(table_model);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				System.out.println("row:" + table.rowAtPoint(e.getPoint()) + "col:" + table.columnAtPoint(e.getPoint()));
			}
		});
		
		for (String[] s: controller.getKeyValuePairData()){
			((DefaultTableModel) table_model).addRow(s);
		}

		table.setModel(table_model);

		//For ordering
		sorter = new TableRowSorter<TableModel>(table_model);
		table.setRowSorter(sorter);
		ArrayList<RowSorter.SortKey> key = new ArrayList<RowSorter.SortKey>();
		key.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));//default is ascending order of first column i.e. alphabetical order of words
		sorter.setSortKeys(key);
		sorter.sort();

		table.getTableHeader().setReorderingAllowed(false);

		//Alignment for the cells http://stackoverflow.com/a/7433758
		DefaultTableCellRenderer alignment_renderer = new DefaultTableCellRenderer();
		alignment_renderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(String.class, alignment_renderer);
		table.setDefaultRenderer(Integer.class, alignment_renderer);

		//adds scroll pane to table to panel
		scroll_pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		scroll_pane.setVisible(true);
		scroll_pane.setMinimumSize(new Dimension(550, 400));

		return scroll_pane;

	}
	public void triggerSet(boolean something){
		if (something){
			table.getCellEditor().stopCellEditing();
		} else {
			int column =table.getSelectedColumn();
			int row=table.getSelectedRow();
			Object old_value=table.getValueAt(row, column);
			table.getCellEditor().stopCellEditing();
			table.setValueAt(old_value, row, column);
		}
	}

}
