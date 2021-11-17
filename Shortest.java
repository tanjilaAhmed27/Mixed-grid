import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Vertex;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

public class Shortest {

	Topology topo;
	public Shortest(Topology t) {
		topo = t;
	}
	public ArrayList<Path> kShortestPaths(int source, int destination, int K, ArrayList<Link> linksToIgnore) {

		ArrayList<Path> candidatePaths = new ArrayList<Path>();

		Graph g = createGraph(linksToIgnore);

		YenTopKShortestPathsAlg ytkpa = new YenTopKShortestPathsAlg(g, g.get_vertex(source), g.get_vertex(destination));
		List<edu.asu.emit.qyan.alg.model.Path> result_list = new Vector<edu.asu.emit.qyan.alg.model.Path>();
		result_list = ytkpa.get_shortest_paths(g.get_vertex(source), g.get_vertex(destination), K);

		for (int i = 0; i < result_list.size(); i++) {
			edu.asu.emit.qyan.alg.model.Path p = result_list.get(i);
			List<BaseVertex> vertexList = p.get_vertices();
			Vector<Link> Links = new Vector<Link>();
			for (int j = 0; j < vertexList.size() - 1; j++) {
				int u = vertexList.get(j).get_id();
				int v = vertexList.get(j + 1).get_id();
				Links.add(topo.getLink(u, v));
				// System.out.println("addlink: " + this.getLink(u, v).getID());
			}
			Path path = new Path(Links);
			candidatePaths.add(path);
		}
		return candidatePaths;
	}

	public Graph createGraph(ArrayList<Link> linksToIgnore) {

		Graph G = new Graph();
		G.set_vertex_num(topo.numNodes);

		for (int i = 0; i < G._vertex_num; ++i) {
			BaseVertex vertex = new Vertex();
			vertex.set_id(i);
			G._vertex_list.add(vertex);
			G._id_vertex_index.put(vertex.get_id(), vertex);
		}

		for (int i = 0; i < topo.numNodes; i++)
			for (int j = 0; j < topo.numNodes; j++)
				if (topo.linkExists(i, j)) {
					Link link = topo.getLink(i, j);
					if (linksToIgnore != null) {
						boolean isFound = false;

						for (Link linkToTest : linksToIgnore) {
							if (link.getID() == linkToTest.getID()) {
								isFound = true;
								break;
							}
						}
						if (!isFound) {
							double cost = 1.0;
							G.add_edge(i, j, 50000);
						}
					} else {
						double cost = 1.0;
						G.add_edge(i, j, 50000);
					}
				}
		return G;
	}
	public ArrayList<Path> kShortestPathsFancy(int source, int destination, int K, ArrayList<Link> linksToIgnore) {

		ArrayList<Path> candidatePaths = new ArrayList<Path>();

		Graph g = createGraphForFancy(linksToIgnore, source);

		YenTopKShortestPathsAlg ytkpa = new YenTopKShortestPathsAlg(g, g.get_vertex(source), g.get_vertex(destination));
		List<edu.asu.emit.qyan.alg.model.Path> result_list = new Vector<edu.asu.emit.qyan.alg.model.Path>();
		result_list = ytkpa.get_shortest_paths(g.get_vertex(source), g.get_vertex(destination), K);

		for (int i = 0; i < result_list.size(); i++) {
			edu.asu.emit.qyan.alg.model.Path p = result_list.get(i);
			List<BaseVertex> vertexList = p.get_vertices();
			Vector<Link> Links = new Vector<Link>();
			for (int j = 0; j < vertexList.size() - 1; j++) {
				int u = vertexList.get(j).get_id();
				int v = vertexList.get(j + 1).get_id();
				Links.add(topo.getLink(u, v));
				// System.out.println("addlink: " + this.getLink(u, v).getID());
			}
			Path path = new Path(Links);
			candidatePaths.add(path);
		}
		return candidatePaths;
	}

	public Graph createGraphForFancy(ArrayList<Link> linksToIgnore, int source) {

		Graph G = new Graph();
		G.set_vertex_num(topo.numNodes);

		for (int i = 0; i < G._vertex_num; ++i) {
			BaseVertex vertex = new Vertex();
			vertex.set_id(i);
			G._vertex_list.add(vertex);
			G._id_vertex_index.put(vertex.get_id(), vertex);
		}

		for (int i = 0; i < topo.numNodes; i++)
			for (int j = 0; j < topo.numNodes; j++)
				if (topo.linkExists(i, j)) {
					Link link = topo.getLink(i, j);
					if (linksToIgnore != null) {
						boolean isFound = false;

						for (Link linkToTest : linksToIgnore) {
							if (link.getID() == linkToTest.getID()) {
								isFound = true;
								break;
							}
						}
						if (!isFound) {
							double cost = 1.0;
							G.add_edge(i, j, topo.getLink(i, j).weights.get(source) + 1);
						}
					} else {
						double cost = 1.0;
						G.add_edge(i, j, topo.getLink(i, j).weights.get(source) + 1);
					}
				}
		return G;
	}
	
}
