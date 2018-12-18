package naoArms;

import static utils.GlobalVariables.NAO_CENTERPOINT_Z;
import static utils.GlobalVariables.NAO_LIMIT_X;
import static utils.GlobalVariables.NAO_LIMIT_Y;
import static utils.GlobalVariables.NAO_NEGATIVELIMIT_Z;
import static utils.GlobalVariables.NAO_POSITIVELIMIT_Z;
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

import nao.Controller;

public class Arms {

	private Controller controller;
	protected static ALMotion motion;
	protected static CopyOnWriteArrayList<List<Float>> leftArmCommands;
	protected static CopyOnWriteArrayList<List<Float>> rightArmCommands;
	private LeftArmThread leftArmThread;
	private RightArmThread rightArmThread;

	public Arms(Controller controller, ALMotion motion) {
		this.controller = controller;
		Arms.motion = motion;
		leftArmCommands = new CopyOnWriteArrayList<List<Float>>();
		rightArmCommands = new CopyOnWriteArrayList<List<Float>>();

		this.leftArmThread = new LeftArmThread();
		this.rightArmThread = new RightArmThread();

		this.leftArmThread.start();
		this.rightArmThread.start();
	}

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

	public void moveArm(String handSide, float x, float y, float z, float wx, float wy, float wz)
			throws CallError, InterruptedException {
		List<Float> newPosition6D = scaleArm6DPosition(handSide, x, y, z, wx, wy, wz);

		if (handSide.equals("L")) {
			leftArmCommands.add(newPosition6D);
			System.out.println("LArm: " + newPosition6D.toString());
		} else if (handSide.equals("R")) {
			rightArmCommands.add(newPosition6D);
			System.out.println("RArm: " + newPosition6D.toString());
		}
	}

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
			newWX = newWX - 90;
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

	public void clearArmCommandsList() {
		leftArmCommands.clear();
		rightArmCommands.clear();
	}
}
