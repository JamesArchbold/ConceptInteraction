package influence.graph.suppliers;

import influence.edges.Edge;

import com.google.common.base.Supplier;

public class EdgeSupplier implements Supplier<Edge> {

	private int count;
	
	public EdgeSupplier() {
		count = 0;
	}
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public Edge get() {
		return new Edge(count++);
	}

}
