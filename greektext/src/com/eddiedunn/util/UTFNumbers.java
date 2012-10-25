package com.eddiedunn.util;

import java.util.HashMap;
import java.util.Map;

public class UTFNumbers {

	public Map<Integer, String> convertUTF8toPhoneticGreek; 
	public Map<String, Integer> convertPhoneticGreekToUTF8;
	public Map<Integer, Integer> convertFromSymbolFontToUTF8Greek;
	
	public UTFNumbers(){
		convertUTF8toPhoneticGreek = new HashMap<Integer, String>();
		convertPhoneticGreekToUTF8  = new HashMap<String,Integer>();
		convertFromSymbolFontToUTF8Greek = new HashMap<Integer,Integer>();
		populateconvertToPhoneticGreek();
		populateconvertFromPhoneticGreek();
		populateConvertFromSymbolFontToUTF8Greek();
		
		
	}
	public Map<Integer, Integer> getEG(){
		return convertFromSymbolFontToUTF8Greek;
	}
	private void populateConvertFromSymbolFontToUTF8Greek(){
		// start with Capital Letters
		// A  gose to big alpha
		convertFromSymbolFontToUTF8Greek.put(65,913);
		// B goes to big beta
		convertFromSymbolFontToUTF8Greek.put(66,914);
		// E goes to big epsilon
		convertFromSymbolFontToUTF8Greek.put(69,917);
		// G goes to big gamma
		convertFromSymbolFontToUTF8Greek.put(71,915);
		// M goes to Big Mu
		convertFromSymbolFontToUTF8Greek.put(77,924);
		// V goes to final sigma
		convertFromSymbolFontToUTF8Greek.put(86,962);
		// Z goes to big Zeta
		convertFromSymbolFontToUTF8Greek.put(90,918);

		// next do small letters
		// a goes to alpha
		convertFromSymbolFontToUTF8Greek.put(97,945);
		// b goes to beta
		convertFromSymbolFontToUTF8Greek.put(98,946);
		// c goes to chi
		convertFromSymbolFontToUTF8Greek.put(99,967);
		// d goes to delta
		convertFromSymbolFontToUTF8Greek.put(100,948);
		// e goes to epsilon
		convertFromSymbolFontToUTF8Greek.put(101,949);
		// f goes to phi
		convertFromSymbolFontToUTF8Greek.put(102,966);
		// g goes to gamma
		convertFromSymbolFontToUTF8Greek.put(103,947);
		// h goes to eta
		convertFromSymbolFontToUTF8Greek.put(104,951);
		// i goes to iota
		convertFromSymbolFontToUTF8Greek.put(105,953);
		// k goes to kappa
		convertFromSymbolFontToUTF8Greek.put(107,954);
		// l goes to lamda
		convertFromSymbolFontToUTF8Greek.put(108,955);
		// m goes to mu
		convertFromSymbolFontToUTF8Greek.put(109,956);
		// n goes to nu
		convertFromSymbolFontToUTF8Greek.put(110,957);
		// o goes to omicron
		convertFromSymbolFontToUTF8Greek.put(111,959);
		// p goes to pi
		convertFromSymbolFontToUTF8Greek.put(112,960);
		// q goes to theta
		convertFromSymbolFontToUTF8Greek.put(113,952);
		// r goes to rho
		convertFromSymbolFontToUTF8Greek.put(114,961);
		// s goes to sigma
		convertFromSymbolFontToUTF8Greek.put(115,963);
		// t goes to tau
		convertFromSymbolFontToUTF8Greek.put(116,964);
		// u goes to upsilon
		convertFromSymbolFontToUTF8Greek.put(117,965);
		// w goes to omega
		convertFromSymbolFontToUTF8Greek.put(119,969);
		// x goes to xi
		convertFromSymbolFontToUTF8Greek.put(120,958);
		// y goes to psi
		convertFromSymbolFontToUTF8Greek.put(121,968);
		// z goes to zeta
		convertFromSymbolFontToUTF8Greek.put(122,950);
	}
	private void populateconvertFromPhoneticGreek(){
		convertPhoneticGreekToUTF8.put( "alpha",945);
		convertPhoneticGreekToUTF8.put( "beta",946);
		convertPhoneticGreekToUTF8.put( "gamma",947);
		convertPhoneticGreekToUTF8.put( "delta",948);
		convertPhoneticGreekToUTF8.put( "epsilon",949);
		convertPhoneticGreekToUTF8.put( "zeta",950);
		convertPhoneticGreekToUTF8.put( "eta",951);
		convertPhoneticGreekToUTF8.put( "theta",952);		
		convertPhoneticGreekToUTF8.put( "iota",953);
		convertPhoneticGreekToUTF8.put( "kappa",954);
		convertPhoneticGreekToUTF8.put( "lamda",955);
		convertPhoneticGreekToUTF8.put( "mu",956);
		convertPhoneticGreekToUTF8.put( "nu",957);
		convertPhoneticGreekToUTF8.put( "xi",958);
		convertPhoneticGreekToUTF8.put( "omicron",959);
		convertPhoneticGreekToUTF8.put( "pi",960);
		convertPhoneticGreekToUTF8.put( "rho",961);
		convertPhoneticGreekToUTF8.put("finalsigma",962);
		convertPhoneticGreekToUTF8.put("sigma",963);
		convertPhoneticGreekToUTF8.put( "tau",964);
		convertPhoneticGreekToUTF8.put( "upsilon",965);
		convertPhoneticGreekToUTF8.put( "phi",966);
		convertPhoneticGreekToUTF8.put( "chi",967);
		convertPhoneticGreekToUTF8.put( "psi",968);		
		convertPhoneticGreekToUTF8.put( "omega",969);
		
		convertPhoneticGreekToUTF8.put( "BigAlpha",913);
		convertPhoneticGreekToUTF8.put( "BigBeta",914);
		convertPhoneticGreekToUTF8.put( "BigGamma",915);
		convertPhoneticGreekToUTF8.put( "BigDelta",916);
		convertPhoneticGreekToUTF8.put( "BigEpsilon",917);
		convertPhoneticGreekToUTF8.put( "BigZeta",918);
		convertPhoneticGreekToUTF8.put( "BigEta",919);
		convertPhoneticGreekToUTF8.put( "BigTheta",920);		
		convertPhoneticGreekToUTF8.put( "BigIota",921);
		convertPhoneticGreekToUTF8.put( "BigKappa",922);
		convertPhoneticGreekToUTF8.put( "BigLamda",932);
		convertPhoneticGreekToUTF8.put( "BigMu",924);
		convertPhoneticGreekToUTF8.put( "BigNu",925);
		convertPhoneticGreekToUTF8.put( "BigXi",926);
		convertPhoneticGreekToUTF8.put( "BigOmicron",927);
		convertPhoneticGreekToUTF8.put( "BigPi",928);
		convertPhoneticGreekToUTF8.put( "BigRho",929);
		convertPhoneticGreekToUTF8.put( "BigSigma",931);
		convertPhoneticGreekToUTF8.put( "BigTau",932);
		convertPhoneticGreekToUTF8.put( "BigUpsilon",933);
		convertPhoneticGreekToUTF8.put( "BigPhi",934);
		convertPhoneticGreekToUTF8.put( "BigChi",935);
		convertPhoneticGreekToUTF8.put( "BigPsi",936);		
		convertPhoneticGreekToUTF8.put( "BigOmega",937);				
	}
	private void populateconvertToPhoneticGreek(){
		convertUTF8toPhoneticGreek.put(945, "alpha");
		convertUTF8toPhoneticGreek.put(946, "beta");
		convertUTF8toPhoneticGreek.put(947, "gamma");
		convertUTF8toPhoneticGreek.put(948, "delta");
		convertUTF8toPhoneticGreek.put(949, "epsilon");
		convertUTF8toPhoneticGreek.put(950, "zeta");
		convertUTF8toPhoneticGreek.put(951, "eta");
		convertUTF8toPhoneticGreek.put(952, "theta");		
		convertUTF8toPhoneticGreek.put(953, "iota");
		convertUTF8toPhoneticGreek.put(954, "kappa");
		convertUTF8toPhoneticGreek.put(955, "lamda");
		convertUTF8toPhoneticGreek.put(956, "mu");
		convertUTF8toPhoneticGreek.put(957, "nu");
		convertUTF8toPhoneticGreek.put(958, "xi");
		convertUTF8toPhoneticGreek.put(959, "omicron");
		convertUTF8toPhoneticGreek.put(960, "pi");
		convertUTF8toPhoneticGreek.put(961, "rho");
		convertUTF8toPhoneticGreek.put(962, "finalsigma");
		convertUTF8toPhoneticGreek.put(963, "sigma");
		convertUTF8toPhoneticGreek.put(964, "tau");
		convertUTF8toPhoneticGreek.put(965, "upsilon");
		convertUTF8toPhoneticGreek.put(966, "phi");
		convertUTF8toPhoneticGreek.put(967, "chi");
		convertUTF8toPhoneticGreek.put(968, "psi");		
		convertUTF8toPhoneticGreek.put(969, "omega");
		
		convertUTF8toPhoneticGreek.put(913, "BigAlpha");
		convertUTF8toPhoneticGreek.put(914, "BigBeta");
		convertUTF8toPhoneticGreek.put(915, "BigGamma");
		convertUTF8toPhoneticGreek.put(916, "BigDelta");
		convertUTF8toPhoneticGreek.put(917, "BigEpsilon");
		convertUTF8toPhoneticGreek.put(918, "BigZeta");
		convertUTF8toPhoneticGreek.put(919, "BigEta");
		convertUTF8toPhoneticGreek.put(920, "BigTheta");		
		convertUTF8toPhoneticGreek.put(921, "BigIota");
		convertUTF8toPhoneticGreek.put(922, "BigKappa");
		convertUTF8toPhoneticGreek.put(923, "BigLamda");
		convertUTF8toPhoneticGreek.put(924, "BigMu");
		convertUTF8toPhoneticGreek.put(925, "BigNu");
		convertUTF8toPhoneticGreek.put(926, "BigXi");
		convertUTF8toPhoneticGreek.put(927, "BigOmicron");
		convertUTF8toPhoneticGreek.put(928, "BigPi");
		convertUTF8toPhoneticGreek.put(929, "BigRho");
		convertUTF8toPhoneticGreek.put(931, "BigSigma");
		convertUTF8toPhoneticGreek.put(932, "BigTau");
		convertUTF8toPhoneticGreek.put(933, "BigUpsilon");
		convertUTF8toPhoneticGreek.put(934, "BigPhi");
		convertUTF8toPhoneticGreek.put(935, "BigChi");
		convertUTF8toPhoneticGreek.put(936, "BigPsi");		
		convertUTF8toPhoneticGreek.put(937, "BigOmega");			
	}
}
