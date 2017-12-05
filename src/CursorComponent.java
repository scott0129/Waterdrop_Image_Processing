

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class CursorComponent extends JPanel {
	
	int x1;
	int y1;
	int x2;
	int y2;
	
    private BufferedImage image;


    public void setImage(BufferedImage bf) {
    	image = bf;
    }

    public void setVariables(int x1, int y1, int x2, int y2) {
    	this.x1 = x1;
    	this.y1 = y1;
    	this.x2 = x2;
    	this.y2 = y2;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
        g.drawLine(x1, y1, x2, y2);
    }

}