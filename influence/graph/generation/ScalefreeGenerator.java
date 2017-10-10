package influence.graph.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import influence.concepts.Concept;
import influence.edges.Edge;
import influence.edges.TypedEdge;
import influence.edges.WeightedEdge;
import influence.graph.suppliers.DirectedGraphSupplier;
import influence.graph.suppliers.EdgeSupplier;
import influence.graph.suppliers.GraphSupplier;
import influence.graph.suppliers.ProbabilityNodeSupplier;
import influence.graph.suppliers.ThresholdNodeSupplier;
import influence.graph.suppliers.WeightedEdgeSupplier;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.ProbabilityNode;
import influence.nodes.ThresholdNode;
import influence.graph.generation.BarabasiAlbertGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

public class ScalefreeGenerator {

		private EdgeSupplier eF;
		private ProbabilityNodeSupplier pNF;
		private WeightedEdgeSupplier wEF;
		private ThresholdNodeSupplier tNF;
		private GraphSupplier gF;
		private DirectedGraphSupplier dGF;
		
		public ScalefreeGenerator() {
			eF = new EdgeSupplier();
			pNF = new ProbabilityNodeSupplier();
			wEF = new WeightedEdgeSupplier();
			tNF = new ThresholdNodeSupplier();
			gF = new GraphSupplier();
			dGF = new DirectedGraphSupplier();
		}
		
		public Graph<ThresholdNode, WeightedEdge> directedGraphMaker(int nodeNum, int init, int edgeNum) {
			BarabasiAlbertGenerator<ThresholdNode, WeightedEdge> bAG = 
					new BarabasiAlbertGenerator<ThresholdNode, WeightedEdge>(dGF, tNF, wEF, init, edgeNum, new HashSet<ThresholdNode>());
			bAG.evolveGraph(nodeNum - init);
			Graph<ThresholdNode, WeightedEdge> sG= bAG.create();
			return sG;
		}
		
		public Graph<ProbabilityNode, Edge> undirectedGraphMaker(int nodeNum, int init, int edgeNum) {
			BarabasiAlbertGenerator<ProbabilityNode, Edge> bAG = 
					new BarabasiAlbertGenerator<ProbabilityNode, Edge>(gF, pNF, eF, init, edgeNum, new HashSet<ProbabilityNode>());

			System.out.println("Initial made");
			bAG.evolveGraph(nodeNum - init);
			System.out.println("Evolved");
			Graph<ProbabilityNode, Edge> sG= bAG.create();
			eF.reset();
			pNF.reset();
			return sG;
		}
		
		public Graph<ProbabilityNode, Edge> undirectedGraphMaker(int nodeNum, int init, int edgeNum, int seed) {
			BarabasiAlbertGenerator<ProbabilityNode, Edge> bAG = 
					new BarabasiAlbertGenerator<ProbabilityNode, Edge>(gF, pNF, eF, init, edgeNum, seed, new HashSet<ProbabilityNode>());
			
			bAG.evolveGraph(nodeNum - init);
			Graph<ProbabilityNode, Edge> sG= bAG.create();
			eF.reset();
			pNF.reset();
			return sG;
		}
		
		public Graph<MultiProbabilityNode,  TypedEdge> undirectedMultiGraphMakerSplit(int nodeNum, int init, int edgeNum, ArrayList<Concept> conNum, int seed){
			Graph<ProbabilityNode, Edge> g = undirectedGraphMaker(nodeNum, init, edgeNum, seed);
			
			SparseMultigraph<MultiProbabilityNode, TypedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedEdge>();
			ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
			
			for (int i = 0; i < nodeNum; i++){
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
		//	eF.reset();
		//	pNF.reset();
			return sMg;
		}
		
		public Graph<MultiProbabilityNode, TypedEdge> undirectedMultiGraphMakerSplit(int nodeNum, int init, int edgeNum, ArrayList<Concept> conNum){
			Graph<ProbabilityNode, Edge> g = undirectedGraphMaker(nodeNum, init, edgeNum);
			
			SparseMultigraph<MultiProbabilityNode, TypedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedEdge>();
			ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
			
			for (int i = 0; i < nodeNum; i++){
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
		//	eF.reset();
		//	pNF.reset();
			return sMg;
			
		}
		
		public Graph<MultiProbabilityNode, TypedEdge> undirectedMultiGraphMaker(int nodeNum, int init, int edgeNum, ArrayList<Concept> conNum){
			ArrayList<Graph<ProbabilityNode, Edge>> graphList = new ArrayList<Graph<ProbabilityNode, Edge>>();
			
			for (int i = 0; i < conNum.size(); i++){
				graphList.add(undirectedGraphMaker(nodeNum, init, edgeNum));
		//		eF.reset();
		//		pNF.reset();
			}
			
			SparseMultigraph<MultiProbabilityNode, TypedEdge> sMg = new SparseMultigraph<MultiProbabilityNode, TypedEdge>();
			ArrayList<MultiProbabilityNode> nodesToAdd = new ArrayList<MultiProbabilityNode>();
			
			for (int i = 0; i < nodeNum; i++){
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
