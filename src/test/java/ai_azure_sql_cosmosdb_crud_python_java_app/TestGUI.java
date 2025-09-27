package ai_azure_sql_cosmosdb_crud_python_java_app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TestGUI {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Test GUI");
			frame.setSize(400, 300);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}
}
