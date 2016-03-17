
package robot;

import java.awt.Point;

public class Turing {
	private static float fieldOfView = 47.15f;
	private static final float heightToTopOfUFromGround = 96, heightOfCameraFromGround = 17, degreesOfBottomOfFOV = 42;

	/**
	 * Gets the number of degrees to turn to the right.
	 * 
	 * @param width
	 *            The width of the screen
	 * @param center
	 *            The point representing the center of the goal
	 * @return The number of degrees to turn to the right.
	 */
	public static float getDegreesToTurn(float width, Point center) {
		return fieldOfView * (center.x / width - 0.5f);
	}

	public static double getDistanceFromGoal(float width, float height, Point center) {
		float yFOV = fieldOfView * height / width;
		float x = height - (height - center.y);
		float z = yFOV * (x / height);
		float y = degreesOfBottomOfFOV - (0.5f * yFOV);
		float q = y + z;
		//float q = degreesOfBottomOfFOV - (0.5f * yFOV) + yFOV * (height - (height - center.y) / height);
		return ((heightToTopOfUFromGround - heightOfCameraFromGround) / Math.tan(q));
	}
}
