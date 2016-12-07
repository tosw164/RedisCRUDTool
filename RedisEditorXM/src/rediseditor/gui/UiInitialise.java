package rediseditor.gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;

public class UiInitialise {
	
	public static java.awt.Component createPadding(int width, int height){
		return Box.createRigidArea(new Dimension(width, height));
	}

	public static JButton createRefreshButton(){
		JButton refresh_button = new JButton("Refresh Table");
		refresh_button.setMaximumSize(new Dimension(300,30));
		
		return refresh_button;
	}

	public static JButton createSaveButton(){
		JButton save_button = new JButton("save all");
		save_button.setMaximumSize(new Dimension(300,30));

		return save_button;
	}

	public static JButton createAddRowButton(){
		JButton add_button = new JButton("Add New Row");
		add_button.setMaximumSize(new Dimension(300, 30));

		return add_button;
	}

	public static JButton createDeleteRowButton(){
		JButton delete_button = new JButton("Delete Selected");
		delete_button.setMaximumSize(new Dimension(300,30));

		return delete_button;
	}

	public static JButton createCloseButton(){
		JButton close_button = new JButton("close");
		close_button.setMaximumSize(new Dimension(300,30));

		return close_button;
	}
	
}
