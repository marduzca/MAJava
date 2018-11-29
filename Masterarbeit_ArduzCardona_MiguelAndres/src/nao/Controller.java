package nao;

import com.aldebaran.qi.Session;

import java.util.ArrayList;
import java.util.List;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.helper.proxies.ALAnimatedSpeech;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import naoArms.Arms;
import naoLegs.Legs;
import static utils.GlobalVariables.*;

public class Controller {

	private Arms arms;
	private Legs legs;
	private NaoServer server;

	private static Session session;
	private ALMemory memory;
	private ALMotion motion;
	private ALTextToSpeech textToSpeech;
	private ALRobotPosture robotPosture;

	public Controller() {
		server = new NaoServer(this);
		SERVER_ACTIVE = true;
	}

	private void startServer() {
		server.start();
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

			arms = new Arms();
			legs = new Legs();
			
			robotPosture.applyPosture("Stand", 0.5f);

//			
//			
//
//			System.out.println(motion.getAngles("LHand", true));
//			
//			moveArm();	
//			
//			List<Float> positionLeftArm = motion.getPosition("RArm", 2, true);
//			
//			double x = Math.toDegrees(positionLeftArm.get(3));
//			double y = Math.toDegrees(positionLeftArm.get(4));
//			double z = Math.toDegrees(positionLeftArm.get(5));
//			
//			System.out.println("Rotation: x: " + x + ", y: " + y + ", z: " + z);
//			
//			int i = 0;
//			while(i < 500) {
//				i++;
//				
//				positionLeftArm = motion.getPosition("RArm", 2, true);
//				
//				x = Math.toDegrees(positionLeftArm.get(3));
//				y = Math.toDegrees(positionLeftArm.get(4));
//				z = Math.toDegrees(positionLeftArm.get(5));
//				
//				System.out.println("Rotation: x: " + x + ", y: " + y + ", z: " + z);
//				Thread.sleep(1000);	
//			}
			
			//Position 1
//			float x_vr = -0.1034929f;
//			float y_vr = -0.2159249f;
//			float z_vr = 0.2296475f;
//			float wx_vr = 305.3242f;
//			float wy_vr = 46.32946f;
//			float wz_vr = 327.7415f;
			
			//Position 2
//			float x_vr = -0.2810796f;
//			float y_vr = -0.8256781f;
//			float z_vr = 0.1512662f;
//			float wx_vr = 50.42723f;
//			float wy_vr = 353.7392f;
//			float wz_vr = 343.2585f;
			
			//Position 3
			float x_vr = -0.2956451f;
			float y_vr = 0.3446695f;
			float z_vr = 0.2353459f;
			float wx_vr = 290.6177f;
			float wy_vr = 345.4064f;
			float wz_vr = 11.30276f;
			
			//Position 4
//			float x_vr = -0.80000f;
//			float y_vr = -0.0825301f;
//			float z_vr = 0.08158922f;
//			float wx_vr = 351.2268f;
//			float wy_vr = 296.9288f;
//			float wz_vr = 5.548159f;
			
			//Position 5
//			float x_vr = -0.1925855f;
//			float y_vr = -0.1174417f;
//			float z_vr = 0.6342354f;
//			float wx_vr = 355.2504f;
//			float wy_vr = 355.478f;
//			float wz_vr = 2.466391f;
			
			System.out.println(scaleArm6DPosition("L", z_vr, x_vr, y_vr, wz_vr, wx_vr, wy_vr));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void runCommand(String[] command) throws Exception {
		switch (command[0]) {
		case "INI":
			initialize("tcp://" + command[1] + ":9559");
			break;

		case "MOV":
			motion.moveTo(Float.parseFloat(command[1]), Float.parseFloat(command[3]), 0.0f);
			break;

		case "ARM":
			String side = command[1];
			float x = Float.parseFloat(command[4]);
			float y = Float.parseFloat(command[2]);
			float z = Float.parseFloat(command[3]);
			float wx = Float.parseFloat(command[7]);
			float wy = Float.parseFloat(command[5]);
			float wz = Float.parseFloat(command[6]);
			
			List<Float> newPosition6D = new ArrayList<Float>();

			newPosition6D = scaleArm6DPosition(side, x, y, z, wx, wy, wz);
			
//			try {
				if(side.equals("L")) {
					System.out.println("LArm: " + newPosition6D.toString());
					motion.positionInterpolation("LArm", 2, newPosition6D, 63, 1.0, true);
				}
				else if(side.equals("R")) {
					System.out.println("RArm: " + newPosition6D.toString());
					//motion.positionInterpolation("RArm", 2, newPosition6D, 63, 1.0, true);
				}
//			} catch (CallError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
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
			server.join();
			break;

		default:
			throw new Exception("Non existent control command");
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

	private void moveArm() {

		List<Float> positionVector = new ArrayList<Float>();
		positionVector.add(0.21775617f);
		positionVector.add(0.11903204f);
		positionVector.add(0.40224576f);
//		positionVector.add(toFloatRadians(-133.67054f));
//		positionVector.add(toFloatRadians(147.7415f));
//		positionVector.add(toFloatRadians(125.3242f));
		positionVector.add(toFloatRadians(0f));
		positionVector.add(toFloatRadians(0f));
		positionVector.add(toFloatRadians(0f));
		
		try {
			positionVector = motion.getPosition("LArm", 2, true);
			positionVector.set(1, 0.36f);
		} catch (CallError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		
		try {
			motion.positionInterpolation("LArm", 2, positionVector, 63, 1.0, true);
		} catch (CallError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		Controller controller = new Controller();
		controller.initialize(NAOMI_IP);
		controller.startServer();
	}
}
