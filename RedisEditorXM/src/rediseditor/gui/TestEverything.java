package rediseditor.gui;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.midi.ControllerEventListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.sun.media.sound.ModelAbstractChannelMixer;

import redis.clients.jedis.Jedis;
import rediseditor.redis.RedisController;

public class TestEverything extends JFrame {

	private JPanel ui_panel;
	private JTable table;
	private JScrollPane scroll_pane;
	private static RedisController controller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestEverything frame = new TestEverything();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TestEverything() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(800, 500);
		setResizable(false);

		controller = RedisController.getInstance();
		
		setupJPanel();
	}

	private void setupJPanel(){
		ui_panel = new JPanel();
		setContentPane(ui_panel);

		ui_panel.setLayout(null);

		setupKeyValueList();
		setupConnectButton();
		setupAddButton();
		setupDeleteButton();
		setupUpdateButton();
		setupCloseButton();
		
	}

	private void setupKeyValueList(){
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

		table.getTableHeader().setReorderingAllowed(false);

		//Alignment for the cells http://stackoverflow.com/a/7433758
		DefaultTableCellRenderer alignment_renderer = new DefaultTableCellRenderer();
		alignment_renderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(String.class, alignment_renderer);
		table.setDefaultRenderer(Integer.class, alignment_renderer);

		//adds scroll pane to table to panel
		scroll_pane = new JScrollPane(table);
		ui_panel.add(scroll_pane);
		scroll_pane.setVisible(true);
		scroll_pane.setLocation(50,50);
		scroll_pane.setSize(550, 400);

	}

	private void setupConnectButton(){
		JButton connection_button = new JButton("Connect");
		connection_button.setSize(100,50);
		connection_button.setLocation(650, 50);
		ui_panel.add(connection_button);
		connection_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("connect");
			}
		});
	}

	private void setupAddButton(){
		JButton add_button = new JButton("Add");
		add_button.setSize(100,50);
		add_button.setLocation(650, 150);
		ui_panel.add(add_button);
		add_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("add");
				controller.ping();
			}
		});
	}

	private void setupDeleteButton(){
		JButton delete_button = new JButton("Delete");
		delete_button.setSize(100,50);
		delete_button.setLocation(650, 250);
		ui_panel.add(delete_button);
		delete_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("delete");
			}
		});
	}

	private void setupUpdateButton(){
		JButton update_button = new JButton("Update");
		update_button.setSize(100,50);
		update_button.setLocation(650, 350);
		ui_panel.add(update_button);
		update_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("update");
			}
		});
	}

	private void setupCloseButton(){
		JButton exit_button = new JButton("close");
		exit_button.setSize(50,50);
		exit_button.setLocation(750, 450);
		ui_panel.add(exit_button);
		exit_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

}
