package influence.edges;

public class WeightedEdge extends Edge {
	
	private double weight;
	
	public WeightedEdge(int id, double weight) {
		super(id);
		this.weight = weight;
	}

	public WeightedEdge(int id) {
		super(id);
		this.weight = Math.random();
	}
	
	public double getWeight() {
		return weight;
	}
}
