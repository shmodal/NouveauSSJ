package umontreal.ssj.TempNetworksInterface;

import java.util.ArrayList;

import umontreal.ssj.TempNetworks.LinkBasic;
import umontreal.ssj.TempNetworks.LinkReliability;

public class reliabilityTest implements basicTest {
	
	 private ArrayList<LinkReliability> links;

	@Override
	public ArrayList<LinkBasic> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<?> getLinks2() {
		// TODO Auto-generated method stub
		return links;
	}

}
