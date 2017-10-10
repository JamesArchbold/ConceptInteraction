package MethodTesting;

import java.util.Arrays;

//import java.util.Random;
import influence.concepts.ConceptGenerator;

public class Trials {

	public static void main(String[] args) {
		//arg 0 = seed
		//arg 1 = graph
		//arg 2 = number of concepts
		//arg 3 = boost proportion
		//arg 4 = inhibit proprotion
		//arg 5 = key concept
		//arg 6 = probability
		//arg 7 = number of seeds
		//arg 8 = burn in
		//arg 9 = seed option
		//arg 10 = filepath
		//arg 11 = intervention
		//arg 12 = number of nodes
		
		//Random r = new Random();
		
//		ConceptGenerator cg = new ConceptGenerator(50, 0.3, 0.3, r.nextInt(10000));
//		cg.makeConcepts();
		
//		String[] argu = {"23", "sw", "10", "0.4", "0.0", "0","0.05","50","5","0", "output.txt", "200", "1000"};
//		fullTests(argu);
		
		String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
		if (args[0].contains("full")) {
			if (args[0].equals("full")){
				fullTests(newArgs);
			}
			else {
				String range = args[0].substring(args[0].length()-1);
				int g = Integer.parseInt(range);
				rangeTests(newArgs, g);
			}
		}
		else if (args[0].equals("setCon")){
			setConTests(newArgs);
		}
		else {
			System.err.println("INVALID TEST OPTION");
		}

	}
	
	public static void rangeTests(String[] args, int gRange) {
		ConceptGenerator cg = new ConceptGenerator(Integer.parseInt(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Integer.parseInt(args[0]));
		cg.makeConceptsGauss(gRange);
		
		if (args[1].equals("sf")) {
			Tests test = new Tests(cg.getConcepts(), cg.getConcepts().get(0));
			String filepath = args[10] + "_" + args[12] + "_ "+ args[13] + ".txt";
			test.burnInScaleFree(Integer.parseInt(args[14]), Integer.parseInt(args[2]),
					Double.parseDouble(args[3]), Double.parseDouble(args[4]),
					Double.parseDouble(args[6]), Integer.parseInt(args[7]),
					Integer.parseInt(args[8]), Integer.parseInt(args[9]), Integer.parseInt(args[0]), Integer.parseInt(args[11]), filepath, Integer.parseInt(args[12]), Integer.parseInt(args[13]), gRange);
		}
		else {
			Tests test = new Tests(cg.getConcepts(), cg.getConcepts().get(0));
			String filepath = args[10] + ".txt";
			test.burnInSmallWorld(Integer.parseInt(args[12]),Integer.parseInt(args[2]),
					Double.parseDouble(args[3]), Double.parseDouble(args[4]),
					Double.parseDouble(args[6]), Integer.parseInt(args[7]),
					Integer.parseInt(args[8]), Integer.parseInt(args[9]), Integer.parseInt(args[0]), Integer.parseInt(args[11]), filepath);
		}
	}
	
	public static void setConTests(String[] args) {
		ConceptGenerator cg = new ConceptGenerator(Integer.parseInt(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Integer.parseInt(args[0]));
		cg.makeConceptsSet();
		//cg.printList();
		
		if (args[1].equals("sf")) {
			Tests test = new Tests(cg.getConcepts(), cg.getConcepts().get(0));
			String filepath = args[10] + "_" + args[12] + "_ "+ args[13] + ".txt";
			test.burnInScaleFree(Integer.parseInt(args[14]), Integer.parseInt(args[2]),
					Double.parseDouble(args[3]), Double.parseDouble(args[4]),
					Double.parseDouble(args[6]), Integer.parseInt(args[7]),
					Integer.parseInt(args[8]), Integer.parseInt(args[9]), Integer.parseInt(args[0]), Integer.parseInt(args[11]), filepath, Integer.parseInt(args[12]), Integer.parseInt(args[13]), 0.0);
		}
		else {
			Tests test = new Tests(cg.getConcepts(), cg.getConcepts().get(0));
			String filepath = args[10] + ".txt";
			test.burnInSmallWorld(Integer.parseInt(args[12]),Integer.parseInt(args[2]),
					Double.parseDouble(args[3]), Double.parseDouble(args[4]),
					Double.parseDouble(args[6]), Integer.parseInt(args[7]),
					Integer.parseInt(args[8]), Integer.parseInt(args[9]), Integer.parseInt(args[0]), Integer.parseInt(args[11]), filepath);
		}

	}
	
	public static void fullTests(String[] args){
		ConceptGenerator cg = new ConceptGenerator(Integer.parseInt(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Integer.parseInt(args[0]));
		cg.makeConceptsGauss();
		//cg.printList();
		
		if (args[1].equals("sf")) {
			Tests test = new Tests(cg.getConcepts(), cg.getConcepts().get(0));
			String filepath = args[10] + "_" + args[12] + "_ "+ args[13] + ".txt";
			test.burnInScaleFree(Integer.parseInt(args[14]), Integer.parseInt(args[2]),
					Double.parseDouble(args[3]), Double.parseDouble(args[4]),
					Double.parseDouble(args[6]), Integer.parseInt(args[7]),
					Integer.parseInt(args[8]), Integer.parseInt(args[9]), Integer.parseInt(args[0]), Integer.parseInt(args[11]), filepath, Integer.parseInt(args[12]), Integer.parseInt(args[13]), 2.5);
		}
		else {
			Tests test = new Tests(cg.getConcepts(), cg.getConcepts().get(0));
			String filepath = args[10] + ".txt";
			test.burnInSmallWorld(Integer.parseInt(args[12]),Integer.parseInt(args[2]),
					Double.parseDouble(args[3]), Double.parseDouble(args[4]),
					Double.parseDouble(args[6]), Integer.parseInt(args[7]),
					Integer.parseInt(args[8]), Integer.parseInt(args[9]), Integer.parseInt(args[0]), Integer.parseInt(args[11]), filepath);
		}
	}

}
