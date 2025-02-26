import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageDecryptionGUI extends JFrame {
    private JTextField passwordField;
    private JLabel imageLabel;
    private File selectedFile;
    private JTextArea messageArea;

    public ImageDecryptionGUI() {
        setTitle("Image Steganography - Decryption");
        setSize(400, 300);
        setLayout(new GridLayout(5, 1));

        JButton selectImageButton = new JButton("Select Encrypted Image");
        imageLabel = new JLabel("No Image Selected", SwingConstants.CENTER);
        passwordField = new JTextField("Enter password");
        JButton decryptButton = new JButton("Decrypt");
        messageArea = new JTextArea();
        messageArea.setEditable(false);

        add(selectImageButton);
        add(imageLabel);
        add(passwordField);
        add(decryptButton);
        add(new JScrollPane(messageArea));

        selectImageButton.addActionListener(e -> chooseImage());
        decryptButton.addActionListener(e -> decryptImage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            imageLabel.setText("Selected: " + selectedFile.getName());
        }
    }

    private void decryptImage() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select an image.");
            return;
        }

        try {
            BufferedImage img = ImageIO.read(selectedFile);
            StringBuilder message = new StringBuilder();
            int n = 0, m = 0, z = 0;

            while (true) {
                int rgb = img.getRGB(n, m);
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;

                char extractedChar;
                if (z == 0) extractedChar = (char) red;
                else if (z == 1) extractedChar = (char) green;
                else extractedChar = (char) blue;

                if (extractedChar == '\0') break; // Stop at null character
                message.append(extractedChar);

                n = (n + 1) % img.getWidth();
                if (n == 0) m = (m + 1) % img.getHeight();
                z = (z + 1) % 3;
            }

            messageArea.setText("Decrypted Message: " + message.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error decrypting image.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ImageDecryptionGUI();
    }
}
