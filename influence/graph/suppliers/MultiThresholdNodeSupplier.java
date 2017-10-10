package influence.graph.suppliers;

import influence.nodes.MultiThresholdNode;

import com.google.common.base.Supplier;

public class MultiThresholdNodeSupplier implements Supplier<MultiThresholdNode> {
	private int count = 0;
	private int concepts = 0;
	
	public MultiThresholdNodeSupplier(int concepts) {
		count = 0;
		this.concepts = concepts;
	}
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public MultiThresholdNode get() {
		return new MultiThresholdNode(count++, concepts);
	}

}