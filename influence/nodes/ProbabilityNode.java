package influence.nodes;

public class ProbabilityNode extends Node {

	boolean activated;
	
	public ProbabilityNode(int id) {
		super(id);
		activated = false;
	}
	
	public boolean isActivated() {
		return activated;
	}
	
	public void activate(){
		activated = true;
	}
	
	public void deactivate() {
		activated = false;
	}
	
	

}
