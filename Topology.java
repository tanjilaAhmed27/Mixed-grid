import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;

import javax.naming.ldap.UnsolicitedNotification;
import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.JFrame;
//import util.*;

public class Topology {
	public Link allLinks[];
	public Node allNodes[];
	public int numNodes;
	public static Vector adjMatrix[][];
	public double minX, minY, maxX, maxY;
	Shortest shortest;
	public Vector pathMatrix[][];

	public int disasterRadius; // in km
	BufferedWriter output = null;
	BufferedWriter outputVerbose = null;
	BufferedWriter outputPercentage = null;

	int dataShareEffect = 0; // added effect of sharing a data path with the
								// control path
	int controllerShareEffect = 0; // added effect of sharing a control path
									// with another control path

	private void openFiles() {
		try {
			File filesum = new File("out_summary.txt");
			output = new BufferedWriter(new FileWriter(filesum));
			File filever = new File("out_verbose.txt");
			outputVerbose = new BufferedWriter(new FileWriter(filever));
			File afile = new File("out_percentage.txt");
			outputPercentage = new BufferedWriter(new FileWriter(afile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Constructor for ILP
	public Topology(String filename) throws IOException {

		//openFiles();

		ReadTopo.readTopoFromFile(filename, this);
		shortest = new Shortest(this);
		//findLimits();
		findInitialShortestDataPaths();
		//addDatapathWeights();
	}


	private void findInitialShortestDataPaths() throws IOException {
		for (int i = 0; i < allNodes.length; i++) {
			for (int j = 0; j < allNodes.length; j++) {

				if (i != j) {
					ArrayList<Path> path = shortest.kShortestPaths(allNodes[i].nodeID, allNodes[j].nodeID, Constants.maximum_path, null);

					pathMatrix[i][j].set(0, path.size());

					for(int x=0; x< path.size();x++) {
						pathMatrix[i][j].set(x + 1, path.get(x));
						//System.out.println(allNodes[i].nodeID + " xto " + allNodes[j].nodeID + " " + pathMatrix[i][j].get(x+1) + "\n");
					}

				}
			}
		}
	}


	public boolean linkExists(int ingress, int egress) {
		if (this.adjMatrix[ingress][egress].elementAt(0) == "1")
			return true;
		else
			return false;
	}

	public Link getLink(int ingress, int egress) {

		if (!this.linkExists(ingress, egress)) {
			System.out.println("Error, the link (" + ingress + "-" + egress + ") does not exist");
			return null;
		} else
			return (Link) this.adjMatrix[ingress][egress].elementAt(1);
	}

	public boolean containsLink(Link link, ArrayList<Link> linkArr) {

		for (Link alink : linkArr) {
			if (alink.equals(link))
				return true;
		}
		return false;
	}

	public boolean isFlex(int node){
		if(node==1||node==2||node==5||node==12||node==13||node==11)
			return true;
		else
			return false;
	}

}
