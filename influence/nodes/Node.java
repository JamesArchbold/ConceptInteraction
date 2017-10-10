package influence.nodes;

import influence.exceptions.NotSameTypeException;

public class Node {
	protected int id;
	private double value;
	public Node(int id) {
		this.id = id;
		this.value = 0;
	}
	
	public int getId(){
		return id;
	}

	public void setValue(double i) {
		value = i;
	}
	
	public double getValue() {
		return value;
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Node) {
			Node n = (Node) obj;
			if (n.id == this.id) {
				return true;
			}
			else {
				return false;
			}
		}
		
		throw new NotSameTypeException("Object is not a Node!");
	}
}
