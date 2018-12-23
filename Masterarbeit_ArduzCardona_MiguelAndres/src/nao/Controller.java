package nao;

import static utils.GlobalVariables.ARM;
import static utils.GlobalVariables.ARM_MOVEMENT_ACTIVE;
import static utils.GlobalVariables.HAND;
import static utils.GlobalVariables.INITIALIZE;
import static utils.GlobalVariables.MOVE;
import static utils.GlobalVariables.NAOMI_IP;
import static utils.GlobalVariables.SERVER_ACTIVE;
import static utils.GlobalVariables.STOP;
import static utils.GlobalVariables.TURN;
import static utils.GlobalVariables.WALK_MOVEMENT_ACTIVE;
import static utils.GlobalVariables.*;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import naoArms.Arms;
import naoLegs.Legs;
import server.NaoServer;
import utils.GlobalVariables;

public class Controller {

	private Arms arms;
	private Legs legs;
	private NaoServer server;

	private static Session session;
	private ALMemory memory;
	private ALMotion motion;
	private ALTextToSpeech textToSpeech;
	private ALRobotPosture robotPosture;
	private ALAutonomousLife autonomousLife;
	
	public static void main(String[] args) {
		Controller controller = new Controller();
		//controller.initialize(NAOMI_IP);
		controller.startServer();
	}

	// Random test comment
	public Controller() {
		server = new NaoServer(this);
		SERVER_ACTIVE = true;
		ARM_MOVEMENT_ACTIVE = true;
	}

	private void startServer() {
		// server.start();
		server.run();
	}

	private void initialize(String IP_Address) {
		try {
			session = new Session();
			Future<Void> fut = session.connect(IP_Address);
			fut.get();

			motion = new ALMotion(session);
			memory = new ALMemory(session);
			textToSpeech = new ALTextToSpeech(session);
			robotPosture = new ALRobotPosture(session);
			autonomousLife = new ALAutonomousLife(session);

			arms = new Arms(this, motion);
			legs = new Legs(this, motion, robotPosture);

			if (!autonomousLife.getState().equals("disabled")) {
				autonomousLife.setState("disabled");
				motion.wakeUp();
			}

			robotPosture.applyPosture("Stand", 0.5f);
			
			System.out.println("Limit x: " + VR_LIMIT_X + "\nLimit y: " + VR_LIMIT_Y + "\nLimit posi z: " + VR_POSITIVELIMIT_Z + "\nLimit nega z" + VR_NEGATIVELIMIT_Z + "\nHeight: " + USER_HEIGHT);
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
			String armSide = command[1];
			float armX = Float.parseFloat(command[4]);
			float armY = Float.parseFloat(command[2]);
			float armZ = Float.parseFloat(command[3]);
			float armWX = Float.parseFloat(command[7]);
			float armWY = Float.parseFloat(command[5]);
			float armWZ = Float.parseFloat(command[6]);

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
