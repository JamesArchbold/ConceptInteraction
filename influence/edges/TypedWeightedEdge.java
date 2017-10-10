package influence.edges;

import influence.nodes.MultiProbabilityNode;

public class TypedWeightedEdge extends Edge {

	private int concept;
	private double weight;
	
	public TypedWeightedEdge(int id, int concept, double weight) {
		super(id);
		this.concept = concept;
		this.weight = weight;
	}

	public TypedWeightedEdge(int id, int concept) {
		super(id);
		this.concept = concept;
		this.weight = Math.random();
	}
	
	public int getConcept() {
		return concept;
	}
	
	public double getWeight(){
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
}
