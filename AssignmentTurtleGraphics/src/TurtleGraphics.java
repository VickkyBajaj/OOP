import uk.ac.leedsbeckett.oop.LBUGraphics;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class TurtleGraphics extends LBUGraphics {
    private final List<String> commandLog = new ArrayList<>();
    private boolean unsavedChanges = false;
    private final Color defaultPenColor = Color.RED;
    private final int defaultPenSize = 1;

    public static void main(String[] args) {
        new TurtleGraphics();
    }

    @Override
    public void processCommand(String inputCommand) {
        System.out.println("The command typed was: "+inputCommand);
        commandLog.add(inputCommand);
        unsavedChanges = true;

        try {
            executeUserCommand(inputCommand);
        } catch (Exception ex) {
            showError("Command failed: " + ex.getMessage());
        }
    }

    private void executeUserCommand(String fullCommand) {
        String[] commandParts = fullCommand.split(" ");
        String mainCommand = commandParts[0].toLowerCase();

        switch (mainCommand) {
            case "about":
                showAboutInfo();
                break;
            case "penup":
                liftPen();
                break;
            case "pendown":
                lowerPen();
                break;
            case "left":
                handleTurnCommand(commandParts, false);
                break;
            case "right":
                handleTurnCommand(commandParts, true);
                break;
            case "move":
                handleMovement(commandParts, true);
                break;
            case "reverse":
                handleMovement(commandParts, false);
                break;
            case "black":
                changePenColor(Color.BLACK);
                break;
            case "yellow":
                changePenColor(Color.YELLOW);
                break;
            case "green":
                changePenColor(Color.GREEN);
                break;
            case "red":
                changePenColor(Color.RED);
                break;
            case "white":
                changePenColor(Color.WHITE);
                break;
            case "reset":
                reset();
                break;
            case "clear":
                clear();
                break;
            case "save":
                storeImageToFile();
                break;
            case "load":
                retrieveImageFromFile();
                break;
            case "savecommands":
                storeCommandsToFile();
                break;
            case "loadcommands":
                retrieveCommandsFromFile();
                break;
            case "circle":
                drawCircle(commandParts);
                break;
            case "square":
                drawSquareShape(commandParts);
                break;
            case "pencolour":
                setCustomPenColor(commandParts);
                break;
            case "penwidth":
                adjustPenThickness(commandParts);
                break;
            case "triangle":
                drawTriangleShape(commandParts);
                break;
            default:
                showError("Unknown command: '" + mainCommand + "'");
                break;
        }
    }

    private void handleTurnCommand(String[] parts, boolean isRightTurn) {
        if (parts.length < 2) {
            showError("Missing angle for turn command");
            return;
        }

        try {
            int angle = Integer.parseInt(parts[1]);
            if (angle < 0 || angle > 360) {
                showError("Angle must be 0-360 degrees");
            } else if (isRightTurn) {
                right(angle);
            } else {
                left(angle);
            }
        } catch (NumberFormatException e) {
            showError("Invalid angle value");
        }
    }

    private void handleMovement(String[] parts, boolean isForward) {
        if (parts.length < 2) {
            showError("Missing distance for movement");
            return;
        }

        try {
            int distance = Integer.parseInt(parts[1]);
            if (distance < 0) {
                showError("Distance can't be negative");
            } else {
                forward(isForward ? distance : -distance);
            }
        } catch (NumberFormatException e) {
            showError("Invalid distance value");
        }
    }

    private void drawCircle(String[] parts) {
        if (parts.length < 2) {
            showError("Missing size for circle");
            return;
        }

        try {
            int radius = Integer.parseInt(parts[1]);
            circle(radius);
        } catch (NumberFormatException e) {
            showError("Invalid circle size");
        }
    }

    private void drawSquareShape(String[] parts) {
        if (parts.length < 2) {
            showError("Missing size for square");
            return;
        }

        try {
            int sideLength = Integer.parseInt(parts[1]);
            if (sideLength <= 0) {
                showError("Square size must be positive");
            } else {
                drawFourSidedShape(sideLength, 90);
            }
        } catch (NumberFormatException e) {
            showError("Invalid square size");
        }
    }

    private void drawFourSidedShape(int size, int turnAngle) {
        boolean originalPenState = getPenState();
        setPenState(true);

        for (int i = 0; i < 4; i++) {
            forward(size);
            right(turnAngle);
        }

        setPenState(originalPenState);
    }

    private void drawTriangleShape(String[] parts) {
        if (parts.length == 2) {
            handleEquilateralTriangle(parts);
        } else if (parts.length == 4) {
            handleScaleneTriangle(parts);
        } else {
            showError("Triangle needs 1 or 3 parameters");
        }
    }

    private void handleEquilateralTriangle(String[] parts) {
        try {
            int sideLength = Integer.parseInt(parts[1]);
            if (sideLength <= 0) {
                showError("Size must be positive");
            } else {
                drawThreeSidedShape(sideLength, sideLength, sideLength);
            }
        } catch (NumberFormatException e) {
            showError("Invalid size for triangle");
        }
    }

    private void handleScaleneTriangle(String[] parts) {
        try {
            int a = Integer.parseInt(parts[1]);
            int b = Integer.parseInt(parts[2]);
            int c = Integer.parseInt(parts[3]);

            if (a <= 0 || b <= 0 || c <= 0) {
                showError("All sides must be positive");
            } else if (a + b <= c || a + c <= b || b + c <= a) {
                showError("Invalid triangle dimensions");
            } else {
                drawThreeSidedShape(a, b, c);
            }
        } catch (NumberFormatException e) {
            showError("Invalid triangle side lengths");
        }
    }

    private void drawThreeSidedShape(int a, int b, int c) {
        boolean originalPenState = getPenState();
        setPenState(true);

        double angleA = calculateAngle(b, c, a);
        double angleB = calculateAngle(a, c, b);
        double angleC = calculateAngle(a, b, c);

        forward(c);
        right((int) (180 - Math.toDegrees(angleB)));
        forward(a);
        right((int) (180 - Math.toDegrees(angleC)));
        forward(b);
        right((int) (180 - Math.toDegrees(angleA)));

        setPenState(originalPenState);
    }

    private double calculateAngle(double adjacent1, double adjacent2, double opposite) {
        return Math.acos((adjacent1 * adjacent1 + adjacent2 * adjacent2 - opposite * opposite)
                / (2.0 * adjacent1 * adjacent2));
    }

    private void setCustomPenColor(String[] parts) {
        if (parts.length < 4) {
            showError("Need RGB values for color");
            return;
        }

        try {
            int red = Integer.parseInt(parts[1]);
            int green = Integer.parseInt(parts[2]);
            int blue = Integer.parseInt(parts[3]);

            if (invalidColorComponent(red) || invalidColorComponent(green) || invalidColorComponent(blue)) {
                showError("Color values must be 0-255");
            } else {
                setPenColour(new Color(red, green, blue));
            }
        } catch (NumberFormatException e) {
            showError("Invalid color values");
        }
    }

    private boolean invalidColorComponent(int value) {
        return value < 0 || value > 255;
    }

    private void adjustPenThickness(String[] parts) {
        if (parts.length < 2) {
            showError("Missing width value");
            return;
        }

        try {
            int width = Integer.parseInt(parts[1]);
            if (width <= 0) {
                showError("Width must be positive");
            } else {
                setStroke(width);
            }
        } catch (NumberFormatException e) {
            showError("Invalid width value");
        }
    }

    private void storeImageToFile() {
        try {
            ImageIO.write(getBufferedImage(), "png", new File("turtle_image.png"));
            showMessage("Drawing saved successfully");
            unsavedChanges = false;
        } catch (IOException e) {
            showError("Failed to save drawing: " + e.getMessage());
        }
    }

    private void retrieveImageFromFile() {
        if (unsavedChanges && !confirmAction("Save current drawing before loading?")) {
            return;
        }

        try {
            File imageFile = new File("turtle_image.png");
            if (imageFile.exists()) {
                setBufferedImage(ImageIO.read(imageFile));
                showMessage("Drawing loaded successfully");
                unsavedChanges = false;
            } else {
                showError("No saved drawing found");
            }
        } catch (IOException e) {
            showError("Failed to load drawing: " + e.getMessage());
        }
    }

    private void storeCommandsToFile() {
        try (PrintWriter writer = new PrintWriter("turtle_commands.txt")) {
            commandLog.forEach(writer::println);
            showMessage("Commands saved successfully");
            unsavedChanges = false;
        } catch (IOException e) {
            showError("Failed to save commands: " + e.getMessage());
        }
    }

    private void retrieveCommandsFromFile() {
        if (unsavedChanges && !confirmAction("Save current commands before loading?")) {
            return;
        }

        File commandFile = new File("turtle_commands.txt");
        if (!commandFile.exists()) {
            showError("No saved commands found");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(commandFile))) {
            List<String> loadedCommands = new ArrayList<>();
            String command;
            while ((command = reader.readLine()) != null) {
                loadedCommands.add(command);
            }

            commandLog.clear();
            reset();

            for (String cmd : loadedCommands) {
                executeUserCommand(cmd);
                commandLog.add(cmd);
            }

            unsavedChanges = false;
            showMessage("Commands loaded successfully");
        } catch (IOException e) {
            showError("Failed to load commands: " + e.getMessage());
        }
    }

    private boolean confirmAction(String message) {
        int choice = JOptionPane.showConfirmDialog(null,
                message, "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (unsavedChanges) {
                storeCommandsToFile();
            }
            return true;
        }
        return choice == JOptionPane.NO_OPTION;
    }

    private void liftPen() {
        setPenState(false);
    }

    private void lowerPen() {
        setPenState(true);
    }

    private void changePenColor(Color newColor) {
        setPenColour(newColor);
    }

    private void showAboutInfo() {
        super.about();
        showMessage("Enhanced Turtle Graphics by Vicky Bajaj");
    }

    @Override
    public void reset() {
        super.reset();
        setPenColour(defaultPenColor);
        setStroke(defaultPenSize);
        lowerPen();
    }

    private void showMessage(String text) {
        displayMessage(text);
    }

    private void showError(String text) {
        displayMessage("Error: " + text);
    }
}