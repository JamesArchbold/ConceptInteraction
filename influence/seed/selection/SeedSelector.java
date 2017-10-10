package influence.seed.selection;
//methods for selecting different heuristics, mostly used seedSelectionAnt and seedSelectionNew methods at bottom of class
import influence.concepts.Concept;
import influence.concepts.ExtendedConcept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class SeedSelector {
	public static void seedSelection(Concept target, Concept boosting,
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg, int seedSize, HashSet<MultiProbabilityNode> seedsTarget, HashSet<MultiProbabilityNode> seedsBoost, int seedSelection,
			Random rand, double interaction, int graphSize, HashMap<Integer, MultiProbabilityNode> nodes, String graphType, String gChar, int run) {
		
		while (seedsBoost.size() < seedSize) {
			if (seedSelection == 0) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(boosting);
				seedsBoost.add(nodes.get(id));
			}
			else if (seedSelection == 2) {
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 3){
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 4) {
				HashSet<MultiProbabilityNode> seeds = BoostingPlacement.getSeedsBoostingAll(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 5) {
				HashSet<MultiProbabilityNode> seeds = BoostingPlacement.getSeedsBoostingOutgoing(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 6) {
				HashSet<MultiProbabilityNode> seeds = BoostingPlacement.getSeedsBoostingAllHops(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 7) {
				HashSet<MultiProbabilityNode> seeds = BoostingPlacement.getSeedsBoostingOutgoingHops(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 8) {
				HashSet<MultiProbabilityNode> seeds = BoostingDiscount.getSeeds(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 9) {
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 10) {
				HashSet<MultiProbabilityNode> seeds = DegreeDiscount.getSeedsLTSame(smg, seedSize, target);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
			else if (seedSelection == 11) {
				HashSet<MultiProbabilityNode> seeds = SingleDiscount.getSeedsTarget(smg, seedSize, target);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
			else if (seedSelection == 12) {
				int eCount = 0;
				SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> newG = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
				for (MultiProbabilityNode m : smg.getVertices()) {
					newG.addVertex(m);
				}
				for (TypedWeightedEdge t : smg.getEdges()){
					if (t.getConcept() == target.getId()) {
						if (smg.getEdgeType(t) == EdgeType.UNDIRECTED){
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, target.getId(), t.getWeight());
							TypedWeightedEdge t2 = new TypedWeightedEdge(eCount+1, target.getId(), t.getWeight());
							Pair<MultiProbabilityNode> ends = smg.getEndpoints(t);
							
							newG.addEdge(t1, ends.getFirst(), ends.getSecond(), EdgeType.DIRECTED);
							newG.addEdge(t2, ends.getSecond(), ends.getFirst(), EdgeType.DIRECTED);
							
						//	System.out.println(newG.getEdgeType(t1));
							eCount = eCount + 2;
						}
						else{
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, target.getId(), t.getWeight());
							newG.addEdge(t1, smg.getSource(t), smg.getDest(t),EdgeType.DIRECTED);
							eCount = eCount + 1;
						}
					}
				}
				
				HashSet<MultiProbabilityNode> seeds = PMAIHeuristc.getSeedsBurnIn(newG, seedSize, (1.0/320.0), seedsTarget);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
				
			}
			else if (seedSelection == 13) {
				int eCount = 0;
				SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> newG = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
				for (MultiProbabilityNode m : smg.getVertices()) {
					newG.addVertex(m);
				}
				for (TypedWeightedEdge t : smg.getEdges()){
					if (t.getConcept() == boosting.getId()) {
						if (smg.getEdgeType(t) == EdgeType.UNDIRECTED){
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, boosting.getId(), t.getWeight());
							TypedWeightedEdge t2 = new TypedWeightedEdge(eCount+1, boosting.getId(), t.getWeight());
							Pair<MultiProbabilityNode> ends = smg.getEndpoints(t);
							newG.addEdge(t1, ends.getFirst(), ends.getSecond(), EdgeType.DIRECTED);
							newG.addEdge(t2, ends.getSecond(), ends.getFirst(), EdgeType.DIRECTED);
							eCount = eCount + 2;
						}
						else{
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, boosting.getId(), t.getWeight());
							newG.addEdge(t1, smg.getSource(t), smg.getDest(t),EdgeType.DIRECTED);
							eCount = eCount + 1;
						}
					}
				}
				
				HashSet<MultiProbabilityNode> seeds = PMAIHeuristc.getSeeds(newG, seedSize, (1.0/320.0));
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
				
			}
			else if (seedSelection == 14) {
				ArrayList<MultiProbabilityNode> sT = new ArrayList<MultiProbabilityNode>();
				for (MultiProbabilityNode m : seedsTarget){
					sT.add(m);
				}
				int eCount = 0;
				SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> newG = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
				for (MultiProbabilityNode m : smg.getVertices()) {
					newG.addVertex(m);
				}
				for (TypedWeightedEdge t : smg.getEdges()){
					if (smg.getEdgeType(t) == EdgeType.UNDIRECTED){
						TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, t.getConcept(), t.getWeight());
						TypedWeightedEdge t2 = new TypedWeightedEdge(eCount+1, t.getConcept(), t.getWeight());
						Pair<MultiProbabilityNode> ends = smg.getEndpoints(t);
						newG.addEdge(t1, ends.getFirst(), ends.getSecond(), EdgeType.DIRECTED);
						newG.addEdge(t2, ends.getSecond(), ends.getFirst(), EdgeType.DIRECTED);
						eCount = eCount + 2;
					}
					else{
						TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, t.getConcept(), t.getWeight());
						newG.addEdge(t1, smg.getSource(t), smg.getDest(t),EdgeType.DIRECTED);
						eCount = eCount + 1;
					}
				}
				
				HashSet<MultiProbabilityNode> seeds = PMAIBoostHeuristic.getSeeds(newG, seedSize, (1.0/320.0), interaction, sT, target, boosting);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
				
			}
			else if (seedSelection == 15) {
				HashSet<MultiProbabilityNode> seeds = DegreeDiscount.getSeedsBoostDegDis(smg, seedSize, target, boosting);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
				
			}
			else if (seedSelection == 16) {
				System.out.println("CHoosing");
				long start = System.currentTimeMillis();
				HashSet<MultiProbabilityNode> seeds;
				if (target.getType().equals("lt")) {
					seeds = ExpectedPotential.getSeedsLT(smg, seedSize, 0.01, target, boosting, seedsTarget);
				}
				else {
					seeds = ExpectedPotential.getSeeds(smg, seedSize, 0.01, target, boosting, seedsTarget);
				}
				
				
				long end = System.currentTimeMillis();
				long milli = end - start;
				double sec = milli/1000.0;
				double min = sec / 60.0;
				double hor = min / 60.0;
				System.out.println("Time taken (mintes):" + min);
				System.out.println("Time taken (hours):" + hor);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
			}
			else if (seedSelection == 160) {
				System.out.println("CHoosing");
				long start = System.currentTimeMillis();
				HashSet<MultiProbabilityNode> seeds = ExpectedPotentialTreeBase.getSeeds(smg, seedSize, 0.01, target, boosting, seedsTarget);
				long end = System.currentTimeMillis();
				long milli = end - start;
				double sec = milli/1000.0;
				double min = sec / 60.0;
				double hor = min / 60.0;
				System.out.println("Time taken (mintes):" + min);
				System.out.println("Time taken (hours):" + hor);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
			}
			else if (seedSelection == 17) {
				System.out.println("Choosing");
				long start = System.currentTimeMillis();
				HashSet<MultiProbabilityNode> seeds = MIIP.getSeeds(smg, seedSize, target, boosting, seedsTarget);
				long end = System.currentTimeMillis();
				long milli = end - start;
				double sec = milli/1000.0;
				double min = sec / 60.0;
				double hor = min / 60.0;
				System.out.println("Time taken (mintes):" + min);
				System.out.println("Time taken (hours):" + hor);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
			}
			else if (seedSelection == 18) {
				for (MultiProbabilityNode n : seedsTarget) {
					n.deactivate(target);
				}
				seedsTarget.clear();
				HashSet<MultiProbabilityNode> seeds = DegreeDiscount.getSeedsLT(smg, seedSize, target);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
			else if (seedSelection == 19) {
				//Do nothing - We just want random target seeds
				break;
			}
			else if (seedSelection == 20){
				for (MultiProbabilityNode n : seedsTarget) {
					n.deactivate(target);
				}
				seedsTarget.clear();
				HashSet<MultiProbabilityNode> seeds = Degree.getSeeds(smg, seedSize);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
			else if (seedSelection == 21){
				for (MultiProbabilityNode n : seedsTarget) {
					n.deactivate(target);
				}
				seedsTarget.clear();
				HashSet<MultiProbabilityNode> seeds = SingleDiscount.getSeeds(smg, seedSize);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
		}
	}
	
	public static void seedSelectionNew(ArrayList<ExtendedConcept> concepts, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, int seedSize, 
			ArrayList<HashSet<MultiProbabilityNode>> seedGroups, int seedSelection, Random rand, int graphSize, 
			HashMap<Integer, MultiProbabilityNode> nodes, int conToSelect, int targetCon, String graphType, String gChar, int run) {
		
		HashSet<MultiProbabilityNode> seedsToGet = seedGroups.get(conToSelect);
		HashSet<MultiProbabilityNode> seedsTarget = seedGroups.get(targetCon);
		Concept conActivating = concepts.get(conToSelect);
		Concept tarActivating = concepts.get(targetCon);
		System.out.println("Choosing heuristic " + seedSelection + " for concept " + conToSelect);
		while (seedsToGet.size() < seedSize) {
			
			if (seedSelection == 0) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(conActivating);
				seedsToGet.add(nodes.get(id));
			}
			else if (seedSelection == 2) {
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				for (MultiProbabilityNode n : seeds) {
					n.activate(conActivating);
					seedsToGet.add(n);
				}
			}
			else if (seedSelection == 3){
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				for (MultiProbabilityNode n : seeds) {
					n.activate(conActivating);
					seedsToGet.add(n);
				}
			}
			else if (seedSelection == 4) {
				HashSet<MultiProbabilityNode> seeds = ExpectedInfectedDiscount.getSeedsBurnIn(smg, seedSize, conActivating);
				for (MultiProbabilityNode n : seeds) {
					n.activate(conActivating);
					seedsToGet.add(n);
				}
				
			}
			else if (seedSelection == 5) {
				HashSet<MultiProbabilityNode> seeds = WeightedDiscount.getSeedsBurnIn(smg, seedSize, conActivating);
				for (MultiProbabilityNode n : seeds) {
					n.activate(conActivating);
					seedsToGet.add(n);
				}
			}
			else if (seedSelection == 9) {
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				System.out.println(conToSelect);
				for (MultiProbabilityNode n : seeds) {
					n.activate(conActivating);
					seedsToGet.add(n);
				}
			}
			else if (seedSelection == 12) {
				int eCount = 0;
				SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> newG = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
				for (MultiProbabilityNode m : smg.getVertices()) {
					newG.addVertex(m);
				}
				for (TypedWeightedEdge t : smg.getEdges()){
					if (t.getConcept() == conActivating.getId()) {
						if (smg.getEdgeType(t) == EdgeType.UNDIRECTED){
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, conActivating.getId(), t.getWeight());
							TypedWeightedEdge t2 = new TypedWeightedEdge(eCount+1, conActivating.getId(), t.getWeight());
							Pair<MultiProbabilityNode> ends = smg.getEndpoints(t);
							
							newG.addEdge(t1, ends.getFirst(), ends.getSecond(), EdgeType.DIRECTED);
							newG.addEdge(t2, ends.getSecond(), ends.getFirst(), EdgeType.DIRECTED);
							
						//	System.out.println(newG.getEdgeType(t1));
							eCount = eCount + 2;
						}
						else{
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, conActivating.getId(), t.getWeight());
							newG.addEdge(t1, smg.getSource(t), smg.getDest(t),EdgeType.DIRECTED);
							eCount = eCount + 1;
						}
					}
				}
				
				HashSet<MultiProbabilityNode> seeds = PMAIHeuristc.getSeedsBurnIn(newG, seedSize, (1.0/320.0), seedsToGet);
				for (MultiProbabilityNode n : seeds) {
					n.activate(conActivating);
					seedsToGet.add(n);
				}
				break;	
			}
			else if (seedSelection == 16) {
				System.out.println("CHoosing");
				long start = System.currentTimeMillis();
				HashSet<MultiProbabilityNode> seeds;
				if (tarActivating.getType().equals("lt")) {
					seeds = ExpectedPotential.getSeedsLT(smg, seedSize, 0.01, tarActivating, conActivating, seedsTarget);
				}
				else {
					seeds = ExpectedPotential.getSeeds(smg, seedSize, 0.01, tarActivating, conActivating, seedsTarget);
				}
				
				
				long end = System.currentTimeMillis();
				long milli = end - start;
				double sec = milli/1000.0;
				double min = sec / 60.0;
				double hor = min / 60.0;
				System.out.println("Time taken (mintes):" + min);
				System.out.println("Time taken (hours):" + hor);
				for (MultiProbabilityNode n : seeds) {
					n.activate(conActivating);
					seedsToGet.add(n);
				}
				break;
			}
			else if (seedSelection == 17) {
				System.out.println("Choosing");
				long start = System.currentTimeMillis();
				HashSet<MultiProbabilityNode> seeds = MIIP.getSeeds(smg, seedSize, tarActivating, conActivating, seedsTarget);
				long end = System.currentTimeMillis();
				long milli = end - start;
				double sec = milli/1000.0;
				double min = sec / 60.0;
				double hor = min / 60.0;
				System.out.println("Time taken (mintes):" + min);
				System.out.println("Time taken (hours):" + hor);
				for (MultiProbabilityNode n : seeds) {
					n.activate(conActivating);
					seedsToGet.add(n);
				}
				break;
			}
		}
	}
	
	public static void seedSelectionAnt(ArrayList<ExtendedConcept> concepts, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, int seedSize, 
			ArrayList<HashSet<MultiProbabilityNode>> seedGroups, int seedSelection, Random rand, int graphSize, 
			HashMap<Integer, MultiProbabilityNode> nodes, int conToSelect, int targetCon, String graphType, String gChar, int run) {
		
		HashSet<MultiProbabilityNode> seedsToGet = seedGroups.get(conToSelect);
		HashSet<MultiProbabilityNode> seedsTarget = seedGroups.get(targetCon);
		Concept conActivating = concepts.get(conToSelect);
		Concept tarActivating = concepts.get(targetCon);
		System.out.println("Choosing heuristic " + seedSelection + " for concept " + conToSelect);
		while (seedsToGet.size() < seedSize) {
			
			if (seedSelection == 0) {
				int id = rand.nextInt(graphSize);
				seedsToGet.add(nodes.get(id));
			}
			else if (seedSelection == 2) {
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
			}
			else if (seedSelection == 3){
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
			}
			else if (seedSelection == 4) {
				HashSet<MultiProbabilityNode> seeds = ExpectedInfectedDiscount.getSeedsBurnIn(smg, seedSize, conActivating);
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
				
			}
			else if (seedSelection == 5) {
				HashSet<MultiProbabilityNode> seeds = WeightedDiscount.getSeedsBurnIn(smg, seedSize, conActivating);
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
			}
			else if (seedSelection == 9) {
				HashSet<MultiProbabilityNode> seeds = seedReader(graphSize, graphType, gChar, run, seedSelection, seedSize, nodes);
				System.out.println(conToSelect);
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
			}
			else if (seedSelection == 12) {
				int eCount = 0;
				SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> newG = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
				for (MultiProbabilityNode m : smg.getVertices()) {
					newG.addVertex(m);
				}
				for (TypedWeightedEdge t : smg.getEdges()){
					if (t.getConcept() == conActivating.getId()) {
						if (smg.getEdgeType(t) == EdgeType.UNDIRECTED){
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, conActivating.getId(), t.getWeight());
							TypedWeightedEdge t2 = new TypedWeightedEdge(eCount+1, conActivating.getId(), t.getWeight());
							Pair<MultiProbabilityNode> ends = smg.getEndpoints(t);
							
							newG.addEdge(t1, ends.getFirst(), ends.getSecond(), EdgeType.DIRECTED);
							newG.addEdge(t2, ends.getSecond(), ends.getFirst(), EdgeType.DIRECTED);
							
						//	System.out.println(newG.getEdgeType(t1));
							eCount = eCount + 2;
						}
						else{
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, conActivating.getId(), t.getWeight());
							newG.addEdge(t1, smg.getSource(t), smg.getDest(t),EdgeType.DIRECTED);
							eCount = eCount + 1;
						}
					}
				}
				
				HashSet<MultiProbabilityNode> seeds = PMAIHeuristc.getSeedsBurnIn(newG, seedSize, (1.0/320.0), seedsToGet);
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
				break;	
			}
			else if (seedSelection == 16) {
				System.out.println("CHoosing");
				long start = System.currentTimeMillis();
				HashSet<MultiProbabilityNode> seeds;
				if (tarActivating.getType().equals("lt")) {
					seeds = ExpectedPotential.getSeedsLT(smg, seedSize, 0.01, tarActivating, conActivating, seedsTarget);
				}
				else {
					seeds = ExpectedPotential.getSeeds(smg, seedSize, 0.01, tarActivating, conActivating, seedsTarget);
				}
				
				
				long end = System.currentTimeMillis();
				long milli = end - start;
				double sec = milli/1000.0;
				double min = sec / 60.0;
				double hor = min / 60.0;
				System.out.println("Time taken (mintes):" + min);
				System.out.println("Time taken (hours):" + hor);
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
				break;
			}
			else if (seedSelection == 17) {
				System.out.println("Choosing");
				long start = System.currentTimeMillis();
				HashSet<MultiProbabilityNode> seeds = MIIP.getSeeds(smg, seedSize, tarActivating, conActivating, seedsTarget);
				long end = System.currentTimeMillis();
				long milli = end - start;
				double sec = milli/1000.0;
				double min = sec / 60.0;
				double hor = min / 60.0;
				System.out.println("Time taken (mintes):" + min);
				System.out.println("Time taken (hours):" + hor);
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
				break;
			}
			else if (seedSelection == 18) {
				HashSet<MultiProbabilityNode> seeds;
				Concept c = null;
				if (concepts.size() > 3) {
					c = concepts.get(2);
				}
				if (tarActivating.getType().equals("lt")) {
					seeds =AntMPG.getSeedsLT(smg, seedSize, 0.01, tarActivating, conActivating, seedsTarget, c);
				}
				else {
					seeds =AntMPG.getSeeds(smg, seedSize, 0.01, tarActivating, conActivating, seedsTarget, c);
				}
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
				break;
			}
			else if (seedSelection == 19){
				HashSet<MultiProbabilityNode> seeds;
				if (tarActivating.getType().equals("lt")) {
					seeds = ExtMPG.getSeedsLT(smg, seedSize, 0.01, tarActivating, conActivating, seedsTarget, concepts.get(2));
				}
				else {
					seeds = testMPG.getSeeds(smg, seedSize, 0.01, tarActivating, conActivating, seedsTarget, concepts.get(2));
				}
				for (MultiProbabilityNode n : seeds) {
					seedsToGet.add(n);
				}
				break;
			}
		}
	}
	
	public static HashSet<MultiProbabilityNode> seedReader(int graphSize, String graphType, String graphChar, int run, int heuristic, int seedSize, HashMap<Integer, MultiProbabilityNode> nodes) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		
		if (graphType.equals("SW")) {
			File file = new File("hRes" + graphSize + "N/"+ graphSize + "NodeSmallWorldGraphExpo" + ((Double.parseDouble(graphChar))*100) + "Num" 
					+ run + "seeds" + seedSize + "h" + heuristic + ".txt");
			
			BufferedReader br = null;
			try {
				FileReader fstream = new FileReader(file.getAbsoluteFile());
				br = new BufferedReader(fstream);
				String line;
				while ((line = br.readLine()) != null) {
					int currNode = Integer.parseInt(line);
					seeds.add(nodes.get(currNode));
				}
				
				br.close();
				fstream.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		else if (graphType.equals("SF")){
			File file = new File("hRes" + graphSize + "N/" + 10 + "Start" + graphChar + "EdgesAdded" + graphSize + 
					"NodeScaleFreeGraph" + "Num" + run + "seeds" + seedSize + "h" + heuristic + ".txt");
			
			BufferedReader br = null;
			try {
				FileReader fstream = new FileReader(file.getAbsoluteFile());
				br = new BufferedReader(fstream);
				String line;
				while ((line = br.readLine()) != null) {
					int currNode = Integer.parseInt(line);
					seeds.add(nodes.get(currNode));
				}
				
				br.close();
				fstream.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		else {
			File file = new File("hResStan/"+ graphType + "seeds" + seedSize + "h" + heuristic + ".txt");
			
			BufferedReader br = null;
			try {
				FileReader fstream = new FileReader(file.getAbsoluteFile());
				br = new BufferedReader(fstream);
				String line;
				while ((line = br.readLine()) != null) {
					int currNode = Integer.parseInt(line);
					seeds.add(nodes.get(currNode));
				}
				
				br.close();
				fstream.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return seeds;
	}
}
