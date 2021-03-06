package call.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import call.AbstractId;
import call.Config;
import call.Config.Option;
import call.ConfigListener;
import call.ContactList;
import call.ContactScanner;
import call.Id;

public class MainMenu extends AbstractId implements ActionListener, ConfigListener {

	private static final String TEXT_MENU_CONNECTION = "Connection";
	private static final String TEXT_MENUITEM_CONTACTS_RELOAD = "Reload contacts";
	private static final String TEXT_MENUITEM_CONTACTS_ADD = "Add contact";

	private static final String TEXT_MENU_VIEW = "View";
	private static final String TEXT_MENUITEM_SHOW_CONSOLE = "Show terminal";

	private static final String TEXT_MENU_SETTINGS = "Settings";
	private static final String TEXT_MENUITEM_SETTINGS_AUDIO = "Audio Devices";

	private static final String TEXT_MENU_HELP = "Help";
	private static final String TEXT_MENUITEM_HELP_ABOUT = "About";

	private final MainWindow main;

	private final JMenuBar menubar;
	private JMenuItem itemContactsAdd;
	private JMenuItem itemContactsReload;
	private JCheckBoxMenuItem checkboxShowConsole;
	private JMenuItem itemSettingsAudio;
	private JMenuItem itemHelpAbout;

	public MainMenu(MainWindow main) {
		this.main = main;

		menubar = new JMenuBar();
		menubar.add(createContactsMenu());
		menubar.add(createViewMenu());
		menubar.add(createSettingsMenu());
		menubar.add(createHelpMenu());

		// config listener
		Config.addConfigListener(this);
		Config.notifyConfigListener(this);
	}

	private JMenu createContactsMenu() {
		JMenu menu = new JMenu(TEXT_MENU_CONNECTION);
		menu.setMnemonic(KeyEvent.VK_C);

		itemContactsAdd = new JMenuItem(TEXT_MENUITEM_CONTACTS_ADD, Resources.ICON_CONTACTS_ADD);
		itemContactsAdd.setMnemonic(KeyEvent.VK_A);
		itemContactsAdd.addActionListener(this);
		menu.add(itemContactsAdd);

		itemContactsReload = new JMenuItem(TEXT_MENUITEM_CONTACTS_RELOAD, Resources.ICON_CONTACTS_RELOAD);
		itemContactsReload.setMnemonic(KeyEvent.VK_R);
		itemContactsReload.addActionListener(this);
		menu.add(itemContactsReload);

		return menu;
	}

	private JMenu createViewMenu() {
		JMenu menu = new JMenu(TEXT_MENU_VIEW);
		menu.setMnemonic(KeyEvent.VK_V);

		checkboxShowConsole = new JCheckBoxMenuItem(TEXT_MENUITEM_SHOW_CONSOLE, Resources.ICON_CONSOLE);
		checkboxShowConsole.setMnemonic(KeyEvent.VK_O);
		checkboxShowConsole.addActionListener(this);
		menu.add(checkboxShowConsole);

		return menu;
	}

	private JMenu createSettingsMenu() {
		JMenu menu = new JMenu(TEXT_MENU_SETTINGS);
		menu.setMnemonic(KeyEvent.VK_S);

		itemSettingsAudio = new JMenuItem(TEXT_MENUITEM_SETTINGS_AUDIO, Resources.ICON_SETTINGS_AUDIO);
		itemSettingsAudio.setMnemonic(KeyEvent.VK_C);
		itemSettingsAudio.addActionListener(this);
		menu.add(itemSettingsAudio);

		return menu;
	}

	private JMenu createHelpMenu() {
		JMenu menu = new JMenu(TEXT_MENU_HELP);
		menu.setMnemonic(KeyEvent.VK_H);

		itemHelpAbout = new JMenuItem(TEXT_MENUITEM_HELP_ABOUT, Resources.ICON_HELP_ABOUT);
		itemHelpAbout.setMnemonic(KeyEvent.VK_R);
		itemHelpAbout.addActionListener(this);
		menu.add(itemHelpAbout);

		return menu;
	}

	public JMenuBar getJMenuBar() {
		return menubar;
	}

	@Override
	public String getId() {
		return "MainMenu";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem source = (JMenuItem) e.getSource();

			new Thread(new MenuItemHandler(source), "MainMenu -> MenuItemHandler").start();
		}
	}

	public class MenuItemHandler implements Runnable {

		private final JMenuItem source;

		public MenuItemHandler(JMenuItem source) {
			this.source = source;
		}

		@Override
		public void run() {
			switch (source.getText()) {

			case TEXT_MENUITEM_CONTACTS_RELOAD:
				ContactList.clear();
				ContactScanner.scanNow();
				break;

			case TEXT_MENUITEM_CONTACTS_ADD:
				String host = JOptionPane.showInputDialog(null, "Host name or IP address:", "", 1);
				if (host != null && host.length() > 0) {
					ContactScanner.addHostOfInterest(host);
					ContactScanner.scanNow();
				}
				break;

			case TEXT_MENUITEM_SHOW_CONSOLE:
				JCheckBoxMenuItem checkbox = (JCheckBoxMenuItem) source;
				Config.SHOW_CONSOLE.setBooleanValue(checkbox.getState());
				break;

			case TEXT_MENUITEM_SETTINGS_AUDIO:
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						MainTabs tabs = main.getTabs();
						SettingsAudioTab codecs = main.getCodecs();
						tabs.addTab(Resources.TABNAME_SETTINGS_AUDIO, codecs.getComponent(),
								Resources.ICON_SETTINGS_AUDIO);
						tabs.showTab(Resources.TABNAME_SETTINGS_AUDIO);
					}
				});
				break;

			case TEXT_MENUITEM_HELP_ABOUT:
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(
								main.getWindow(),
								"This program is free software: you can redistribute it and/or modify\n"
										+ "it under the terms of the GNU General Public License as published by\n"
										+ "the Free Software Foundation, either version 3 of the License, or\n"
										+ "(at your option) any later version."
										+ "\n\n"
										+ "This program is distributed in the hope that it will be useful,\n"
										+ "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
										+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
										+ "GNU General Public License for more details."
										+ "\n\n"
										+ "You should have received a copy of the GNU General Public License\n"
										+ "along with this program. If not, write to" + "\n\n"
										+ "       The Free Software Foundation, Inc.\n"
										+ "       51 Franklin Street, Fifth Floor\n"
										+ "       Boston, MA 02110-1301  USA"

								, "About", JOptionPane.PLAIN_MESSAGE);
					}
				});
				break;

			}
		}
	}

	@Override
	public void onConfigUpdate(Option option, float value) {}

	@Override
	public void onConfigUpdate(Option option, int value) {}

	@Override
	public void onConfigUpdate(Option option, boolean value) {
		if (option.equals(Config.SHOW_CONSOLE)) {
			if (value != checkboxShowConsole.getState())
				checkboxShowConsole.setState(value);
		}
	}

	@Override
	public void onConfigUpdate(Option option, String value) {}

	@Override
	public void onConfigUpdate(Option option, Id value) {}
}
