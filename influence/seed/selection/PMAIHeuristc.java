package influence.seed.selection;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class PMAIHeuristc {

	public static HashSet<MultiProbabilityNode> getSeedsBurnIn
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, double thresh, HashSet<MultiProbabilityNode> keyCon) {
		
		ArrayList<MultiProbabilityNode> seeds = new ArrayList<MultiProbabilityNode>();
		
		//HashMap<MultiProbabilityNode, Set<MultiProbabilityNode>> PMIIA = new HashMap<MultiProbabilityNode, Set<MultiProbabilityNode>>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		HashMap<MultiProbabilityNode, Double> alpha = new HashMap<MultiProbabilityNode, Double> ();
		HashMap<MultiProbabilityNode, Double> apValue = new HashMap<MultiProbabilityNode, Double> ();
		HashMap<MultiProbabilityNode, MultiProbabilityNode> currPMIIA = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		TIntHashSet IS = new TIntHashSet();
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(0.0);
			nodes.put(m.getId(), m);
			nodesToSort.add(m);
		}
		
		int nodeCount = 0;
		for (MultiProbabilityNode m : g.getVertices()) {
			System.out.println("Calculating PMIIA");
			currPMIIA = calcPMIIASpeed(m, thresh, seeds, g, IS, nodes);
		//	PMIIA.put(m, currPMIIA.keySet());
			System.out.println("Size: " + currPMIIA.keySet().size());
			for (MultiProbabilityNode u : currPMIIA.keySet()) {
				//System.out.println(u.getId());
				apValue.put(u, 0.0);
				alpha.put(u, -100.0);
			}
			
		//	System.out.println("Calculating Alpha for node " + m.getId());
			HashMap<MultiProbabilityNode, Double> totalDone = new HashMap<MultiProbabilityNode, Double>();
			for (MultiProbabilityNode u : currPMIIA.keySet()) {
				//System.out.println("-----Alpha start-----");
				calcAlpha(m, u, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, totalDone);
				//System.out.println("-----Alpha end-------");
				double infAl = alpha.get(u) * (1 - apValue.get(u));
				u.setValue(u.getValue() + infAl);
			}
			System.out.println("Nodes done: " + (nodeCount+1));
			nodeCount++;
			
			totalDone.clear();
			alpha.clear();
			apValue.clear();
			currPMIIA.clear();
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
	    sortedN.addAll(g.getVertices());
		
	    for (MultiProbabilityNode m : keyCon) {
	    	seeds.add(m);
	    }
	    
	    sortedN.removeAll(seeds);
		while (seeds.size() < seedCount * 2){
		//	System.out.println("Seed count: " + seeds.size());
			TIntHashSet PMIOA = new TIntHashSet();
			//HashSet<MultiProbabilityNode> currentPMIIA = new HashSet<MultiProbabilityNode>();
			MultiProbabilityNode u = sortedN.last();
			
			PMIOA = calcPMIOA(u, thresh, seeds, g, nodes);
		//	System.out.println("PMIOA Done");
		//	System.out.println(PMIOA.size());
			TIntIterator pmioaInt = PMIOA.iterator();
			//System.out.println("PMIIA Starting");
			while (pmioaInt.hasNext()){
				MultiProbabilityNode v = nodes.get(pmioaInt.next());
				currPMIIA = calcPMIIASpeed(v, thresh, seeds, g, IS, nodes);
			//	System.out.println("PMIIA done");
			//	System.out.println("Size: " + currPMIIA.keySet().size());
				for (MultiProbabilityNode vv :currPMIIA.keySet()) {
					//System.out.println(u.getId());
					alpha.put(vv, -100.0);
				}
				
				//HashMap<MultiProbabilityNode, Boolean> APcalcd = new HashMap<MultiProbabilityNode, Boolean>();
				HashMap<MultiProbabilityNode, Double> totalDone = new HashMap<MultiProbabilityNode, Double>();
				for (MultiProbabilityNode w : currPMIIA.keySet()){
					if (seeds.contains(w)) {
						continue;
					}
					calcAPValLoop(w, seeds, g, currPMIIA.keySet(), apValue);
					System.out.println("AP done");
					calcAlpha(v, w, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, totalDone);
					System.out.println("Alpha done");
					double infAl = alpha.get(w) * (1 - apValue.get(w));
					w.setValue(w.getValue() - infAl);
				}
			//	APcalcd.clear();
				totalDone.clear();
				alpha.clear();
				apValue.clear();
				currPMIIA.clear();
			}
			//System.out.println("PMIIA fully done");
			seeds.add(u);
			pmioaInt = PMIOA.iterator();
			while (pmioaInt.hasNext()){
				MultiProbabilityNode v = nodes.get(pmioaInt.next());
				if (v.equals(u)) {
					continue;
				}
				currPMIIA = calcPMIIASpeed(v, thresh, seeds, g, IS, nodes);
			//	System.out.println("Size: " + currPMIIA.keySet().size());
				//PMIIA.put(v, currPMIIA.keySet());
				for (MultiProbabilityNode vv : currPMIIA.keySet()) {
					//System.out.println(u.getId());
					alpha.put(vv, -100.0);
				}
				
				//HashMap<MultiProbabilityNode, Boolean> APcalcd = new HashMap<MultiProbabilityNode, Boolean>();
				HashMap<MultiProbabilityNode, Double> totalDone = new HashMap<MultiProbabilityNode, Double>();
				for (MultiProbabilityNode w : currPMIIA.keySet()){
					calcAPValLoop(w, seeds, g, currPMIIA.keySet(), apValue);
					calcAlpha(v, w, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, totalDone);
					double infAl = alpha.get(w) * (1 - apValue.get(w));
					w.setValue(w.getValue() + infAl);
				}
				
				totalDone.clear();
				alpha.clear();
				apValue.clear();
				currPMIIA.clear();
			}
			System.out.println(seeds.size());
			sortedN.clear();
			sortedN.addAll(nodesToSort);
			sortedN.removeAll(seeds);
			
		}
		
		HashSet<MultiProbabilityNode> fullSeeds = new HashSet<MultiProbabilityNode>();
		fullSeeds.addAll(seeds);
		return fullSeeds;
		
	}
	
	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, double thresh) {
		
		ArrayList<MultiProbabilityNode> seeds = new ArrayList<MultiProbabilityNode>();
		
		//HashMap<MultiProbabilityNode, Set<MultiProbabilityNode>> PMIIA = new HashMap<MultiProbabilityNode, Set<MultiProbabilityNode>>();
		
		HashMap<MultiProbabilityNode, Double> alpha = new HashMap<MultiProbabilityNode, Double> ();
		HashMap<MultiProbabilityNode, Double> apValue = new HashMap<MultiProbabilityNode, Double> ();
		HashMap<MultiProbabilityNode, MultiProbabilityNode> currPMIIA = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		TIntHashSet IS = new TIntHashSet();
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(0.0);
			nodes.put(m.getId(), m);
		}
		
		int nodeCount = 0;
		for (MultiProbabilityNode m : g.getVertices()) {
	//		System.out.println("Calculating PMIIA for node " + m.getId());
			currPMIIA = calcPMIIASpeed(m, thresh, seeds, g, IS, nodes);
		//	PMIIA.put(m, currPMIIA.keySet());
	//		System.out.println("Size: " + currPMIIA.keySet().size());
			for (MultiProbabilityNode u : currPMIIA.keySet()) {
				//System.out.println(u.getId());
				apValue.put(u, 0.0);
				alpha.put(u, -100.0);
			}
			
		//	System.out.println("Calculating Alpha for node " + m.getId());
			
			HashMap<MultiProbabilityNode, Double> totalDone = new HashMap<MultiProbabilityNode, Double>();

			for (MultiProbabilityNode u : currPMIIA.keySet()) {
				//System.out.println("-----Alpha start-----");
				calcAlpha(m, u, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, totalDone);
				//System.out.println("-----Alpha end-------");
				double infAl = alpha.get(u) * (1 - apValue.get(u));
				u.setValue(u.getValue() + infAl);
			}
			System.out.println("Nodes done: " + (nodeCount+1));
			//if (nodeCount > 100) {break;}
			nodeCount++;
			alpha.clear();
			apValue.clear();
			currPMIIA.clear();
			totalDone.clear();
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
	    sortedN.addAll(g.getVertices());
		
		while (seeds.size() < seedCount){
			System.out.println("Seed count: " + seeds.size());
			TIntHashSet PMIOA = new TIntHashSet();
	//		System.out.println("PMIOA Done");
			//HashSet<MultiProbabilityNode> currentPMIIA = new HashSet<MultiProbabilityNode>();
			MultiProbabilityNode u = sortedN.last();
			
			PMIOA = calcPMIOA(u, thresh, seeds, g, nodes);
	//		System.out.println("PMIOA Done");
			System.out.println(PMIOA.size());
			TIntIterator pmioaInt = PMIOA.iterator();
		//	System.out.println("PMIIA Starting");
			int d = 0;
			while (pmioaInt.hasNext()){
				d++;
				MultiProbabilityNode v = nodes.get(pmioaInt.next());
				currPMIIA = calcPMIIASpeed(v, thresh, seeds, g, IS, nodes);
				//System.out.println("PMIIA done: " + d + " out of " + PMIOA.size() + ". Seeds: " + seeds.size());
				for (MultiProbabilityNode vv : currPMIIA.keySet()) {
					//System.out.println(u.getId());
					alpha.put(vv, -100.0);
				}
				
		//		HashMap<MultiProbabilityNode, Boolean> APcalcd = new HashMap<MultiProbabilityNode, Boolean>();
				int h = 0;
				HashMap<MultiProbabilityNode, Double> totalDone = new HashMap<MultiProbabilityNode, Double>();
				for (MultiProbabilityNode w : currPMIIA.keySet()){
					
					if (seeds.contains(w)) {
						continue;
					}
			//		System.out.println("AP Begin:");
					calcAPValLoop(w, seeds, g, currPMIIA.keySet(), apValue);
			//		System.out.println("Alpha Begin:");
					calcAlpha(v, w, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, totalDone);

					double infAl = alpha.get(w) * (1 - apValue.get(w));
					w.setValue(w.getValue() - infAl);
				}

				alpha.clear();
				totalDone.clear();
				apValue.clear();
				currPMIIA.clear();
			}
		//	System.out.println("PMIIA fully done");
			
			seeds.add(u);
		//	System.out.println(u.getId());
			pmioaInt = PMIOA.iterator();
			while (pmioaInt.hasNext()){
				MultiProbabilityNode v = nodes.get(pmioaInt.next());
				if (v.equals(u)) {
					continue;
				}
				currPMIIA = calcPMIIASpeed(v, thresh, seeds, g, IS, nodes);
				//PMIIA.put(v, currPMIIA.keySet());
				for (MultiProbabilityNode vv : currPMIIA.keySet()) {
					//System.out.println(u.getId());
					alpha.put(vv, -100.0);
				}
				
	//			HashMap<MultiProbabilityNode, Boolean> APcalcd = new HashMap<MultiProbabilityNode, Boolean>();
				HashMap<MultiProbabilityNode, Double> totalDone = new HashMap<MultiProbabilityNode, Double>();
				for (MultiProbabilityNode w : currPMIIA.keySet()){
					if (seeds.contains(w)) {
						continue;
					}
					calcAPValLoop(w, seeds, g, currPMIIA.keySet(), apValue);
			//		System.out.println("2nd ALpha");
					calcAlpha(v, w, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, totalDone);
					double infAl = alpha.get(w) * (1 - apValue.get(w));
					w.setValue(w.getValue() + infAl);
				}
				
				alpha.clear();
				apValue.clear();
				currPMIIA.clear();
				totalDone.clear();
			}
			
			sortedN.clear();
			sortedN.addAll(g.getVertices());
			sortedN.removeAll(seeds);
			
		}
		
		HashSet<MultiProbabilityNode> fullSeeds = new HashSet<MultiProbabilityNode>();
		fullSeeds.addAll(seeds);
		return fullSeeds;
		
	}

	private static double calcAPVal(MultiProbabilityNode u, ArrayList<MultiProbabilityNode> seeds,
			Graph<MultiProbabilityNode, TypedWeightedEdge> g, Set<MultiProbabilityNode> nodesInPMIIA, HashMap<MultiProbabilityNode, Double> apValue) {
		
		//System.out.println(apCalcd.keySet().size());
		HashSet<TypedWeightedEdge> inNeighboursInPMIIA = new HashSet<TypedWeightedEdge>();
		if (seeds.contains(u)) {
			return 1;
		}
		if (apValue.containsKey(u)) {
			return apValue.get(u);
		}
		
		else {			
			int neighCount = 0;
			for (TypedWeightedEdge incomingEdge : g.getInEdges(u)) {
				MultiProbabilityNode incomingNeighbour = g.getSource(incomingEdge);
				if (nodesInPMIIA.contains(incomingNeighbour) && !incomingNeighbour.equals(u)) {
					inNeighboursInPMIIA.add(incomingEdge);
					neighCount++;
				}
			}
			
			if (neighCount == 0) {
				//apCalcd.put(u, true);
				return 0;
			}
			
			double total = 1;
			
			for (TypedWeightedEdge edge : inNeighboursInPMIIA) {
				MultiProbabilityNode s = g.getSource(edge);
				double APV = 0;
				if (apValue.containsKey(s)){
					APV = apValue.get(s);
				}
				else {
				//	System.out.println(s.getId());
				//	System.out.println(u.getId());
					HashSet<MultiProbabilityNode> remainingNodes = new HashSet<MultiProbabilityNode>();
					remainingNodes.addAll(nodesInPMIIA);
					remainingNodes.remove(u);
					APV = calcAPVal(s,seeds,g, remainingNodes, apValue);
					apValue.put(s, APV);
					//apCalcd.put(s, true);
				}
				total = total * (1 - (APV * edge.getWeight()));
			}
			
			return (1 - total);
			
		}
	}
	
	private static void calcAPValLoop2(MultiProbabilityNode u, ArrayList<MultiProbabilityNode> seeds,
			Graph<MultiProbabilityNode, TypedWeightedEdge> g, Set<MultiProbabilityNode> nodesInPMIIA, HashMap<MultiProbabilityNode, Double> apValue) {
		
		//System.out.println(apCalcd.keySet().size());
		HashSet<TypedWeightedEdge> inNeighboursInPMIIA = new HashSet<TypedWeightedEdge>();
		HashMap<Integer, HashSet<MultiProbabilityNode>> generations = new HashMap<Integer, HashSet<MultiProbabilityNode>>();
		int genCount = 0;
		
		if (seeds.contains(u)) {
			apValue.put(u, 1.0);
		}
		else if (!apValue.containsKey(u)) {
			
			int neighCount = 0;
			HashSet<MultiProbabilityNode> gen = new HashSet<MultiProbabilityNode>();
			for (TypedWeightedEdge incomingEdge : g.getInEdges(u)) {
				MultiProbabilityNode incomingNeighbour = g.getSource(incomingEdge);
				if (nodesInPMIIA.contains(incomingNeighbour) && !incomingNeighbour.equals(u)) {
					gen.add(incomingNeighbour);
					
					inNeighboursInPMIIA.add(incomingEdge);
					neighCount++;
				}
			}
			
			if (neighCount == 0) {
				//apCalcd.put(u, true);
				apValue.put(u, 0.0);
			}
			else {
				
				generations.put(genCount, new HashSet<MultiProbabilityNode>());
				generations.get(genCount).add(u);
				genCount++;
				generations.put(genCount, gen);
				genCount++;
				
				HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
				HashSet<MultiProbabilityNode> currGen = new HashSet<MultiProbabilityNode>();
				seenBefore.addAll(gen);
				currGen.addAll(gen);
				HashSet<MultiProbabilityNode> nextGen = new HashSet<MultiProbabilityNode>();
				while (!currGen.isEmpty()) {
					for (MultiProbabilityNode m : currGen) {
						for (MultiProbabilityNode source : g.getPredecessors(m)) {
							if (nodesInPMIIA.contains(source) && !seenBefore.contains(source)){
								nextGen.add(source);
							}
							if (seeds.contains(source)) {
								apValue.put(source, 1.0);
								//System.out.println(source.getId());
							}
						}
					}
					seenBefore.addAll(nextGen);
					HashSet<MultiProbabilityNode> now = new HashSet<MultiProbabilityNode>();
					now.addAll(currGen);
					generations.put(genCount, now);
					genCount++;
					currGen.clear();
					currGen.addAll(nextGen);
					nextGen.clear();
				}
				
				//EACH GEN CAN WORK BACKWARDS. NEED SEEDS.
				int genMax = genCount - 1;
				while (genCount > 0) {
					genCount--;
					HashSet<MultiProbabilityNode> currentSet = generations.get(genCount);
					double total = 1;
					for (MultiProbabilityNode m : currentSet) {
						if (!apValue.containsKey(m)) {
							for (MultiProbabilityNode n : g.getPredecessors(m)) {
								if (seeds.contains(n)){
									TypedWeightedEdge edge = g.findEdge(n, m);
								//	System.out.println(n.getId());
								//	System.out.println(edge.getId());
									total = total * (1 - (apValue.get(n) * edge.getWeight()));
								}
								else if (genCount != genMax) {
									if (generations.get(genCount+1).contains(n)) {
										TypedWeightedEdge edge = g.findEdge(n, m);
										total = total * (1 - (apValue.get(n) * edge.getWeight()));
									}
								}
							}
							apValue.put(m, 1 - total);
						}
					}
				}
				//return (1 - total);
			}
		}
	}
	
	private static void calcAPValLoop(MultiProbabilityNode u, ArrayList<MultiProbabilityNode> seeds,
			Graph<MultiProbabilityNode, TypedWeightedEdge> g, Set<MultiProbabilityNode> nodesInPMIIA, HashMap<MultiProbabilityNode, Double> apValue) {
		
		//System.out.println(apCalcd.keySet().size());
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> inNeighboursInPMIIA = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
		HashMap<Integer, HashSet<MultiProbabilityNode>> generations = new HashMap<Integer, HashSet<MultiProbabilityNode>>();
		int genCount = 0;
		
		if (seeds.contains(u)) {
			apValue.put(u, 1.0);
		}
		else if (!apValue.containsKey(u)) {
			
			int neighCount = 0;
			HashSet<MultiProbabilityNode> gen = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> explore = new HashSet<MultiProbabilityNode>();
			inNeighboursInPMIIA.put(u, new HashSet<MultiProbabilityNode>());
			for (TypedWeightedEdge incomingEdge : g.getInEdges(u)) {
				MultiProbabilityNode incomingNeighbour = g.getSource(incomingEdge);
				if (nodesInPMIIA.contains(incomingNeighbour) && !incomingNeighbour.equals(u)) {
					
					if (!apValue.containsKey(incomingNeighbour))
					{explore.add(incomingNeighbour);}
					gen.add(incomingNeighbour);
					inNeighboursInPMIIA.get(u).add(incomingNeighbour);
					//inNeighboursInPMIIA.add(incomingEdge);
					neighCount++;
				}
			}
			
			if (neighCount == 0) {
				//apCalcd.put(u, true);
				apValue.put(u, 0.0);
			}
			else {
				
				generations.put(genCount, new HashSet<MultiProbabilityNode>());
				generations.get(genCount).add(u);
				genCount++;
				generations.put(genCount, gen);
				genCount++;
				
				HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
				HashSet<MultiProbabilityNode> currGen = new HashSet<MultiProbabilityNode>();
				seenBefore.addAll(gen);
				currGen.addAll(explore);
				HashSet<MultiProbabilityNode> nextGen = new HashSet<MultiProbabilityNode>();
				while (!currGen.isEmpty()) {
					for (MultiProbabilityNode m : currGen) {
						HashSet<MultiProbabilityNode> neighNeigh = new HashSet<MultiProbabilityNode>();
						for (MultiProbabilityNode source : g.getPredecessors(m)) {
							if (nodesInPMIIA.contains(source) && !seenBefore.contains(source)){
								nextGen.add(source);
								neighNeigh.add(source);
								if (!apValue.containsKey(source)) {
									explore.add(source);
								}
							}
							if (seeds.contains(source)) {
								apValue.put(source, 1.0);
								neighNeigh.add(source);
								//System.out.println(source.getId());
							}
						}
						
						inNeighboursInPMIIA.put(m, neighNeigh);
					}
					seenBefore.addAll(nextGen);
					HashSet<MultiProbabilityNode> now = new HashSet<MultiProbabilityNode>();
					now.addAll(currGen);
					generations.put(genCount, now);
					genCount++;
					currGen.clear();
					currGen.addAll(explore);
					explore.clear();
					nextGen.clear();
				}
				
				//EACH GEN CAN WORK BACKWARDS. NEED SEEDS.
				int genMax = genCount - 1;
				while (genCount > 0) {
					genCount--;
				//	System.out.println(genCount + " : " + generations.get(genCount).size());
					HashSet<MultiProbabilityNode> currentSet = generations.get(genCount);
					double total = 1;
					for (MultiProbabilityNode m : currentSet) {
						if (!apValue.containsKey(m)) {
							for (MultiProbabilityNode n : inNeighboursInPMIIA.get(m)) {
								TypedWeightedEdge edge = g.findEdge(n, m);
								total = total * (1 - (apValue.get(n) * edge.getWeight()));
							}
							apValue.put(m, 1 - total);
						}
					}
				}
				//return (1 - total);
			}
		}
	}
	
	private static TIntHashSet calcPMIOA(MultiProbabilityNode m, double thresh, ArrayList<MultiProbabilityNode> seeds,
			Graph<MultiProbabilityNode, TypedWeightedEdge> g, HashMap<Integer, MultiProbabilityNode> nodes) {
		
		Graph<MultiProbabilityNode, TypedWeightedEdge> currG = g;
		for (MultiProbabilityNode STR : seeds) {
			currG.removeVertex(STR);
		}
		TIntHashSet PMIOA = new TIntHashSet();
		HashMap<MultiProbabilityNode, MultiProbabilityNode> PMIIA = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		HashMap<MultiProbabilityNode, Double> pathCost = new HashMap<MultiProbabilityNode, Double>();
		
		HashSet<MultiProbabilityNode> currentConsider = new HashSet<MultiProbabilityNode>();
	//	System.out.println(thresh);
		for(TypedWeightedEdge outgoingEdge : currG.getOutEdges(m)) {
			
			MultiProbabilityNode s = currG.getDest(outgoingEdge);
			//System.out.println(outgoingEdge.getWeight());
			//System.out.println(s.getId());
			if (!seeds.contains(s)) {
				if (outgoingEdge.getWeight() > thresh) {
					PMIIA.put(s, m);
					pathCost.put(s, outgoingEdge.getWeight());
					//System.out.println(s.getId() + "adding");
					currentConsider.add(s);
				}
			}
		}
		
		HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
		seenBefore.addAll(currentConsider);
		HashSet<MultiProbabilityNode> nextToConsider = new HashSet<MultiProbabilityNode>();
		while(!currentConsider.isEmpty()) {
		//	System.out.println(currentConsider.size());
			for (MultiProbabilityNode curr : currentConsider) {
				for(TypedWeightedEdge outgoingEdge : currG.getOutEdges(curr)) {
					MultiProbabilityNode s = currG.getOpposite(curr, outgoingEdge);
					if (!seeds.contains(s)) {
						double sCost = outgoingEdge.getWeight() * pathCost.get(curr);
						if (sCost > thresh){
							if (PMIIA.containsKey(s)){
								if (pathCost.get(s) < sCost) {
									PMIIA.put(s, curr);
									pathCost.put(s, sCost);
									nextToConsider.add(s);
								}
							}
							else {
								PMIIA.put(s, curr);
								pathCost.put(s, sCost);
								nextToConsider.add(s);
							}
						}
					}
				}
			}
			seenBefore.addAll(nextToConsider);
			currentConsider.clear();
			currentConsider.addAll(nextToConsider);
			nextToConsider.clear();
		}
		
		for (MultiProbabilityNode m1 : PMIIA.keySet()) {
			PMIOA.add(m1.getId());
		}
		return PMIOA;
	}

	private static void calcAlpha(MultiProbabilityNode v, MultiProbabilityNode u, ArrayList<MultiProbabilityNode> seeds, 
			HashMap<MultiProbabilityNode, MultiProbabilityNode> PMIIA, Set<MultiProbabilityNode> nodesInPMIIA, Graph<MultiProbabilityNode, TypedWeightedEdge> g,
			HashMap<MultiProbabilityNode, Double> apValue, HashMap<MultiProbabilityNode, Double> alpha, HashMap<MultiProbabilityNode, Double> totalDone) {
		
		
		//System.out.println(v.getId());
		if (v.equals(u)) {
			alpha.put(u, 1.0);
		}
		else if (alpha.get(u) == -100.0) {
			boolean next = true;
			MultiProbabilityNode nextNode = PMIIA.get(u);
			ArrayList<MultiProbabilityNode> lists = new ArrayList<MultiProbabilityNode>();
			lists.add(u);
			while (next) {

				if (v.equals(nextNode)) {
					next = false;
					alpha.put(nextNode, 1.0);
				}
				else if (alpha.get(nextNode) != -100.0) {
					next = false;
				}
				else {
					lists.add(nextNode);
					nextNode = PMIIA.get(nextNode);
				}
				
				
			}

			for (int i = lists.size() - 1; i >= 0; i--) {
				MultiProbabilityNode mm = lists.get(i);
				double total = 1;

				nextNode = PMIIA.get(mm);
				if (seeds.contains(nextNode)) {
					alpha.put(mm, 0.0);
				}
				else {
					if (!totalDone.containsKey(nextNode)) {
						for (TypedWeightedEdge incomingEdge : g.getInEdges(nextNode)) {
							MultiProbabilityNode s = g.getSource(incomingEdge);
							if (nodesInPMIIA.contains(s)) {
								total = total * (1 - (apValue.get(s) * incomingEdge.getWeight()));
							}
						}
						totalDone.put(nextNode, total);
					}
					
					TypedWeightedEdge edge = g.findEdge(mm, nextNode);
					double diver = 1 - (apValue.get(mm) * edge.getWeight());
					double newTot = totalDone.get(nextNode) / diver;
			//		System.out.println(newTot);
					alpha.put(mm, alpha.get(nextNode) * edge.getWeight() * newTot);
				}
			}
		}
	}


	private static HashMap<MultiProbabilityNode, MultiProbabilityNode> calcPMIIASpeed(MultiProbabilityNode m, double thresh, ArrayList<MultiProbabilityNode> seeds, 
		Graph<MultiProbabilityNode, TypedWeightedEdge> g, TIntHashSet IS, HashMap<Integer, MultiProbabilityNode> nodes) {
		
	HashSet<MultiProbabilityNode> seedsToRemove = new HashSet<MultiProbabilityNode>();
	
	//MAITransformer MAITrans = new MAITransformer();
	
	HashMap<MultiProbabilityNode, MultiProbabilityNode> PMIIA = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
	HashMap<MultiProbabilityNode, Double> pathCost = new HashMap<MultiProbabilityNode, Double>();
	HashMap<MultiProbabilityNode, Double> seedCost = new HashMap<MultiProbabilityNode, Double>();
	HashSet<MultiProbabilityNode> currentConsider = new HashSet<MultiProbabilityNode>();
	HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
	HashMap<MultiProbabilityNode, MultiProbabilityNode> seedPath = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
	List<TypedWeightedEdge> path = null;
	//System.out.println("Copy g");
	Graph<MultiProbabilityNode, TypedWeightedEdge> currG = g;
	//System.out.println("Copy g done");
	HashSet<MultiProbabilityNode> seedsInRange = new HashSet<MultiProbabilityNode>();
	
	for(TypedWeightedEdge incomingEdge : currG.getInEdges(m)) {
		MultiProbabilityNode s = currG.getSource(incomingEdge);
		if (incomingEdge.getWeight() > thresh) {
			if (seeds.contains(s)) {seedsInRange.add(s);}
			pathCost.put(s, incomingEdge.getWeight());
			currentConsider.add(s);
		}
	}
	
	HashSet<MultiProbabilityNode> nextToConsider = new HashSet<MultiProbabilityNode>();
	while(!currentConsider.isEmpty()) {
		for (MultiProbabilityNode curr : currentConsider){
			for(TypedWeightedEdge incomingEdge : currG.getInEdges(curr)) {
				MultiProbabilityNode s = currG.getSource(incomingEdge);
				double sCost = incomingEdge.getWeight() * pathCost.get(curr);
				if (sCost > thresh){
					pathCost.put(s, sCost);
					if (seeds.contains(s)) {seedsInRange.add(s);}
					nextToConsider.add(s);
				}
			}
		}
		currentConsider.clear();
		currentConsider.addAll(nextToConsider);
		nextToConsider.clear();
	}
	
	
	pathCost = new HashMap<MultiProbabilityNode, Double>();
	
	for (int i = 0; i < seeds.size(); i++) {
		MultiProbabilityNode currSeed = seeds.get(i);
		int currSeedID = currSeed.getId();
		seedCost = new HashMap<MultiProbabilityNode, Double>();
		currentConsider = new HashSet<MultiProbabilityNode>();
		nextConsider = new HashSet<MultiProbabilityNode>();
		
		currentConsider.add(m);
		seedCost.put(m, 1.0);
		if (!IS.contains(currSeedID) & seedsInRange.contains(currSeed)){
			
			while (!currentConsider.isEmpty()) {
				for (MultiProbabilityNode currentNode : currentConsider) {
					for(TypedWeightedEdge incomingEdge : currG.getInEdges(currentNode)) {
						MultiProbabilityNode s = currG.getSource(incomingEdge);
						Double currentPathCost = seedCost.get(currentNode) * incomingEdge.getWeight();
						if (currentPathCost > thresh) {
							if (seedCost.get(s) != null) {
								if (seedCost.get(s) < currentPathCost) {
									seedCost.put(s, currentPathCost);
									nextConsider.add(s);
									seedPath.put(s, currentNode);
								}
							}
							else {
								seedCost.put(s, incomingEdge.getWeight());
								nextConsider.add(s);
								seedPath.put(s, currentNode);
							}
							
						}
					}
				}
				currentConsider.clear();
				currentConsider.addAll(nextConsider);
				nextConsider.clear();
			}
			
			double dist = 0;
			
			if (seedCost.get(currSeed) != null) {
				dist = seedCost.get(currSeed);
				MultiProbabilityNode currentNode = currSeed;
				path = new ArrayList<TypedWeightedEdge>();
				while (currentNode.getId() != m.getId()){
					MultiProbabilityNode next = seedPath.get(currentNode);
					path.add(currG.findEdge(currentNode, next));
					currentNode = next;
				}
			}
			
			if (dist > thresh) {
				for (int j = 0; j < path.size(); j++) {
					
					//System.out.println(path.get(j).getId());
					MultiProbabilityNode test = currG.getSource(path.get(j));
					if (seeds.contains(test) && !seedsToRemove.contains(test)) {
						IS.add(currSeedID);
						break;
					}
				}
				if (!IS.contains(currSeedID)) {
					if (PMIIA.containsKey(currSeed)){
						if (pathCost.get(currSeed) < dist) {
							PMIIA.put(currSeed, currG.getDest(path.get(path.size()-1)));
							pathCost.put(currSeed, dist);
						} 
					}
					else {
						PMIIA.put(currSeed, currG.getDest(path.get(path.size()-1)));
						pathCost.put(currSeed, dist);
					}
				}
			}
			
		}
		seedsToRemove.add(currSeed);
		//currG.removeVertex(currSeed);
	}
	
	currentConsider = new HashSet<MultiProbabilityNode>();
	
	//System.out.println("local area");
	for(TypedWeightedEdge incomingEdge : currG.getInEdges(m)) {
		MultiProbabilityNode s = currG.getSource(incomingEdge);
		if (!seedsToRemove.contains(s)) {
			if (incomingEdge.getWeight() > thresh) {
				PMIIA.put(s, m);
				pathCost.put(s, incomingEdge.getWeight());
				currentConsider.add(s);
			}
		}
	}
	
	nextToConsider = new HashSet<MultiProbabilityNode>();
	while(!currentConsider.isEmpty()) {
		for (MultiProbabilityNode curr : currentConsider){
			for(TypedWeightedEdge incomingEdge : currG.getInEdges(curr)) {
				MultiProbabilityNode s = currG.getSource(incomingEdge);
				if (!seedsToRemove.contains(s)) {
					double sCost = incomingEdge.getWeight() * pathCost.get(curr);
					if (sCost > thresh) {
						if (PMIIA.containsKey(s)){
							if (pathCost.get(s) < sCost) {
								PMIIA.put(s, curr);
								pathCost.put(s, sCost);
								nextToConsider.add(s);
							}
						}
						else {
							PMIIA.put(s, curr);
							pathCost.put(s, sCost);
							nextToConsider.add(s);
						}
					}
				}
			}
		}
		currentConsider.clear();
		currentConsider.addAll(nextToConsider);
		nextToConsider.clear();
	}
	//System.out.println("local area done");
	
	return PMIIA;
}


}

class MAITransformer implements Transformer<TypedWeightedEdge, Number>{

	@Override
	public Number transform(TypedWeightedEdge arg0) {
		return Math.log10(arg0.getWeight()) * -1;
	}

}
