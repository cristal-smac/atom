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


public class FortuneWheel implements java.io.Serializable {

    private String[] choices;
    private double[] probabilities;

    /** Ce constructeur repose sur une repartition equi-probable.
     *  Deux formes de parametrage pour ce constructeur:
     *  - ["a", "b", "c"] trois choix equiprobables
     *  - ["a", "0.3", "b", "0.4", "c", "0.3"] probabilites intercalees
     *    entre les differents choix
     *  Provient de la difficulte de creer de vrai listes en Java ou
     *     alors on termine sur new Object[] mais il faut passer des types
     *     wrapper, ce qui alourdit encore l'ecriture !
     *  autre possibilite "un 0.5 deux 0.3 trois 0.2" et on parse une chaÃ®ne,
     *  ce qui eviterait d'avoir a  allouer un tableau ...
     *
     * @param choices la liste des choix possibles, chaque item aura 1/choix
     * chance d'etre tire.
     */
    public FortuneWheel(String[] choices) {
        boolean onlyKeys = false;
        double sum = 0.0;
        try {
            Double.parseDouble(choices[choices.length-1]);
        } catch (NumberFormatException nbe) {
            onlyKeys = true;
        }
        if (onlyKeys) {
            this.choices = choices;
            this.probabilities = new double[choices.length];
            for (int i=0; i < choices.length; i++) {
                this.probabilities[i] = 1.0f/((float) choices.length);
            }
        } else { // We have an alternance of key, probability
            this.choices = new String[choices.length/2];
            this.probabilities = new double[choices.length/2];
            for (int i=0; i <= choices.length/2+1; i=i+2) {
                // System.out.println(i+" "+choices[i]+" "+choices[i+1]);
                this.choices[i/2] = choices[i];
                this.probabilities[i/2] = Double.parseDouble(choices[i+1]);
                sum += this.probabilities[i/2];
            }
            if ((sum -1.0) > 0.01) {
                throw new RuntimeException("Wheel incomplete: "+sum);
            }
        }
    }

    public String roll() {
        double value = (double) fr.cristal.smac.atom.Random.nextDouble();
        // System.out.print(value+" ");
        double sum = 0.0f;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (value < sum) {
                return choices[i];
            }
        }
        return choices[choices.length-1];
    }

    public String toString() {
        StringBuffer bf = new StringBuffer("[");
        for (int i = 0; i < probabilities.length; i++) {
            bf.append("("+choices[i]+","+probabilities[i]+")");
        }
        bf.append("]");
        return bf.toString();
    }

    public static void main(String[] args) {
        // First we try a wheel with equiprobable choices
        FortuneWheel fw = new FortuneWheel(new String[]{"un", "deux", "trois"});
        System.out.println(fw);
        for (int i=0; i<5; i++) {
            System.out.println(i+" => "+fw.roll()+" ");
        }
        // Second test, we try a wheel with several ponderations
        fw = new FortuneWheel(new String[]{"un", "0.2", "deux", "0.3", "trois", "0.5"});
        System.out.println(fw);
        for (int i=0; i<5; i++) {
            System.out.println(i+" => "+fw.roll()+" ");
        }
    }
}

