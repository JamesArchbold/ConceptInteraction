package influence.graph.makers;

import influence.edges.WeightedEdge;

import org.apache.commons.collections15.Factory;

public class WeightedEdgeFactory implements Factory<WeightedEdge> {
	private int count;
	
	public WeightedEdgeFactory(){
		count = 0;
	}
	
	public void reset(){
		count = 0;
	}
	
	@Override
	public WeightedEdge create() {
		return new WeightedEdge(count++);
	}
	

}
