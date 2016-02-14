package com.chris.shide;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

public class Shide extends Thread {
	private final String ICON_DARKMODE = "/com/chris/shide/icons/tray_icon_darkmode.png";
	private final String ICON_DEFAULT = "/com/chris/shide/icons/tray_icon_default.png";
	private final String[] READ_SHOW_ALL_FILES = new String[] { "defaults", "read", "com.apple.finder",
			"AppleShowAllFiles" };
	private final String WRITE_SHOW_ALL_FILES = "defaults write com.apple.finder AppleShowAllFiles ";
	private final String KILL_FINDER = "KillAll Finder";
	private final String[] INTERFACE_STYLE = new String[] { "defaults", "read", "-g", "AppleInterfaceStyle" };
	private final String[] ADD_LOGIN_ITEM = new String[] { "osascript", "-e",
			"tell application \"System Events\" to make login item at end with properties {path:\"/Applications/Shide.app\", hidden:false}" };
	private final String[] REMOVE_LOGIN_ITEM = new String[] { "osascript", "-e",
			"tell application \"System Events\" to delete login item \"Shide\"" };
	private final String[] READ_LOGIN_ITEM = new String[] { "osascript", "-e",
			"tell application \"System Events\" to get the name of every login item" };
	private CheckboxMenuItem showHideItem;
	private CheckboxMenuItem openAutomaticallyItem;
	private PopupMenu popup;
	private Image image;
	private TrayIcon trayIcon;
	private SystemTray systemtray;
	private boolean darkMode;

	public static void main(String[] args) {
		System.setProperty("apple.awt.UIElement", "true");
		new Shide();
	}

	public Shide() {
		if (SystemTray.isSupported()) {
			About dialog = new About();
			dialog.setLocationRelativeTo(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			popup = new PopupMenu();

			selectIcon();

			trayIcon = new TrayIcon(image, "Shide", popup);

			systemtray = SystemTray.getSystemTray();

			ItemListener showHideListener = new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					try {
						CheckboxMenuItem checkBoxMenuItem = (CheckboxMenuItem) e.getSource();
						Runtime.getRuntime().exec(WRITE_SHOW_ALL_FILES + checkBoxMenuItem.getState());
						Runtime.getRuntime().exec(KILL_FINDER);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			};

			ItemListener openAutomaticallyListener = new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					CheckboxMenuItem checkBoxMenuItem = (CheckboxMenuItem) e.getSource();
					try {
						if (checkBoxMenuItem.getState()) {
							Runtime.getRuntime().exec(ADD_LOGIN_ITEM);
						} else {
							Runtime.getRuntime().exec(REMOVE_LOGIN_ITEM);
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			};

			ActionListener aboutListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(true);
				}
			};

			ActionListener quitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			};

			showHideItem = new CheckboxMenuItem("show");
			showHideItem.setState(stateShowAllFiles());
			showHideItem.addItemListener(showHideListener);
			popup.add(showHideItem);
			popup.addSeparator();

			openAutomaticallyItem = new CheckboxMenuItem("open automatically");
			openAutomaticallyItem.setState(detectSettings());
			openAutomaticallyItem.addItemListener(openAutomaticallyListener);
			popup.add(openAutomaticallyItem);

			MenuItem aboutItem = new MenuItem("about...");
			aboutItem.addActionListener(aboutListener);
			popup.add(aboutItem);
			popup.addSeparator();

			MenuItem quitItem = new MenuItem("quit");
			quitItem.addActionListener(quitListener);
			popup.add(quitItem);

			trayIcon.setImageAutoSize(true);

			try {
				systemtray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("Error:" + e.getMessage());
			}
		} else {
			System.err.println("Error: SystemTray no es soportado");
		}
		start();
	}

	private boolean isMacMenuBarDarkMode() {
		try {
			Process proc = Runtime.getRuntime().exec(INTERFACE_STYLE);
			proc.waitFor(100, TimeUnit.MILLISECONDS);
			return proc.exitValue() == 0;
		} catch (IOException | InterruptedException | IllegalThreadStateException ex) {
			return false;
		}
	}

	private void selectIcon() {
		if (isMacMenuBarDarkMode()) {
			image = new ImageIcon(getClass().getResource(ICON_DARKMODE)).getImage();
			darkMode = true;
		} else {
			image = new ImageIcon(getClass().getResource(ICON_DEFAULT)).getImage();
			darkMode = false;
		}
	}

	private String executeCommand(String[] command) {
		try {
			Process terminal = Runtime.getRuntime().exec(command);
			BufferedReader result = new BufferedReader(new InputStreamReader(terminal.getInputStream(), "UTF-8"));
			return result.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean stateShowAllFiles() {
		boolean active = false;
		active = Boolean.parseBoolean(executeCommand(READ_SHOW_ALL_FILES));
		return active;
	}

	private boolean detectingChange() {
		boolean change = false;
		boolean mode = isMacMenuBarDarkMode();
		if (mode != darkMode) {
			change = true;
		}
		return change;
	}

	private void shitf() {
		selectIcon();
		systemtray.remove(trayIcon);
		trayIcon.setPopupMenu(null);
		trayIcon = new TrayIcon(image, "Shide", popup);
		try {
			systemtray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private boolean detectSettings() {
		boolean config = false;
		String result = executeCommand(READ_LOGIN_ITEM);
		if (result.contains("Shide") || result.contains("Shide, ") || result.contains(", Shide")) {
			config = true;
		}
		return config;
	}

	@Override
	public void run() {
		boolean change = false;
		while (true) {
			openAutomaticallyItem.setState(detectSettings());
			showHideItem.setState(stateShowAllFiles());
			change = detectingChange();
			if (change) {
				shitf();
				change = false;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
