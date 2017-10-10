package influence.graph.suppliers;

import java.util.ArrayList;

import influence.concepts.Concept;
import influence.nodes.MultiProbabilityNode;

import com.google.common.base.Supplier;

public class MultiProbabilityNodeSupplier implements Supplier<MultiProbabilityNode> {
	private int count;
	private ArrayList<Concept> concepts;
	
	public MultiProbabilityNodeSupplier(ArrayList<Concept> concepts) {
		count = 0;
		this.concepts = concepts;
	}
	
	public void reset() {
		count = 0;
	}
	
	@Override
	public MultiProbabilityNode get() {
		return new MultiProbabilityNode(count++, concepts);
	}

}
