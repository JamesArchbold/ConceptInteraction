package MethodTesting;
//made synthetic networks used in work
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import edu.uci.ics.jung.algorithms.generators.random.ErdosRenyiGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import influence.edges.Edge;
import influence.edges.WeightedEdge;
import influence.graph.generation.GraphReaderWriter;
import influence.graph.makers.EdgeFactory;
import influence.graph.makers.GraphFactory;
import influence.graph.makers.ProbabilityNodeFactory;
import influence.graph.makers.UndirectedGraphFactory;
import influence.graph.suppliers.EdgeSupplier;
import influence.graph.suppliers.GraphSupplier;
import influence.graph.suppliers.ProbabilityNodeSupplier;
import influence.graphs.characteristics.CommunityDetector;
import influence.nodes.Node;
import influence.nodes.ProbabilityNode;

public class GraphMaker {

	public static void main(String[] args) {
		GraphReaderWriter grw = new GraphReaderWriter();
		//arg 0 = SW or SF
		//arg 1 = size of graph
		//arg 2 = graph number
		//arg 3 = exponent for SW / probabiltiy of edge for random
		//arg 4 = start nodes for SF
		//arg 5 = number of nodes added per turn for SF
		
//		if (args[0].equals("SW")) {
//			int size = Integer.parseInt(args[1]);
//			grw.graphSmallWorldMaker(100, size/100, Double.parseDouble(args[3]), Integer.parseInt(args[2]), "graphFiles"+args[1]+"N");
//		}
//		else if (args[1].equals("SF")) {
//			grw.graphScaleFreeMaker(Integer.parseInt(args[1]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[2]), "graphFiles"+args[1]+"N");
//		}
//		else if (args[2].equals("RN")) {
//			grw.randomGraphMaker(Integer.parseInt(args[1]), "graphFiles"+args[1]+"N", Integer.parseInt(args[2]), Integer.parseInt(args[3]));
//		}
		
		ArrayList<Integer> graphSize = new ArrayList<Integer>();
		graphSize.add(1000);
		graphSize.add(2000);
		graphSize.add(3000);
		graphSize.add(4000);
		graphSize.add(5000);
		graphSize.add(6000);
		graphSize.add(7000);
		graphSize.add(8000);
		graphSize.add(9000);
		graphSize.add(10000);
		graphSize.add(15000);
		graphSize.add(20000);
		graphSize.add(25000);
		graphSize.add(30000);
		graphSize.add(35000);
		graphSize.add(40000);
		graphSize.add(45000);
		graphSize.add(50000);
		graphSize.add(75000);
		graphSize.add(100000);
		graphSize.add(250000);
		graphSize.add(500000);
		
		ArrayList<Integer> edgeAdd = new ArrayList<Integer>();
		edgeAdd.add(5);
		edgeAdd.add(10);
		edgeAdd.add(25);
		edgeAdd.add(50);
		
		
		for (Integer nodes : graphSize) {
			for (Integer edges : edgeAdd){
				for (int i = 0; i < 100; i++) {
					grw.randomGraphMaker(nodes, "graphFiles"+nodes+"N", i, edges);
					System.out.println(i + " " + edges  + " " + nodes);
				}
			}
		}
		
	}
	
	public static void communitiyDetection() {
		
		EdgeFactory eF;
		ProbabilityNodeFactory pNF;
		GraphFactory gF;
		EdgeSupplier eFS;
		ProbabilityNodeSupplier pNFS;
		UndirectedGraphFactory UgF;
		GraphSupplier gFS;
		
		eF = new EdgeFactory();
		pNF = new ProbabilityNodeFactory();
		gF = new GraphFactory();
		UgF = new UndirectedGraphFactory();
		eFS = new EdgeSupplier();
		pNFS = new ProbabilityNodeSupplier();
		gFS = new GraphSupplier();
		
		ErdosRenyiGenerator<ProbabilityNode, Edge> erG = new ErdosRenyiGenerator<ProbabilityNode, Edge>(UgF, pNF, eF, 10000, 0.025);
	
		Graph<Node, WeightedEdge> graph = new SparseMultigraph<Node, WeightedEdge>();
		HashMap<Integer, Node> nodeSet = new HashMap<Integer, Node>();
		for (int i =0; i < 500000; i++){
			Node node = new Node(i);
			graph.addVertex(node);
			nodeSet.put(i, node);
			System.out.println(i);
		}
		
		Random rand = new Random();
		int max = 50;
		int edgeCount = 0;
		for (Node n : graph.getVertices()) {
			int neighbourCount = rand.nextInt(max) + 1;
			HashSet<Integer> neighbours = new HashSet<Integer>();
			
			for (Node nn : graph.getNeighbors(n)) {
				neighbours.add(nn.getId());
			}
			
			while (neighbours.size() < neighbourCount) {
				int selected = rand.nextInt(500000);
				if (!neighbours.contains(selected)) {
					neighbours.add(selected);
					graph.addEdge(new WeightedEdge(edgeCount++, rand.nextDouble()), n, nodeSet.get(selected));
				}
			}
		}
		
		
		System.out.println("Graph Made");
		long start = System.currentTimeMillis();
		//HashMap<Integer, ArrayList<Node>> comm = CommunityDetector.getCommunities(graph);
//		long end = System.currentTimeMillis();
//		HashSet<Integer> nodeIds = new HashSet<Integer>();
//		for (Integer key : comm.keySet()) {
//			System.out.println("Community : " + key);
//			System.out.println("Members: ");
//			for (Node n : comm.get(key)){
//				if (nodeIds.contains(n.getId())){
//					System.out.println("ERROR");
//				}
//				nodeIds.add(n.getId());
//			}
//		}
	//	long fullTime = end - start;
	//	double sec = fullTime/1000.0;
	//	double minutes = sec/60.0;
	//	double hour = minutes/60.0;
//		System.out.println("Running for: " + (sec) + " seconds");
//		System.out.println("Running for: " + (minutes) + " minutes");
//		System.out.println("Running for: " + (hour) + " hours");
	}
}





























