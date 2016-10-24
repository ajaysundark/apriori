package karup002;

/**
 * java hcrminer minsup minconf inputfile outputfile hfrange maxleafsize
 *                  0     1        2         3           4       5
 **/

import java.io.FileNotFoundException;

public class hcrminer {
        private static TransactionManager manager;

        public static void main(String[] args) {
        if (args.length!=6) {
            System.err.println("Incorrect usage; Usage : java hcrminer minsup minconf inputfile outputfile hfrange maxleafsize ");
            return;
        }
        try {
            manager = TransactionManager
                    .getInstance()
                    .registerInputFile(args[2])
                    .createTransactionsTable()
                    .registerMinSupport(Float.parseFloat(args[0]));

            HashTreeManager.getInstance().setHfrange(Integer.parseInt(args[4]));
            Apriori apriori = new Apriori();
            if (apriori.initialize()) {
                FrequentKItemSets order1Set = apriori.getOrder1FreqSet();
                if (null != order1Set) {
                    System.out.println(order1Set);
                }
                float start = System.nanoTime();
                 apriori.generateFreqItemSets();
                float end = System.nanoTime();
                System.out.println("Time taken for freq generation : " + (end-start)/1000000000 + "(s) for input file : " + args[2]
                    + ", minsupport : " + args[0] + ", hfrange : " + args[4]);
            }
        } catch (FileNotFoundException | NumberFormatException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
    }
}
