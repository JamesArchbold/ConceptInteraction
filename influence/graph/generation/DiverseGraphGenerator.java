package influence.graph.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import influence.graph.generation.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import influence.concepts.Concept;
import influence.edges.Edge;
import influence.edges.TypedWeightedEdge;
import influence.graph.makers.EdgeFactory;
import influence.graph.makers.GraphFactory;
import influence.graph.makers.ProbabilityNodeFactory;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.ProbabilityNode;

public class DiverseGraphGenerator {
	private EdgeFactory eF;
	private ProbabilityNodeFactory pNF;
	private GraphFactory gF;
	
	public DiverseGraphGenerator() {
		eF = new EdgeFactory();
		pNF = new ProbabilityNodeFactory();
		gF = new GraphFactory();
	}
	
	public Graph<ProbabilityNode, Edge> undirectedGraphMaker(int row, int col, double expo) {
		KleinbergSmallWorldGenerator<ProbabilityNode,Edge> swG = new KleinbergSmallWorldGenerator<ProbabilityNode,Edge>(gF, pNF, eF,row, col, expo);
		eF.reset();
		pNF.reset();
		return swG.create();
	}
	
	public Graph<MultiProbabilityNode, TypedWeightedEdge> ICICgraphMaker(int row, int col, double expo, ArrayList<Concept> conNum, ArrayList<Double> spreads){
		SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
		new Random();
		ArrayList<Graph<ProbabilityNode, Edge>> graphList = new ArrayList<Graph<ProbabilityNode, Edge>>();
		
		for (int i = 0; i < conNum.size(); i++){
			graphList.add(undirectedGraphMaker(row,col,expo));
		}
		
		for (int i = 0; i < row*col; i++){
			MultiProbabilityNode n = new MultiProbabilityNode(i, conNum);
			nodesToAdd.add(n);
			sMg.addVertex(nodesToAdd.get(i));
		}
		
		int edgeId = 0;
		for (int i = 0; i < conNum.size(); i++) {
			Collection<Edge> edges = graphList.get(i).getEdges();
			
			for (Edge e : edges) {
				Pair<ProbabilityNode> pair = graphList.get(i).getEndpoints(e);
				TypedWeightedEdge tE = new TypedWeightedEdge(edgeId++, conNum.get(i).getId());
				tE.setWeight(spreads.get(i));
				//System.out.println(spreads.get(i));
				sMg.addEdge(tE, nodesToAdd.get(pair.getFirst().getId()), nodesToAdd.get(pair.getSecond().getId()), EdgeType.UNDIRECTED);
				
			}
		}
		
		return sMg;
	}
	public Graph<MultiProbabilityNode, TypedWeightedEdge> LTLTgraphMaker(int row, int col, double expo, ArrayList<Concept> conNum, int LTID, int LTID2){
		SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
		Random ran = new Random();
		ArrayList<Graph<ProbabilityNode, Edge>> graphList = new ArrayList<Graph<ProbabilityNode, Edge>>();
		
		for (int i = 0; i < conNum.size(); i++){
			graphList.add(undirectedGraphMaker(row,col,expo));
		}
		
		
		for (int i = 0; i < row*col; i++){
			MultiProbabilityNode n = new MultiProbabilityNode(i, conNum);
			n.addAttribute(LTID, Math.random());
			n.addAttribute(LTID2, Math.random());
			nodesToAdd.add(n);
			sMg.addVertex(nodesToAdd.get(i));
		}
		
		int edgeId = 0;
		for (int i = 0; i < conNum.size(); i++) {
			Collection<Edge> edges = graphList.get(i).getEdges();
			
			for (Edge e : edges) {
				Pair<ProbabilityNode> pair = graphList.get(i).getEndpoints(e);
				TypedWeightedEdge tE = new TypedWeightedEdge(edgeId++, conNum.get(i).getId());
				if (i == LTID) {
					tE.setWeight(ran.nextDouble() / 2);
					sMg.addEdge(tE, nodesToAdd.get(pair.getFirst().getId()), nodesToAdd.get(pair.getSecond().getId()), EdgeType.DIRECTED);
				}
				else {
					tE.setWeight(ran.nextDouble() / 2);
					sMg.addEdge(tE, nodesToAdd.get(pair.getFirst().getId()), nodesToAdd.get(pair.getSecond().getId()), EdgeType.DIRECTED);
				}
				
			}
		}
		
		return sMg;
	}
	public Graph<MultiProbabilityNode, TypedWeightedEdge> multiGraphMaker(int row, int col, double expo, ArrayList<Concept> conNum, int LTID, double ICChance) {
		SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
		Random ran = new Random();
		ArrayList<Graph<ProbabilityNode, Edge>> graphList = new ArrayList<Graph<ProbabilityNode, Edge>>();
		
		for (int i = 0; i < conNum.size(); i++){
			graphList.add(undirectedGraphMaker(row,col,expo));
		}
		
		
		for (int i = 0; i < row*col; i++){
			MultiProbabilityNode n = new MultiProbabilityNode(i, conNum);
			n.addAttribute(LTID, Math.random());
			nodesToAdd.add(n);
			sMg.addVertex(nodesToAdd.get(i));
		}
		
		int edgeId = 0;
		for (int i = 0; i < conNum.size(); i++) {
			Collection<Edge> edges = graphList.get(i).getEdges();
			
			for (Edge e : edges) {
				Pair<ProbabilityNode> pair = graphList.get(i).getEndpoints(e);
				TypedWeightedEdge tE = new TypedWeightedEdge(edgeId++, conNum.get(i).getId());
				if (i == LTID) {
					tE.setWeight(ran.nextDouble() / 2);
					sMg.addEdge(tE, nodesToAdd.get(pair.getFirst().getId()), nodesToAdd.get(pair.getSecond().getId()), EdgeType.DIRECTED);
				}
				else {
					tE.setWeight(ICChance);
					sMg.addEdge(tE, nodesToAdd.get(pair.getFirst().getId()), nodesToAdd.get(pair.getSecond().getId()), EdgeType.UNDIRECTED);
				}
				
			}
		}
		
		return sMg;

	}
}
