package rediseditor.gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;

public class UiInitialise {
	
	public static java.awt.Component createPadding(int width, int height){
		return Box.createRigidArea(new Dimension(width, height));
	}

	public static JButton createButton(String text){
		JButton button = new JButton(text);
		button.setMaximumSize(new Dimension(300,30));
		
		return button;
	}

	
}
