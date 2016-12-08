package rediseditor.gui;

import javax.swing.JOptionPane;

/**
 * Class that holds information and contains methods used to display various
 * messages/prompts to the user during use of the application.
 * 
 * @author theooswanditosw164
 */
public class DialogBoxes {
	
	/**
	 * Method that displays a message dialog with an OK button
	 * 
	 * @param message	message to be displayed in the dialog box
	 */
	public static void displayErrorMessage(String message){
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);

	}

	/**
	 * Displays a warning message with given @param message
	 * Uses PromptType enum to show it is a Warning not Confirmation
	 * 
	 * @return boolean true if user presses yes, otherwise return false
	 */
	public static boolean displayWarningPrompt(String message){
		return displayPrompt(message, PromptType.Warning);

	}

	/**
	 * Displays a Confirmation message with given @param message
	 * Uses PromptType enum to show it is a Confirmation not Warning
	 * 
	 * @return boolean true if user presses yes, otherwise return false
	 */
	public static boolean displayConfirmationPrompt(String message){
		return displayPrompt(message, PromptType.Confirmation);
	}
	
	/**
	 * Underlying method that actually shows the YES/NO dialog
	 * 
	 * @param message	Passed from displayWarningPrompt/displayConfirmationPrompt
	 * 					denotes what should be displayed to user above yes/no buttons
	 * @param type		PromptType denoting what kind of title to have
	 * @return			boolean true if "yes" pressed, otherwise return false
	 */
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
	
	/**
	 * Enum denoting two different prompt types, either a confirmation prompt or warning prompt
	 * @author theooswanditosw164
	 */
	private enum PromptType{
		Confirmation, Warning;
	}

}
