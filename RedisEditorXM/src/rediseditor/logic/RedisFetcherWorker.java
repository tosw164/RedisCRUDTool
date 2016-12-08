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

final class RedisFetcherWorker extends SwingWorker<Void, Object[]> {
	/**
	 * Holds parent JPanel
	 */
	private final RedisEditor redisEditor;

	/**
	 * @param redisEditor
	 */
	RedisFetcherWorker(RedisEditor redisEditor) {
		this.redisEditor = redisEditor;
	}

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

		for (String[] s: RedisEditor.controller.getKeyValuePairData()){
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
		this.redisEditor.table = (JTable) chunks.get(chunks.size() -1)[0];
		this.redisEditor.table_model = (DefaultTableModel) chunks.get(chunks.size() - 1)[1];
		this.redisEditor.scroll_pane = (JScrollPane) chunks.get(chunks.size() - 1)[2];
		this.redisEditor.add(this.redisEditor.scroll_pane, BorderLayout.CENTER);
		RedisEditor.frame.pack();
	}
}