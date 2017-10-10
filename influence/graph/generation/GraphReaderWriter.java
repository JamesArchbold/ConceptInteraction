package influence.graph.generation;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;
import influence.concepts.Concept;
import influence.concepts.ExtendedConcept;
import influence.edges.Edge;
import influence.edges.TypedWeightedEdge;
import influence.edges.WeightedEdge;
import influence.graph.makers.EdgeFactory;
import influence.graph.makers.GraphFactory;
import influence.graph.makers.ProbabilityNodeFactory;
import influence.graph.makers.UndirectedGraphFactory;
import influence.graph.suppliers.EdgeSupplier;
import influence.graph.suppliers.GraphSupplier;
import influence.graph.suppliers.ProbabilityNodeSupplier;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.Node;
import influence.nodes.ProbabilityNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import influence.graph.generation.BarabasiAlbertGenerator;
import influence.graph.generation.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.algorithms.generators.random.ErdosRenyiGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphReaderWriter {
	private EdgeFactory eF;
	private ProbabilityNodeFactory pNF;
	private GraphFactory gF;
	private EdgeSupplier eFS;
	private ProbabilityNodeSupplier pNFS;
	private UndirectedGraphFactory UgF;
	private GraphSupplier gFS;
	
	public GraphReaderWriter() {
		eF = new EdgeFactory();
		pNF = new ProbabilityNodeFactory();
		gF = new GraphFactory();
		UgF = new UndirectedGraphFactory();
		eFS = new EdgeSupplier();
		pNFS = new ProbabilityNodeSupplier();
		gFS = new GraphSupplier();
	}
	
	public Graph<ProbabilityNode, Edge> undirectedSWGraphMaker(int row, int col, double expo) {
		KleinbergSmallWorldGenerator<ProbabilityNode,Edge> swG = new KleinbergSmallWorldGenerator<ProbabilityNode,Edge>(gF, pNF, eF,row, col, expo);
		eF.reset();
		pNF.reset();
		return swG.create();
	}
	
	public Graph<ProbabilityNode, Edge> undirectedSFGraphMaker(int numNodes, int start, int edgesToAdd) {
		BarabasiAlbertGenerator<ProbabilityNode, Edge> bAG = 
				new BarabasiAlbertGenerator<ProbabilityNode, Edge>(gFS, pNFS, eFS, start, edgesToAdd, new HashSet<ProbabilityNode>());
		
		bAG.evolveGraph(numNodes - start);
		eF.reset();
		pNF.reset();
		return bAG.create();
	}


	public Graph<MultiProbabilityNode, TypedWeightedEdge> graphReadIn(int sizeOfG, int graphID, ArrayList<? extends Concept> conceptList, boolean setThresholds, double thresholdAvg, double icInfection){
		
		
		SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		FileReader fstream = null;
	//	System.out.println("Opening graph:" + graphID);
		File file = new File("graphFiles" + sizeOfG + "N/" + sizeOfG + "NodeSmallWorldGraph" + graphID + ".txt");
		HashMap<Integer, MultiProbabilityNode> nodesToAdd = new HashMap<Integer, MultiProbabilityNode>();
		BufferedReader br = null;
		Random rand = null;
		for (int i = 0; i<sizeOfG; i++) {
			MultiProbabilityNode n = new MultiProbabilityNode(i, conceptList);
			nodesToAdd.put(n.getId(), n);
			sMg.addVertex(n);
		}
		
		try {
			fstream = new FileReader(file.getAbsoluteFile());
			br = new BufferedReader(fstream);
			
			String line;
			int seed = 0;
			int edgeId = 0;
			
			while ((line = br.readLine()) != null) {
				if (line.contains("Seed")) {
					seed = Integer.parseInt(line.split(":")[1]);
					rand = new Random(seed);
				}
				else {
					String ids[] = line.split(":");
					for (Concept c : conceptList) {
						TypedWeightedEdge tE = new TypedWeightedEdge(edgeId++, c.getId());
						if (c.getType().equals("lt")) {
							tE.setWeight(rand.nextDouble());
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.DIRECTED);
						}
						else if (c.getType().equals("ic")) {
							tE.setWeight(icInfection);
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.UNDIRECTED);
						}
					}
					
				}
			}
			rand = new Random(seed*2);
			for (MultiProbabilityNode n : sMg.getVertices()) {
				for (Concept c : conceptList) {
					if (c.getType().equals("lt")) {
						n.addAttribute(c.getId(), chooseThreshold(rand, setThresholds, thresholdAvg));
					}
				}
			}
			
			br.close();
			fstream.close();
			//System.out.println(edgeId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sMg;
	}	
	
	public Graph<MultiProbabilityNode, TypedWeightedEdge> graphReadInSW(int sizeOfG, double expo, int graphID, ArrayList<? extends Concept> conceptList, boolean setThresholds, double thresholdAvg, double icInfection){
		SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		FileReader fstream = null;
		//File file = new File("graphFiles" + sizeOfG + "N/" + sizeOfG + "NodeSmallWorldGraph" + graphID + ".txt");
		//System.out.println("Opening graph:" + graphID);
		File file = new File("graphFiles" + sizeOfG + "N/"+ sizeOfG + "NodeSmallWorldGraphExpo" + (expo*100) + "Num" + graphID + ".txt");
		HashMap<Integer, MultiProbabilityNode> nodesToAdd = new HashMap<Integer, MultiProbabilityNode>();
		BufferedReader br = null;
		Random rand = null;
		for (int i = 0; i<sizeOfG; i++) {
			MultiProbabilityNode n = new MultiProbabilityNode(i, conceptList);
			nodesToAdd.put(n.getId(), n);
			sMg.addVertex(n);
		}
		try {
			fstream = new FileReader(file.getAbsoluteFile());
			br = new BufferedReader(fstream);
			String line;
			int seed = 0;
			int edgeId = 0;
			
			while ((line = br.readLine()) != null) {
				if (line.contains("Seed")) {
					seed = Integer.parseInt(line.split(":")[1]);
					rand = new Random(seed);
				}
				else {
					String ids[] = line.split(":");
					for (Concept c : conceptList) {
						TypedWeightedEdge tE = new TypedWeightedEdge(edgeId++, c.getId());
						if (c.getType().equals("lt")) {
							tE.setWeight(rand.nextDouble());
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.DIRECTED);
						}
						else {
							tE.setWeight(icInfection);
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.UNDIRECTED);
						}
					}
					
				}
			}
			rand = new Random(seed+2);
			for (MultiProbabilityNode n : sMg.getVertices()) {
				for (Concept c : conceptList) {
					if (c.getType().equals("lt")) {
						n.addAttribute(c.getId(), chooseThreshold(rand, setThresholds, thresholdAvg));
					}
				}
			}
			
			br.close();
			fstream.close();
			//System.out.println(edgeId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sMg;
	}

	public Graph<MultiProbabilityNode, TypedWeightedEdge> graphReadInSF(int startNum, int edgesToAdd, int sizeOfG, int graphID, ArrayList<? extends Concept> conceptList, boolean setThresholds, double thresholdAvg, double icInfection){
		SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		FileReader fstream = null;
		//File file = new File("graphFiles" + sizeOfG + "N/" + sizeOfG + "NodeSmallWorldGraph" + graphID + ".txt");
		File file = new File("graphFiles" + sizeOfG + "N/" + startNum + "Start" + edgesToAdd + "EdgesAdded" + sizeOfG + 
				"NodeScaleFreeGraph" + "Num" + graphID + ".txt");
		HashMap<Integer, MultiProbabilityNode> nodesToAdd = new HashMap<Integer, MultiProbabilityNode>();
		BufferedReader br = null;
		Random rand = null;
		for (int i = 0; i<sizeOfG; i++) {
			MultiProbabilityNode n = new MultiProbabilityNode(i, conceptList);
			nodesToAdd.put(n.getId(), n);
			sMg.addVertex(n);
		}
		
		try {
			fstream = new FileReader(file.getAbsoluteFile());
			br = new BufferedReader(fstream);
			
			String line;
			int seed = 0;
			int edgeId = 0;
			
			while ((line = br.readLine()) != null) {
				if (line.contains("Seed")) {
					seed = Integer.parseInt(line.split(":")[1]);
					rand = new Random(seed);
				}
				else {
					String ids[] = line.split(":");
					for (Concept c : conceptList) {
						TypedWeightedEdge tE = new TypedWeightedEdge(edgeId++, c.getId());
						if (c.getType().equals("lt")) {
							tE.setWeight(rand.nextDouble());
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.DIRECTED);
						}
						else {
							tE.setWeight(icInfection);
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.UNDIRECTED);
						}
					}
					
				}
			}
			rand = new Random(seed+2);
			for (MultiProbabilityNode n : sMg.getVertices()) {
				for (Concept c : conceptList) {
					if (c.getType().equals("lt")) {
						n.addAttribute(c.getId(), chooseThreshold(rand, setThresholds, thresholdAvg));
					}
				}
			}
			
			br.close();
			fstream.close();
			//System.out.println(edgeId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sMg;
	}
	
	public Graph<MultiProbabilityNode, TypedWeightedEdge> graphReadInRN(int sizeOfG, int p, int graphID, ArrayList<? extends Concept> conceptList, boolean setThresholds, double thresholdAvg, double icInfection){
		SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		FileReader fstream = null;
		//File file = new File("graphFiles" + sizeOfG + "N/" + sizeOfG + "NodeSmallWorldGraph" + graphID + ".txt");
		//System.out.println("Opening graph:" + graphID);
		File file = new File("graphFiles" + sizeOfG + "N/"+ sizeOfG + "NodeRandomNetworkEdges" + (p) + "Num" + graphID + ".txt");
		HashMap<Integer, MultiProbabilityNode> nodesToAdd = new HashMap<Integer, MultiProbabilityNode>();
		BufferedReader br = null;
		Random rand = null;
		for (int i = 0; i<sizeOfG; i++) {
			MultiProbabilityNode n = new MultiProbabilityNode(i, conceptList);
			nodesToAdd.put(n.getId(), n);
			sMg.addVertex(n);
		}
		try {
			fstream = new FileReader(file.getAbsoluteFile());
			br = new BufferedReader(fstream);
			String line;
			int seed = 0;
			int edgeId = 0;
			
			while ((line = br.readLine()) != null) {
				if (line.contains("Seed")) {
					seed = Integer.parseInt(line.split(":")[1]);
					rand = new Random(seed);
				}
				else {
					String ids[] = line.split(":");
					for (Concept c : conceptList) {
						TypedWeightedEdge tE = new TypedWeightedEdge(edgeId++, c.getId());
						if (c.getType().equals("lt")) {
							tE.setWeight(rand.nextDouble());
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.DIRECTED);
						}
						else {
							tE.setWeight(icInfection);
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.UNDIRECTED);
						}
					}
					
				}
			}
			rand = new Random(seed+2);
			for (MultiProbabilityNode n : sMg.getVertices()) {
				for (Concept c : conceptList) {
					if (c.getType().equals("lt")) {
						n.addAttribute(c.getId(), chooseThreshold(rand, setThresholds, thresholdAvg));
					}
				}
			}
			
			br.close();
			fstream.close();
			//System.out.println(edgeId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sMg;
	}
	
	public Graph<MultiProbabilityNode, TypedWeightedEdge> graphReadInStandford(String graphFolder, String filename, ArrayList<? extends Concept> conceptList, double icInfection, boolean setThresholds, double thresholdAvg, int seed){
		SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		FileReader fstream = null;
		//File file = new File("graphFiles" + sizeOfG + "N/" + sizeOfG + "NodeSmallWorldGraph" + graphID + ".txt");
		File file = new File(graphFolder + "/"+ filename);
		HashMap<Integer, MultiProbabilityNode> nodesToAdd = new HashMap<Integer, MultiProbabilityNode>();
		BufferedReader br = null;
		Random rand = null;
		
		try {
			fstream = new FileReader(file.getAbsoluteFile());
			br = new BufferedReader(fstream);
			
			String line;
			rand = new Random(seed);
			int edgeId = 0;
			TIntHashSet idSet = new TIntHashSet();
			while ((line = br.readLine()) != null) {
				if (!line.contains("#")) {
					String ids[] = line.split("\t");
					
					if (!idSet.contains(Integer.parseInt((ids[0])))) {
						MultiProbabilityNode n = new MultiProbabilityNode(Integer.parseInt(ids[0]), conceptList);
						nodesToAdd.put(n.getId(), n);
						sMg.addVertex(n);
						idSet.add(Integer.parseInt(ids[0]));
					}
					
					if (!idSet.contains(Integer.parseInt((ids[1])))) {
						MultiProbabilityNode n = new MultiProbabilityNode(Integer.parseInt(ids[1]), conceptList);
						nodesToAdd.put(n.getId(), n);
						sMg.addVertex(n);
						idSet.add(Integer.parseInt(ids[1]));
					}
					
					for (Concept c : conceptList) {
						TypedWeightedEdge tE = new TypedWeightedEdge(edgeId++, c.getId());
						if (c.getType().equals("lt")) {
							tE.setWeight(rand.nextDouble());
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.DIRECTED);
						}
						else {
							tE.setWeight(icInfection);
							sMg.addEdge(tE, nodesToAdd.get(Integer.parseInt(ids[0])), nodesToAdd.get(Integer.parseInt(ids[1])), EdgeType.UNDIRECTED);
						}
					}
					
				}
			}
			
			TIntHashSet ids = new TIntHashSet();
			TIntArrayList newids = new TIntArrayList();
			
			for (int ii = 0; ii < sMg.getVertexCount(); ii++) {
				ids.add(ii);
			} 
			
			for (MultiProbabilityNode node : sMg.getVertices()){
				if (ids.contains(node.getId())) {
					ids.remove(node.getId());
				}
				
				if (node.getId() >= sMg.getVertexCount()) {
					newids.add(node.getId());
				}
			}
			
			TIntIterator ider = ids.iterator();
			int k = 0;
			while (ider.hasNext()) {
				int id = ider.next();
				MultiProbabilityNode curr = nodesToAdd.get(newids.get(k));
				nodesToAdd.remove(curr.getId());
				curr.setId(id);
				nodesToAdd.put(curr.getId(), curr);
				k = k + 1;
			}
			
			rand = new Random(seed+2);
			for (MultiProbabilityNode n : sMg.getVertices()) {
				for (Concept c : conceptList) {
					if (c.getType().equals("lt")) {
						n.addAttribute(c.getId(), chooseThreshold(rand, setThresholds, thresholdAvg));
					}
				}
			}
			
			br.close();
			fstream.close();
			//System.out.println(edgeId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sMg;
	}
	
	private double chooseThreshold(Random rand, boolean setThresholds, double thresholdAvg) {
		if (setThresholds) {
			return thresholdAvg;
		}
		else {
			return (rand.nextGaussian() * 0.05) + thresholdAvg;
		}
	}

	public void graphSmallWorldMaker(int row, int col, double expo, int number, String graphFolder) {
		File file = new File(graphFolder + "/"+ row*col + "NodeSmallWorldGraphExpo" + (expo*100) + "Num" + number + ".txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Random rand = new Random();
		try {
			bw.write("Seed for weights:" + rand.nextInt(10000000) + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Graph<ProbabilityNode, Edge> edgeGraph = undirectedSWGraphMaker(row, col, expo);
		Collection<Edge> edgeSet = edgeGraph.getEdges();
		
		for (Edge edge : edgeSet) {
			Pair<ProbabilityNode> pair = edgeGraph.getEndpoints(edge);
			try {
				bw.write(pair.getFirst().getId() + ":" + pair.getSecond().getId() + "\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void randomGraphMaker (int nodes, String graphFolder, int number, int edges) {
		File file = new File(graphFolder + "/"+ nodes + "NodeRandomNetworkEdges" + (edges) + "Num" + number + ".txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Random rand = new Random();
		try {
			bw.write("Seed for weights:" + rand.nextInt(10000000) + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		HashMap<Integer, Node> nodeSet = new HashMap<Integer, Node>();
		for (int i =0; i < nodes; i++){
			Node node = new Node(i);
			graph.addVertex(node);
			nodeSet.put(i, node);
		//	System.out.println(i);
		}
		
		int max = edges;
		int edgeCount = 0;
		for (Node n : graph.getVertices()) {
			int neighbourCount = rand.nextInt(max) + 1;
			HashSet<Integer> neighbours = new HashSet<Integer>();
			
			for (Node nn : graph.getNeighbors(n)) {
				neighbours.add(nn.getId());
			}
			
			while (neighbours.size() < neighbourCount) {
				int selected = rand.nextInt(nodes);
				if (!neighbours.contains(selected)) {
					neighbours.add(selected);
					graph.addEdge(new WeightedEdge(edgeCount++, rand.nextDouble()), n, nodeSet.get(selected));
				}
			}
		}
		Collection<Edge> edgeSet = graph.getEdges();
		for (Edge edge : edgeSet) {
			Pair<Node> pair = graph.getEndpoints(edge);
			try {
				bw.write(pair.getFirst().getId() + ":" + pair.getSecond().getId() + "\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void randomGraphMaker(int nodes, String graphFolder, int number, double p){
		File file = new File(graphFolder + "/"+ nodes + "NodeRandomNetworkProb" + (p*100) + "Num" + number + ".txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Random rand = new Random();
		try {
			bw.write("Seed for weights:" + rand.nextInt(10000000) + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ErdosRenyiGenerator<ProbabilityNode, Edge> erG = new ErdosRenyiGenerator<ProbabilityNode, Edge>(UgF, pNF, eF, nodes, p);
		eF.reset();
		pNF.reset();
		Graph<ProbabilityNode, Edge> edgeGraph = erG.create();
		
		Collection<Edge> edgeSet = edgeGraph.getEdges();
		
		for (Edge edge : edgeSet) {
			Pair<ProbabilityNode> pair = edgeGraph.getEndpoints(edge);
			try {
				bw.write(pair.getFirst().getId() + ":" + pair.getSecond().getId() + "\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	
	public void graphScaleFreeMaker(int numOfNodes, int startNum, int edgesToAdd, int number, String graphFolder) {
		File file = new File(graphFolder + "/"+ startNum + "Start" + edgesToAdd + "EdgesAdded" + numOfNodes + 
				"NodeScaleFreeGraph" + "Num" + number + ".txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bw.write("Seed for weights:" + number + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Graph<ProbabilityNode, Edge> edgeGraph = undirectedSFGraphMaker(numOfNodes, startNum, edgesToAdd);
		Collection<Edge> edgeSet = edgeGraph.getEdges();
		
		for (Edge edge : edgeSet) {
			Pair<ProbabilityNode> pair = edgeGraph.getEndpoints(edge);
			try {
				bw.write(pair.getFirst().getId() + ":" + pair.getSecond().getId() + "\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
