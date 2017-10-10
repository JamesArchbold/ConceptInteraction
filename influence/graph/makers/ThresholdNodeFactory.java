package influence.graph.makers;

import influence.nodes.ThresholdNode;

import org.apache.commons.collections15.Factory;

public class ThresholdNodeFactory implements Factory<ThresholdNode> {
	private int count;
	
	public ThresholdNodeFactory(){
		count = 0;
	}
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public ThresholdNode create() {
		return new ThresholdNode(count++);
	}

}
