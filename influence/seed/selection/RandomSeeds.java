package influence.seed.selection;

import influence.concepts.Concept;
import influence.edges.Edge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import edu.uci.ics.jung.graph.Graph;

public class RandomSeeds {
	private Random r;
	
	public RandomSeeds() {
		r = new Random();
	}
	
	public RandomSeeds(int s) {
		r = new Random(s);
	}
	
	public <E extends Node, T extends Edge> HashSet<E> getSeeds
	(Graph<E, T> g, int seedCount) {
		HashSet<E> seeds = new HashSet<E>();
		ArrayList<E> nodes = new ArrayList<E>();
		
		for (E m : g.getVertices()) {
			nodes.add(m);
		}
		
	//	Random r = new Random();
		
		while (seeds.size() < seedCount) {
			seeds.add(nodes.get(r.nextInt(nodes.size())));
		}
		
		return seeds;
		
	}
	
	public <T extends Edge> HashSet<MultiProbabilityNode> getSeedsBurnIn
	(Graph<MultiProbabilityNode, T> g, int seedCount, Concept con) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		ArrayList<MultiProbabilityNode> nodes = new ArrayList<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : g.getVertices()) {
			nodes.add(m);
		}
		
	//	Random r = new Random();
		
		while (seeds.size() < seedCount) {
			MultiProbabilityNode choice = nodes.get(r.nextInt(nodes.size())); 
			if (!choice.isActivated(con)){
				seeds.add(choice);
			}
		}
		
		return seeds;
		
	}
}
