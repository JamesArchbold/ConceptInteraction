package influence.graph.makers;

import java.util.ArrayList;

import influence.concepts.Concept;
import influence.nodes.MultiProbabilityNode;

import org.apache.commons.collections15.Factory;

public class MultiProbabilityNodeFactory implements Factory<MultiProbabilityNode> {
	private int count;
	private ArrayList<Concept> concepts;
	
	public MultiProbabilityNodeFactory(ArrayList<Concept> concepts) {
		count = 0;
		this.concepts = concepts;
	}
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public MultiProbabilityNode create() {
		return new MultiProbabilityNode(count++, concepts);
	}

}
