package robot;


import java.awt.Point;
import java.util.ArrayList;

import processing.Blur;
import processing.ImageProcessor;
import processing.ShapeFinder;
import saving.ScreenSetup;

public class Main {
	private final static boolean setupPicturePosition = false;
	private final static boolean displayPictures = true, displayFinalPicture = true;
	private final static boolean showLowerCorners = true;
  
	public static void main(String[] args) {
            Comms.init();
            int lastState=-10;
		if (setupPicturePosition) {
			ScreenSetup.setupScreen();
		} 
                else {
                    for (int i = 0; i < 100000; i++) {
                        ScreenSetup.loadData();
			Window.init();
                        
			if (Comms.shouldRunVision()) {//state==1
                            Comms.setState(2);
                            System.out.println("Running vision...");
                            try {
                                runProgram();
                                System.out.println("Done running...");
                                Comms.setState(3);
                            }
                            catch(Exception e) {
                                e.printStackTrace();
                                System.out.println("VISION CRASHED!!!");
                                Comms.setState(-2);
                            }
                        }
                        if (lastState!=Comms.getState()) {
                            lastState=(int)(Comms.getState());
                            System.out.println("Current State:: "+lastState);
                        }
			ScreenSetup.stop(.1);
                        
                    }
		}
                System.out.println("Program is done.");
	}

	private static void runProgram() {
		long startTime=System.currentTimeMillis();
		ScreenSetup.loadData();
		Window.init();
		
		float[][] pixels=Window.getPixels(ScreenSetup.pictureStartX, ScreenSetup.pictureStartY,
				ScreenSetup.pictureEndX, ScreenSetup.pictureEndY);
		pixels=ImageProcessor.scaleImage(pixels, 300);
		float[][] oldPixels=ImageProcessor.scaleImage(pixels, 300);
		if (displayPictures) Window.displayPixels(pixels, "Pre-ExponentialCurve");
                System.out.println("Printing picture");
		
                ImageProcessor.normalize(pixels);
		ImageProcessor.applyExponentialCurve(pixels, 4);
		if (displayPictures); Window.displayPixels(pixels, "Post-ExponentialCurve");
		
                
                ImageProcessor.normalize(pixels);//added
		boolean[][] cutOff=ImageProcessor.applyCutoff(0.4f, true, pixels);
		if (displayPictures) Window.displayPixels(cutOff, "Cuttoff Curve");//added
		
		float[][] corners=ShapeFinder.getPointsWithTopCorner(cutOff);
		ImageProcessor.applyExponentialCurve(corners, 4);
		if (displayPictures) Window.displayPixels(corners, "Likely Corners");
		
		Blur.applyGuassianBlur(corners, 10);
		if (displayPictures) Window.displayPixels(corners, "Likely Corners, Blurred");
		
		ImageProcessor.normalize(corners);
		ImageProcessor.applyExponentialCurve(corners, 4);
		if (displayPictures) Window.displayPixels(corners, "Likely Corners, Normalized");
		
		//curve, blur, normalize, curve
                float[][] bottomRightCorner=null, bottomLeftCorner=null;
		if (showLowerCorners) {
                    bottomRightCorner=ShapeFinder.getPointsWithBottomRightCorner(cutOff);
                    ImageProcessor.applyExponentialCurve(bottomRightCorner, 4);//curve
                    Blur.applyGuassianBlur(bottomRightCorner, 10);//blur
                    ImageProcessor.normalize(bottomRightCorner);//normalize
                    ImageProcessor.applyExponentialCurve(bottomRightCorner, 4);//curve
                    if (displayPictures) Window.displayPixels(bottomRightCorner, "Bottom Right Corner");
		
                    bottomLeftCorner=ShapeFinder.getPointsWithBottomLeftCorner(cutOff);
                    ImageProcessor.applyExponentialCurve(bottomLeftCorner, 4);//curve
                    Blur.applyGuassianBlur(bottomLeftCorner, 10);//blur
                    ImageProcessor.normalize(bottomLeftCorner);//normalize
                    ImageProcessor.applyExponentialCurve(bottomLeftCorner, 4);//curve
                    if (displayPictures) Window.displayPixels(bottomLeftCorner, "Bottom Left Corner");
		}
		//get the peaks and combine them
		ArrayList<Point> peaks=ShapeFinder.getPeaks(corners);
		ArrayList<Point> bottomRightPeak=ShapeFinder.getPeaks(bottomRightCorner);
		ArrayList<Point> bottomLeftPeak=ShapeFinder.getPeaks(bottomLeftCorner);
		ArrayList<Point> combined=new ArrayList<Point>();
		combined.addAll(peaks);
		combined.addAll(bottomRightPeak);
		combined.addAll(bottomLeftPeak);
		if (peaks.size()!=2) {
			System.err.println("Error: not exectly 2 peaks found");
		}
		else {
			Point left=peaks.get(0), right=peaks.get(1);
			Point target=new Point((left.x+right.x)/2, (left.y+right.y)/2);
			if (displayPictures||displayFinalPicture) Window.displayPixelsWithPeaks(oldPixels, combined, target, "Found it?");
			
			float degreesToTurn=Turing.getDegreesToTurn(pixels.length, target);
			System.out.println("*** Turning: "+degreesToTurn+" degrees.***");
                        
			double distance=Turing.getDistanceFromGoal(pixels.length, pixels[0].length, target);
                        System.out.println("*** Distance: "+distance+" inches.***");
                        
                        Comms.setAngle(0);//degreesToTurn);
                        Comms.setDistance(0);//distance);
		}
		
		System.out.println("Took "+(0.0+System.currentTimeMillis()-startTime)/1000+" seconds.");
		
	}

}
