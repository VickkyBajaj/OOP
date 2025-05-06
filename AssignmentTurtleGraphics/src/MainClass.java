import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.FlowLayout;

public class MainClass {
    public static void main(String[] args) {
        new MainClass();
    }

    public MainClass() {
        // Create the main frame
        JFrame mainFrame = new JFrame("Turtle Graphics Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new FlowLayout());

        // Create the turtle graphics panel
        TurtleGraphics turtleGraphics = new TurtleGraphics();
        mainFrame.add(turtleGraphics);

        // Set up the frame
        mainFrame.pack();
        mainFrame.setVisible(true);

        // Show welcome message and instructions
        showWelcomeMessage(turtleGraphics);
    }

    private void showWelcomeMessage(TurtleGraphics turtleGraphics) {
        String message = "Welcome to Turtle Graphics!\n\n" +
                "Available commands:\n" +
                "- about: Show about information\n" +
                "- penup/pendown: Lift or lower the pen\n" +
                "- left/right [degrees]: Turn turtle\n" +
                "- move/reverse [distance]: Move turtle\n" +
                "- black/red/green/white: Change pen color\n" +
                "- pencolour R G B: Set custom color (0-255)\n" +
                "- penwidth [size]: Set pen thickness\n" +
                "- clear/reset: Clear screen or reset turtle\n" +
                "- square [size]: Draw square\n" +
                "- triangle [size] or [side1 side2 side3]: Draw triangle\n" +
                "- save/load: Save or load image\n" +
                "- savecommands/loadcommands: Save or load command history\n\n" +
                "Type commands in the text field below.";

        JOptionPane.showMessageDialog(null, message, "Turtle Graphics Help", JOptionPane.INFORMATION_MESSAGE);

        // Display initial about information
        turtleGraphics.about();
    }
}