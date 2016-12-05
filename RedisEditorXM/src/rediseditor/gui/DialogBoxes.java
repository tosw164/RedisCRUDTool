package rediseditor.gui;

import javax.swing.JOptionPane;

public class DialogBoxes {
	public static void displayErrorMessage(String message){
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);

	}

	public static boolean displayWarningPrompt(String message){
		if( JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
			return true;
		} else{
			return false;
		}

	}


}
