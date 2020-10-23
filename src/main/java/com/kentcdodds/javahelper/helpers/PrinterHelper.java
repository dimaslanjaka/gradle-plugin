package com.kentcdodds.javahelper.helpers;

import com.kentcdodds.javahelper.extras.MessageConsole;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;

/**
 * Good for debugging, this class allows you to send your prints somewhere and change whether it all prints. Sometimes
 * you want to reduce the amount your code prints to isolate a problem. This way you just set the PrintHelper
 * instance.setPrint(true) and it'll print everything you send to the printALot method.
 *
 * @author Kent
 */
public class PrinterHelper {

	private static PrinterHelper instance;
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	private boolean print = false;
	private JDialog outputDialog = new JDialog();
	private JTextPane textPane = new JTextPane();

	public PrinterHelper() {
		textPane.setPreferredSize(new java.awt.Dimension(450, 200));
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new java.awt.Dimension(
						(textPane.getPreferredSize().width + 20), textPane.getPreferredSize().height));
		outputDialog.getContentPane().add(scrollPane);
		SwingHelper.centerAndPack(outputDialog);
		outputDialog.addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				System.setErr(getInstance().getOriginalErr());
				System.setOut(getInstance().getOriginalOut());
			}
		});
	}

	public static PrinterHelper getInstance() {
		if (instance == null) {
			instance = new PrinterHelper();
		}
		return instance;
	}

	//<editor-fold defaultstate="collapsed" desc="Print">

	/**
	 * Prints the given String if the print variable is true
	 *
	 * @param object to print
	 */
	public static void print(Object object) {
		if (getInstance().isPrint()) {
			System.out.print(object);
		}
	}

	/**
	 * Prints the given String followed by a new line if the print variable is true
	 *
	 * @param object to print
	 */
	public static void println(Object object) {
		if (getInstance().isPrint()) {
			System.out.println(object);
		}
	}

	/**
	 * Prints a new line if Print on the current instance is true
	 */
	public static void println() {
		if (getInstance().isPrint()) {
			System.out.println();
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Print Err">

	/**
	 * Prints the given String in error type if the print variable is true
	 *
	 * @param object to print
	 */
	public static void printErr(Object object) {
		if (getInstance().isPrint()) {
			System.err.print(object);
		}
	}

	/**
	 * Prints the given String followed by a newline in error type if the print variable is true
	 *
	 * @param object to print
	 */
	public static void printErrln(Object object) {
		if (getInstance().isPrint()) {
			System.err.println(object);
		}
	}

	/**
	 * Prints a new error line if Print on the current instance is true
	 */
	public static void printErrln() {
		if (getInstance().isPrint()) {
			System.err.println();
		}
	}
	//</editor-fold>

	/**
	 * Convenience method. sets the dialog title and then calls showMessageConsole()
	 *
	 * @param dialogTitle
	 */
	public static void showMessageConsole(String dialogTitle) {
		getInstance().getOutputDialog().setTitle(dialogTitle);
		if (getInstance().getOutputDialog().isVisible()) {
			return;
		}
		MessageConsole mc = new MessageConsole(getInstance().getTextPane());
		mc.redirectOut();
		mc.redirectErr(Color.red, null);
		mc.setMessageLines(10000);
		getInstance().getOutputDialog().setVisible(true);
	}

	/**
	 * Shows the output window and sends all messages sent to PrintHelper via print methods to this window as well.
	 */
	public static void showMessageConsole() {
		showMessageConsole(getInstance().getOutputDialog().getTitle());
	}

	/**
	 * Calls setPrint on the instance of the printerhelper
	 *
	 * @param print
	 */
	public static void setInstancePrint(boolean print) {
		getInstance().setPrint(print);
	}

	/**
	 * @return the print
	 */
	public boolean isPrint() {
		return print;
	}

	/**
	 * @param print
	 */
	public void setPrint(boolean print) {
		this.print = print;
	}

	/**
	 * @return the outputDialog
	 */
	public JDialog getOutputDialog() {
		return outputDialog;
	}

	/**
	 * @param outputDialog the outputDialog to set
	 */
	public void setOutputDialog(JDialog outputDialog) {
		this.outputDialog = outputDialog;
	}

	/**
	 * @return the originalOut
	 */
	public PrintStream getOriginalOut() {
		return originalOut;
	}

	/**
	 * @return the originalErr
	 */
	public PrintStream getOriginalErr() {
		return originalErr;
	}

	/**
	 * @return the textArea
	 */
	public JTextPane getTextPane() {
		return textPane;
	}

	/**
	 * @param textArea the textArea to set
	 */
	public void setTextPane(JTextPane textArea) {
		this.textPane = textArea;
	}
}
