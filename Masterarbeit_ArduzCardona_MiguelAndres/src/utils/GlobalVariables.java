package utils;

/**
 * GlobalVariables contains variables that are relevant for the system and that
 * are access from several points in the infrastructure and therefore must be
 * reachable from everywhere.
 * 
 * @author Miguel Arduz
 */
public class GlobalVariables {
	// Flags for server and threads
	public static boolean SERVER_ACTIVE;
	public static boolean ARM_MOVEMENT_ACTIVE;
	public static boolean WALK_MOVEMENT_ACTIVE;

	// Measurement data incoming from the client
	public static float VR_LIMIT_X;
	public static float VR_LIMIT_Y;
	public static float VR_POSITIVELIMIT_Z;
	public static float VR_NEGATIVELIMIT_Z;
	public static float USER_HEIGHT;

	// Possible input command types
	public static final String INITIALIZE = "INI";
	public static final String MOVE = "MOV";
	public static final String ARM = "ARM";
	public static final String HAND = "HND";
	public static final String TURN = "TRN";
	public static final String STOP = "STP";
	public static final String POSTURE = "PST";
}
