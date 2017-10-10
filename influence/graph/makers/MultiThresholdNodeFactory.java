package influence.graph.makers;

import influence.nodes.MultiThresholdNode;

import org.apache.commons.collections15.Factory;

public class MultiThresholdNodeFactory implements Factory<MultiThresholdNode> {
	private int count = 0;
	private int concepts = 0;
	
	public MultiThresholdNodeFactory(int concepts) {
		count = 0;
		this.concepts = concepts;
	}
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public MultiThresholdNode create() {
		return new MultiThresholdNode(count++, concepts);
	}

}
