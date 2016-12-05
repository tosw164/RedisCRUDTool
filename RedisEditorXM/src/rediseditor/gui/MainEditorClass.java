package rediseditor.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

	/**
	 * Launch the application.
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

	public static void createGUI(){
		frame = new JFrame("RedisEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setPreferredSize(new Dimension(1000, 800));
		frame.add(new MainEditorClass());
		frame.pack();
		frame.setVisible(true);
	}

	public MainEditorClass() {
		controller = RedisController.getInstance(""); //TODO delete later

		Box left_button_cluster = Box.createVerticalBox();
		
		left_button_cluster.add(setupConnectButton());
		
		createPadding(left_button_cluster, 1, 50);
		
		left_button_cluster.add(setupUpdateButton());
		
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

	private JButton setupConnectButton(){
		JButton connection_button = new JButton("Connect");
		connection_button.setMaximumSize(new Dimension(300,30));
		connection_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("connect");
			}
		});

		return connection_button;
	}

	private JButton setupUpdateButton(){
		JButton update_button = new JButton("Update Selected");
		update_button.setMaximumSize(new Dimension(300,30));
		update_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("update");
				refreshTable();
				
			}
		});

		return update_button;
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
				((DefaultTableModel)table.getModel()).addRow(new Object[]{"Hello", "World"});
				controller.ping();
				clearTextFields();
			}
		});

		return add_button;
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
				System.out.println(key_field.getText());
				System.out.println(value_field.getText());
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
						((DefaultTableModel)table.getModel()).removeRow(table.getSelectedRow());

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

	private JScrollPane setupKeyValueList(){
		String[] table_headings = {"Key", "Value"};
		int row_count = 0;

		DefaultTableModel table_model = new DefaultTableModel(table_headings, row_count){
			public boolean isCellEditable(int row, int column){
				return false;
			}

			public Class getColumnClass(int column){
				switch (column) {
				default:
					return String.class;
				}
			}
		};

		table = new JTable(table_model);

		for (String[] s: controller.getKeyValuePairData()){
			table_model.addRow(s);
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

}
