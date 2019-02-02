package naoLegs;

import static utils.GlobalVariables.USER_HEIGHT;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import utils.Util;

/**
 * NavigationHandler takes care of handling all the requests that have anything
 * to do with leg movements, including turns. The values are received and first
 * scaled (if necessary) to create equal values relative to the robot's size.
 * Afterward the request is executed.
 * 
 * @author Miguel Arduz
 *
 */
public class NavigationHandler {

	private ALMotion motion;
	private ALRobotPosture robotPosture;

	private final float NAO_HEIGHT = 0.58f;
	private boolean SCALED_WALK = true;

	/**
	 * Normal structure that initializes all important elements of the class.
	 * 
	 * @param motion       Motion module of the robot
	 * @param robotPosture Posture module of the robot
	 */
	public NavigationHandler(ALMotion motion, ALRobotPosture robotPosture) {
		this.motion = motion;
		this.robotPosture = robotPosture;
	}

	/**
	 * Handles the walk request by first scaling the given values (if necessary) and
	 * then executing the movement in the robot.
	 * 
	 * @param x Displacement on the X axis
	 * @param y Displacement on the Y axis
	 * @throws CallError            Triggered if there are problems internally in
	 *                              the robot's library
	 * @throws InterruptedException Triggered if there are problems internally in
	 *                              the robot's library
	 */
	public void walkTo(float x, float y) throws CallError, InterruptedException {
		robotPosture.applyPosture("StandInit", 0.7f);

		float[] walkingCoordinates = new float[2];

		walkingCoordinates = scaleWalkingCoordinates(x, y, SCALED_WALK);

		x = walkingCoordinates[0];
		y = walkingCoordinates[1];

		motion.moveTo(x, y, 0f);
	}

	/**
	 * Handles the turn requests by just executing the movement command on the robot
	 * with the given value. The direction and angle of the turn is defined already
	 * on the client side.
	 * 
	 * @param turnTheta Angle of turn
	 * @throws CallError            Triggered if there are problems internally in
	 *                              the robot's library
	 * @throws InterruptedException Triggered if there are problems internally in
	 *                              the robot's library
	 */
	public void turnTo(float turnTheta) throws CallError, InterruptedException {

		robotPosture.applyPosture("StandInit", 0.7f);
		motion.moveTo(0f, 0f, Util.toFloatRadians(turnTheta));
	}

	/**
	 * Scales the given displacement values depending on the user's and the robot's
	 * height. This is only done if the feature for it is enabled.
	 * 
	 * @param x           Displacement on the X axis
	 * @param y           Displacement on the Y axis
	 * @param scaleHeight True if the values should be scaled
	 * @return The scaled displacement values that can directly be applied to the
	 *         robot
	 */
	private float[] scaleWalkingCoordinates(float x, float y, boolean scaleHeight) {
		if (scaleHeight) {
			x = (x / USER_HEIGHT) * NAO_HEIGHT;
			y = ((y / USER_HEIGHT) * NAO_HEIGHT) * -1;
		} else {
			y *= -1;
		}

		return new float[] { x, y };
	}
}