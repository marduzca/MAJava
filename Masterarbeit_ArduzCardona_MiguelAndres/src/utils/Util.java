package utils;

import java.util.ArrayList;
import java.util.List;

import com.aldebaran.qi.helper.proxies.ALMotion;

/**
 * Util contains two simple methods that are used everywhere in the program.
 * This class offers these two methods as service to avoid redundancy.
 * 
 * @author Miguel Arduz
 *
 */
public class Util {

	/**
	 * Gets the current angles of the assigned joint. Done very often in the code,
	 * so to spare us some lines.
	 * 
	 * @param joint Joint we need to get the angles from.
	 * @param motion Motion module of the robot's SDK
	 * @return List with the current angles of the joint.
	 */ 
	public static List<Float> getAnglesFrom(String joint, ALMotion motion) {
		List<Float> anglesList = new ArrayList<Float>();

		try {
			anglesList = motion.getAngles(joint, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return anglesList;
	}

	/**
	 * Converts an angle from degrees to radians.
	 * 
	 * @param degree Degree to be transformed
	 * @return Same degree in radians
	 */
	public static float toFloatRadians(double degree) {

		return (float) Math.toRadians(degree);

	}
}