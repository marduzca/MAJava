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

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;

import nao.Controller;

public class Arms {
	
	private Controller controller;
	private ALMotion motion;
	private List<List<Float>> leftArmCommands;
	private List<List<Float>> rightArmCommands;

	public Arms(Controller controller, ALMotion motion) {
		this.controller = controller;
		this.motion = motion;
	}
	
	public void moveArm(String side, float x, float y, float z, float wx, float wy, float wz) throws CallError, InterruptedException {
		List<Float> newPosition6D = scaleArm6DPosition(side, x, y, z, wx, wy, wz);

		if (side.equals("L")) {
			System.out.println("LArm: " + newPosition6D.toString());
			leftArmCommands.add(newPosition6D);
			
			motion.positionInterpolation("LArm", 2, newPosition6D, 63, 1.0, true);
		} else if (side.equals("R")) {
			System.out.println("RArm: " + newPosition6D.toString());
			rightArmCommands.add(newPosition6D);

			motion.positionInterpolation("RArm", 2, newPosition6D, 63, 1.0, true);
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
		
		//Scale 3D positions
		newX = (x / VR_LIMIT_X) * NAO_LIMIT_X;
		
		if(side.equals("L")) {
			newY = (-y / VR_LIMIT_Y) * NAO_LIMIT_Y;
		}
		else if(side.equals("R")) {
			newY = ((y / VR_LIMIT_Y) * NAO_LIMIT_Y) * -1;
		}
		
		if(z > 0) {
			newZ = NAO_CENTERPOINT_Z + ((z / VR_POSITIVELIMIT_Z) * NAO_POSITIVELIMIT_Z);
		}
		else if (z < 0) {
			z = z * -1;
			newZ = NAO_CENTERPOINT_Z - ((z / VR_NEGATIVELIMIT_Z) * NAO_NEGATIVELIMIT_Z);
		}
		
		//Scale rotations
		if(wx > 180 && wx < 360) {
			newWX = (360 - wx) * -1;
		}
		else if(wx > 0 && wx < 180) {
			newWX = wx;
		}
		
		if(side.equals("R")) {
			newWX = (newWX * -1) + 90;
		}
		else if(side.equals("L")) {
			newWX = newWX - 90;
		}
		
		if(wy > 180 && wy < 360) {
			newWY = (360 - wy) * -1;
		}
		else if(wy > 0 && wy < 180) {
			newWY = wy;
		}
		
		if(wz > 180 && wz < 360) {
			newWZ = 360 - wz;
		}
		else if(wz > 0 && wz < 180) {
			newWZ = wz * -1;
		}
		
		position6D.add(newX);
		position6D.add(newY);
		position6D.add(newZ);
		position6D.add(toFloatRadians(newWX));
		position6D.add(toFloatRadians(newWY));
		position6D.add(toFloatRadians(newWZ));

		
		return position6D;
	}
	
	/**
	 * Converts an angle from degrees to radians
	 * 
	 * @param degree - Degree to be transformed
	 * @return Same degree in radians
	 */
	private float toFloatRadians(double degree) {

		return (float) Math.toRadians(degree);
		
	}
}
