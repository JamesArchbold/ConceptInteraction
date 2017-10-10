package influence.graph.suppliers;

import influence.nodes.ThresholdNode;

import com.google.common.base.Supplier;

public class ThresholdNodeSupplier implements Supplier<ThresholdNode> {
	private int count;
	
	public ThresholdNodeSupplier(){
		count = 0;
	}
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public ThresholdNode get() {
		return new ThresholdNode(count++);
	}

}