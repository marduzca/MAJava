package utils;

/**
 * GlobalVariables is the connector between both Master and Client Threads. It
 * contains variables that can be updated from both sides and can therefore
 * allow their interaction.
 * 
 * @author Miguel Arduz
 */
public class GlobalVariables {
	public static boolean SERVER_ACTIVE;
	public static boolean ARM_MOVEMENT_ACTIVE;
	public static boolean WALK_MOVEMENT_ACTIVE;

	public static float VR_LIMIT_X;
	public static float VR_LIMIT_Y;
	public static float VR_POSITIVELIMIT_Z;
	public static float VR_NEGATIVELIMIT_Z;
	public static float USER_HEIGHT;

	public static final float NAO_LIMIT_X = 0.21f;
	public static final float NAO_LIMIT_Y = 0.32f;
	public static final float NAO_POSITIVELIMIT_Z = 0.17f;
	public static final float NAO_NEGATIVELIMIT_Z = 0.27f;
	public static final float NAO_CENTERPOINT_Z = 0.48f;
	public static final float NAO_HEIGHT = 0.58f;

	public static boolean SCALED_WALK = false;

	// Input commands
	public static final String INITIALIZE = "INI";
	public static final String MOVE = "MOV";
	public static final String ARM = "ARM";
	public static final String HAND = "HND";
	public static final String TURN = "TRN";
	public static final String STOP = "STP";
	public static final String POSTURE = "PST";
}
