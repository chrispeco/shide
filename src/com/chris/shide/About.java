package com.chris.shide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.swingplus.JHyperlink;

public class About extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public About() {
		setResizable(false);
		setTitle("About Shide");
		setBounds(100, 100, 377, 233);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblShide = new JLabel("Shide");
			lblShide.setBounds(160, 26, 82, 33);
			lblShide.setFont(new Font("Helvetica", Font.PLAIN, 32));
			contentPanel.add(lblShide);
		}
		
		JLabel lblVersion = new JLabel("Versión 1.0.0");
		lblVersion.setFont(new Font("Helvetica", Font.PLAIN, 11));
		lblVersion.setBounds(160, 59, 69, 16);
		contentPanel.add(lblVersion);
		
		JLabel lblIcon = new JLabel();
		ImageIcon icon = new ImageIcon(getClass().getResource("/com/chris/shide/icons/search.png"));
		lblIcon.setIcon(icon);
		lblIcon.setBounds(48, 75, 64, 64);
		contentPanel.add(lblIcon);
		
		JLabel lblDeveloper = new JLabel("Developer");
		lblDeveloper.setFont(new Font("Helvetica", Font.BOLD, 12));
		lblDeveloper.setBounds(160, 121, 69, 16);
		contentPanel.add(lblDeveloper);
		
		JLabel lblSource = new JLabel("Source");
		lblSource.setFont(new Font("Helvetica", Font.BOLD, 12));
		lblSource.setBounds(160, 149, 61, 16);
		contentPanel.add(lblSource);
		
		JHyperlink lblChrisPrezCornejo = new JHyperlink("Chris Pérez Cornejo", "https://www.stackoverflow.com/");
		lblChrisPrezCornejo.setFont(new Font("Helvetica", Font.PLAIN, 12));
		lblChrisPrezCornejo.setForeground(Color.BLACK);
		lblChrisPrezCornejo.setBounds(241, 121, 130, 16);
		contentPanel.add(lblChrisPrezCornejo);
		
		JHyperlink lblGithub = new JHyperlink("github", "https://www.stackoverflow.com/");
		lblGithub.setFont(new Font("Helvetica", Font.PLAIN, 12));
		lblGithub.setForeground(Color.BLACK);
		lblGithub.setBounds(241, 149, 61, 16);
		contentPanel.add(lblGithub);
	}
}
