package shared;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSharedOperations {
    public static BufferedImage loadImage(String path) {
        BufferedImage image = null;
        int lastIndexOfDot = path.lastIndexOf('.');
        String format = path.substring(lastIndexOfDot + 1);
        if (format.matches("svg")) {


            return image;
        } else {
            try {
                image = ImageIO.read(new File(path));
            } catch (IOException ex) {
                System.out.println("Error has occured during file reading: " + ex.getMessage());
            }
            return image;
        }
    }

    public static void saveImage(BufferedImage img, String path) {
        try {
            int lastIndexOfDot = path.lastIndexOf('.');
            String format = path.substring(lastIndexOfDot + 1);
            ImageIO.write(img, format, new File(path));
        } catch (IOException ex) {
            System.out.println("Error has occured during file writing: " + ex.getMessage());
        }
    }

    public static BufferedImage convertIconToImage(ImageIcon icon) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();
        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();
        return image;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

}
