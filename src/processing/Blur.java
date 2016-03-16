package processing;

public class Blur {
	
	public static void applyGuassianBlur(float[][] image, int size) {
		if (size<1||size>10) {
			System.err.println("Error: Guassian blur size should be between 0 and 10, both exclusive");
		}
		
		float[][] blurredImage=new float[image.length][image[0].length];
		
		for (int x=0; x<image.length; x++) {
			for (int y=0; y<image[x].length; y++) {
				blurredImage[x][y]=applyBlurToPixel(image, x, y, size);
			}
		}
		
		for (int x=0; x<image.length; x++) {
			for (int y=0; y<image[x].length; y++) {
				image[x][y]=blurredImage[x][y];
			}
		}
	}
	
	private static float applyBlurToPixel(float[][] image, int x, int y, int size) {
		float total=0, maxTotal=0;
		if (size>x||size>y||x+size>=image.length||y+size>=image[0].length) {
			return 0f;
		}
		for (int dX=-size; dX<=size; dX++) {
			for (int dY=-size; dY<=size; dY++) {
				total+=image[x+dX][y+dY]*(2*size-Math.abs(dX)-Math.abs(dY));
				maxTotal+=2*size-Math.abs(dX)-Math.abs(dY);
			}
		}
		return total/maxTotal;
	}
}
