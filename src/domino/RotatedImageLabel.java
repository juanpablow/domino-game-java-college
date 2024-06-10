package domino;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class RotatedImageLabel extends JLabel {
  
  public static ImageIcon rotateImageIcon(ImageIcon icon, int angle) {
    BufferedImage bufferedImage = toBufferedImage(icon.getImage());
    double radians = Math.toRadians(angle);
    double sin = Math.abs(Math.sin(radians)), cos = Math.abs(Math.cos(radians));
    int w = bufferedImage.getWidth();
    int h = bufferedImage.getHeight();
    int newWidth = (int) Math.floor(w * cos + h * sin);
    int newHeight = (int) Math.floor(h * cos + w * sin);

    BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, bufferedImage.getType());
    Graphics2D g2d = rotatedImage.createGraphics();
    AffineTransform at = new AffineTransform();
    at.translate((newWidth - w) / 2, (newHeight - h) / 2);

    int x = w / 2;
    int y = h / 2;

    at.rotate(radians, x, y);
    g2d.setTransform(at);
    g2d.drawImage(bufferedImage, 0, 0, null);
    g2d.dispose();

    return new ImageIcon(rotatedImage);
}

private static BufferedImage toBufferedImage(Image img) {
    if (img instanceof BufferedImage) {
        return (BufferedImage) img;
    }
    // Create a buffered image with transparency
    BufferedImage bimage = new BufferedImage(
            img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    // Draw the image on to the buffered image
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();

    return bimage;
}

}
