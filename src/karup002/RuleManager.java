package karup002;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by karup002 on 10/24/2016.
 * Singleton to manage rule generation
 */
public class RuleManager {
    private static RuleManager ourInstance = new RuleManager();
    private String outFileName;

    public float getMinConf() {
        return minConf;
    }

    public void setMinConf(float minConf) {
        this.minConf = minConf;
    }

    private float minConf;

    public static RuleManager getInstance() {
        return ourInstance;
    }

    private RuleManager() {
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    public void genConfidenceAndPrune() {
        FrequentItemStore store = FrequentItemStore.getInstance();
        int currentOrder = store.getMaxorder();
        float minSup = TransactionManager.getInstance().getMinSupport();
        try {
            PrintWriter pw = new PrintWriter(new File(outFileName));
            StringBuilder sb = new StringBuilder();
            while (currentOrder>1) {
                FrequentKItemSets fk = store.getFrequentItemSetFromCollection(currentOrder);
                for (Iterator<Map.Entry<Integer,ItemSet>> entryItr = fk.getBaskets().entrySet().iterator();
                        entryItr.hasNext();) {
                    float confidence = 0;
                    ItemSet superSet = entryItr.next().getValue();
                    ItemSet consequent = new ItemSet();
                    Integer[] itemArray = (Integer[]) superSet.getItems().toArray(new Integer[superSet.getNumberOfItems()]);
                    for (int i = 0; i < itemArray.length; i++) {
                        consequent.addItem(itemArray[i]);
                        ItemSet antecedent = new ItemSet();
                        for (int j = i+1; j < itemArray.length; j++) {
                            antecedent.addItem(itemArray[j]);
                        }


                        if (antecedent.getNumberOfItems()==0 || consequent.getNumberOfItems()==0) continue;

                        int confNr = store.getSupportCountForItemSet(antecedent);
                        int confDr = store.getSupportCountForItemSet(consequent);

                        if (confDr==0 || confNr==0) continue;

                        confidence = (float) confNr/confDr;
                        if (confidence<minConf) {
                            entryItr.remove();
                            Integer[] antecedentArr = (Integer[]) antecedent.getItems().toArray(new Integer[antecedent.getNumberOfItems()]);
                            // prune all of the antecedent subsets as they will also be infrequent rules
                            Long allMasks = (long) (1 << antecedentArr.length);
                            for (long l = 1; l < allMasks; l++)
                            {
                                ItemSet sub = new ItemSet();
                                for (int j = 0; j < antecedentArr.length; j++) {
                                    if ((l & (1 << j)) > 0) {
                                        sub.addItem(antecedentArr[j]);
                                    }
                                }
                                store.pruneItemSet(sub);
                                sub = null;
                            }
                        }
                        else {
                            sb.append(antecedent);
                            sb.append('|');
                            sb.append(consequent);
                            sb.append('|');
                            sb.append(minSup);
                            sb.append('|');
                            sb.append(confidence);
                            pw.println(sb.toString());
                            sb.setLength(0);
                        }

                        consequent.removeLastItem();
                        antecedent = null;
                    }
                }
                currentOrder--;
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
