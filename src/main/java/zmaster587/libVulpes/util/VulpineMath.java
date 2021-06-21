package zmaster587.libVulpes.util;

public class VulpineMath {
	public static double log2 = Math.log(2);
	
	public static double log2(double in) {return Math.log(in)/log2;}
	
	public static int greatestCommonFactor(int[] num) {
		int largestGCF = greatestCommonFactor(num[0], num[1]);
		
		for(int i = 2; i < num.length; i++){
			int gcf = greatestCommonFactor(num[i], largestGCF);
			if(largestGCF < gcf)
				largestGCF = gcf;
		}
		
		return largestGCF;
	}
	
	public static boolean isBetween(int number, int A, int B) {
		if(A > B) {
			int buffer = A;
			A = B;
			B = buffer;
		}
		return A <= number && number <= B;
	}
	
	public static boolean isBetween(double number, double A, double B) {
		return A <= number  && number <= B;
	}
	
	public static int greatestCommonFactor(int numA, int numB) {
		if(numB == 0) return numA;
		return greatestCommonFactor(numB, numA % numB);
	}
}
