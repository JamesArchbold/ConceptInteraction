package influence.nodes;

public class MultiThresholdNode extends Node {

	double[] thresholds;
	boolean[] activated;
	
	public MultiThresholdNode(int id, int concepts, double[] thresholds) {
		super(id);
		
		activated = new boolean[concepts];
		this.thresholds = new double[concepts];
		
		for (int i = 0; i < concepts; i++){
			activated[i] = false;
			this.thresholds[i] = thresholds[i];
		}
		
	}
	
	public MultiThresholdNode(int id, int concepts) {
		super(id);
		activated = new boolean[concepts];
		this.thresholds = new double[concepts];
		
		for (int i = 0; i < concepts; i++){
			activated[i] = false;
			this.thresholds[i] = Math.random();
		}
	}

	public double getThreshold(int index) {
		return thresholds[index];
	}
	
	public boolean isActivated(int index) {
		return activated[index];
	}
	
	public void activate(int index) {
		activated[index] = true;
	}
	
	public void deactivate(int index) {
		activated[index] = false;
	}
	
}
