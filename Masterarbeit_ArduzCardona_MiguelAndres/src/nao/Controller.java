package nao;

import static utils.GlobalVariables.ARM;
import static utils.GlobalVariables.ARM_MOVEMENT_ACTIVE;
import static utils.GlobalVariables.HAND;
import static utils.GlobalVariables.INITIALIZE;
import static utils.GlobalVariables.MOVE;
import static utils.GlobalVariables.POSTURE;
import static utils.GlobalVariables.SERVER_ACTIVE;
import static utils.GlobalVariables.STOP;
import static utils.GlobalVariables.TURN;
import static utils.GlobalVariables.USER_HEIGHT;
import static utils.GlobalVariables.VR_LIMIT_X;
import static utils.GlobalVariables.VR_LIMIT_Y;
import static utils.GlobalVariables.VR_NEGATIVELIMIT_Z;
import static utils.GlobalVariables.VR_POSITIVELIMIT_Z;
import static utils.GlobalVariables.WALK_MOVEMENT_ACTIVE;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import naoArms.Arms;
import naoLegs.Legs;
import network.NaoServer;

public class Controller {

	private Arms arms;
	private Legs legs;
	private NaoServer server;

	private static Session session;
	private ALMotion motion;
	private ALRobotPosture robotPosture;
	private ALAutonomousLife autonomousLife;

	private String armSide;
	private float armX;
	private float armY;
	private float armZ;
	private float armWX;
	private float armWY;
	private float armWZ;

	public static void main(String[] args) {
		Controller controller = new Controller();
		controller.startServer();
	}

	// Random test comment
	public Controller() {
		server = new NaoServer(this);
		SERVER_ACTIVE = true;
		ARM_MOVEMENT_ACTIVE = true;
	}

	private void startServer() {
		server.run();
	}

	private void initialize(String IP_Address) {
		try {
			session = new Session();
			Future<Void> fut = session.connect(IP_Address);
			fut.get();

			motion = new ALMotion(session);
			robotPosture = new ALRobotPosture(session);
			autonomousLife = new ALAutonomousLife(session);

			arms = new Arms(motion);
			legs = new Legs(motion, robotPosture);

			if (!autonomousLife.getState().equals("disabled")) {
				autonomousLife.setState("disabled");
				motion.wakeUp();
			}

			robotPosture.applyPosture("Stand", 0.5f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runCommand(String[] command) throws Exception {
		switch (command[0]) {
		case INITIALIZE:
			String robotIPAddress = command[1];
			VR_LIMIT_X = Float.parseFloat(command[5]);
			VR_LIMIT_Y = Float.parseFloat(command[2]);
			VR_POSITIVELIMIT_Z = Float.parseFloat(command[3]);
			VR_NEGATIVELIMIT_Z = Float.parseFloat(command[4]) * -1;
			USER_HEIGHT = Float.parseFloat(command[6]);

			initialize("tcp://" + robotIPAddress + ":9559");
			break;

		case MOVE:
			float walkX = Float.parseFloat(command[2]);
			float walkY = Float.parseFloat(command[1]);

			WALK_MOVEMENT_ACTIVE = true;
			// Clear old and unused arm commands
			arms.clearArmCommandsList();

			legs.walkTo(walkX, walkY);
			WALK_MOVEMENT_ACTIVE = false;
			break;

		case ARM:
			armSide = command[1];
			armX = Float.parseFloat(command[4]);
			armY = Float.parseFloat(command[2]);
			armZ = Float.parseFloat(command[3]);
			armWX = Float.parseFloat(command[7]);
			armWY = Float.parseFloat(command[5]);
			armWZ = Float.parseFloat(command[6]);

			arms.moveArm(armSide, armX, armY, armZ, armWX, armWY, armWZ);
			break;

		case HAND:
			String handSide = command[1];
			String handAction = command[2];

			arms.moveHand(handSide, handAction);
			break;

		case TURN:
			float turnTheta = Float.parseFloat(command[1]);

			WALK_MOVEMENT_ACTIVE = true;
			// Clear old and unused arm commands
			arms.clearArmCommandsList();

			legs.turnTo(turnTheta);
			WALK_MOVEMENT_ACTIVE = false;
			break;

		case POSTURE:
			String postureName = command[1];

			robotPosture.applyPosture(postureName, 0.5f);
			break;

		case STOP:
			robotPosture.applyPosture("Stand", 0.5f);
			SERVER_ACTIVE = false;
			ARM_MOVEMENT_ACTIVE = false;
			// server.join();
			break;

		default:
			throw new Exception("Non existent control command");
		}
	}
}