package influence.graph.suppliers;

import influence.edges.WeightedEdge;

import com.google.common.base.Supplier;

public class WeightedEdgeSupplier implements Supplier<WeightedEdge> {
	private int count;
	
	public WeightedEdgeSupplier(){
		count = 0;
	}
	
	public void reset(){
		count = 0;
	}

	@Override
	public WeightedEdge get() {
		return new WeightedEdge(count++);
	}
	

}
