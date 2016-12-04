import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JList;
import javax.swing.JButton;

public class TestEverything extends JFrame {

	private JPanel ui_panel;

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

	/**
	 * Create the frame.
	 */
	public TestEverything() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(800, 500);
		setResizable(false);
		
		
		ui_panel = new JPanel();
		setContentPane(ui_panel);
		ui_panel.setLayout(null);
		
		JList key_value_JList= new JList();
		key_value_JList.setSize(550, 400);
		key_value_JList.setLocation(50,50);
		ui_panel.add(key_value_JList);
		
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
		
		JButton add_button = new JButton("Add");
		add_button.setSize(100,50);
		add_button.setLocation(650, 150);
		ui_panel.add(add_button);
		add_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("add");
			}
		});
		
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

	private void setupTable(){
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
		
	}

}
