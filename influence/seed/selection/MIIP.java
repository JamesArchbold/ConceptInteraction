package influence.seed.selection;

import gnu.trove.map.hash.TIntObjectHashMap;
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;

public class MIIP {

	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept target, Concept secondary, HashSet<MultiProbabilityNode> seedsTarget) {
		
		//short code block finds all nodes currently capable of spreading target concept
		//DiverseICConcept target2 = (DiverseICConcept) target;
		HashSet<MultiProbabilityNode> nodesAll = new HashSet<MultiProbabilityNode>();
		if (target.getType().equals("ic")) {
			DiverseICConcept target2 = (DiverseICConcept) target;
			if (target2.getLastActivated().size() == 0) {
				nodesAll = seedsTarget;
			}
			else {
				nodesAll = target2.getLastActivated();
			}
		}
		else {
			nodesAll = seedsTarget;
		}
		
		//access to all nodes
		TIntObjectHashMap<MultiProbabilityNode> nodesFullSet = new TIntObjectHashMap<MultiProbabilityNode>();
		for (MultiProbabilityNode m: g.getVertices()) {
			nodesFullSet.put(m.getId(), m);
		}
		
		// For each seed, find top 2 independent paths to all nodes 
		// with all probabilities the same - equivalent to two shortest paths to seed nodes - easily found
		
		//from each seed we explore outward finding shortest paths
		
		//once a path for a given node is more than one of the two current stored - we can stop exploring
		
		//for each node must store 2 different paths
		
		
		HashMap<MultiProbabilityNode, Double[]> pathProbs = new HashMap<MultiProbabilityNode, Double[]>();
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> descendents = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
		HashMap<MultiProbabilityNode, MultiProbabilityNode> ancestor = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		HashMap<MultiProbabilityNode, MultiProbabilityNode> ancestor2 = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		System.out.println("Finding tree paths for each seed:");
		int iCount = 0;
		for (MultiProbabilityNode seed : nodesAll) {
			iCount++;
		//	System.out.println("Seed " + iCount + " out of " + nodesAll.size());
			Double[] pathseed = new Double[2];
			pathseed[0] = 1.0;
			pathseed[1] = 1.0;
			pathProbs.put(seed, pathseed);
			HashMap<MultiProbabilityNode, Double> actProb = new HashMap<MultiProbabilityNode, Double>();
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			int iiCount = 0;
			for (TypedWeightedEdge t : g.getOutEdges(seed)) {
				iiCount++;
			//	System.out.println("Seed " + iCount + " out of " + nodesAll.size() + ". Out Edges: " + iiCount + "/" + g.getOutEdges(seed).size() + ".");
				MultiProbabilityNode neigh = g.getOpposite(seed, t);
				double seedC = (1 + target.getInternalEnvironment(seed));
				double cost = t.getWeight() * seedC * (1 + target.getExternalEnvironment(neigh));
				if (!neigh.isActivated(target)) {
					if (!pathProbs.containsKey(neigh)) {
						Double[] paths = new Double[2];
						paths[0] = cost;
						paths[1] = 0.0;
						pathProbs.put(neigh, paths);
						actProb.put(neigh, cost);
						neighbours.add(neigh);
						if (!descendents.containsKey(seed)) {
							HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
							d.add(neigh);
							descendents.put(seed, d);
						}
						else {
							if (ancestor.containsKey(neigh)) {
								descendents.get(ancestor.get(neigh)).remove(neigh);
							}
							descendents.get(seed).add(neigh);
						}
						ancestor.put(neigh, seed);
						
					}
					else {
						Double[] paths = pathProbs.get(neigh);
						if (paths[1] < paths[0] && cost > paths[1]) {
							paths[1] = cost;
							actProb.put(neigh, cost);
							neighbours.add(neigh);
							if (!descendents.containsKey(seed)) {
								HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
								d.add(neigh);
								descendents.put(seed, d);
							}
							else {
								if (ancestor2.containsKey(neigh)) {
									descendents.get(ancestor2.get(neigh)).remove(neigh);
								}
								descendents.get(seed).add(neigh);
							}
							ancestor2.put(neigh, seed);
						}
						else if (paths[0] < paths[1] && cost > paths[0]) {
							paths[0] = cost;
							actProb.put(neigh, cost);
							neighbours.add(neigh);
							if (!descendents.containsKey(seed)) {
								HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
								d.add(neigh);
								descendents.put(seed, d);
							}
							else {
								if (ancestor.containsKey(neigh)) {
									descendents.get(ancestor.get(neigh)).remove(neigh);
								}
								descendents.get(seed).add(neigh);
							}
							ancestor.put(neigh, seed);
						}
						else if (cost > paths[1]) {
							paths[1] = cost;
							actProb.put(neigh, cost);
							neighbours.add(neigh);
							if (!descendents.containsKey(seed)) {
								HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
								d.add(neigh);
								descendents.put(seed, d);
							}
							else {
								if (ancestor2.containsKey(neigh)) {
									descendents.get(ancestor2.get(neigh)).remove(neigh);
								}
								descendents.get(seed).add(neigh);
							}
							ancestor2.put(neigh, seed);
						}
						else if (cost > paths[0]) {
							paths[0] = cost;
							actProb.put(neigh, cost);
							neighbours.add(neigh);
							if (!descendents.containsKey(seed)) {
								HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
								d.add(neigh);
								descendents.put(seed, d);
							}
							else {
								if (ancestor.containsKey(neigh)) {
									descendents.get(ancestor.get(neigh)).remove(neigh);
								}
								descendents.get(seed).add(neigh);
							}
							ancestor.put(neigh, seed);
						}
					}
				}
			}
			System.out.println("Nodes with descendents: " + descendents.keySet().size());
			HashSet<MultiProbabilityNode> nextNeighbours = new HashSet<MultiProbabilityNode>();
		//	HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
			int gg = 0;
			while (neighbours.size() > 0 && gg <= 5) {
				gg++;
			//	System.out.println("Seed " + iCount + " out of " + nodesAll.size() + ". Nodes to explore: " + neighbours.size() + ".");
			//	System.out.println(gg++);
				for (MultiProbabilityNode neigh : neighbours) {
					double currCost = 1 + target.getInternalEnvironment(neigh);
					for (TypedWeightedEdge t : g.getOutEdges(neigh)) {
						MultiProbabilityNode currNeigh = g.getOpposite(neigh, t);
						double cost = t.getWeight() * currCost * (1 + target.getExternalEnvironment(currNeigh));
						if (!currNeigh.isActivated(target)) {
							double currProb = actProb.get(neigh) * cost;
							
							if (!pathProbs.containsKey(currNeigh)){
								Double[] paths = new Double[2];
								paths[0] = currProb;
								paths[1] = 0.0;
								pathProbs.put(currNeigh, paths);
								actProb.put(currNeigh, currProb);
								nextNeighbours.add(currNeigh);
								
								if (!descendents.containsKey(neigh)) {
									HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
									d.add(currNeigh);
									descendents.put(neigh, d);
								}
								else {
									if (ancestor.containsKey(currNeigh)) {
										descendents.get(ancestor.get(currNeigh)).remove(currNeigh);
									}
									descendents.get(neigh).add(currNeigh);
								}
								ancestor.put(currNeigh, neigh);
							}
							else {
								Double[] paths = pathProbs.get(currNeigh);
								if (paths[1] < paths[0] && currProb > paths[1]) {
									paths[1] = currProb;
									actProb.put(currNeigh, currProb);
									nextNeighbours.add(currNeigh);
									if (!descendents.containsKey(neigh)) {
										HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
										d.add(currNeigh);
										descendents.put(neigh, d);
									}
									else {
										if (ancestor2.containsKey(currNeigh)) {
											descendents.get(ancestor2.get(currNeigh)).remove(currNeigh);
										}
										descendents.get(neigh).add(currNeigh);
									}
									ancestor2.put(currNeigh, neigh);
								}
								else if (paths[0] < paths[1] && currProb > paths[0]) {
									paths[0] = currProb;
									actProb.put(currNeigh, currProb);
									nextNeighbours.add(currNeigh);
									if (!descendents.containsKey(neigh)) {
										HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
										d.add(currNeigh);
										descendents.put(neigh, d);
									}
									else {
										if (ancestor.containsKey(currNeigh)) {
											descendents.get(ancestor.get(currNeigh)).remove(currNeigh);
										}
										descendents.get(neigh).add(currNeigh);
									}
									ancestor.put(currNeigh, neigh);
								}
								else if (currProb > paths[1]) {
									paths[1] = currProb;
									actProb.put(currNeigh, currProb);
									nextNeighbours.add(currNeigh);
									if (!descendents.containsKey(neigh)) {
										HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
										d.add(currNeigh);
										descendents.put(neigh, d);
									}
									else {
										if (ancestor2.containsKey(currNeigh)) {
											descendents.get(ancestor2.get(currNeigh)).remove(currNeigh);
										}
										descendents.get(neigh).add(currNeigh);
									}
									ancestor2.put(currNeigh, neigh);
								}
								else if (currProb > paths[0]) {
									paths[0] = currProb;
									actProb.put(currNeigh, currProb);
									nextNeighbours.add(currNeigh);
									if (!descendents.containsKey(neigh)) {
										HashSet<MultiProbabilityNode> d = new HashSet<MultiProbabilityNode>();
										d.add(currNeigh);
										descendents.put(neigh, d);
									}
									else {
										if (ancestor.containsKey(currNeigh)) {
											descendents.get(ancestor.get(currNeigh)).remove(currNeigh);
										}
										descendents.get(neigh).add(currNeigh);
									}
									ancestor.put(currNeigh, neigh);
								}
							}
						}	
					}
				}
				
				neighbours.clear();
				neighbours.addAll(nextNeighbours);
				nextNeighbours.clear();
			}
		}

		System.out.println("Nodes with descendents: " + descendents.keySet().size());
		
		// calculate ap for each node using the 2 most probable paths across all seed nodes
		// ap = 1 - ((1 - 1st path prob) * (1 - 2nd path prob))
	//	System.out.println("Calculating AP for " + pathProbs.keySet().size() + " nodes.");
		HashMap<MultiProbabilityNode, Double> actProb = new HashMap<MultiProbabilityNode, Double>();
		iCount = 0;
		for (MultiProbabilityNode currNode : pathProbs.keySet()) {
			iCount++;
		//	System.out.println(iCount + "/" + pathProbs.keySet().size());
			if (!currNode.isActivated(target)) {
				Double[] probs = pathProbs.get(currNode);
				
				double act = 1 - ( (1-probs[0]) * (1-probs[1]) );
				actProb.put(currNode, act);
			}
		}
		
		System.out.println("Nodes with descendents: " + descendents.keySet().size());
		//calculate gain for all nodes 
		//gain is (new prob/old prob - 1) for all out neighbours * ap for all descendents of that neighbour
		System.out.println("Calculating gain for " + actProb.keySet().size() + " nodes");
		iCount = 0;
		int dUsed = 0;
		for (MultiProbabilityNode currNode : actProb.keySet()){
			double totalGain = 0;
			iCount++;
		//	System.out.println(iCount + "/" + actProb.keySet().size());
			long start = System.currentTimeMillis();
			double currCost2 = 1 + target.getInternalEnvironment(currNode);
			
			for (TypedWeightedEdge t : g.getOutEdges(currNode)) {
				MultiProbabilityNode currentNeigh = g.getOpposite(currNode, t);
				double prob = t.getWeight() * currCost2 * (1 + target.getExternalEnvironment(currentNeigh));
				
				double newProb = prob * (1 + target.getConceptInternal(secondary));
				double totalDescend = 0.0;
				HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
				HashSet<MultiProbabilityNode> nextNeighbours = new HashSet<MultiProbabilityNode>();
				HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
				if (descendents.containsKey(currentNeigh)){
					neighbours.addAll(descendents.get(currentNeigh));
				}
				int gg = 0;
				while (neighbours.size() > 0 && gg <= 6) {
					gg++;
					for (MultiProbabilityNode neighNeigh : neighbours) {
						if (!seenBefore.contains(neighNeigh)) {
							totalDescend = totalDescend + actProb.get(neighNeigh);
							if (descendents.containsKey(neighNeigh)){
								nextNeighbours.addAll(descendents.get(neighNeigh));
							}
						}
					}
					
					seenBefore.addAll(neighbours);
					neighbours.clear();
					neighbours.addAll(nextNeighbours);
					nextNeighbours.clear();
				}
				newProb = (newProb/prob) - 1;
				newProb = newProb * totalDescend;
				totalGain = totalGain + newProb;
				//System.out.println("gg = " + gg);
			}
			
			currNode.setValue(totalGain);
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		sortedN.addAll(actProb.keySet());
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
    	MultiProbabilityNode u = sortedN.last();
    	seeds.add(u);
    	
    	//System.out.println("Now selecting seeds");
    	System.out.println("Nodes with descendents: " + descendents.keySet().size());
    	while (seeds.size() < seedCount) {
    //		System.out.println("Seeds Selected: " + seeds.size() + ". ID: " + u.getId());
    		double baseProb = actProb.get(u);
    		
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> nextNeighbours = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
			neighbours.add(u); 
			
			System.out.println("Seeds Selected: " + seeds.size() + ". Updating Activation Probabilities.");
			int gg = 0;
			while (neighbours.size() > 0) {
				gg++;
		//		System.out.println(gg);
				for (MultiProbabilityNode curr : neighbours){
					if (descendents.containsKey(curr)) {
						for (MultiProbabilityNode dec : descendents.get(curr)) {
							if (!seenBefore.contains(dec)) {
								if (ancestor.get(dec).getId() == curr.getId()) {
									pathProbs.get(dec)[0] = pathProbs.get(dec)[0] * (1 + target.getConceptInternal(secondary));
								}
								else if (ancestor2.get(dec).getId() == curr.getId()) {
									pathProbs.get(dec)[1] = pathProbs.get(dec)[1] * (1 + target.getConceptInternal(secondary));
								}
								nextNeighbours.add(dec);
							}
						}
					}
				}
				seenBefore.addAll(neighbours);
				neighbours.clear();
				neighbours.addAll(nextNeighbours);
				nextNeighbours.clear();
			}
			System.out.println("Done updating AP");
			HashSet<MultiProbabilityNode> ancestList = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> nextAncestList = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> fullAncestList = new HashSet<MultiProbabilityNode>();
			
			ancestList.add(ancestor.get(u));
			ancestList.add(ancestor2.get(u));
			fullAncestList.addAll(ancestList);
			//System.out.println("Seeds Selected: " + seeds.size() + ". Finding all ancestors.");
			int gg2 = 0;
			while (ancestList.size() > 0 & gg2 <= 6) {
				gg2++;
			//	System.out.println(gg2);
				for (MultiProbabilityNode m : ancestList) {
					if (ancestor.containsKey(m)) {
						//fullAncestList.add(ancestor.get(m));
						nextAncestList.add(ancestor.get(m));
					}
					if (ancestor2.containsKey(m)) {
						//fullAncestList.add(ancestor2.get(m));
						nextAncestList.add(ancestor2.get(m));
					}
				}
				
				nextAncestList.remove(fullAncestList);
				fullAncestList.addAll(nextAncestList);
				ancestList.clear();
				ancestList.addAll(nextAncestList);
				nextAncestList.clear();
				
			}
			
			fullAncestList.addAll(seenBefore);
			System.out.println("Seeds Selected: " + seeds.size() + ". Updating Gain for all nodes on paths.");
			for (MultiProbabilityNode currNode : fullAncestList) {
				double totalGain = 0;
				double currCost = 1 + target.getInternalEnvironment(currNode);
				for (TypedWeightedEdge t : g.getOutEdges(currNode)) {
					MultiProbabilityNode currentNeigh = g.getOpposite(currNode, t);
					double prob = (t.getWeight() * currCost * (1 + target.getExternalEnvironment(currentNeigh)));
					double newProb = prob * (1 + target.getConceptInternal(secondary));
					double totalDescend = 0.0;
					HashSet<MultiProbabilityNode> neighbours2 = new HashSet<MultiProbabilityNode>();
					HashSet<MultiProbabilityNode> nextNeighbours2 = new HashSet<MultiProbabilityNode>();
					HashSet<MultiProbabilityNode> seenBefore2 = new HashSet<MultiProbabilityNode>();
					if (descendents.containsKey(currentNeigh)){
						neighbours2.addAll(descendents.get(currentNeigh));
					}
					while (neighbours2.size() > 0) {
						for (MultiProbabilityNode neighNeigh : neighbours2) {
							if (!seenBefore2.contains(neighNeigh) && !seeds.contains(neighNeigh) && !neighNeigh.isActivated(target)) {
								totalDescend = totalDescend + actProb.get(neighNeigh);
								if (descendents.containsKey(neighNeigh)){
									nextNeighbours2.addAll(descendents.get(neighNeigh));
								}
							}
						}
						
						seenBefore2.addAll(neighbours2);
						neighbours2.clear();
						neighbours2.addAll(nextNeighbours2);
						nextNeighbours2.clear();
					}
					
					newProb = (newProb/prob) - 1;
					newProb = newProb * totalDescend;
					totalGain = totalGain + newProb;
				}
				
				currNode.setValue(totalGain);
			}
			
			actProb.remove(u);
			
			sortedN.clear();
			sortedN.addAll(actProb.keySet());
			u = sortedN.last();
	    	seeds.add(u);
	    	//System.out.println("Nodes with descendents: " + descendents.keySet().size());
    		
    	}
		
		// must adjust ap for descendents of selected node due to boost
		
		//repeat
    	
		return seeds;
	}
}
