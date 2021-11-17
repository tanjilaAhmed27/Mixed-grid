
import org.apache.commons.math3.analysis.function.Constant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class ReadTopo {
	
	static public void readTopoFromFile(String fileName, Topology topo) throws IOException {
		ArrayList<Link> links = new ArrayList<Link>();

		ArrayList<Node> nodes = new ArrayList<Node>();


		topo.adjMatrix = new Vector[Constants.num_nodes][Constants.num_nodes];
		for (int i = 0; i < Constants.num_nodes; i++)
			for (int j = 0; j < Constants.num_nodes; j++) {
				topo.adjMatrix[i][j] = new Vector();
				topo.adjMatrix[i][j].addElement("0");
				topo.adjMatrix[i][j].addElement(new Object());
			}

		topo.pathMatrix = new Vector[Constants.num_nodes][Constants.num_nodes];
		for (int i = 0; i < Constants.num_nodes; i++)
			for (int j = 0; j < Constants.num_nodes; j++) {
				topo.pathMatrix[i][j] = new Vector();
				topo.pathMatrix[i][j].addElement(0);
				for(int k=0; k< Constants.maximum_path; k++)
					topo.pathMatrix[i][j].addElement(new Path());
			}

		boolean nodeisdone = false;
		FileReader f_read = new FileReader(fileName);
		BufferedReader b_read = new BufferedReader(f_read);


		while (b_read.ready()) {
			String line = b_read.readLine();
			if (line.equals("") || line.startsWith("#"))
				continue; // ignores blank lines

			String[] parts = line.split(",");

			if (parts.length == 4) {
				int source = Integer.parseInt(parts[0]);
				int dest = Integer.parseInt(parts[1]);
				int typ = Integer.parseInt(parts[2]);
				double cap = Integer.parseInt(parts[3]);

				if (topo.linkExists(source, dest))
					continue;
				Link newLink = new Link(source, dest, typ, cap);
				links.add(newLink);
				topo.adjMatrix[source][dest].set(0, "1");
				topo.adjMatrix[source][dest].set(1, newLink);

				Link newLinkReverse = new Link(dest, source, typ, cap);
				links.add(newLinkReverse);
				topo.adjMatrix[dest][source].set(0, "1");
				topo.adjMatrix[dest][source].set(1, newLinkReverse);
			} else if (parts.length == 1) {
				int nodeid = Integer.parseInt(parts[0]);
				nodes.add(new Node(nodeid, 0, 0));
			}
		}

		topo.allLinks = new Link[Constants.num_links];
		topo.allNodes = new Node[Constants.num_nodes];

		for (int i = 0; i < topo.allLinks.length; i++) {
			topo.allLinks[i] = links.get(i);
			//System.out.println(allLinks[i].startNodeID + "-" + allLinks[i].endNodeID);
		}
		for (int i = 0; i < topo.allNodes.length; i++) {
			topo.allNodes[i] = nodes.get(i);
			//System.out.println(allNodes[i].nodeID + "-" + allNodes[i].xcoord + "-" + allNodes[i].ycoord);
		}
		topo.numNodes = topo.allNodes.length;
		//calculateLinkLengths(topo);

//		for (int i = 0; i < 14; i++) {
//
//			System.out.print("Neighbors of node " + i);
//
//			for (int j = 0; j < 14; j++) {
//				if (topo.adjMatrix[i][j].elementAt(0) == "1")
//					System.out.print(" " + j + " ");
//
//			}
//			System.out.println();
//		}
	}
}


//private void readTopoFromFileATT(String fileName) throws IOException {
//	ArrayList<Link> links;
//	ArrayList<Node> nodes;
//	links = new ArrayList<Link>();
//	nodes = new ArrayList<Node>();
//
//	FileReader f_read = new FileReader(fileName);
//	BufferedReader b_read = new BufferedReader(f_read);
//
//	b_read.readLine();// ignore first line
//	// get the number of nodes at the second line
//	numNodes = Integer.parseInt(b_read.readLine());
//
//	this.adjMatrix = new Vector[this.numNodes][this.numNodes];
//	for (int i = 0; i < this.numNodes; i++)
//		for (int j = 0; j < this.numNodes; j++) {
//			this.adjMatrix[i][j] = new Vector();
//			this.adjMatrix[i][j].addElement("0");
//			this.adjMatrix[i][j].addElement(new Object());
//		}
//
//	for (int i = 0; i < numNodes; i++)
//		nodes.add(new Node(i));
//	// ignore lines 3-5
//	b_read.readLine(); // eg: #Number of Links
//	b_read.readLine(); // eg. 56
//	b_read.readLine(); // eg. #Unidirectional links (Originating node,
//						// Terminating node, Link Capacity)
//
//	while (b_read.ready()) {
//		String line = b_read.readLine();
//		if (line.equals("") || line.startsWith("#"))
//			continue; // ignores blank lines
//
//		StringTokenizer tokens = new StringTokenizer(line, ",");
//		int source = Integer.parseInt(tokens.nextToken()) - 1;
//		int destination = Integer.parseInt(tokens.nextToken()) - 1;
//		int capacity = Integer.parseInt(tokens.nextToken());
//		double weight = Double.parseDouble(tokens.nextToken());
//		int noLambdas = Integer.parseInt(tokens.nextToken());
//		int length = Integer.parseInt(tokens.nextToken());
//		double statAvail = Double.parseDouble(tokens.nextToken());
//		Link newLink = new Link(source, destination, capacity, length);
//		links.add(newLink);
//		this.adjMatrix[source][destination].set(0, "1");
//		this.adjMatrix[source][destination].set(1, newLink);
//	}
//	allLinks = new Link[links.size()];
//	allNodes = new Node[nodes.size()];
//
//	for (int i = 0; i < allLinks.length; i++) {
//		allLinks[i] = links.get(i);
//	}
//	for (int i = 0; i < allNodes.length; i++) {
//		allNodes[i] = nodes.get(i);
//	}
//}
