package rediseditor.gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;

/**
 * Class that handles creating button and padding UI elements
 * RedisEditor tool
 * 
 * @author theooswanditosw164
 */
public class UiInitialise {
	
	/**
	 * Method that creates and returns a rigid area to add to box to allow flexible 
	 * spacing between UI elements to show grouping of UI elements (usually buttons)
	 * 
	 * @param width		pixel width of rigid area to create
	 * @param height	pixel height of rigid area to create
	 * @return	Instance of the rigid area to be added to create padding space between buttons
	 */
	public static java.awt.Component createPadding(int width, int height){
		return Box.createRigidArea(new Dimension(width, height));
	}

	/**
	 * Method that creates JButton instance from given specification and returns it
	 * 
	 * @param text	text to be displayed in button
	 * @return JButton instance created
	 */
	public static JButton createButton(String text){
		JButton button = new JButton(text);
		button.setMaximumSize(new Dimension(300,30));
		
		return button;
	}	
}
