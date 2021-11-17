

import java.io.BufferedWriter;
import java.util.ArrayList;

public class Settings {
	public BufferedWriter summaryOutput;
	public BufferedWriter verboseOutput;
	public BufferedWriter percentageOutput;
//	public ArrayList<Controller> controllers = new ArrayList<Controller>();

	public String fileName="topo.txt";
	public int disasterRadius;
	public int disasterMoveWalk;
	public int controllerShareEffect;// if both 0, then shortest path
	public int dataShareEffect;
}
