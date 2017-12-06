

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ListenerComponent implements MouseListener {
	
	ImageProcessor parentClass;
	
	public ListenerComponent(ImageProcessor mediaFileExample) {
		parentClass = mediaFileExample;
	}

	@Override
	public void mouseClicked(MouseEvent m) {
		parentClass.setGroundLevel(m.getY());
		
		System.out.println("clicked!");
		
		parentClass.processImages();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	

}
