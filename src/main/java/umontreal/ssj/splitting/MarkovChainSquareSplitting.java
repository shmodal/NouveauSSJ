package umontreal.ssj.splitting;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

/* MarkovChain used for implementation of the GS Simple bivariate uniform example
 */


public class MarkovChainSquareSplitting extends MarkovChainWithImportance {
	double y1;
	double y2;

	public MarkovChainSquareSplitting(double y1,double y2) {
		super();
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public double getPerformance() {
		return 0;
	}

	//@Override
	public void nextStep(RandomStream stream, double gamma) {
		// TODO Auto-generated method stub
		stream.resetNextSubstream();
		double a = stream.nextDouble();
		if (a <0.5) { // on resample y1 en premier
			this.y1 = sampleY(stream,this.y2,gamma);
			this.y2 = sampleY(stream,this.y1,gamma);		
		}
		else {
			this.y2 = sampleY(stream,this.y1,gamma);
			this.y1 = sampleY(stream,this.y2,gamma);
		}
	}
	
	// sample y1 en fonction de y2,  y1 en fonction de y2 par ex
	public double sampleY(RandomStream stream, double y, double gamm) {
		if (y>gamm) {
			return stream.nextDouble();
		}
		else {
			double newB = gamm + (1-gamm)*stream.nextDouble();
			return newB;
		}
		
		
	}
	

	//@Override
	public double getImportance() {
		return Math.max(y1,y2);
	}

	
	// A MODIFER. Choix des streams ?independance.
	//@Override
	public void initialState() {
		RandomStream stream = new LFSR113();
		y1 = stream.nextDouble();
		stream = new MRG31k3p();
		y2 = stream.nextDouble();
		// TODO Auto-generated method stub
		
	}

	//@Override
//	public void nextStep(RandomStream stream) {
//		// TODO Auto-generated method stub		
//	}
}
