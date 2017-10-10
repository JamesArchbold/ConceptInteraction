package influence.seed.selection;

import influence.concepts.Concept;
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
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;

public class PMAIBoostHeuristic {
	
	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, double thresh, double interaction, ArrayList<MultiProbabilityNode> seeds, Concept keyCon, Concept boostCon) {
		
		ArrayList<MultiProbabilityNode> seedsBoosting = new ArrayList<MultiProbabilityNode>();
		
		//HashMap<MultiProbabilityNode, Set<MultiProbabilityNode>> PMIIA = new HashMap<MultiProbabilityNode, Set<MultiProbabilityNode>>();
		
		HashMap<MultiProbabilityNode, Double> alpha = new HashMap<MultiProbabilityNode, Double> ();
		HashMap<MultiProbabilityNode, Double> apValue = new HashMap<MultiProbabilityNode, Double> ();
		HashMap<MultiProbabilityNode, Double> apValueBoost = new HashMap<MultiProbabilityNode, Double> ();
		HashMap<MultiProbabilityNode, MultiProbabilityNode> currPMIIA = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		TIntHashSet IS = new TIntHashSet();
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(0.0);
			nodes.put(m.getId(), m);
		}
		
		int nodeCount = 0;
		for (MultiProbabilityNode m : g.getVertices()) {
			if (!seeds.contains(m)) {
				currPMIIA = calcPMIIASpeed(m, thresh, seeds, g, IS, nodes, keyCon);
				//PMIIA.put(m, currPMIIA.keySet());
				HashMap<MultiProbabilityNode, Boolean> APcalcd = new HashMap<MultiProbabilityNode, Boolean>();
				for (MultiProbabilityNode u : currPMIIA.keySet()) {
				//	System.out.println(u.getId());
					apValueBoost.put(u, 0.0);
					apValue.put(u, calcAPVal(u, seeds, g, currPMIIA.keySet(), APcalcd, apValue, keyCon));
					alpha.put(u, -100.0);
				}
				
			//	System.out.println("Calculating Alpha for node " + m.getId());
			//	System.out.println("Size of PMMIA:" + PMIIA.get(m).size());
				for (MultiProbabilityNode u : currPMIIA.keySet()) {
					//System.out.println("-----Alpha start-----");
					alpha.put(u, calcAlpha(m, u, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, keyCon) * interaction);
					//System.out.println("-----Alpha end-------");
					double infAl = apValue.get(u) * alpha.get(u) * (1 - apValueBoost.get(u));
					u.setValue(u.getValue() + infAl);
				}
				alpha.clear();
				apValue.clear();
				apValueBoost.clear();
				APcalcd.clear();
				currPMIIA.clear();
				IS.clear();
			}
			System.out.println("Nodes done: " + (nodeCount+1));
			nodeCount++;
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
	    sortedN.addAll(g.getVertices());
		
		while (seedsBoosting.size() < seedCount){
			System.out.println("Seed count: " + seedsBoosting.size());
			TIntHashSet PMIOA = new TIntHashSet();
			//HashSet<MultiProbabilityNode> currentPMIIA = new HashSet<MultiProbabilityNode>();
			MultiProbabilityNode u = sortedN.last();
			
			PMIOA = calcPMIOA(u, thresh, seeds, g, nodes, keyCon);
			TIntIterator pmioaInt = PMIOA.iterator();
			while (pmioaInt.hasNext()){
				MultiProbabilityNode v = nodes.get(pmioaInt.next());
				if (!seeds.contains(v)){
					currPMIIA = calcPMIIASpeed(v, thresh, seeds, g, IS, nodes, keyCon);
					for (MultiProbabilityNode vv : currPMIIA.keySet()) {
						//System.out.println(u.getId());
						alpha.put(vv, -100.0);
					}
					
					HashMap<MultiProbabilityNode, Boolean> APcalcd = new HashMap<MultiProbabilityNode, Boolean>();
					HashMap<MultiProbabilityNode, Boolean> APcalcdBoost = new HashMap<MultiProbabilityNode, Boolean>();
					for (MultiProbabilityNode w : currPMIIA.keySet()){
						if (seeds.contains(w)) {
							continue;
						}
						apValueBoost.put(w, calcAPVal(w, seedsBoosting, g, currPMIIA.keySet(), APcalcdBoost, apValueBoost, boostCon));
						apValue.put(w, calcAPVal(w, seeds, g, currPMIIA.keySet(), APcalcd, apValue, keyCon));
						alpha.put(w, calcAlpha(v, w, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, keyCon) * interaction);
						double infAl = apValue.get(w) * alpha.get(w) * (1 - apValueBoost.get(w));
						w.setValue(w.getValue() - infAl);
					}
					alpha.clear();
					apValue.clear();
					apValueBoost.clear();
					currPMIIA.clear();
					IS.clear();
					APcalcd.clear();
					APcalcdBoost.clear();
				}
			}
			
			seedsBoosting.add(u);
			for (TypedWeightedEdge t : g.getIncidentEdges(u)) {
				if (t.getConcept() == keyCon.getId()) {
					t.setWeight(t.getWeight() * (1+interaction));
					//System.out.println("Edge " + t.getId() + " now is:" +t.getWeight());
				}
				
			}
			
			pmioaInt = PMIOA.iterator();
			while (pmioaInt.hasNext()){
				MultiProbabilityNode v = nodes.get(pmioaInt.next());
				if (!seeds.contains(v)){
					if (v.equals(u)) {
						continue;
					}
					currPMIIA = calcPMIIASpeed(v, thresh, seeds, g, IS, nodes, keyCon);
					//PMIIA.put(v, currPMIIA.keySet());
					for (MultiProbabilityNode vv : currPMIIA.keySet()) {
						//System.out.println(u.getId());
						alpha.put(vv, -100.0);
					}
					
					HashMap<MultiProbabilityNode, Boolean> APcalcd = new HashMap<MultiProbabilityNode, Boolean>();
					HashMap<MultiProbabilityNode, Boolean> APcalcdBoost = new HashMap<MultiProbabilityNode, Boolean>();
					for (MultiProbabilityNode w : currPMIIA.keySet()){
						apValueBoost.put(w, calcAPVal(w, seedsBoosting, g, currPMIIA.keySet(), APcalcdBoost, apValueBoost, boostCon));
						apValue.put(w, calcAPVal(w, seeds, g, currPMIIA.keySet(), APcalcd, apValue, keyCon));
						alpha.put(w, calcAlpha(v, w, seeds, currPMIIA, currPMIIA.keySet(), g, apValue, alpha, keyCon) * interaction);
						double infAl = apValue.get(w) * alpha.get(w) * (1 - apValueBoost.get(w));
						w.setValue(w.getValue() + infAl);
					}
					
					alpha.clear();
					apValue.clear();
					apValueBoost.clear();
					currPMIIA.clear();
					IS.clear();
					APcalcd.clear();
					APcalcdBoost.clear();
				}
			}
			
			sortedN.clear();
			sortedN.addAll(g.getVertices());
			sortedN.removeAll(seedsBoosting);
			
		}
		
		HashSet<MultiProbabilityNode> fullSeeds = new HashSet<MultiProbabilityNode>();
		fullSeeds.addAll(seedsBoosting);
		return fullSeeds;
		
	}

	private static double calcAPVal(MultiProbabilityNode u, ArrayList<MultiProbabilityNode> seeds,
			Graph<MultiProbabilityNode, TypedWeightedEdge> g, Set<MultiProbabilityNode> nodesInPMIIA, 
			HashMap<MultiProbabilityNode, Boolean> apCalcd, HashMap<MultiProbabilityNode, Double> apValue, Concept con) {
		
		HashSet<TypedWeightedEdge> inNeighboursInPMIIA = new HashSet<TypedWeightedEdge>();
		if (seeds.contains(u)) {
			apCalcd.put(u, true);
		//	System.out.println("Setting " + u.getId() + " to true - line 169");
			apValue.put(u, 1.0);
			return 1;
		}
		if (!apCalcd.containsKey(u)) {
			apCalcd.put(u, false);
		//	System.out.println("Setting " + u.getId() + " to false - line 174");
		}
		if (apCalcd.get(u)){
		//	System.out.println("Retrieving " + u.getId() + " - line 177");
			return apValue.get(u);
		}
		else {
			
			int neighCount = 0;
			for (TypedWeightedEdge incomingEdge : g.getInEdges(u)) {
				if (incomingEdge.getConcept() == con.getId()) {
					MultiProbabilityNode incomingNeighbour = g.getSource(incomingEdge);
					if (nodesInPMIIA.contains(incomingNeighbour) && !incomingNeighbour.equals(u)) {
						inNeighboursInPMIIA.add(incomingEdge);
						neighCount++;
					}
				}
			}
			
			if (neighCount == 0) {
				apCalcd.put(u, true);
				apValue.put(u, 0.0);
				return 0;
			}
			
			double total = 1;
			for (TypedWeightedEdge edge : inNeighboursInPMIIA) {
				MultiProbabilityNode s = g.getSource(edge);
				if (!apCalcd.containsKey(s)) {
					apCalcd.put(s, false);
			//		System.out.println("Setting " + s.getId() + " to false - line 199");
				}
			}
			
			for (TypedWeightedEdge edge : inNeighboursInPMIIA) {
				MultiProbabilityNode s = g.getSource(edge);
				double APV = 0;
				if (apCalcd.get(s)){
			//		System.out.println("Retrieving " + s.getId() + " - line 207");
					APV = apValue.get(s);
				}
				else {
				//	System.out.println(s.getId());
				//	System.out.println(u.getId());
					HashSet<MultiProbabilityNode> remainingNodes = new HashSet<MultiProbabilityNode>();
					remainingNodes.addAll(nodesInPMIIA);
					remainingNodes.remove(u);
					APV = calcAPVal(s,seeds,g, remainingNodes, apCalcd, apValue, con);
					apValue.put(s, APV);
					//apCalcd.put(s, true);
				}
				total = total * (1 - (APV * edge.getWeight()));
			}
			
			apCalcd.put(u, true);
		//	System.out.println("Setting " + u.getId() + " to true - line 223");
			apValue.put(u, 1 - total);
			return (1 - total);
			
		}
	}

	private static TIntHashSet calcPMIOA(MultiProbabilityNode m, double thresh, ArrayList<MultiProbabilityNode> seeds,
			Graph<MultiProbabilityNode, TypedWeightedEdge> g, HashMap<Integer, MultiProbabilityNode> nodes, Concept keyCon) {
		
		Graph<MultiProbabilityNode, TypedWeightedEdge> currG = copyG(g, keyCon);
		for (MultiProbabilityNode STR : seeds) {
			if (!STR.equals(m)){
				currG.removeVertex(STR);
			}
		}
		TIntHashSet PMIOA = new TIntHashSet();
		HashMap<MultiProbabilityNode, MultiProbabilityNode> PMIIA = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		HashMap<MultiProbabilityNode, Double> pathCost = new HashMap<MultiProbabilityNode, Double>();
		
		TIntHashSet currentConsider = new TIntHashSet();
		
		for(TypedWeightedEdge outgoingEdge : currG.getOutEdges(m)) {
			MultiProbabilityNode s = currG.getDest(outgoingEdge);
			if (outgoingEdge.getWeight() > thresh) {
				PMIIA.put(s, m);
				pathCost.put(s, outgoingEdge.getWeight());
				currentConsider.add(s.getId());
			}
		}
		
		TIntHashSet seenBefore = new TIntHashSet();
		seenBefore.addAll(currentConsider);
		TIntHashSet nextToConsider = new TIntHashSet();
		while(!currentConsider.isEmpty()) {
			//System.out.println(currentConsider.size());
			TIntIterator currIT = currentConsider.iterator();
			while (currIT.hasNext()) {
				MultiProbabilityNode curr = nodes.get(currIT.next());
				for(TypedWeightedEdge outgoingEdge : currG.getOutEdges(curr)) {
					MultiProbabilityNode s = currG.getSource(outgoingEdge);
					double sCost = outgoingEdge.getWeight() * pathCost.get(curr);
					
					if (sCost > thresh) {
						if (PMIIA.containsKey(s)){
							if (pathCost.get(s) < sCost) {
								PMIIA.put(s, curr);
								pathCost.put(s, sCost);
								nextToConsider.add(s.getId());
							}
						}
						else {
							PMIIA.put(s, curr);
							pathCost.put(s, sCost);
							nextToConsider.add(s.getId());
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

	private static Double calcAlpha(MultiProbabilityNode v, MultiProbabilityNode u, ArrayList<MultiProbabilityNode> seeds, 
			HashMap<MultiProbabilityNode, MultiProbabilityNode> PMIIA, Set<MultiProbabilityNode> nodesInPMIIA, Graph<MultiProbabilityNode, TypedWeightedEdge> g,
			HashMap<MultiProbabilityNode, Double> apValue, HashMap<MultiProbabilityNode, Double> alpha, Concept keyCon) {
		
		
		//System.out.println(v.getId());
		if (v.equals(u)) {
			return 1.0;
		}
		else if (alpha.get(u) != -100.0) {
			return alpha.get(u);
		}
		else {
			MultiProbabilityNode nextNode = PMIIA.get(u);
			///System.out.println("Current " + u.getId());
			///System.out.println("Next " + nextNode.getId());
			if (seeds.contains(nextNode)){
				return 0.0;
			}
			else{
				double total = 1;
				for (TypedWeightedEdge incomingEdge : g.getInEdges(nextNode)) {
					if (incomingEdge.getConcept() == keyCon.getId()){
						MultiProbabilityNode s = g.getSource(incomingEdge);
						if (nodesInPMIIA.contains(s) && !u.equals(s)) {
							total = total * (1 - (apValue.get(s) * incomingEdge.getWeight()));
						}
					}
				}
				
				double alp;
				if (nextNode.equals(v)) {
					alp = 1;
				}
				else {
					alp = alpha.get(nextNode);
				} 
				
				if (alp == -100.0) {
					alp = calcAlpha(v, nextNode, seeds, PMIIA, nodesInPMIIA, g, apValue, alpha, keyCon);
					alpha.put(nextNode,alp);
				}
				
				double ew = 0;
				for (TypedWeightedEdge tt : g.findEdgeSet(u, nextNode)) {
					if (tt.getConcept() == keyCon.getId()){
						ew = tt.getWeight();
					}
				}
				
				return alp * ew * total;
				
			}
		}
	}
	
	private static Graph<MultiProbabilityNode, TypedWeightedEdge> copyG(Graph<MultiProbabilityNode, TypedWeightedEdge> g, Concept keyCon) {
		Graph<MultiProbabilityNode, TypedWeightedEdge> gNew = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
		
		for (MultiProbabilityNode n : g.getVertices()){ gNew.addVertex(n);}
		for (TypedWeightedEdge t : g.getEdges()) { 
			if (t.getConcept() == keyCon.getId()){
			//	System.out.println("Adding edge");
				gNew.addEdge(t, g.getSource(t), g.getDest(t), EdgeType.DIRECTED);
			}
		}
		return gNew;
	}

	private static HashMap<MultiProbabilityNode, MultiProbabilityNode> calcPMIIASpeed(MultiProbabilityNode m, double thresh, ArrayList<MultiProbabilityNode> seeds, 
			Graph<MultiProbabilityNode, TypedWeightedEdge> g, TIntHashSet IS, HashMap<Integer, MultiProbabilityNode> nodes, Concept keyCon) {
		TIntHashSet seedsToRemove = new TIntHashSet();
		
		MAITransformer2 MAITrans = new MAITransformer2();
		
		HashMap<MultiProbabilityNode, MultiProbabilityNode> PMIIA = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		HashMap<MultiProbabilityNode, Double> pathCost = new HashMap<MultiProbabilityNode, Double>();
		HashMap<MultiProbabilityNode, Double> seedCost = new HashMap<MultiProbabilityNode, Double>();
		HashSet<MultiProbabilityNode> currentConsider = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		Graph<MultiProbabilityNode, TypedWeightedEdge> currG = copyG(g, keyCon);
		HashMap<MultiProbabilityNode, MultiProbabilityNode> seedPath = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		List<TypedWeightedEdge> path = null;
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

				//System.out.println(seeds.get(i).getId());
				//System.out.println(m.getId());
				//System.out.println(seeds.contains(m));
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
						if (seeds.contains(test) && !seedsToRemove.contains(test.getId())) {
							IS.add(seeds.get(i).getId());
							break;
						}
					}
					if (!IS.contains(seeds.get(i).getId())) {
						if (PMIIA.containsKey(seeds.get(i))){
							if (pathCost.get(seeds.get(i)) < dist) {
								PMIIA.put(seeds.get(i), currG.getDest(path.get(path.size()-1)));
								pathCost.put(seeds.get(i), dist);
							} 
						}
						else {
							PMIIA.put(seeds.get(i), currG.getDest(path.get(path.size()-1)));
							pathCost.put(seeds.get(i), dist);
						}
					}
				}
				
			}
			seedsToRemove.add(currSeedID);
			currG.removeVertex(currSeed);
		}
		
		currentConsider = new HashSet<MultiProbabilityNode>();
		
		for(TypedWeightedEdge incomingEdge : currG.getInEdges(m)) {
			MultiProbabilityNode s = currG.getSource(incomingEdge);
			if (incomingEdge.getWeight() > thresh) {
				PMIIA.put(s, m);
				pathCost.put(s, incomingEdge.getWeight());
				currentConsider.add(s);
			}
		}
		
		nextToConsider = new HashSet<MultiProbabilityNode>();
		while(!currentConsider.isEmpty()) {
			for (MultiProbabilityNode curr : currentConsider){
				for(TypedWeightedEdge incomingEdge : currG.getInEdges(curr)) {
					MultiProbabilityNode s = currG.getSource(incomingEdge);
					double sCost = incomingEdge.getWeight() * pathCost.get(curr);
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
			currentConsider.clear();
			currentConsider.addAll(nextToConsider);
			nextToConsider.clear();
		}
		
		return PMIIA;
	}


}

class MAITransformer2 implements Transformer<TypedWeightedEdge, Number>{

	@Override
	public Number transform(TypedWeightedEdge arg0) {
		return Math.log10(arg0.getWeight()) * -1;
	}

}