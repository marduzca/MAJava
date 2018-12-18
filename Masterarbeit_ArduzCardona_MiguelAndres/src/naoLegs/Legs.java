package naoLegs;

import static utils.GlobalVariables.NAO_HEIGHT;
import static utils.GlobalVariables.USER_HEIGHT;

import java.awt.peer.RobotPeer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import nao.Controller;
import utils.Util;

public class Legs {

	private Controller controller;
	private ALMotion motion;
	private ALRobotPosture robotPosture;

	public Legs(Controller controller, ALMotion motion, ALRobotPosture robotPosture) {
		this.controller = controller;
		this.motion = motion;
		this.robotPosture = robotPosture;
	}

	public void walkTo(float x, float y) {
		try {
			robotPosture.applyPosture("StandInit", 0.7f);
			float[] walkingCoordinates = scaleWalkingCoordinates(x, y);
			x = walkingCoordinates[0];
			y = walkingCoordinates[1];

			motion.moveTo(x, y, 0f);
			System.out.println("MoveTo: " + x + ", " + y);
		} catch (CallError | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void turnTo(float turnTheta) {
		try {
			robotPosture.applyPosture("StandInit", 0.7f);
			motion.moveTo(0f, 0f, Util.toFloatRadians(turnTheta));
		} catch (CallError | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private float[] scaleWalkingCoordinates(float x, float y) {
		x = (x / USER_HEIGHT) * NAO_HEIGHT;
		y = ((y / USER_HEIGHT) * NAO_HEIGHT) * -1;

		return new float[] { x, y };
	}
}
