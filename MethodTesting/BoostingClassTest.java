package MethodTesting;
//small boosting test class
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.edges.TypedWeightedEdge;
import influence.graph.generation.GraphReaderWriter;
import influence.nodes.MultiProbabilityNode;
import influence.seed.selection.BoostingDiscount;
import influence.seed.selection.BoostingPlacement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import edu.uci.ics.jung.graph.Graph;

public class BoostingClassTest {

	public static void main(String args[]) {
		GraphReaderWriter grw = new GraphReaderWriter();
		int graphSize = 1000;
		int seedSize = 100;
		
		for (int i = 0; i < 1; i++){
			Concept ic = new DiverseICConcept(0, i);
			Concept lt = new DiverseLTConcept(1);
			ic.addConceptInteractions(lt, 1, 1);
	

			ArrayList<Concept> cList = new ArrayList<Concept>();
			cList.add(ic);
			cList.add(lt);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(1000, i, cList, false, 0.8, 0.1);
			HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
			//int seedSize = 500;
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}

			while (seedsIC.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(ic);
				seedsIC.add(nodes.get(id));
				
			}
            
			HashSet<MultiProbabilityNode> seed2 = BoostingDiscount.getSeeds(smg, seedSize, ic, lt, seedsIC, 1);
			HashSet<MultiProbabilityNode> seed3 = BoostingPlacement.getSeedsBoostingOutgoingHops(smg, seedSize, ic, lt, seedsIC, 1);
			
			for (MultiProbabilityNode m : seed2) {
				//seedsLT.add(m);
			    System.out.println(m.getId());
			}
			 System.out.println("------_");
			for (MultiProbabilityNode m : seed2) {
				//m.activate(lt);
				//seedsLT.add(m);
				if (!seedsIC.contains(m)) {
					 System.out.println(m.getId());
				}
			   
			}
			System.out.println("++++++++++++++++++++++++++++");
			for (MultiProbabilityNode m : seed3) {
				//seedsLT.add(m);
			    System.out.println(m.getId());
			}
			 System.out.println("------_");
			for (MultiProbabilityNode m : seed3) {
				//m.activate(lt);
				//seedsLT.add(m);
				if (!seedsIC.contains(m)) {
					 System.out.println(m.getId());
				}
			   
			}
			
			System.out.println("=========================");
			for (MultiProbabilityNode m : seed3) {
				//seedsLT.add(m);
				if (!seed2.contains(m)){
					System.out.println(m.getId());
			    }
			}
			 System.out.println("------_");
			for (MultiProbabilityNode m : seed3) {
				//m.activate(lt);
				//seedsLT.add(m);
				if (!seedsIC.contains(m) && !seed2.contains(m)) {
					 System.out.println(m.getId());
				}
			   
			}
			
			System.out.println("#####################");
			for (MultiProbabilityNode m : seed2) {
				//seedsLT.add(m);
				if (!seed3.contains(m)){
					System.out.println(m.getId());
			    }
			}
			 System.out.println("------_");
			for (MultiProbabilityNode m : seed2) {
				//m.activate(lt);
				//seedsLT.add(m);
				if (!seedsIC.contains(m) && !seed3.contains(m)) {
					 System.out.println(m.getId());
				}
			   
			}
			
		}
	}
}
