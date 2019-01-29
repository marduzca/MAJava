package naoLegs;

import static utils.GlobalVariables.NAO_HEIGHT;
import static utils.GlobalVariables.USER_HEIGHT;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import utils.GlobalVariables;
import utils.Util;

public class Legs {

	private ALMotion motion;
	private ALRobotPosture robotPosture;

	public Legs(ALMotion motion, ALRobotPosture robotPosture) {
		this.motion = motion;
		this.robotPosture = robotPosture;
	}

	public void walkTo(float x, float y) throws CallError, InterruptedException {
		robotPosture.applyPosture("StandInit", 0.7f);

		float[] walkingCoordinates = new float[2];

		walkingCoordinates = scaleWalkingCoordinates(x, y, GlobalVariables.SCALED_WALK);

		x = walkingCoordinates[0];
		y = walkingCoordinates[1];

		motion.moveTo(x, y, 0f);
	}
	
	public void turnTo(float turnTheta) throws CallError, InterruptedException {

		robotPosture.applyPosture("StandInit", 0.7f);
		motion.moveTo(0f, 0f, Util.toFloatRadians(turnTheta));
	}
	
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
