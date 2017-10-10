package influence.nodes;

public class ThresholdNode extends Node {

	double threshold;
	boolean activated;
	
	public ThresholdNode(int id, double threshold) {
		super(id);
		activated = false;
		this.threshold = threshold;
	}
	
	public ThresholdNode(int id) {
		super(id);
		activated = false;
		this.threshold = Math.random();
	}

	public double getThreshold() {
		return threshold;
	}
	
	public boolean isActivated() {
		return activated;
	}
	
	public void activate() {
		activated = true;
	}
	
	public void deactivate() {
		activated = false;
	}
	
}
