package influence.seed.selection;
//MGP extended to account for different types of concepts
import gnu.trove.map.hash.TObjectDoubleHashMap;
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;

public class ExtMPG {

	public static HashSet<MultiProbabilityNode> getSeedsLT
		(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, double thresh, Concept target, Concept inhibiting, HashSet<MultiProbabilityNode> seedsTarget, Concept modifying) {
			HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
			
			HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> mapOfPathCosts = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
			HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> seedReachable = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
			TObjectDoubleHashMap<MultiProbabilityNode> actProb = new TObjectDoubleHashMap<MultiProbabilityNode>();
			TObjectDoubleHashMap<MultiProbabilityNode> modProb = new TObjectDoubleHashMap<MultiProbabilityNode>();
			DiverseLTConcept target2 = (DiverseLTConcept) target;
			Collection<MultiProbabilityNode> nodesAll = null;
			nodesAll = seedsTarget;

			
			double infLimit;
			
			if (target.getConceptInternal(inhibiting) <= -1) {
				infLimit = Double.MAX_VALUE;
			}
			else if (target.getConceptInternal(inhibiting) > 0) {
				infLimit = 1.0;
			}
			else {
				infLimit = 1.0 / (1.0 + target.getConceptInternal(inhibiting));
			}
			int ii = 0;
			for (MultiProbabilityNode curr : nodesAll) {
				System.out.println("Checking " + (ii++) + " out of " + nodesAll.size());
				if (curr.isActivated(target)){
					TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMapLT(g, curr, thresh, seeds, target, inhibiting, infLimit);
					Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
					while (keyIter.hasNext()) {
						MultiProbabilityNode m = keyIter.next();
						if (actProb.containsKey(m)) {
							actProb.put(m, actProb.get(m)+pathCost.get(m));
						}
						else {
							seedReachable.put(m, new HashSet<MultiProbabilityNode>());
							//neighbourReachable.put(m, new TIntHashSet());
							actProb.put(m,pathCost.get(m));
						}
						seedReachable.get(m).add(curr);
					}
					mapOfPathCosts.put(curr, pathCost);
				}
			}
			
			for (MultiProbabilityNode curr: g.getVertices()) {
				if (curr.isActivated(modifying)){
					TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMapLT2(g, curr, thresh, seeds, modifying, infLimit);
					Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
					modProb.put(curr, 1);
					while (keyIter.hasNext()){
						MultiProbabilityNode m = keyIter.next();
						if (modProb.containsKey(m)){
							if (modProb.get(m) < 1) {
								modProb.put(m, modProb.get(m)+pathCost.get(m));
								if (modProb.get(m) > 1) {
									modProb.put(m, 1);
								}
							}
						}
						else {
							modProb.put(m, pathCost.get(m));
						}
					}
				}
			}
			
			int i = 0;
			ii = 0;
			TObjectDoubleHashMap<MultiProbabilityNode> expected = new TObjectDoubleHashMap<MultiProbabilityNode>();
			Iterator<MultiProbabilityNode> actIter = actProb.keySet().iterator();
			while (actIter.hasNext()) {
				MultiProbabilityNode current = actIter.next();
				System.out.println("Checking " + (ii++) + " out of " + actProb.size());
				ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>> pathCostList = getPathCostDoubleLT(g, current, thresh,seeds, target, inhibiting, infLimit, modifying, modProb);
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = pathCostList.get(0);
				double total = 0;
				Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
				while (keyIter.hasNext()) {
					MultiProbabilityNode m = keyIter.next();
					total = total + pathCost.get(m);
				}
				
				pathCost = pathCostList.get(1);
				double total2 = 0;
				keyIter = pathCost.keySet().iterator();
				while (keyIter.hasNext()) {
					MultiProbabilityNode m = keyIter.next();
					total2 = total2 + pathCost.get(m);
				}
				total = total2 - total;
				if (target.getConceptExternal(inhibiting) < 0) {
					total = total * -1;
				}
				expected.put(current, total);
				
				current.setValue(actProb.get(current) * expected.get(current));
			}
			
			Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
			SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		   // sortedN.addAll(actProb.keySet());
		    
			actIter = actProb.keySet().iterator();
			for (MultiProbabilityNode ac : actProb.keySet()) {
				sortedN.add(ac);
			}
		    
		    
	    	MultiProbabilityNode u = sortedN.last();
	    	seeds.add(u);
	    	actProb.remove(u);
	    	expected.remove(u);
	    	HashSet<MultiProbabilityNode> changed = new HashSet<MultiProbabilityNode>();
	    	
	    	 while (seeds.size() < seedCount && sortedN.size() > 0){
	    		 System.out.println(seeds.size());
	 	    	Iterator<MultiProbabilityNode> seedIter = seedReachable.get(u).iterator();
	 	    	while (seedIter.hasNext()) {
	 	    		MultiProbabilityNode tSeed = seedIter.next();

	 	    		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMapLT(g, tSeed, thresh, seeds, target, inhibiting, infLimit);
	 	    		TObjectDoubleHashMap<MultiProbabilityNode> oldCost = mapOfPathCosts.get(tSeed);
	 	    		
	 	    		Iterator<MultiProbabilityNode> oldCostIter = oldCost.keySet().iterator();
	 	    		while (oldCostIter.hasNext()) {
	 	    			MultiProbabilityNode m = oldCostIter.next();
	 	    			if (!seeds.contains(m)){
	 	    				if (actProb.containsKey(m)) {
	 	    					if (pathCost.containsKey(m)) {
	 			    				actProb.put(m, actProb.get(m) - (oldCost.get(m) - pathCost.get(m)));
	 			    			}
	 			    			else {
	 			    				actProb.put(m, actProb.get(m) - oldCost.get(m));
	 			    			}
	 	    					if (actProb.get(m) <= 0) {
	 		    					actProb.remove(m);
	 		    					expected.remove(m);
	 		    					changed.remove(m);
	 		    				}
	 	    					else {
	 	    						changed.add(m);
	 	    					}
	 	    				}
	 		    
	 	    			}
	 	    		}
	 	    		oldCostIter = pathCost.keySet().iterator();
	 	    		while (oldCostIter.hasNext()) {
	 	    			MultiProbabilityNode m = oldCostIter.next();
	 	    			oldCost.put(m, pathCost.get(m));
	 	    		}
	 	    	}
	 	    	

	 	    	Set<MultiProbabilityNode> neighIter = getIncomingNeighboursLT(g, u, thresh, seeds, target, inhibiting, infLimit);
	 	    	int kk = 0;
	 	    	for (MultiProbabilityNode tNeigh : neighIter) {

	 	    		if (actProb.containsKey(tNeigh) && !seeds.contains(tNeigh)) {
	 	    			ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>> pathCostList = getPathCostDoubleLT(g, tNeigh, thresh,seeds, target, inhibiting, infLimit, modifying, modProb);
		    			TObjectDoubleHashMap<MultiProbabilityNode> pathCost = pathCostList.get(0);
		    			double total = 0;
		    			Iterator<MultiProbabilityNode> pathIter = pathCost.keySet().iterator();
		    			while(pathIter.hasNext()) {
		    				MultiProbabilityNode m = pathIter.next();
		    				total = total + pathCost.get(m);
		    			//	if (!neighbourReachable2.containsKey(m)){
		    				//	neighbourReachable2.put(m, new TIntHashSet());
		    			//	}
		    			//	neighbourReachable2.get(m).add(tNeigh);
		    			}
		    			pathCost = pathCostList.get(1);
		    			double total2 = 0;
		    			pathIter = pathCost.keySet().iterator();
		    			while (pathIter.hasNext()) {
		    				MultiProbabilityNode m = pathIter.next();
		    				total2 = total2 + pathCost.get(m);
		    			}
		    			total = total2 - total;
		    			if (target.getConceptExternal(inhibiting) < 0) {
		    				total = total * -1;
		    			}
		    			if (total != expected.get(tNeigh)) {
		    				expected.put(tNeigh, total);
		    				changed.add(tNeigh);
		    			}
	 	    			
	 	    		}
	 	    	}
	 	    	

	 	    	for (MultiProbabilityNode cu : changed) {
	 	    		cu.setValue(actProb.get(cu) * expected.get(cu));
	 	    	}
	 	    	
	 	    	changed.clear();
	 			sortedN.clear();
	 			
	 			actIter = actProb.keySet().iterator();
	 			for (MultiProbabilityNode ac : actProb.keySet()) {
	 				sortedN.add(ac);
	 			}
	 			if (!sortedN.isEmpty()) {
	 		    	u = sortedN.last();
	 		    	seeds.add(u);
	 		    	actProb.remove(u);
	 		    	expected.remove(u);
	 			}

	 	    	
	 	    }
	 		
	 		return seeds;

	}
	
	private static Set<MultiProbabilityNode> getIncomingNeighboursLT(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting, double infLimit){
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> pathCost = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		Collection<TypedWeightedEdge> currSet = g.getInEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
		
		boolean currBoost = seeds.contains(current);
		double currEnv = 0;
		if (currBoost) {
			currEnv = 1 + target.getInternalEnvironment(current) + target.getConceptInternal(boosting);
		}
		else {
			currEnv = 1 + target.getInternalEnvironment(current);
		}
		
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == target.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current, twe);
				double neighThreshold = neigh.getAttribute(target.getId());
				double relativeInf = twe.getWeight() / neighThreshold;
				double contextCost = 0;
				
				if (seeds.contains(neigh)) {
					contextCost = Math.min(1.0, relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting)));
				} 
				else {
					contextCost = Math.min(1.0, relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh)));
				}
				
				if (contextCost < infLimit && !neigh.isActivated(target)) {
					if (pathCost.containsKey(neigh)) {
						double cost = contextCost;
					//	if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
					//	if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
						pathCost.put(neigh, pathCost.get(neigh) + cost);
					}
					else {
						double cost = contextCost;
					//	if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
					//	if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
						pathCost.put(neigh, cost);
					}
				}
				
				neighbours.add(neigh);
			}
		}
		
		HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
		seenBefore.addAll(neighbours);
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		int hops = 0;
		while (!neighbours.isEmpty() && hops < 3) {

			for (MultiProbabilityNode currentNeighbour : neighbours) {

				boolean currNeighBoost = seeds.contains(currentNeighbour);
				double currNeighEnv = 0;
				if (currNeighBoost) {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour) + target.getConceptInternal(boosting);
				}
				else {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
				}
				double neighThreshold = currentNeighbour.getAttribute(target.getId());
				
				for(TypedWeightedEdge outgoingEdge : g.getInEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == target.getId()) {
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						
						
						double relativeInf = outgoingEdge.getWeight() / neighThreshold;
						double conCost = 0.0;
						
						if (seeds.contains(s)) {
							conCost = Math.min(1.0, relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s) + target.getConceptExternal(boosting)));
						}
						else {
							conCost = Math.min(1.0, relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s)));
						}
						
						if (!s.isActivated(target) & conCost < infLimit) {
							double sCost = conCost * pathCost.get(currentNeighbour);
							if (currNeighBoost) {sCost = sCost * (1 + target.getConceptInternal(boosting));}
							if (seeds.contains(s)) {sCost = sCost * (1+target.getConceptExternal(boosting));}
							if (sCost >= thresh){
								if (!pathCost.containsKey(s)){
									pathCost.put(s, sCost);
									nextConsider.add(s);
								}
							}	
						}
					}
				}
			}
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
			hops++;
		}
		
		return pathCost.keySet();
	}
	
	private static ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>> getPathCostDoubleLT(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting, double infLimit, Concept modifying, TObjectDoubleHashMap<MultiProbabilityNode> modProb) {
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		TObjectDoubleHashMap<MultiProbabilityNode> pathCostMod = new TObjectDoubleHashMap<MultiProbabilityNode>();
		TObjectDoubleHashMap<MultiProbabilityNode> boostProbMap = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//System.out.println(current.getId());
		Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();

		
		int i = 0;
		double currEnv = 1 + target.getInternalEnvironment(current);
		if (seeds.contains(current)) {currEnv = currEnv + target.getConceptInternal(boosting);}
		double modEnv = 1 + boosting.getInternalEnvironment(current);
		double baseBoost = 0;
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == target.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current,twe);
				
				for (TypedWeightedEdge t : g.findEdgeSet(current, neigh)) {
					if (t.getConcept() == boosting.getId()) {
						baseBoost = t.getWeight();
					}
				}
				
				double apBoost = 0;
				
				double contextCost = 0.0;
				if (seeds.contains(neigh)) {
					contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting));
				}
				else {
					contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh));
				}
				
				
				if (contextCost < infLimit && !neigh.isActivated(target)) {
					if (pathCost.containsKey(neigh)) {
						double cost = contextCost;
						if (seeds.contains(neigh)) {
							//cost = cost * (1+target.getConceptExternal(boosting));
							apBoost = 1;
						}
						else {
							if (modProb.contains(neigh)){
								apBoost = baseBoost * (1 + (boosting.getConceptExternal(modifying) * modProb.get(neigh)));
							}
							else {
								apBoost = baseBoost;
							}
						}
						pathCost.put(neigh, pathCost.get(neigh)+cost);
						boostProbMap.put(neigh, apBoost);
						apBoost = apBoost * target.getConceptInternal(boosting);
						pathCostMod.put(neigh, pathCostMod.get(neigh) + (apBoost * cost));
						
					}
					else {
						double cost = contextCost;
						if (seeds.contains(neigh)) {
							//cost = cost * (1+target.getConceptExternal(boosting));
							apBoost = 1;
						}
						else {
							if (modProb.contains(neigh)){
								apBoost = baseBoost * (1 + (boosting.getConceptExternal(modifying) * modProb.get(neigh)));
							}
							else {
								apBoost = baseBoost;
							}
						}
						pathCost.put(neigh,cost);
						boostProbMap.put(neigh, apBoost);
						apBoost = apBoost * target.getConceptInternal(boosting);
						pathCostMod.put(neigh, (apBoost * cost));
					}
					neighbours.add(neigh);
				}
			}
		}
		
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		//double currBoost = 0;
		//System.out.println("Neighbours");
		int hops = 0;
		while (!neighbours.isEmpty() && hops < 3) {
			//currBoost = baseBoost * currBoost;
			for (MultiProbabilityNode currentNeighbour : neighbours) {
				
				boolean currNeighBoost = seeds.contains(currentNeighbour);
				double currNeighEnv = 0.0;
				
				if (currNeighBoost) {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour) + target.getConceptInternal(boosting);
				}
				else {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
				}
//				
//				if (currNeighBoost) {
//					currNeighBoostChance = boostProbMap.get(currentNeighbour);
//				}
//				else {
//					currNeighBoostChance = ;
//				}
//				
//				if (modProb.contains(currentNeighbour)){
//					currNeighBoostChance = currNeighBoostChance * (1 + (boosting.getConceptInternal(modifying) * modProb.get(currentNeighbour)));
//				}
				
				for(TypedWeightedEdge outgoingEdge : g.getOutEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == target.getId()){
						double currNeighBoostChance = 0;
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						
						if (!seeds.contains(s)){
							for (TypedWeightedEdge t : g.findEdgeSet(currentNeighbour,s)) {
								if (t.getConcept() == boosting.getId()) {
									currNeighBoostChance = boostProbMap.get(currentNeighbour) * t.getWeight();
									if (modProb.contains(currentNeighbour)){
										currNeighBoostChance = currNeighBoostChance * (1 + (boosting.getConceptInternal(modifying) * modProb.get(currentNeighbour)));
									}
								}
							}
						}
						
						double apBoost = 0;
						double conCost = 0.0;
						
						if (seeds.contains(s)) {
							conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s) + target.getConceptExternal(boosting));
						}
						else {
							conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s));
						}
						if (!s.isActivated(target) & conCost < infLimit) {
							double sCost = conCost * pathCost.get(currentNeighbour);
							
							if (seeds.contains(s)) {
								//sCost = sCost * (1+target.getConceptExternal(boosting));
								apBoost = 1;
							}
							else {
								if (modProb.contains(s)){
									apBoost = currNeighBoostChance * (1 + (boosting.getConceptExternal(modifying) * modProb.get(s)));
								}
								else {
									apBoost = currNeighBoostChance;
								}
							}
							
							if (sCost >= thresh){
								
								
								if (pathCost.containsKey(s)){
									if (pathCost.get(s) < sCost) {
										pathCost.put(s, sCost);
										boostProbMap.put(s, apBoost);
										apBoost = apBoost * target.getConceptInternal(boosting);
										pathCostMod.put(s, (apBoost * sCost));
										nextConsider.add(s);
									}
								}
								else {
									pathCost.put(s, sCost);
									boostProbMap.put(s, apBoost);
									apBoost = apBoost * target.getConceptInternal(boosting);
									pathCostMod.put(s, (apBoost * sCost));
									nextConsider.add(s);
								}
							}	
						}
					}
				}
			}
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
			hops++;
		}
		ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>> pathList = new ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>>();
		pathList.add(pathCost);
		pathList.add(pathCostMod);
		return pathList;
	}
	
	private static TObjectDoubleHashMap<MultiProbabilityNode> getPathCostMapLT2(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, double infLimit) {
				
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
				//System.out.println(current.getId());
				Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
				HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();

			//	int i = 0;
				double currEnv = 1 + target.getInternalEnvironment(current);
				for (TypedWeightedEdge twe : currSet) {
					if (twe.getConcept() == target.getId()) {
						MultiProbabilityNode neigh = g.getOpposite(current,twe);
						double contextCost = Math.min(1.0, twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh)));
						if (contextCost >= thresh && !neigh.isActivated(target)) {
							if (pathCost.containsKey(neigh)) {
								double cost = contextCost;
								pathCost.put(neigh, pathCost.get(neigh)+cost);
							}
							else {
								double cost = contextCost;
								pathCost.put(neigh,cost);
							}
							neighbours.add(neigh);
						}
					}
				}
				
				HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
				
				//System.out.println("Neighbours");
				int hops = 0;
				while (!neighbours.isEmpty() && hops < 3) {

					for (MultiProbabilityNode currentNeighbour : neighbours) {
						
						double currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
						
						for(TypedWeightedEdge outgoingEdge : g.getOutEdges(currentNeighbour)) {
							if (outgoingEdge.getConcept() == target.getId()) {
								MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
								if (!s.isActivated(target)) {
									double conCost = Math.min(1.0, outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s)));
									double sCost = conCost * pathCost.get(currentNeighbour);
									if (sCost >= thresh){
										if (pathCost.containsKey(s)){
											if (pathCost.get(s) < sCost) {
												pathCost.put(s, sCost);
												nextConsider.add(s);
											}
										}
										else {
											pathCost.put(s, sCost);
											nextConsider.add(s);
										}
									}	
								}
							}
						}
					}
					neighbours.clear();
					neighbours.addAll(nextConsider);
					nextConsider.clear();
					hops++;
				}
				
				return pathCost;
			}
			
	private static TObjectDoubleHashMap<MultiProbabilityNode> getPathCostMapLT(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
					HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting, double infLimit) {
				
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
				//System.out.println(current.getId());
				Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
				HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
				double currEnv = 1 + target.getInternalEnvironment(current);
//				int i = 0;
				
				for (TypedWeightedEdge twe : currSet) {
					if (twe.getConcept() == target.getId()) {
						MultiProbabilityNode neigh = g.getOpposite(current,twe);
						double neighThreshold = neigh.getAttribute(target.getId());
						double relativeInf = twe.getWeight() / neighThreshold;
						double contextCost = 0;
						
						if (seeds.contains(neigh)) {
							contextCost = Math.min(1.0, relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting)));
						} 
						else {
							contextCost = Math.min(1.0, relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh)));
						}
						
						if (contextCost < infLimit && !neigh.isActivated(target)) {
							if (pathCost.containsKey(neigh)) {
								double cost = contextCost;
								//if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
								pathCost.put(neigh, pathCost.get(neigh)+cost);
							}
							else {
								double cost = contextCost;
								//if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
								pathCost.put(neigh,cost);
							}
							neighbours.add(neigh);
						}
					}
				}
				
				HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
				
				//System.out.println("Neighbours");
				int hops = 0;
				while (!neighbours.isEmpty() && hops < 3) {

					for (MultiProbabilityNode currentNeighbour : neighbours) {

						boolean currNeighBoost = seeds.contains(currentNeighbour);
						double currNeighEnv = 0;
						if (currNeighBoost) {
							currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour) + target.getConceptInternal(boosting);
						}
						else {
							currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
						}
						
						for(TypedWeightedEdge outgoingEdge : g.getOutEdges(currentNeighbour)) {
							if (outgoingEdge.getConcept() == target.getId()) {
								MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
								double neighThreshold = s.getAttribute(target.getId());
								double relativeInf = outgoingEdge.getWeight() / neighThreshold;
								double conCost = 0.0;
								
								if (seeds.contains(s)) {
									conCost = Math.min(1.0, relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s) + target.getConceptExternal(boosting)));
								}
								else {
									conCost = Math.min(1.0, relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s)));
								}
								if (!s.isActivated(target) & conCost < infLimit) {
									double sCost = conCost * pathCost.get(currentNeighbour);
								//	if (currNeighBoost) {sCost = sCost * (1 + target.getConceptInternal(boosting));}
								//	if (seeds.contains(s)) {sCost = sCost * (1+target.getConceptExternal(boosting));}
									
									if (sCost >= thresh){
										
										if (pathCost.containsKey(s)){
											if (pathCost.get(s) < sCost) {
												pathCost.put(s, sCost);
												nextConsider.add(s);
											}
										}
										
										else {
											pathCost.put(s, sCost);
											nextConsider.add(s);
										}
									}	
									
								}
							}
						}
					}
					neighbours.clear();
					neighbours.addAll(nextConsider);
					nextConsider.clear();
					hops++;
				}
				
				return pathCost;
			}
}
