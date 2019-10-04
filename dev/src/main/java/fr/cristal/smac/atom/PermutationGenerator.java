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

//--------------------------------------
// Systematically generate permutations.
// recopie de : http://www.merriampark.com/perm.htm
//--------------------------------------
import java.math.BigInteger;

public class PermutationGenerator
{

    private int[] a;
    private BigInteger numLeft;
    private BigInteger total;

    //-----------------------------------------------------------
    // Constructor. WARNING: Don't make n too large.
    // Recall that the number of permutations is n!
    // which can be very large, even when n is as small as 20 --
    // 20! = 2,432,902,008,176,640,000 and
    // 21! is too big to fit into a Java long, which is
    // why we use BigInteger instead.
    //----------------------------------------------------------
    public PermutationGenerator(int n)
    {
        if (n < 1)
            throw new IllegalArgumentException("Min 1");
        a = new int[n];
        total = getFactorial(n);
        reset();
    }

    //------
    // Reset
    //------
    public void reset()
    {
        for (int i = 0; i < a.length; i++)
            a[i] = i;
        numLeft = new BigInteger(total.toString());
    }

    //------------------------------------------------
    // Return number of permutations not yet generated
    //------------------------------------------------
    public BigInteger getNumLeft()
    {
        return numLeft;
    }

    //------------------------------------
    // Return total number of permutations
    //------------------------------------
    public BigInteger getTotal()
    {
        return total;
    }

    //-----------------------------
    // Are there more permutations?
    //-----------------------------
    public boolean hasMore()
    {
        return numLeft.compareTo(BigInteger.ZERO) == 1;
    }

    //------------------
    // Compute factorial
    //------------------
    private static BigInteger getFactorial(int n)
    {
        BigInteger fact = BigInteger.ONE;
        for (int i = n; i > 1; i--)
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        return fact;
    }

    //--------------------------------------------------------
    // Generate next permutation (algorithm from Rosen p. 284)
    //--------------------------------------------------------
    public int[] getNext()
    {

        if (numLeft.equals(total))
        {
            numLeft = numLeft.subtract(BigInteger.ONE);
            return a;
        }

        int temp;

        // Find largest index j with a[j] < a[j+1]

        int j = a.length - 2;
        while (a[j] > a[j + 1])
        {
            j--;
        }

        // Find index k such that a[k] is smallest integer
        // greater than a[j] to the right of a[j]

        int k = a.length - 1;
        while (a[j] > a[k])
        {
            k--;
        }

        // Interchange a[j] and a[k]

        temp = a[k];
        a[k] = a[j];
        a[j] = temp;

        // Put tail end of permutation after jth position in increasing order

        int r = a.length - 1;
        int s = j + 1;

        while (r > s)
        {
            temp = a[s];
            a[s] = a[r];
            a[r] = temp;
            r--;
            s++;
        }

        numLeft = numLeft.subtract(BigInteger.ONE);
        return a;

    }

    public static void main(String args[])
    {
        /*
         * TEST BASIC
         * Cette classe n'effecture pas proprement dit les permutations, mais
         * renvoie à chaque appel de getNext() un tableau d'indices des éléments
         * à aller chercher. A la création on passe donc juste le nombre d'objets
         * (la taille du tableau qui contient les éléments) et chaque appel
         * de getNext() donne le tablmeau des indices à utiliser.
         */

        int[] indices;
        String[] elements = {"a", "b", "c", "d"};
        PermutationGenerator x = new PermutationGenerator(elements.length);
        StringBuffer permutation;
        while (x.hasMore())
        {
            permutation = new StringBuffer();
            indices = x.getNext();
            for (int i = 0; i < indices.length; i++)
                permutation.append(elements[indices[i]]);
            System.out.println(permutation.toString());
        }
    }
}
