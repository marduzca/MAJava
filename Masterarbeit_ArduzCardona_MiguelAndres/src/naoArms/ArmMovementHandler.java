package naoArms;

import static utils.GlobalVariables.VR_LIMIT_X;
import static utils.GlobalVariables.VR_LIMIT_Y;
import static utils.GlobalVariables.VR_NEGATIVELIMIT_Z;
import static utils.GlobalVariables.VR_POSITIVELIMIT_Z;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;

import utils.Util;

/**
 * ArmMovementHandler takes care of handling all the requests that have anything
 * to do with arm movements, including hand movements. The values are received
 * and first scaled to fit the values required for the robot. Afterward the
 * request is executed.
 * 
 * @author Miguel Arduz
 *
 */
public class ArmMovementHandler {

	protected static ALMotion motion;
	protected static CopyOnWriteArrayList<List<Float>> leftArmCommands;
	protected static CopyOnWriteArrayList<List<Float>> rightArmCommands;
	private LeftArmThread leftArmThread;
	private RightArmThread rightArmThread;

	private final float NAO_LIMIT_X = 0.21f;
	private final float NAO_LIMIT_Y = 0.32f;
	private final float NAO_POSITIVELIMIT_Z = 0.17f;
	private final float NAO_NEGATIVELIMIT_Z = 0.27f;
	private final float NAO_CENTERPOINT_Z = 0.48f;

	/**
	 * Normal constructor that initializes all important elements of the class and
	 * starts both threads.
	 * 
	 * @param motion Motion module from the robot to control movements
	 */
	public ArmMovementHandler(ALMotion motion) {
		ArmMovementHandler.motion = motion;
		leftArmCommands = new CopyOnWriteArrayList<List<Float>>();
		rightArmCommands = new CopyOnWriteArrayList<List<Float>>();

		this.leftArmThread = new LeftArmThread();
		this.rightArmThread = new RightArmThread();

		this.leftArmThread.start();
		this.rightArmThread.start();
	}

	/**
	 * Handles the arm movement requests. First the values are scaled to get a new
	 * 6D position and then the scaled command is added to the correspondent list,
	 * depending on what arm it is for.
	 * 
	 * @param handSide Arm the request is for
	 * @param x        X axis of the 3D position
	 * @param y        Y axis of the 3D position
	 * @param z        Z axis of the 3D position
	 * @param wx       X axis of the 3D rotation
	 * @param wy       Y axis of the 3D rotation
	 * @param wz       Z axis of the 3D rotation
	 * @throws CallError            Triggered if there are problems internally in
	 *                              the robot's library
	 * @throws InterruptedException Triggered if there are problems internally in
	 *                              the robot's library
	 */
	public void moveArm(String handSide, float x, float y, float z, float wx, float wy, float wz)
			throws CallError, InterruptedException {
		List<Float> newPosition6D = scaleArm6DPosition(handSide, x, y, z, wx, wy, wz);

		if (handSide.equals("L")) {
			leftArmCommands.add(newPosition6D);
		} else if (handSide.equals("R")) {
			rightArmCommands.add(newPosition6D);
		}
	}

	/**
	 * Handles the hand movement requests. Based on the given parameters it decided
	 * wether to open or close the correspondent hand.
	 * 
	 * @param handSide   Hand the request is for
	 * @param handAction Action to be performed for the hand (open or close)
	 * @throws CallError            Triggered if there are problems internally in
	 *                              the robot's library
	 * @throws InterruptedException Triggered if there are problems internally in
	 *                              the robot's library
	 */
	public void moveHand(String handSide, String handAction) throws CallError, InterruptedException {
		List<Float> handAngles = new ArrayList<>();

		if (handSide.equals("L")) {

			handAngles = Util.getAnglesFrom("LHand", motion);

			if (handAction.equals("open")) {
				// Open hand
				handAngles.set(0, handAngles.get(0) - Util.toFloatRadians(-70));
				motion.setAngles("LHand", handAngles, 0.3f);
			} else {
				// Close hand
				handAngles.set(0, handAngles.get(0) - Util.toFloatRadians(50));
				motion.setAngles("LHand", handAngles, 0.3f);
			}

		} else if (handSide.equals("R")) {

			handAngles = Util.getAnglesFrom("RHand", motion);

			if (handAction.equals("open")) {
				// Open hand
				handAngles.set(0, handAngles.get(0) - Util.toFloatRadians(-70));
				motion.setAngles("RHand", handAngles, 0.3f);
			} else {
				// Close hand
				handAngles = Util.getAnglesFrom("RHand", motion);
				handAngles.set(0, handAngles.get(0) - Util.toFloatRadians(50));
				motion.setAngles("RHand", handAngles, 0.3f);
			}
		}
	}

	/**
	 * The method gets the 6D position incoming from the client and transforms it to
	 * the mapped 6D position for the robot. Depending on the side of the arm it
	 * does some computations to define the new positions. The computations are done
	 * with help of the measure data received from the client and with other measure
	 * data already present in the system.
	 * 
	 * @param side Arm the request is for
	 * @param x    X axis of the 3D position
	 * @param y    Y axis of the 3D position
	 * @param z    Z axis of the 3D position
	 * @param wx   X axis of the 3D rotation
	 * @param wy   Y axis of the 3D rotation
	 * @param wz   Z axis of the 3D rotation
	 * @return The scaled 6D position that can be directly applied to the robot
	 */
	private List<Float> scaleArm6DPosition(String side, float x, float y, float z, float wx, float wy, float wz) {
		List<Float> position6D = new ArrayList<Float>();

		float newX = 0;
		float newY = 0;
		float newZ = 0;
		float newWX = 0;
		float newWY = 0;
		float newWZ = 0;

		// Scale 3D positions
		newX = (x / VR_LIMIT_X) * NAO_LIMIT_X;

		if (side.equals("L")) {
			newY = (-y / VR_LIMIT_Y) * NAO_LIMIT_Y;
		} else if (side.equals("R")) {
			newY = ((y / VR_LIMIT_Y) * NAO_LIMIT_Y) * -1;
		}

		if (z > 0) {
			newZ = NAO_CENTERPOINT_Z + ((z / VR_POSITIVELIMIT_Z) * NAO_POSITIVELIMIT_Z);
		} else if (z < 0) {
			z = z * -1;
			newZ = NAO_CENTERPOINT_Z - ((z / VR_NEGATIVELIMIT_Z) * NAO_NEGATIVELIMIT_Z);
		}

		// Scale rotations
		if (wx > 180 && wx < 360) {
			newWX = (360 - wx) * -1;
		} else if (wx > 0 && wx < 180) {
			newWX = wx;
		}

		if (side.equals("R")) {
			newWX = (newWX * -1) + 90;
		} else if (side.equals("L")) {
			newWX = (newWX * -1) - 90;
		}

		if (wy > 180 && wy < 360) {
			newWY = (360 - wy) * -1;
		} else if (wy > 0 && wy < 180) {
			newWY = wy;
		}

		if (wz > 180 && wz < 360) {
			newWZ = 360 - wz;
		} else if (wz > 0 && wz < 180) {
			newWZ = wz * -1;
		}

		position6D.add(newX);
		position6D.add(newY);
		position6D.add(newZ);
		position6D.add(Util.toFloatRadians(newWX));
		position6D.add(Util.toFloatRadians(newWY));
		position6D.add(Util.toFloatRadians(newWZ));

		return position6D;
	}

	/**
	 * Allows the Controller to clear both lists easily when the values are not
	 * necessary anymore.
	 */
	public void clearArmCommandsList() {
		leftArmCommands.clear();
		rightArmCommands.clear();
	}
}