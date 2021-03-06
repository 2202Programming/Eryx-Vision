package robot;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import saving.ScreenSetup;

public class Window {
	private static Robot r2d2;
	private static boolean useSameWindow = true;
	private static JPanel panel;

        private static final boolean rotateImage=false;
        
	private static int x = 0, y = 50;

	public static void init() {
		try {
			r2d2 = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static float[][] getPixels(int startX, int startY, int endX, int endY) {
		Rectangle circle = new Rectangle(startX, startY, endX - startX, endY - startY);
		BufferedImage bufferedImage = r2d2.createScreenCapture(circle);
		float[][] brightness = new float[bufferedImage.getWidth()][bufferedImage.getHeight()];

		for (int x = 0; x < bufferedImage.getWidth(); x++) {
			for (int y = 0; y < bufferedImage.getHeight(); y++) {
				brightness[x][y] = getLumanence(x, y, bufferedImage);
			}
		}
                if (rotateImage) {
                    brightness=rotatePixels270Degrees(brightness);
                }
		return brightness;
	}

        private static float[][] rotatePixels270Degrees(float[][] brightness) {
            float[][] newBrightness=new float[brightness[0].length][brightness.length];
            //fix new array
            
            for (int x=0; x<brightness.length; x++) {
                for (int y=0; y<brightness[x].length; y++) {
                    newBrightness[newBrightness.length-y-1][x]=brightness[x][y];
                }
            }
            
            return newBrightness;
        }
        
	private static float getLumanence(int x, int y, BufferedImage image) {
		Color color = new Color(image.getRGB(x, y));
		// I have no idea where these magic numbers came from. I just kinda
		// copied them from a formula on Wikepedia.
                float redValue=-0.2f;//0.2126f;
                float greenValue= 1.0f;//0.7152f;
                float blueValue= -0.2f;//0.0722f;
		return Math.max(0, redValue * color.getRed() / 255 + greenValue * color.getGreen() / 255 + blueValue * color.getBlue() / 255);
	}

	public static void displayPixels(float[][] pixels, String pictureName) {
		BufferedImage toDraw = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < pixels.length; x++) {
			for (int y = 0; y < pixels[x].length; y++) {
				Color inRGB = new Color(pixels[x][y], pixels[x][y], pixels[x][y]);
				toDraw.setRGB(x, y, inRGB.getRGB());
			}
		}
		drawImage(toDraw, pictureName);
	}

	public static void displayPixels(boolean[][] pixels, String pictureName) {
		BufferedImage toDraw = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < pixels.length; x++) {
			for (int y = 0; y < pixels[x].length; y++) {
				Color inRGB;
				if (pixels[x][y]) {
					inRGB = Color.yellow;
				} else {
					inRGB = Color.black;
				}
				toDraw.setRGB(x, y, inRGB.getRGB());
			}
		}
		drawImage(toDraw, pictureName);
	}

	public static void displayPixelsWithPeaks(float[][] pixels, ArrayList<Point> peaks, Point target,
			String pictureName) {
		BufferedImage toDraw = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < pixels.length; x++) {
			for (int y = 0; y < pixels[x].length; y++) {
				Color inRGB = new Color(pixels[x][y], pixels[x][y], pixels[x][y]);
				toDraw.setRGB(x, y, inRGB.getRGB());
			}
		}
		System.out.println("Number of Peaks: " + peaks.size());
		Color peakColor = Color.red, targetColor = Color.green;
		for (Point peak : peaks) {
			toDraw.setRGB(peak.x, peak.y, peakColor.getRGB());
			toDraw.setRGB(peak.x - 1, peak.y, peakColor.getRGB());
			toDraw.setRGB(peak.x + 1, peak.y, peakColor.getRGB());
			toDraw.setRGB(peak.x, peak.y - 1, peakColor.getRGB());
			toDraw.setRGB(peak.x, peak.y + 1, peakColor.getRGB());
		}

		for (int x = -5; x <= 5; x++) {
			for (int y = -5; y <= 5; y++) {
				if (Math.max(Math.abs(x), Math.abs(y)) % 2 == 0)
					toDraw.setRGB(target.x + x, target.y + y, targetColor.getRGB());
			}
		}
		drawImage(toDraw, pictureName);
	}

	private static void drawImage(BufferedImage i, String pictureName) {
		if (!useSameWindow || panel == null) {
			panel = new JPanel();
			panel.setPreferredSize(new Dimension(i.getWidth(), i.getHeight()));
			JFrame frame = new JFrame();
			frame.setTitle(pictureName);
			frame.add(panel);
			frame.pack();
			frame.setResizable(false);
			if (!useSameWindow) {
				frame.setLocation(x, y);
			} else {
				frame.setLocation(1000, 200);
			}
			x += i.getWidth();
			if (x > i.getWidth() * 3) {
				x = 0;
				y += i.getHeight();
			}
			frame.setForeground(Color.black);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			ScreenSetup.stop(.1);
		}

		Graphics graphics = panel.getGraphics();
		graphics.drawImage(i, 0, 0, null);
	}

	/*public static float getAverageInputColor() {
		float[][] screen = getPixels(ScreenSetup.readStartX, ScreenSetup.readStartY, ScreenSetup.readEndX,
				ScreenSetup.readEndY);
		float average = 0;
		for (int x = 0; x < screen.length; x++) {
			for (int y = 0; y < screen[x].length; y++) {
				average += screen[x][y];
			}
		}
		average /= screen.length * screen[0].length;
		return average;
	}

	public static void typeDegrees(float degrees) {
		r2d2.mouseMove(ScreenSetup.boxX, ScreenSetup.boxY);
		r2d2.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		r2d2.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		r2d2.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		r2d2.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		String asString = "" + degrees;
		StringSelection stringSelection = new StringSelection(asString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, stringSelection);

		r2d2.keyPress(KeyEvent.VK_CONTROL);
		r2d2.keyPress(KeyEvent.VK_V);
		r2d2.keyRelease(KeyEvent.VK_V);
		r2d2.keyRelease(KeyEvent.VK_CONTROL);
		
		r2d2.keyPress(KeyEvent.VK_ENTER);
		r2d2.keyRelease(KeyEvent.VK_ENTER);
	}*/
}
