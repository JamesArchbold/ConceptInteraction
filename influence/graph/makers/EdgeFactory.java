package influence.graph.makers;

import influence.edges.Edge;

import org.apache.commons.collections15.Factory;

public class EdgeFactory implements Factory<Edge> {

	private int count;
	
	public EdgeFactory() {
		count = 0;
	}
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public Edge create() {
		return new Edge(count++);
	}

}
