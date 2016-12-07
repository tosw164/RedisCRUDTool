package rediseditor.logic;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rediseditor.gui.DialogBoxes;
import rediseditor.gui.UiInitialise;
import rediseditor.redis.RedisController;

public class MainEditorClass extends JPanel {

	private static JFrame frame;
	private static RedisController controller;

	private JButton refresh_button;
	private JButton save_button;
	private JButton cancel_button;
	private JButton addrow_button;
	private JButton deleterow_button;
	private JButton connect_button;
	private JButton close_button;

	private JTable table;
	private DefaultTableModel table_model;
	private JScrollPane scroll_pane;
	private TableRowSorter<TableModel> sorter;



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
		frame.add(new MainEditorClass());
		frame.pack();
		frame.setVisible(true);
	}

	public MainEditorClass() {
		controller = RedisController.getInstance(""); //TODO delete later don't always want localhost

		initialise_button_components();

		//Initialise container for button cluster on the left
		Box left_button_cluster = Box.createVerticalBox();

		//Setup refresh table button at the top with 50 pixel padding below
		left_button_cluster.add(refresh_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 50));

		//Setup save and cancel buttons for when user is editing cell
		left_button_cluster.add(save_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 10));
		left_button_cluster.add(cancel_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 50));

		left_button_cluster.add(addrow_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 10));
		left_button_cluster.add(deleterow_button);
		left_button_cluster.add(UiInitialise.createPadding(1, 70));

		left_button_cluster.add(connect_button);
		left_button_cluster.add(close_button);


		setLayout(new BorderLayout());
		add(left_button_cluster, BorderLayout.WEST);
		add(setupKeyValueList(), BorderLayout.CENTER);
	}

	private void initialise_button_components(){
		refresh_button = UiInitialise.createRefreshButton();
		refresh_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshButtonLogic();
			}
		});

		save_button = UiInitialise.createSaveButton();
		save_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveButtonLogic();
			}
		});

		cancel_button = UiInitialise.createCancelButton();
		cancel_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelButtonLogic();
			}
		});

		addrow_button = UiInitialise.createAddRowButton();
		addrow_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRowButtonLogic();
			}
		});

		deleterow_button = UiInitialise.createDeleteRowButton();
		deleterow_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteRowButtonLogic();
			}
		});

		connect_button = UiInitialise.createConnectButton();
		connect_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectButtonLogic();
			}
		});

		close_button = UiInitialise.createCloseButton();
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
		System.out.println("save");
		yesnobuttonpress(true);
	}

	private void cancelButtonLogic(){
		yesnobuttonpress(false);
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
	
	private void connectButtonLogic(){
		
	}

	private void closeButtonLogic(){
		System.exit(0);
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
		JTextField cell = new JTextField();
		final TableCellEditor cell_editor = new DefaultCellEditor(cell);
		table.getColumnModel().getColumn(0).setCellEditor(cell_editor);
		table.getColumnModel().getColumn(1).setCellEditor(cell_editor);
		
		InputMap input_map = cell.getInputMap(JComponent.WHEN_FOCUSED);
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "PressSaveButton");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "PressCancelButton");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "DoNothing");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "DoNothing");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), "DoNothing");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DoNothing");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), "DoNothing");

		ActionMap action_map = cell.getActionMap();
		action_map.put("PressSaveButton", new SaveButtonAction());
		action_map.put("PressCancelButton", new CancelButtonAction());
		action_map.put("DoNothing", new DoNothingAction());


		//		editKeyBindings();

		for (String[] s: controller.getKeyValuePairData()){
			((DefaultTableModel) table_model).addRow(s);
		}

		table.setModel(table_model);
		table.getColumnModel().getColumn(0).setPreferredWidth(300);
		table.getColumnModel().getColumn(1).setPreferredWidth(900);


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

		int column =table.getSelectedColumn();
		int row=table.getSelectedRow();
		Object old_key = table.getValueAt(row, 0);
		Object old_value = table.getValueAt(row, 1);


		//Save button pressed
		if (something){
			//TODO determine which cell edited logic here



			//Key edited
			if(column == 0){
				if(old_key.toString().equals("")){
					table.getCellEditor().stopCellEditing();
					controller.add(table.getValueAt(row, column), old_value);

				} else {
					String prompt_text="Are you sure you want to rename " + old_key + "?";
					if (DialogBoxes.displayWarningPrompt(prompt_text)){

						table.getCellEditor().stopCellEditing();
						controller.add(table.getValueAt(row, column), old_value);
						controller.delete(old_key);
					} else {
						triggerSet(false);
					}
				}
				//Value edited
			} else {
				table.getCellEditor().stopCellEditing();
				controller.add(old_key, table.getValueAt(row, column));
			}


			//Cancel button pressed
		} else {	

			//Save current cell contents and overwrite with old value
			table.getCellEditor().stopCellEditing();
			if (column == 0){
				table.setValueAt(old_key, row, column);
			} else  {
				table.setValueAt(old_value, row, column);
			}
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

	private class SaveButtonAction extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("yes");
			yesnobuttonpress(true);
		}
	}
	
	private class CancelButtonAction extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("no");
			yesnobuttonpress(false);
		}
	}
	
	private class DoNothingAction extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("nill");
		}
	}
}
