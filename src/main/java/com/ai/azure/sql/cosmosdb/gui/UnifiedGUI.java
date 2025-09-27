package com.ai.azure.sql.cosmosdb.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.ai.azure.sql.cosmosdb.repository.AzureCosmosRepository;
import com.ai.azure.sql.cosmosdb.repository.AzureSQLRepository;

public class UnifiedGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private AzureSQLRepository sqlRepo;
	private AzureCosmosRepository cosmosRepo;

	public UnifiedGUI(Properties config) {
		setTitle("AI + Azure SQL + CosmosDB");
		setSize(600, 350);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null); // center window

		// Initialize repositories safely
		try {
			sqlRepo = new AzureSQLRepository(config);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error initializing Azure SQL Repository:\n" + e.getMessage(),
					"SQL Init Error", JOptionPane.ERROR_MESSAGE);
		}

		try {
			cosmosRepo = new AzureCosmosRepository(config);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error initializing Cosmos DB Repository:\n" + e.getMessage(),
					"Cosmos Init Error", JOptionPane.ERROR_MESSAGE);
		}

		// GUI components
		JButton fetchSQLBtn = new JButton("Fetch Azure SQL Users");
		JButton fetchCosmosBtn = new JButton("Fetch Cosmos DB Users");

		JTextArea resultArea = new JTextArea();
		resultArea.setEditable(false);

		fetchSQLBtn.addActionListener(e -> {
			if (sqlRepo != null) {
				try {
					List<String> users = sqlRepo.fetchUsers();
					resultArea.setText("Azure SQL Users:\n" + String.join("\n", users));
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error fetching SQL users:\n" + ex.getMessage(),
							"SQL Fetch Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		fetchCosmosBtn.addActionListener(e -> {
			if (cosmosRepo != null) {
				try {
					List<String> users = cosmosRepo.fetchUsers();
					resultArea.setText("Cosmos DB Users:\n" + String.join("\n", users));
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error fetching Cosmos users:\n" + ex.getMessage(),
							"Cosmos Fetch Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(fetchSQLBtn);
		buttonPanel.add(fetchCosmosBtn);

		add(buttonPanel, BorderLayout.NORTH);
		add(new JScrollPane(resultArea), BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		String[] fallbackPaths = { args.length > 0 ? args[0] : null, "config.properties",
				"src/main/java/config.properties" };

		Properties config = new Properties();
		boolean loaded = false;

		for (String path : fallbackPaths) {
			if (path == null)
				continue;

			File configFile = new File(path);
			System.out.println("Trying config: " + configFile.getAbsolutePath());

			if (configFile.exists()) {
				try (FileInputStream fis = new FileInputStream(configFile)) {
					config.load(fis);
					loaded = true;
					System.out.println("✅ Loaded config from: " + configFile.getAbsolutePath());
					break;
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Error reading config:\n" + configFile.getAbsolutePath() + "\n" + e.getMessage(),
							"Config Load Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		if (!loaded) {
			JOptionPane.showMessageDialog(null,
					"❌ config.properties file not found in any of the following locations:\n" + "- " + fallbackPaths[1]
							+ "\n" + "- " + fallbackPaths[2] + "\n\n"
							+ "Please provide a valid path or place the file in one of these locations.",
					"Missing config.properties", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Launch GUI safely
		SwingUtilities.invokeLater(() -> {
			try {
				UnifiedGUI gui = new UnifiedGUI(config);
				gui.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to launch GUI:\n" + e.getMessage(), "GUI Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
