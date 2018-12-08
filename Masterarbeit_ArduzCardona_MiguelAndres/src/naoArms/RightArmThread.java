package naoArms;

import static utils.GlobalVariables.ARM_MOVEMENT_ACTIVE;

import com.aldebaran.qi.CallError;

public class RightArmThread extends Thread{

	@Override
	public void run() {
		try {
			while (ARM_MOVEMENT_ACTIVE) {
				if (!Arms.rightArmCommands.isEmpty()) {
					Arms.motion.positionInterpolation("RArm", 2, Arms.rightArmCommands.remove(0), 63, 1.0, true);
				}
			}
		} catch (CallError | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
