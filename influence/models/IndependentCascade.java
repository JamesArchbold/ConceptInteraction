package influence.models;

import gnu.trove.list.array.TIntArrayList;
import influence.edges.Edge;
import influence.nodes.Node;
import influence.nodes.ProbabilityNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class IndependentCascade extends AbstractModel {

	private double chanceOfInfection;
	private TIntArrayList sizes;
	
	public IndependentCascade(double i) {
		chanceOfInfection = i;
		sizes = new TIntArrayList();
	}

	@Override
	public <E extends Node> void run(Graph<E, Edge> g, Set<E> seeds) {
		Collection<E> neighbours;
		
		HashSet<E> activeThisRound = new HashSet<E>();
		HashSet<E> activated = new HashSet<E>();
		HashSet<E> toActivate = new HashSet<E>();
		
		boolean converged = false;
		
		for (E s : seeds){
			((ProbabilityNode)s).activate();
		}
		
		activated.addAll(seeds);
		activeThisRound.addAll(seeds);
		while (!converged) {
			
			converged = true;
			for (E s : activeThisRound) {
				neighbours = g.getNeighbors(s);
				//System.out.println("Neighbour size is " + neighbours.size() + " for node " + s.getId());
				for (E n : neighbours) {
					ProbabilityNode currNode = (ProbabilityNode) n;
					if (!currNode.isActivated()) {
						double rr = Math.random();
						if (rr < chanceOfInfection) {
						//	System.out.println("Node activated is: " + currNode.getId() + " neighbour of " + s.getId());
							currNode.activate();
							toActivate.add(n);
							converged = false;
						}
					}
				}
			}

			activated.addAll(activeThisRound);
			activeThisRound.clear();
			activeThisRound.addAll(toActivate);
			toActivate.clear();
		}
	
		System.out.println("Final size: " + activated.size());
		sizes.add(activated.size());
		
		if (sizes.size() == 1000) {
			System.out.println("Average: " + (sizes.sum()/1000.0));
		}
	
	}


}
