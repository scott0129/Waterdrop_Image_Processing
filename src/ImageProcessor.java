
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.io.FileOutputStream;
 
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ImageProcessor {

	private static final JFileChooser chooser = new JFileChooser();
    
    public static final int DEFAULT_FIRST = 1;
    
    public static final int DEFAULT_SKIP = 1;
    
    public static final int DEFAULT_RATE = 13001;
    
    public static final int DEFAULT_THRESHOLD = 220;
    
    public static final String choosertitle = "Choose a directory";
    
    //public static final int BLACK_PIXEL = -16777216;
    
    public static final short BLACK_PIXEL = 0;
    
    public static final short WHITE_PIXEL = -1;
    
    public static final double HALFSQR2 = 0.707;
    
    public static final double MASK_PERCENT = 0.95;
    
    public final boolean quickTesting = true;
    
    public final boolean DEBUGGING = false;
    
    public int groundLevel;
    
    public JFrame frame;
    
    public File inputFile;
    
    public File outputFile;
    
    private File[] files;
    
    private int frameFirst;
    
    private int frameSkip;
    
    private int frameRate;
    
    private int frameThreshold;
    
    

    public void getGround() {
        try{
        	
        	groundLevel = 0;

        	if (!quickTesting) {
        	
        	//chooses the directory in which all the images are located
            //chooser.setCurrentDirectory(new java.io.File("C:\\Users\\CPU-642\\Documents\\Matlab Code"));
        	chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Choose folder with input images (hundreds of jpg's)");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            chooser.setAcceptAllFileFilterUsed(false);
            
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
            	//System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            	inputFile = chooser.getSelectedFile();
            	System.out.println("Selected directory : " + inputFile);
            	System.out.println();
            } else {
            	System.exit(0);
            	inputFile = null;
            }
            
            chooser.setDialogTitle("Choose a folder that the processed images should be saved in");
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
            	//System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            	outputFile = chooser.getSelectedFile();
            	System.out.println("Selected directory : " + outputFile);
            	System.out.println();
            } else {
            	System.exit(0);
            	outputFile = null;
            }
            
        	} else {
        		inputFile = new File("C:\\Users\\CPU-642\\Documents\\Matlab Code\\Test");
        		outputFile = new File("C:\\Users\\CPU-642\\Documents\\Matlab Code\\Output");
        	}
            
            // Reading from System.in for parameters
            Scanner reader = new Scanner(System.in);
            
            System.out.println("First Image Number (Default " + DEFAULT_FIRST + "): ");   
        	frameFirst = DEFAULT_FIRST;
        	try {
        		String line = reader.nextLine();
        		frameFirst = Integer.parseInt(line);
        	} catch (Exception e) {
        		System.out.println("Defaulting to " + DEFAULT_FIRST);
        	}
    		System.out.println("---------------");

        	
        	
        	System.out.println("Skip (Default " + DEFAULT_SKIP + "): ");
        	frameSkip = DEFAULT_SKIP;
        	try {
        		String line = reader.nextLine();
        		frameSkip = Integer.parseInt(line);
        	} catch (Exception e) {
        		System.out.println("Defaulting to " + DEFAULT_SKIP);
        	}
    		System.out.println("---------------");

        	
        	System.out.println("Framerate (Default " + DEFAULT_RATE + "): ");
        	frameRate = DEFAULT_RATE;
        	try {
        		String line = reader.nextLine();
        		frameRate = Integer.parseInt(line);
        	} catch (Exception e) {
        		System.out.println("Defaulting to " + DEFAULT_RATE);
        	}
    		System.out.println("---------------");

        	
        	System.out.println("Threshold (Default " + DEFAULT_THRESHOLD + "): ");
        	frameThreshold = DEFAULT_THRESHOLD;
        	try {
        		String line = reader.nextLine();
        		frameThreshold = Integer.parseInt(line);
        	} catch (Exception e) {
        		System.out.println("Defaulting to " + DEFAULT_THRESHOLD);
        	}
    		System.out.println("---------------");

        	
        	reader.close(); 
        	
        	//loads the images one by one, turns them black and white, then saves them.
        	int count = frameFirst;

        	groundLevel = 0;
        	
        		
        	FileFilter fileFilter = new WildcardFileFilter("*.jpg");
        	files = inputFile.listFiles(fileFilter);
        	BufferedImage firstPhoto = ImageIO.read(files[0]);
        	
			CursorComponent cursorComp = new CursorComponent();
        	cursorComp.setImage(firstPhoto);
			
        	frame = new JFrame();
        	frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        	frame.addMouseMotionListener(new MouseMotionAdapter() {

				@Override
				public void mouseDragged(MouseEvent m) {
				}

				@Override
				public void mouseMoved(MouseEvent m) {
					cursorComp.setVariables(0, m.getY() - 58, frame.getWidth(), m.getY() - 58);
					frame.repaint();
				}
        	});
        	
        	
        	frame.addMouseListener(new ListenerComponent(this));
        	
        	frame.setSize(firstPhoto.getWidth(), firstPhoto.getHeight());
        	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	
        	frame.add(cursorComp);
        	frame.setVisible(true);
            
        } catch(Exception e){e.printStackTrace();}
        
    }
    
    public void setGroundLevel(int level) {
    	groundLevel = level;
    	frame.dispose();
    	System.out.println(groundLevel);
    }
    
    public void processImages() {
		try {
			
			boolean[][][] imgBoolArr = new boolean[files.length][ImageIO.read(files[0]).getWidth()][ImageIO.read(files[0]).getHeight()];
			
			for (int i = 0; i < files.length; i++) {
				BufferedImage bwImage = binarize(ImageIO.read(files[i]), frameThreshold, groundLevel);
				
				if (DEBUGGING) {
					ImageIO.write(bwImage, "png", new File(outputFile.toString() + "\\ATestImage_" + i + ".png"));
				}
				
				int imageWidth = bwImage.getWidth();
				int imageHeight = bwImage.getHeight();
				
				//convert Bufferedimage bwImage to boolean array.
				imgBoolArr[i] = binImg2Bool(bwImage);
				
			}
			
			
			imgBoolArr = floodFill(imgBoolArr);
			
			imgBoolArr = filterConsistentPixels(imgBoolArr);
			
			imgBoolArr = filterOutSpecks(imgBoolArr);
			
			int[][] centersOfMass = findCentersOfMass(imgBoolArr);
			
			exportExcel(centersOfMass);
			/*
			for (int i = 0; i < imgBoolArr.length; i++) {
				int xCoM = centersOfMass[i][0];
				int yCoM = centersOfMass[i][1];
				
				imgBoolArr[i][xCoM][yCoM] = true;
				
				if (DEBUGGING) {
					try {
						ImageIO.write(bool2img(imgBoolArr[i]), "png", new File(outputFile.toString() + "\\CoMTestImage_" + i + ".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			*/
			
			
		} catch (Exception e) {e.printStackTrace();}
	}
    
    public void exportExcel(int[][] centersOfMass) {
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	XSSFSheet sheet = workbook.createSheet("coords");
    	
    	int[][] bookData = centersOfMass;
    	
    	int rowCount = 0;
    	
    	for (int[] aBook : bookData) {
    		Row row = sheet.createRow(++rowCount);
    		
    		int columnCount = 0;
    		
    		for (Object field : aBook) {
    			Cell cell = row.createCell(++columnCount);
    			cell.setCellValue((Integer) field);
    		}
    	}
    	
    	try {
    		FileOutputStream outputStream = new FileOutputStream(outputFile.toString() + "\\Coordinates.xlsx");
    		workbook.write(outputStream);
    	}
    	catch (Exception e) {
    		System.out.println(e);
    	}
    }
    
    public int[][] findCentersOfMass(final boolean[][][] inputBoolArray) {
    	int[][] returnArr = new int[inputBoolArray.length][3];

    	
    	
    	int imageWidth = inputBoolArray[0].length;
    	int imageHeight = inputBoolArray[0][0].length;
    	
    	for (int i = 0; i < inputBoolArray.length; i++) {
        	double pixelCount = 0;

        	int xSum = 0;
        	int ySum = 0;

			for (int x = 0; x < imageWidth; x++) {
				for (int y = 0; y < imageHeight; y++) {
					if (!inputBoolArray[i][x][y]) {
						pixelCount++;
						xSum += x;
						ySum += y;
					}
				}
	    	}
			
			returnArr[i][0] = (int)(xSum/pixelCount);
			returnArr[i][1] = (int)(ySum/pixelCount);
			returnArr[i][2] = i;
			

    	}
    	
    	
    	return returnArr;
    }
    
    public boolean[][][] floodFill(final boolean[][][] inputBoolArr) {
		int imageWidth = inputBoolArr[0].length;
		int imageHeight = inputBoolArr[0][0].length;
		
    	boolean[][][] returnBoolArr = new boolean[files.length][imageWidth][imageHeight];
    	
		for (int i = 0; i < files.length; i++) {

			//makes an all-black array.
			
			boolean[][][] imgBoolArray = new boolean[inputBoolArr.length][imageWidth][imageHeight];
			for (int temp = 0; temp < imageWidth; temp++) {
				returnBoolArr[i][temp][0] = true;
				returnBoolArr[i][temp][imageHeight-1] = true;
			}
			
			//Flood-fills from top to bottom, then bottom to top.
			for (int y = 1; y < imageHeight; y++) {
				for (int x = 1; x < imageWidth - 1; x++) {
					if ((returnBoolArr[i][x-1][y-1] == true || returnBoolArr[i][x][y-1] == true || returnBoolArr[i][x+1][y-1] == true) 
							&& inputBoolArr[i][x][y] == true) {
						returnBoolArr[i][x][y] = true;
					} 
				}
			}
			
			for (int y = imageHeight - 2; y >= 0; y--) {
				for (int x = 1; x < imageWidth - 1; x++) {
					if ((returnBoolArr[i][x-1][y+1] == true || returnBoolArr[i][x][y+1] == true || returnBoolArr[i][x+1][y+1] == true) 
							&& inputBoolArr[i][x][y] == true) {
						returnBoolArr[i][x][y] = true;
					} 
				}
			}
			
			
			//System.out.println(ImageIO.write(bwImage, "png", new File(outputFile.toString() + "\\BTestImage_" + i + ".png")));	
			if (DEBUGGING) {
				try {
					ImageIO.write(bool2img(returnBoolArr[i]), "png", new File(outputFile.toString() + "\\BTestImage_" + i + ".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return returnBoolArr;
    }
    
    public boolean[][][] filterOutSpecks(boolean[][][] inputBoolArr) {
    	int imageWidth = inputBoolArr[0].length;
    	int imageHeight = inputBoolArr[0][0].length;
    	
    	boolean[][][] outputBoolArr = new boolean[inputBoolArr.length][imageWidth][imageHeight];
    	
    	for (int i = 0; i < inputBoolArr.length; i++) {
    		for (int x = 0; x < imageWidth; x++) {
    			for (int y = 0; y < imageHeight; y++) {
    				if (inputBoolArr[i][x][y] == true) {
    					outputBoolArr[i][x][y] = true;
    				} else {
    					if (hasSurrounding(x, y, outputBoolArr[i])) {
    						outputBoolArr[i][x][y] = false;
    					} else {
    						outputBoolArr[i][x][y] = true;
    					}
    				}
    			}
    		}
    		
			if (DEBUGGING) {
				try {
					ImageIO.write(bool2img(outputBoolArr[i]), "png", new File(outputFile.toString() + "\\Speck"
							+ "TestImage_" + i + ".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
    	
    	return outputBoolArr;
    }
    
    public boolean[][][] filterConsistentPixels(boolean[][][] imgBoolArr) {
		//Filters out any pixels that are there for most of the frames, determined my MASK_PERCENT.
		
		int[][] collectiveMask = new int[imgBoolArr[0].length][imgBoolArr[0][0].length];
		for (int i = 0; i < imgBoolArr.length; i++) {
			for (int x = 0; x < collectiveMask.length; x++) {
				for (int y = 0; y < collectiveMask[0].length; y++) {
					if (!imgBoolArr[i][x][y]) {
						collectiveMask[x][y]++;
					}
				}
			}
		}
		
		for (int x = 0; x < collectiveMask.length; x++) {
			for (int y = 0; y < collectiveMask[0].length; y++) {
				if (collectiveMask[x][y] >= MASK_PERCENT * imgBoolArr.length) {
					for (int i = 0; i < imgBoolArr.length; i++) {
						imgBoolArr[i][x][y] = true;
					}
				}
			}
		}
		
		for (int i = 0; i < imgBoolArr.length; i++) {
			for (int x = 0; x < collectiveMask.length; x++) {
				for (int y = 0; y < collectiveMask[0].length; y++) {
					if (!imgBoolArr[i][x][y]) {
						if ( !hasSurrounding(x, y, imgBoolArr[i]) ) {
							imgBoolArr[i][x][y] = true;
						}
					}
				}
			}
		}
		
		for (int i = 0; i < imgBoolArr.length; i++) {
			try {
				ImageIO.write(bool2img(imgBoolArr[i]), "png", new File(outputFile.toString() + "\\CTestImage_" + i + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		return imgBoolArr;
		
    }
    
    public static boolean farEnough(int x, int y, int highZ, int[] prevXY) {
    	return Math.sqrt( Math.pow((x - prevXY[0]), 2) + Math.pow(y - prevXY[1],2)) > highZ;
    }
    
    public static boolean hasSurrounding(int x, int y, boolean[][] boolArr) {
    	int ratio = 10;
    	int count = 0;
    	try {
	    	if (!boolArr[x + ratio][y]) {
	    		count++;
	    	}
	    	if (!boolArr[x - ratio][y]) {
	    		count++;
	    	}
	    	if (!boolArr[x][y + ratio]) {
	    		count++;
	    	}
	    	if (!boolArr[x][y - ratio]) {
	    		count++; 
	    	}
	    	if (!boolArr[(int)(x + ratio * HALFSQR2)][y]) {
	    		count++;
	    	}
	    	if (!boolArr[(int)(x - ratio * HALFSQR2)][y]) {
	    		count++;
	    	}
	    	if (!boolArr[x][(int)(y + ratio * HALFSQR2)]) {
	    		count++;
	    	}
	    	if (!boolArr[x][(int)(y - ratio * HALFSQR2)]) {
	    		count++;
	    	}
    	} catch (Exception e) {
    		return false;
    	}
	    	
    	return count > 2;
    }
    
    //tries to fit the biggest octagonal star.
    public static int biggestOctalFit(int x, int y, boolean[][] boolArr) {
    	
    	int ratio = 1;
    	while (ratio < boolArr.length && ratio < boolArr[0].length)
	    	try {
		    	if (!boolArr[x + ratio][y] && !boolArr[x - ratio][y] && !boolArr[x][y + ratio] && !boolArr[x][y - ratio] && 
		    			!boolArr[(int)(x + ratio * HALFSQR2)][y] && !boolArr[(int)(x - ratio * HALFSQR2)][y] &&
		    			!boolArr[x][(int)(y + ratio * HALFSQR2)] && !boolArr[x][(int)(y - ratio * HALFSQR2)]) {
		    		ratio++;
		    	} else {
		    		return ratio;
		    	}
	    	} catch (Exception e) { return ratio;/*e.printStackTrace(); */}
	    	return ratio;
    	
    }
    
    public static boolean[][] shortToBool(short[][] input) {
    	boolean[][] returnOut = new boolean[input.length][input[0].length];
    	for (int x = 0; x < input.length; x++) {
    		for (int y = 0; y < input[x].length; y++) {
    			returnOut[x][y] = (input[x][y] == WHITE_PIXEL);
    		}
    	}
    	return returnOut;
    }
    
    public boolean[][] binImg2Bool(final BufferedImage bwImage) {
    	int imageHeight = bwImage.getHeight();
    	int imageWidth = bwImage.getWidth();
    	
    	boolean[][] singleBoolArr = new boolean[imageWidth][imageHeight];
    	
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				if (bwImage.getRGB(x, y) == WHITE_PIXEL) {
					singleBoolArr[x][y] = true;
				} else {
					singleBoolArr[x][y] = false;
				}
			}
		}
		
		return singleBoolArr;
    }
    
    public static short[][] copy2DArr(short[][] input) {
    	short[][] returnOut = new short[input.length][input[0].length];
    	for (int x = 0; x < input.length; x++) {
    		for (int y = 0; y < input[x].length; y++) {
    			returnOut[x][y] = input[x][y];
    		}
    	}
    	return returnOut;
    }
    
    public static BufferedImage bool2img (boolean[][] input) {
    	BufferedImage returnOut = new BufferedImage(input.length, input[0].length, BufferedImage.TYPE_BYTE_BINARY);
    	for (int x = 0; x < input.length; x++) {
    		for (int y = 0; y < input[x].length; y++) {
    			if (input[x][y]) {
    				returnOut.setRGB(x, y, WHITE_PIXEL);
    			} else {
    				returnOut.setRGB(x, y, BLACK_PIXEL);
    			}
    		}
    	}
    	return returnOut;
    }
    
    public static BufferedImage arr2img (short[][] input) {
    	BufferedImage returnOut = new BufferedImage(input.length, input[0].length, BufferedImage.TYPE_BYTE_BINARY);
    	for (int x = 0; x < input.length; x++) {
    		for (int y = 0; y < input[x].length; y++) {
    			returnOut.setRGB(x, y, input[x][y]);
    		}
    	}
    	return returnOut;
    }
    
    public static BufferedImage binarize(BufferedImage colorPhoto, int threshold, int groundDepth) {
    	int photoWidth = colorPhoto.getWidth();
    	int photoHeight = colorPhoto.getHeight();
    	
    	BufferedImage returnImage = new BufferedImage(photoWidth, photoHeight,BufferedImage.TYPE_BYTE_BINARY);
    	
    	int color;
    	int newPixel;
    	
    	for (int x = 0; x < photoWidth; x++) {
    		for (int y = 0; y < photoHeight; y++) {
    			color = new Color(colorPhoto.getRGB(x, y)).getRed();
    			
    			if (color > threshold || y > groundDepth) {
    				newPixel = 255;
    			} else {
    				newPixel = 0;
    			}
    			newPixel = colorToRGB(newPixel);
    			returnImage.setRGB(x, y, newPixel);
    		}
    		
    	}
    	return returnImage;
    	
    }
    
    private static int colorToRGB(int binaryVal) {
        int newPixel = 0;
        newPixel += binaryVal; newPixel = newPixel << 8;
        newPixel += binaryVal; newPixel = newPixel << 8;
        newPixel += binaryVal; newPixel = newPixel << 8;
        newPixel += binaryVal;

        return newPixel;
    }

}