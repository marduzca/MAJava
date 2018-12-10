package nao;

import static utils.GlobalVariables.ARM_MOVEMENT_ACTIVE;
import static utils.GlobalVariables.NAOMI_IP;
import static utils.GlobalVariables.SERVER_ACTIVE;
import static utils.GlobalVariables.WALK_MOVEMENT_ACTIVE;

import java.util.ArrayList;
import java.util.List;

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
			legs = new Legs(this, motion);

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
		case "INI":
			initialize("tcp://" + command[1] + ":9559");
			break;

		case "MOV":
			float walkX = Float.parseFloat(command[2]);
			float walkY = Float.parseFloat(command[1]);

			WALK_MOVEMENT_ACTIVE = true;
			arms.clearArmCommandsList();
			legs.walkTo(walkX, walkY);
			WALK_MOVEMENT_ACTIVE = false;

			break;

		case "ARM":
			String armSide = command[1];
			float armX = Float.parseFloat(command[4]);
			float armY = Float.parseFloat(command[2]);
			float armZ = Float.parseFloat(command[3]);
			float armWX = Float.parseFloat(command[7]);
			float armWY = Float.parseFloat(command[5]);
			float armWZ = Float.parseFloat(command[6]);

			arms.moveArm(armSide, armX, armY, armZ, armWX, armWY, armWZ);

			break;

		case "OPH":
			if (command[1].equals("true")) {
				// Open hand
				List<Float> handL = getAnglesFrom("LHand");
				handL.set(0, handL.get(0) - toFloatRadians(-70));
				motion.setAngles("LHand", handL, 0.3f);

			} else if (command[1].equals("false")) {
				// Close hand
				List<Float> handL = getAnglesFrom("LHand");
				handL = getAnglesFrom("LHand");
				handL.set(0, handL.get(0) - toFloatRadians(50));
				motion.setAngles("LHand", handL, 0.3f);

			}
			break;

		case "STP":
			SERVER_ACTIVE = false;
			ARM_MOVEMENT_ACTIVE = false;
			// server.join();
			break;

		default:
			throw new Exception("Non existent control command");
		}
	}

	/**
	 * Gets the current angles of the assigned joint. Done very often in the code,
	 * so to spare us some lines.
	 * 
	 * @param joint - Joint we need to get the angles from.
	 * @return List with the current angles of the joint.
	 */
	private List<Float> getAnglesFrom(String joint) {
		List<Float> anglesList = new ArrayList<Float>();

		try {
			anglesList = motion.getAngles(joint, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return anglesList;
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

	public static void main(String[] args) {
		try {
			Controller controller = new Controller();
			controller.initialize(NAOMI_IP);
			controller.startServer();
			System.out.println(Thread.currentThread().getState());
		} catch (Throwable ex) {
			System.err.println("Uncaught exception - " + ex.getMessage());
			ex.printStackTrace(System.err);
		} finally {
			System.out.println("This Thread died: " + Thread.currentThread().getState());
		}

	}
}
