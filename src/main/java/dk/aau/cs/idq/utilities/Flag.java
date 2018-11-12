
package dk.aau.cs.idq.utilities;

/**
 * Cases for Indoor Dense Regions
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.20
 *
 */
public class Flag {
	
	/** indoor buffer region has been processed but real density is yet to be calculated */ 
	public static int IBR = 1;
	
	/** r itself has been processed but there are still uncertain samples to be precisely calculated */ 
	public static int IRHP = 2;
	
	/** r has been fully processed and density has been calculated*/
	public static int IRP = 3;

}
