import java.util.ArrayList;

public class Link {
	private int ID;
	//public double ghz;

	public double getCapacity() {
		return capacity;
	}

	public int getStartNodeID() {
		return startNodeID;
	}

	public int getEndNodeID() {
		return endNodeID;
	}

	public int getTyp() {
		return typ;
	}

	private double capacity; // link capacity
	public int startNodeID;
	public int endNodeID;
	private int typ;
	static int count = 0; 
	ArrayList<Integer> weights = new ArrayList<>(); // her linkin her node icin weighti var.
	public double avail_capacity;

		
	public Link(int source, int dest, int typ, double capacity) {
		this.startNodeID = source;
		this.endNodeID = dest;
		this.capacity = capacity;
		this.avail_capacity = capacity;
		this.typ = typ;
		this.ID = count++;
	}
	
	
	public int getID() {
		return this.ID;
	}
	@Override
	public String toString() {
		String msg = startNodeID + "-"+endNodeID;
		return msg;
	}

	
}
