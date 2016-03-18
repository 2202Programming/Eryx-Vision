
package robot;

import java.awt.Point;

public class Turing {
	private static final float fieldOfView = 47.15f;
	private static final float heightToTopOfUFromGround = 96, heightOfCameraFromGround = 17, degreesOfMiddleOfFOV = 42;
        
        private static final boolean displayDistanceValues=false; 

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

        /**
         * Gets the distance from the goal and displays the information to the consol if displayDistanceValues is true
         * @param width
         * The width of the picture
         * @param height'
         * The height of the picture
         * @param center
         * The center of the goal, as a point
         * @return 
         * The distance from the goal
         */
	public static double getDistanceFromGoal(float width, float height, Point center) {
		float yFOV = fieldOfView * height / width;
		float heightOfCenter = (height - center.y);
		float angleInCamera = yFOV * (heightOfCenter / height);
		float angleOfBottomOfCamera = degreesOfMiddleOfFOV - (yFOV/2);
		float theta = angleInCamera + angleOfBottomOfCamera;
                float h=(heightToTopOfUFromGround - heightOfCameraFromGround);
                
                if (displayDistanceValues) {
                    System.out.println("height" + height);
                    System.out.println(heightOfCenter);
                    System.out.println(angleInCamera);
                    System.out.println(angleOfBottomOfCamera);
                    System.out.println("theta" + theta);
                    System.out.println(h + "h");
                }
                
		return (h / (Math.tan(theta*(Math.PI/180)))*1.7);
	}
}
