package domino;

import java.io.File;

import javax.swing.ImageIcon;

public class Tile {
    private int faceUp;
    private int faceDown;
    private boolean bucha;
    private ImageIcon imageIcon;
    private int angle;

    public int getAngle() {
        return this.angle;
    }

    public void setAngle(int angle) {
        if (isBucha()) {
            this.angle = 0;
            return;
        }
        this.angle = angle;
    }

    public int getFaceUp() {
        return this.faceUp;
    }

    public void setFaceUp(int faceUp) {
        this.faceUp = faceUp;
    }

    public int getFaceDown() {
        return this.faceDown;
    }

    public void setFaceDown(int faceDown) {
        this.faceDown = faceDown;
    }

    public boolean isBucha() {
        if (this.faceUp == this.faceDown) {
            this.bucha = true;

        } else {
            this.bucha = false;
        }
        return this.bucha;
    }

    public ImageIcon getImage() {
        String imagePath = "src" + File.separator + "domino" + File.separator + "sprites" + File.separator + this.faceUp + "-" + this.faceDown + ".png";
        this.imageIcon = new ImageIcon(imagePath);

        return this.imageIcon;
    }
}
