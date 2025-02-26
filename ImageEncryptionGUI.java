import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageEncryptionGUI extends JFrame {
    private JTextField messageField, passwordField;
    private JLabel imageLabel;
    private File selectedFile;

    public ImageEncryptionGUI() {
        setTitle("Image Steganography - Encryption");
        setSize(400, 300);
        setLayout(new GridLayout(5, 1));

        JButton selectImageButton = new JButton("Select Image");
        imageLabel = new JLabel("No Image Selected", SwingConstants.CENTER);
        messageField = new JTextField("Enter secret message");
        passwordField = new JTextField("Enter password");
        JButton encryptButton = new JButton("Encrypt");

        add(selectImageButton);
        add(imageLabel);
        add(messageField);
        add(passwordField);
        add(encryptButton);

        selectImageButton.addActionListener(e -> chooseImage());
        encryptButton.addActionListener(e -> encryptImage());

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

    private void encryptImage() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select an image.");
            return;
        }

        try {
            BufferedImage img = ImageIO.read(selectedFile);
            String msg = messageField.getText() + "\0"; // Add null character
            String password = passwordField.getText();
            int n = 0, m = 0, z = 0;

            for (int i = 0; i < msg.length(); i++) {
                int rgb = img.getRGB(n, m);
                int alpha = (rgb >> 24) & 0xff;
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;

                if (z == 0) red = msg.charAt(i);
                else if (z == 1) green = msg.charAt(i);
                else blue = msg.charAt(i);

                int newRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                img.setRGB(n, m, newRgb);

                n = (n + 1) % img.getWidth();
                if (n == 0) m = (m + 1) % img.getHeight();
                z = (z + 1) % 3;
            }

            File outputFile = new File("Encryptedmsg.png");
            ImageIO.write(img, "png", outputFile);
            JOptionPane.showMessageDialog(this, "Encryption successful! File saved as 'Encryptedmsg.png'.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error encrypting image.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ImageEncryptionGUI();
    }
}
