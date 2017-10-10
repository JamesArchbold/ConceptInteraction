package influence.graph.generation;

import java.util.ArrayList;
import java.util.Collection;
import influence.graph.generation.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import influence.concepts.Concept;
import influence.edges.Edge;
import influence.edges.TypedEdge;
import influence.graph.makers.EdgeFactory;
import influence.graph.makers.GraphFactory;
import influence.graph.makers.ProbabilityNodeFactory;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.ProbabilityNode;

public class SmallworldGenerator {
	private EdgeFactory eF;
	private ProbabilityNodeFactory pNF;
	//private WeightedEdgeFactory wEF;
	//private ThresholdNodeFactory tNF;
	private GraphFactory gF;
	//private DirectedGraphFactory dGF;
	
	public SmallworldGenerator() {
		eF = new EdgeFactory();
		pNF = new ProbabilityNodeFactory();
	//	wEF = new WeightedEdgeFactory();
	//	tNF = new ThresholdNodeFactory();
		gF = new GraphFactory();
	//	dGF = new DirectedGraphFactory();
	}
	
	public Graph<ProbabilityNode, Edge> undirectedGraphMaker(int row, int col, double expo) {
		KleinbergSmallWorldGenerator<ProbabilityNode,Edge> swG = new KleinbergSmallWorldGenerator<ProbabilityNode,Edge>(gF, pNF, eF,row, col, expo);
		eF.reset();
		pNF.reset();
		return swG.create();
	}
	
	public Graph<ProbabilityNode, Edge> undirectedGraphMaker(int row, int col, double expo, int seed) {
		KleinbergSmallWorldGenerator<ProbabilityNode,Edge> swG = new KleinbergSmallWorldGenerator<ProbabilityNode,Edge>(gF, pNF, eF,row, col, expo);
		swG.setRandomSeed(seed);
		eF.reset();
		pNF.reset();
		return swG.create();
	}
	
	public Graph<MultiProbabilityNode, TypedEdge> undirectedMultiGraphMakerSplit(int row, int col, double expo, ArrayList<Concept> conNum, int seed){
		Graph<ProbabilityNode, Edge> g = undirectedGraphMaker(row, col, expo, seed);
		
		SparseMultigraph<MultiProbabilityNode, TypedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedEdge>();
		ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
		
		for (int i = 0; i < (row*col); i++){
			nodesToAdd.add(new MultiProbabilityNode(i, conNum));
			sMg.addVertex(nodesToAdd.get(i));
		}
		
		int edgeId = 0;
		
		Collection<Edge> edges = g.getEdges();
		
		for (int i = 0; i < conNum.size(); i++) {
			for (Edge e : edges) {
				Pair<ProbabilityNode> pair = g.getEndpoints(e);
				TypedEdge tE = new TypedEdge(edgeId++, conNum.get(i).getId());
				
				sMg.addEdge(tE, nodesToAdd.get(pair.getFirst().getId()), nodesToAdd.get(pair.getSecond().getId()));
			}
		}
		
		return sMg;
		
	}
	
	public Graph<MultiProbabilityNode, TypedEdge> undirectedMultiGraphMakerSplit(int row, int col, double expo, ArrayList<Concept> conNum){
		Graph<ProbabilityNode, Edge> g = undirectedGraphMaker(row, col, expo);
		
		SparseMultigraph<MultiProbabilityNode, TypedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedEdge>();
		ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
		
		for (int i = 0; i < (row*col); i++){
			nodesToAdd.add(new MultiProbabilityNode(i, conNum));
			sMg.addVertex(nodesToAdd.get(i));
		}
		
		int edgeId = 0;
		
		Collection<Edge> edges = g.getEdges();
		
		for (int i = 0; i < conNum.size(); i++) {
			for (Edge e : edges) {
				Pair<ProbabilityNode> pair = g.getEndpoints(e);
				TypedEdge tE = new TypedEdge(edgeId++, conNum.get(i).getId());
				
				sMg.addEdge(tE, nodesToAdd.get(pair.getFirst().getId()), nodesToAdd.get(pair.getSecond().getId()));
			}
		}
		
		return sMg;
		
	}
	
	public Graph<MultiProbabilityNode, TypedEdge> unidrectedMultiGraphMaker(int row, int col, double expo, ArrayList<Concept> conNum){
		ArrayList<Graph<ProbabilityNode, Edge>> graphList = new ArrayList<Graph<ProbabilityNode, Edge>>();
		
		for (int i = 0; i < conNum.size(); i++){
			graphList.add(undirectedGraphMaker(row,col,expo));
		//	eF.reset();
			//pNF.reset();
		}
		
		SparseMultigraph<MultiProbabilityNode, TypedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedEdge>();
		ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
		
		for (int i = 0; i < row*col; i++){
			nodesToAdd.add(new MultiProbabilityNode(i, conNum));
			sMg.addVertex(nodesToAdd.get(i));
		}
		
		int edgeId = 0;
		for (int i = 0; i < conNum.size(); i++) {
			Collection<Edge> edges = graphList.get(i).getEdges();
			
			for (Edge e : edges) {
				Pair<ProbabilityNode> pair = graphList.get(i).getEndpoints(e);
				TypedEdge tE = new TypedEdge(edgeId++, conNum.get(i).getId());
				
				sMg.addEdge(tE, nodesToAdd.get(pair.getFirst().getId()), nodesToAdd.get(pair.getSecond().getId()));
			}
		}
		
		return sMg;
	}
	
}
