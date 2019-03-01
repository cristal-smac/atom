/********************************************************** 
ATOM : ArTificial Open Market

Author  : P Mathieu, O Brandouy, Y Secq, Univ Lille, France
Email   : philippe.mathieu@univ-lille.fr
Address : Philippe MATHIEU, CRISTAL, UMR CNRS 9189, 
          Lille  University
          59655 Villeneuve d'Ascq Cedex, france
Date    : 14/12/2008

***********************************************************/


package fr.cristal.smac.atom;


public class Random
{
	private static java.util.Random gen = new java.util.Random();

	private Random()
	{
	}

	public static void setSeed(int seed)
	{
		gen = new java.util.Random(seed);
	}

	public static int nextInt(int n)
	{
		return gen.nextInt(n);
	}

	public static double nextDouble()
	{
		return gen.nextDouble();
	}

	//Gaussian value between 0 and 1 , always positive
        // Remember that the classical method have mean: 0.0, std: 1.0 thus nearly between -5 and +5
	public static double nextGaussian()
	{
		double x = (gen.nextGaussian()+5)/10;
		if (x<0) return 0; else return x;
	}

	public static double nextGaussian(double mean, double std)
        {
            // A CLARIFIER .... ce ne semble pas correct
            double r;
            do {r = gen.nextGaussian()*std+mean;}
            while (r<=mean-std || r>=mean+std) ;
            return r;
        }

	public static void main(String args[])
	{
		int tab[] = new int[101];
		for (int i=1;i<=500000;i++)
//			tab[(int)(Random.nextGaussian()*100)]++;
			tab[(int)(Random.nextGaussian(50 ,10))]++;
		for (int i=0;i<tab.length;i++)
			System.out.println(i+";"+tab[i]);
	}
}

