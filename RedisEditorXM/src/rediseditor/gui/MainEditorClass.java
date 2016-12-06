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
	private TableRowSorter<TableModel> sorter;
	private DefaultTableModel table_model;
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

	public MainEditorClass() {
		controller = RedisController.getInstance(""); //TODO delete later don't always want localhost


		Box left_button_cluster = Box.createVerticalBox();

		left_button_cluster.add(setupRefreshButtton());
		createPadding(left_button_cluster, 1, 50);

		left_button_cluster.add(setupSaveButton());
		createPadding(left_button_cluster, 1, 10);
		left_button_cluster.add(setupCancelButton());
		createPadding(left_button_cluster, 1, 50);
		
		
		left_button_cluster.add(setupAddButton());
		createPadding(left_button_cluster, 1, 10);
		left_button_cluster.add(setupDeleteButton());
		createPadding(left_button_cluster, 1, 70);

		left_button_cluster.add(setupConnectButton());
		left_button_cluster.add(setupCloseButton());


		setLayout(new BorderLayout());
		add(left_button_cluster, BorderLayout.WEST);
		add(setupKeyValueList(), BorderLayout.CENTER);
	}
	
	
	private void createPadding(Box source, int width, int height){
		source.add(Box.createRigidArea(new Dimension(width, height)));
	}
	
	private JButton setupRefreshButtton(){
		JButton refresh_button = new JButton("Refresh Table");
//		refresh_button.setMaximumSize(new Dimension(300,30));
		refresh_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable();
			}
		});

		return refresh_button;
	}

	private JButton setupSaveButton(){
		JButton save_button = new JButton("save");
//		save_button.setMaximumSize(new Dimension(300,30));
		save_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("save");
				yesnobuttonpress(true);
			}
		});

		return save_button;
	}
	
	private JButton setupCancelButton(){
		JButton cancel_button = new JButton("cancel");
//		cancel_button.setMaximumSize(new Dimension(300,30));
		cancel_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("cancel");
				yesnobuttonpress(false);
			}
		});
		return cancel_button;
	}
	
	private JButton setupAddButton(){
		JButton add_button = new JButton("Add New Row");
//		add_button.setMaximumSize(new Dimension(300, 30));
		add_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
//				refreshTable();
				System.out.println("add");
				table_model.addRow(new Object[]{"", ""});
			}
		});

		return add_button;
	}

	private JButton setupDeleteButton(){
		JButton delete_button = new JButton("Delete Selected");
//		delete_button.setMaximumSize(new Dimension(300,30));
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
	
	//TODO Make this button better (actually do something)
	
	private JButton setupConnectButton(){
		JButton connect_button = new JButton("Connect");
		return connect_button;
	}


	private JButton setupCloseButton(){
		JButton close_button = new JButton("close");
//		close_button.setMaximumSize(new Dimension(300,30));
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
	
	
	//------------------------------------------------------------------------------------------
	//LOGIC SECTION
	//------------------------------------------------------------------------------------------
	
	//TODO rename this
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
	
	//TODO multithread this
	private void refreshTable(){
		this.remove(scroll_pane);
		add(setupKeyValueList(), BorderLayout.CENTER);
		frame.pack();
	}
	
	//TODO rename this
	private void yesnobuttonpress(boolean save){
		if (table.isEditing()){
			if (save){
				triggerSet(true);
			} else {
				triggerSet(false);
			}
		}
	}
}
