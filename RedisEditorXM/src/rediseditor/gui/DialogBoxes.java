package rediseditor.gui;

import javax.swing.JOptionPane;

public class DialogBoxes {
	public static void displayErrorMessage(String message){
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);

	}

	public static boolean displayWarningPrompt(String message){
		return displayPrompt(message, PromptType.Warning);

	}

	public static boolean displayConfirmationPrompt(String message){
		return displayPrompt(message, PromptType.Confirmation);
	}
	
	private static boolean displayPrompt(String message, PromptType type){
		String title;
		if (type == PromptType.Confirmation){
			title = "Confirmation";
		} else {
			title = "Warning";
		}
		if( JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
			return true;
		} else{
			return false;
		}
	}
	
	private enum PromptType{
		Confirmation, Warning;
	}

}
