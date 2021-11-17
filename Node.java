import java.util.ArrayList;

public class Node {
	public int nodeID;
	boolean isDC;
	public double xcoord;
	public double ycoord;
	ArrayList<Node> reachableDCs;
	Path pathToController;
	ArrayList<Path> pathToAll = new ArrayList<>();
	public boolean isController = false;
	int assignedController;
	
	public Node(int id, double xcoord, double ycoord) {
		this.xcoord = xcoord;
		this.ycoord = ycoord;
		this.nodeID = id;
		this.reachableDCs = new ArrayList<>();
	}

	public Node(int id) {
		this.nodeID = id;

	}

	public void setPathToCOntroller(Path path) {
		this.pathToController = path;
	}
	@Override
	public String toString() {
		String msg = "node "+ nodeID;
		return msg;
	}
}