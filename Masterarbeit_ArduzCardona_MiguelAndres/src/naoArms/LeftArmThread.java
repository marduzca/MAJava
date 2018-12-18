package naoArms;

import static utils.GlobalVariables.ARM_MOVEMENT_ACTIVE;
import static utils.GlobalVariables.WALK_MOVEMENT_ACTIVE;

import com.aldebaran.qi.CallError;

public class LeftArmThread extends Thread {

	@Override
	public void run() {
		try {
			while (ARM_MOVEMENT_ACTIVE) {
				if (!WALK_MOVEMENT_ACTIVE && !Arms.leftArmCommands.isEmpty()) {
					Arms.motion.positionInterpolation("LArm", 2, Arms.leftArmCommands.remove(0), 63, 1.0, true);
				}
			}
		} catch (CallError | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
