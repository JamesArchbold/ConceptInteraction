package influence.graph.makers;

import influence.nodes.ProbabilityNode;

import org.apache.commons.collections15.Factory;

public class ProbabilityNodeFactory implements Factory<ProbabilityNode> {
	
	private int count;
	
	public ProbabilityNodeFactory(){
		count = 0;
	}	
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public ProbabilityNode create() {
		return new ProbabilityNode(count++);
	}

}
