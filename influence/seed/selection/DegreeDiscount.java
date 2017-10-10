package influence.seed.selection;
//performs Chen et al.'s degree discount heuristic
import gnu.trove.map.hash.TIntDoubleHashMap;
import influence.concepts.Concept;
import influence.edges.Edge;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.Node;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;

public class DegreeDiscount {

	public <E extends Node, T extends Edge> HashSet<E> getSeeds
	(Graph<E, T> g, int seedCount, double prob) {
		
		HashSet<E> seeds = new HashSet<E>();
		HashSet<E> nodesToSort = new HashSet<E>();
		TIntDoubleHashMap tVal = new TIntDoubleHashMap();
		
		for (E m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
			tVal.put(m.getId(), 0);
		//	System.out.println(m.getId() + " : " + m.getValue());
		}
		
		Comparator<E> comp = new MultiProbabilityComparator<E>();
		SortedSet<E> sortedN = new TreeSet<E>(comp);
		
		for (E m : nodesToSort) {
			sortedN.add(m);
		//	System.out.println("Adding " + m.getId());
		}
		
		while (seeds.size() < seedCount) {
			E choice = sortedN.last();
			seeds.add(choice);
			nodesToSort.remove(choice);
	//		System.out.println("Adding " + choice.getId());
			for (E n : g.getNeighbors(choice)){
				tVal.put(n.getId(), (tVal.get(n.getId()) + 1));
				double dVal = g.getNeighborCount(n);
				double tV = tVal.get(n.getId());
				n.setValue(dVal - 2*tV - ((dVal - tV)*tV*prob));
				//System.out.println("Neighbour : " + n.getId());
			}
			
			sortedN.clear();
			
			for (E m : nodesToSort) {
				sortedN.add(m);
			//	System.out.println(m.getId() + " : " + m.getValue());
			}
		}
		
		return seeds;
	}
	
	public <T extends Edge> HashSet<MultiProbabilityNode> getSeedsBurnIn
	(Graph<MultiProbabilityNode, T> g, int seedCount, double prob, Concept con) {
		
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		TIntDoubleHashMap tVal = new TIntDoubleHashMap();
		
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
			tVal.put(m.getId(), 0);
		//	System.out.println(m.getId() + " : " + m.getValue());
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			if (m.isActivated(con)) {
				nodesToSort.remove(m);
				for (MultiProbabilityNode n : g.getNeighbors(m)){
					tVal.put(n.getId(), (tVal.get(n.getId()) + 1));
					double dVal = g.getNeighborCount(n);
					double tV = tVal.get(n.getId());
					n.setValue(dVal - 2*tV - ((dVal - tV)*tV*prob));
				}
			}
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		for (MultiProbabilityNode m : nodesToSort) {
			sortedN.add(m);
		//	System.out.println("Adding " + m.getId());
		}
		
		while (seeds.size() < seedCount) {
			MultiProbabilityNode choice = sortedN.last();
			if (!choice.isActivated(con)) { 
				seeds.add(choice);
			}
			nodesToSort.remove(choice);
	//		System.out.println("Adding " + choice.getId());
			for (MultiProbabilityNode n : g.getNeighbors(choice)){
				tVal.put(n.getId(), (tVal.get(n.getId()) + 1));
				double dVal = g.getNeighborCount(n);
				double tV = tVal.get(n.getId());
				n.setValue(dVal - 2*tV - ((dVal - tV)*tV*prob));
				//System.out.println("Neighbour : " + n.getId());
			}
			
			sortedN.clear();
			
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			//	System.out.println(m.getId() + " : " + m.getValue());
			}
		}
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsLTSame
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept con) {
		
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		TIntDoubleHashMap tVal = new TIntDoubleHashMap();
		TIntDoubleHashMap probVal = new TIntDoubleHashMap();
		
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
			tVal.put(m.getId(), 0);
			probVal.put(m.getId(), 1);
		//	System.out.println(m.getId() + " : " + m.getValue());
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			if (m.isActivated(con)) {
				nodesToSort.remove(m);
				for (MultiProbabilityNode n : g.getNeighbors(m)){
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, n);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							if (e.getConcept() == con.getId()) {
								tVal.put(n.getId(), (tVal.get(n.getId()) + 1));
								probVal.put(n.getId(), (probVal.get(n.getId()) * e.getWeight()));
								double dVal = g.getNeighborCount(n);
								double tV = tVal.get(n.getId());
								n.setValue(dVal - 2*tV - ((dVal - tV)*probVal.get(n.getId())));
								//System.out.println("Neighbour : " + n.getId());
							}
						}
					}
				}
			}
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		for (MultiProbabilityNode m : nodesToSort) {
			sortedN.add(m);
		//	System.out.println("Adding " + m.getId());
		}
		
		System.out.println(nodesToSort.size());
		System.out.println(sortedN.size());
		
		while (seeds.size() < seedCount) {
			MultiProbabilityNode choice = sortedN.last();
			seeds.add(choice);
			nodesToSort.remove(choice);
	//		System.out.println("Adding " + choice.getId());
			for (MultiProbabilityNode n : g.getNeighbors(choice)){
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, n);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == con.getId()) {
							tVal.put(n.getId(), (tVal.get(n.getId()) + 1));
							probVal.put(n.getId(), (probVal.get(n.getId()) * e.getWeight()));
							double dVal = g.getNeighborCount(n);
							double tV = tVal.get(n.getId());
							n.setValue(dVal - 2*tV - ((dVal - tV)*probVal.get(n.getId())));
							//System.out.println("Neighbour : " + n.getId());
						}
					}
				}

			}
			
			sortedN.clear();
			
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			//	System.out.println(m.getId() + " : " + m.getValue());
			}
		}
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsLT
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept con) {
		
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		TIntDoubleHashMap tVal = new TIntDoubleHashMap();
		TIntDoubleHashMap probVal = new TIntDoubleHashMap();
		
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
			tVal.put(m.getId(), 0);
			probVal.put(m.getId(), 1);
		//	System.out.println(m.getId() + " : " + m.getValue());
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		for (MultiProbabilityNode m : nodesToSort) {
			sortedN.add(m);
		//	System.out.println("Adding " + m.getId());
		}
		//System.out.println(seedCount);
		while (seeds.size() < seedCount) {
			System.out.println("Seeds for DD: " + seeds.size());
			MultiProbabilityNode choice = sortedN.last();
			seeds.add(choice);
			nodesToSort.remove(choice);
			//System.out.println("Adding " + choice.getId());
			for (MultiProbabilityNode n : g.getNeighbors(choice)){
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, n);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == con.getId()) {
							tVal.put(n.getId(), (tVal.get(n.getId()) + 1));
							probVal.put(n.getId(), (probVal.get(n.getId()) * e.getWeight()));
							double dVal = g.getNeighborCount(n);
							double tV = tVal.get(n.getId());
							n.setValue(dVal - 2*tV - ((dVal - tV)*probVal.get(n.getId())));
							//System.out.println("Neighbour : " + n.getId());
						}
					}
				}

			}
			
			sortedN.clear();
			
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			//	System.out.println(m.getId() + " : " + m.getValue());
			}
		}
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsBoostDegDis(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyCon, Concept boostCon){
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> keySeeds = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode n : g.getVertices()) {
			if (n.isActivated(keyCon)){
				keySeeds.add(n);
			}
			
		}
		HashSet<MultiProbabilityNode> sortNodes = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : g.getVertices()) {
			double boostInfChance = 0.0;
			double keyInfChance = 0.0;
			double expectedKeyInf = 0.0;

			if (m.isActivated(keyCon)) {
				keyInfChance = 1;
			}
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)) {
				for (TypedWeightedEdge edge : g.findEdgeSet(neighbour, m)){
					
					if (edge.getConcept() == keyCon.getId() && neighbour.isActivated(keyCon) && m.isActivated(keyCon)) {
						keyInfChance = keyInfChance * (1 - edge.getWeight());
					}
					if (edge.getConcept() == boostCon.getId() && neighbour.isActivated(boostCon)) {
						if (boostInfChance == 0) {
							boostInfChance = 1- edge.getWeight();
						}
						else {
							boostInfChance = boostInfChance * (1 - edge.getWeight());
						}	
					}
					
				}
				for (TypedWeightedEdge edge : g.findEdgeSet(m, neighbour)){
					if (edge.getConcept() == keyCon.getId() && !neighbour.isActivated(keyCon)){
						if (expectedKeyInf == 0) {
							expectedKeyInf = edge.getWeight();
						}
						else {
							expectedKeyInf = expectedKeyInf * edge.getWeight();
						}
					}
				}
				
			}
			if (!m.isActivated(keyCon)) {keyInfChance = 1 - keyInfChance;}
			double val = boostInfChance * (keyInfChance * (1 + expectedKeyInf));
			m.setValue(val);
			sortNodes.add(m);
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		sortedN.addAll(sortNodes);
		
		while (seeds.size() < seedCount) {
			MultiProbabilityNode choice = sortedN.last();
			seeds.add(choice);
			sortNodes.remove(choice);
			
			for (MultiProbabilityNode n : g.getNeighbors(choice)){
				double boostInfChance = 0.0;
				double keyInfChance = 0.0;
				double expectedKeyInf = 0.0;

				if (n.isActivated(keyCon)) {
					keyInfChance = 1;
				}
				
				for (MultiProbabilityNode neighbour : g.getNeighbors(n)) {
					for (TypedWeightedEdge edge : g.findEdgeSet(neighbour, n)){
						
						if (edge.getConcept() == keyCon.getId() && neighbour.isActivated(keyCon) && n.isActivated(keyCon)) {
							keyInfChance = keyInfChance * (1 - edge.getWeight());
						}
						if (edge.getConcept() == boostCon.getId() && neighbour.isActivated(boostCon)) {
							if (boostInfChance == 0) {
								boostInfChance = 1 - edge.getWeight();
							}
							else {
								boostInfChance = boostInfChance * (1 - edge.getWeight());
							}	
						}
						
					}
					for (TypedWeightedEdge edge : g.findEdgeSet(n, neighbour)){
						if (edge.getConcept() == keyCon.getId() && !neighbour.isActivated(keyCon)){
							if (expectedKeyInf == 0) {
								expectedKeyInf = edge.getWeight();
							}
							else {
								expectedKeyInf = expectedKeyInf * edge.getWeight();
							}
						}
					}
					
					if (!n.isActivated(keyCon)) {keyInfChance = 1 - keyInfChance;}
					double val = boostInfChance * (keyInfChance * (1 + expectedKeyInf));
					n.setValue(val);
				}
			}
			
			
			sortedN.clear();
			sortedN.addAll(sortNodes);
		}
		
		return seeds;
	}
	
}
