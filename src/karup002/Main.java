package karup002;

/**
 * java Main minsup minconf inputfile outputfile hfrange maxleafsize
 *              0     1        2         3           4       5
 **/

import java.io.FileNotFoundException;

public class Main {
        private static TransactionManager manager;

        public static void main(String[] args) {
        if (args.length!=6) {
            System.err.println("Incorrect usage; Usage : java Main minsup minconf inputfile outputfile hfrange maxleafsize ");
            return;
        }
        try {
            manager = TransactionManager
                    .getInstance()
                    .registerInputFile(args[2])
                    .createTransactionsTable()
                    .registerMinSupport(Float.parseFloat(args[0]));

            HashTreeManager.getInstance().setHfrange(Integer.parseInt(args[4]));
            HashTreeManager.getInstance().setMaxLeafSize(Integer.parseInt(args[5]));

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
                    + ", minsupport : " + args[0] + ", hfrange : " + args[4] + ", and maxleafsize : " + args[5]);
            }

            if(FrequentItemStore.getInstance().isReady()) {
                RuleManager rulemgr = RuleManager.getInstance();
                rulemgr.setMinConf(Float.parseFloat(args[1]));
                rulemgr.setOutFileName(args[3]);
                float start = System.nanoTime();
                rulemgr.genConfidenceAndPrune();
                float end = System.nanoTime();
                System.out.println("Time taken for rule generation : " + (end-start)/1000000000 + "(s) for minconf : " + args[1]);
            }
        } catch (FileNotFoundException | NumberFormatException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
    }
}
