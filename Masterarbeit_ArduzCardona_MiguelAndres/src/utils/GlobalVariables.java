package utils;

/**
 * GlobalVariables is the connector between both Master and Client Threads. It
 * contains variables that can be updated from both sides and can therefore
 * allow their interaction.
 * 
 * @author Miguel Arduz
 */
public class GlobalVariables {

	// Most used IP-addresses for a quick access
	public static final String NAOMI_IP = "tcp://192.168.1.143:9559";
	public static final String NAOEL_IP = "tcp://192.168.1.108:9559";
	public static final String NAOLINA_IP = "tcp://192.168.1.113:9559";

	public static boolean SERVER_ACTIVE;
	public static boolean ARM_MOVEMENT_ACTIVE;
	public static boolean WALK_MOVEMENT_ACTIVE;
	
	public static float VR_LIMIT_X = 0.56f;
	public static float VR_LIMIT_Y = 0.81f;
	public static float VR_POSITIVELIMIT_Z = 0.46f;
	public static float VR_NEGATIVELIMIT_Z = 0.90f;
	
	public static final float NAO_LIMIT_X = 0.21f;
	public static final float NAO_LIMIT_Y = 0.32f;
	public static final float NAO_POSITIVELIMIT_Z = 0.17f;
	public static final float NAO_NEGATIVELIMIT_Z = 0.27f;
	public static final float NAO_CENTERPOINT_Z = 0.48f;
	
	public static float USER_HEIGHT = 1.72f;
	public static final float NAO_HEIGHT = 0.58f;
	
	//Input commands
	public static final String INITIALIZE = "INI";
	public static final String MOVE = "MOV";
	public static final String ARM = "ARM";
	public static final String HAND = "HND";
	public static final String TURN = "TRN";
	public static final String STOP = "STP";
	public static final String POSTURE = "PST";
}
