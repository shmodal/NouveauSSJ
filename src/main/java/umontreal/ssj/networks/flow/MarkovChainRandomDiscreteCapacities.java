package umontreal.ssj.networks.flow;

import java.util.HashMap;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.splitting.MarkovChainWithImportance;

public class MarkovChainRandomDiscreteCapacities extends MarkovChainWithImportance {
	
	protected double[] valuesY;
	protected GraphFlow father; //
	public HashMap <Double,int[]> indices;

	@Override
	public void nextStep(RandomStream stream, double gamma) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getImportance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPerformance() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	// FONCTION DE CLONAGE, cloner correctmeent le tableau Y
}
