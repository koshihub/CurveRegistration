
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

class Main {
	static CanvasPanel canvasPanel;
	final static int width = 600, height = 600;

	public static void main(String args[]) {
		JFrame frame = new JFrame("Curve Registration");

		// event
		frame.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
		});
		
		// main panel
		canvasPanel = new CanvasPanel(Main.width, Main.height);
		
		frame.setBounds(100, 100, Main.width, Main.height);
		frame.setLayout(new BorderLayout());
		frame.add("Center", canvasPanel);
		frame.setVisible(true);
	}

	public static class Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}
}
