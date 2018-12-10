package naoLegs;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;

import nao.Controller;

import static utils.GlobalVariables.*;

public class Legs {
	
	private Controller controller;
	private ALMotion motion;
	private CopyOnWriteArrayList<List<Float>> walkCommands;

	public Legs(Controller controller, ALMotion motion) {
		this.controller = controller;
		this.motion = motion;
		
		this.walkCommands = new CopyOnWriteArrayList<List<Float>>();
	}

	public void walkTo(float x, float y) {
		try {
			float[] walkingCoordinates = scaleWalkingCoordinates(x, y);
			x = walkingCoordinates[0];
			y = walkingCoordinates[1];

			motion.moveTo(x, y, 0f);	
			System.out.println("MoveTo: " + x + ", " + y);
		} catch (CallError | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void turnTo(float theta) {

	}
	
	private float[] scaleWalkingCoordinates(float x, float y) {
		x = (x / USER_HEIGHT) * NAO_HEIGHT;
		y = ((y / USER_HEIGHT) * NAO_HEIGHT) * -1;
		
		return new float[] {x, y};
	}
}
