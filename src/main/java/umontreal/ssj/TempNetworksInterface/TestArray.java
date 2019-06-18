package umontreal.ssj.TempNetworksInterface;

import java.util.ArrayList;
//import umontreal.ssj.TempNetworks.LinkReliability;

import umontreal.ssj.TempNetworks.LinkBasic;
import umontreal.ssj.TempNetworks.LinkReliability;

public class TestArray {

	public static void main(String[] args) {
		ArrayList <LinkReliability> A = new ArrayList<LinkReliability>();
		ArrayList <LinkBasic> B = new ArrayList<LinkBasic>();
		LinkBasic c = new LinkBasic(0,0,1);
		LinkReliability d = new LinkReliability(1,0,1,0.2);
		//A.add(c);
		B.add(d);
		LinkReliability f = (LinkReliability) B.get(0) ;
		System.out.println(f.r);

	}

}
