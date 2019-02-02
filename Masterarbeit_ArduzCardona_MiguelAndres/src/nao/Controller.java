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

import naoArms.ArmMovementHandler;
import naoLegs.NavigationHandler;
import network.NaoServer;

/**
 * Controller is the most important class in the remote system. It serves
 * basically as the interface that connects the requests with the actual
 * functionality of the robot. It also takes care of initializing the system and
 * establishing the connection to the robot.
 * 
 * @author Miguel Arduz
 *
 */
public class Controller {

	private ArmMovementHandler armMovements;
	private NavigationHandler navigation;
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

	/**
	 * Constructor that enables the flag for the server and for the arm threads to
	 * run.
	 */
	public Controller() {
		server = new NaoServer(this);
		SERVER_ACTIVE = true;
		ARM_MOVEMENT_ACTIVE = true;
	}

	/**
	 * Starts the server loop.
	 */
	private void startServer() {
		server.run();
	}

	/**
	 * Initializes the whole system by first establishing the connection with the
	 * robot, initializing the required robot modules and then starting the rest of
	 * the services of the system. The autonomous life status of the robot is also
	 * disabled if necessary.
	 * 
	 * @param IP_Address IP address of the robot to connect to
	 */
	private void initialize(String IP_Address) {
		try {
			session = new Session();
			Future<Void> fut = session.connect(IP_Address);
			fut.get();

			motion = new ALMotion(session);
			robotPosture = new ALRobotPosture(session);
			autonomousLife = new ALAutonomousLife(session);

			armMovements = new ArmMovementHandler(motion);
			navigation = new NavigationHandler(motion, robotPosture);

			if (!autonomousLife.getState().equals("disabled")) {
				autonomousLife.setState("disabled");
				motion.wakeUp();
			}

			robotPosture.applyPosture("Stand", 0.5f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method plays the part of the interface between the commands and the
	 * actual functionality of the robot. It receives a command that is already
	 * filtered and partitioned into its relevant elements and depending on the type
	 * of the command, the correspondent module and functionality is called. The
	 * first element contains the information about the type of command, while the
	 * rest of the elements have the concrete information required for that command.
	 * 
	 * @param command Command from the client, already partitioned with each
	 *                relevant info as one element of the array
	 * @throws Exception Any type of exception that can come back from the other
	 *                   modules that run the specific commands and in case an
	 *                   invalid command arrives
	 */
	public void runCommand(String[] command) throws Exception {
		switch (command[0]) {
		case INITIALIZE:
			// Set the given values and initialize the system
			String robotIPAddress = command[1];
			VR_LIMIT_X = Float.parseFloat(command[5]);
			VR_LIMIT_Y = Float.parseFloat(command[2]);
			VR_POSITIVELIMIT_Z = Float.parseFloat(command[3]);
			VR_NEGATIVELIMIT_Z = Float.parseFloat(command[4]) * -1;
			USER_HEIGHT = Float.parseFloat(command[6]);

			initialize("tcp://" + robotIPAddress + ":9559");
			break;

		case MOVE:
			// Pass the values to the walking module to handle the navigation command
			float walkX = Float.parseFloat(command[2]);
			float walkY = Float.parseFloat(command[1]);

			WALK_MOVEMENT_ACTIVE = true;
			// Clear old and unused arm commands
			armMovements.clearArmCommandsList();

			navigation.walkTo(walkX, walkY);
			WALK_MOVEMENT_ACTIVE = false;
			break;

		case ARM:
			// Pass the values to the arm movement module to handle the arm command
			armSide = command[1];
			armX = Float.parseFloat(command[4]);
			armY = Float.parseFloat(command[2]);
			armZ = Float.parseFloat(command[3]);
			armWX = Float.parseFloat(command[7]);
			armWY = Float.parseFloat(command[5]);
			armWZ = Float.parseFloat(command[6]);

			armMovements.moveArm(armSide, armX, armY, armZ, armWX, armWY, armWZ);
			break;

		case HAND:
			// Pass the values to the arm movement module to handle the arm command
			String handSide = command[1];
			String handAction = command[2];

			armMovements.moveHand(handSide, handAction);
			break;

		case TURN:
			// Pass the values to the walking module to handle the navigation command
			float turnTheta = Float.parseFloat(command[1]);

			WALK_MOVEMENT_ACTIVE = true;
			// Clear old and unused arm commands
			armMovements.clearArmCommandsList();

			navigation.turnTo(turnTheta);
			WALK_MOVEMENT_ACTIVE = false;
			break;

		case POSTURE:
			// Change the posture to apply the given on
			String postureName = command[1];

			robotPosture.applyPosture(postureName, 0.5f);
			break;

		case STOP:
			// Stop the system by desabling the flag for the server and for the arm threads
			robotPosture.applyPosture("Stand", 0.5f);
			SERVER_ACTIVE = false;
			ARM_MOVEMENT_ACTIVE = false;

			break;

		default:
			throw new Exception("Non existent control command");
		}
	}
}