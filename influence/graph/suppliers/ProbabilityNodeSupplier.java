package influence.graph.suppliers;

import influence.nodes.ProbabilityNode;

import com.google.common.base.Supplier;

public class ProbabilityNodeSupplier implements Supplier<ProbabilityNode> {
	
	private int count;
	
	public ProbabilityNodeSupplier(){
		count = 0;
	}	
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public ProbabilityNode get() {
		return new ProbabilityNode(count++);
	}

}