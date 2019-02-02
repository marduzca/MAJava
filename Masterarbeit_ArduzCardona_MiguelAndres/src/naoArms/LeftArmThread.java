package naoArms;

import static utils.GlobalVariables.ARM_MOVEMENT_ACTIVE;
import static utils.GlobalVariables.WALK_MOVEMENT_ACTIVE;

import com.aldebaran.qi.CallError;

/**
 * Thread to deal with the requests for the left arm. As soon as a new command
 * has been added to the correspondent list, it gets executed.
 * 
 * @author Miguel Arduz
 *
 */
public class LeftArmThread extends Thread {

	/**
	 * A constant loop is run checking for any available arm movement commands in
	 * the correspondent list. As soon as a command gets added, it gets executed and
	 * removed from the list. The loop is only stopped when the flag gets
	 * deactivated and that happends only when the system is being turned off.
	 */
	@Override
	public void run() {
		try {
			while (ARM_MOVEMENT_ACTIVE) {
				if (!WALK_MOVEMENT_ACTIVE && !ArmMovementHandler.leftArmCommands.isEmpty()) {
					ArmMovementHandler.motion.positionInterpolation("LArm", 2,
							ArmMovementHandler.leftArmCommands.remove(0), 63, 1.0, true);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// Do nothing, internal irrelevant error
		} catch (CallError | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
