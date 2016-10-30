package karup002;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by karup002 on 10/8/2016.
 * A Singleton-Manager for managing all the transaction data
 */
public class TransactionManager {
    private String fileInput;
    private static TransactionManager instance;
    private Map<Integer,Transaction> transactionTable; // <transId, transaction={items}>
    private Map<Integer, Integer> order1FreqMap; // itemId, frequency {can be thought of C1, CandidateKItemSets(k=1), after pruning}
    private float minSupport;
    private boolean minSupportRegistered;
    private boolean freqSetAvailable;
    private long noOfPurchases;

    private TransactionManager() {
        transactionTable = new HashMap<Integer, Transaction>();
        order1FreqMap = new TreeMap<Integer,Integer>();
    }

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public TransactionManager registerInputFile(String fileInput) throws FileNotFoundException {
        if (fileInput == null) {
            throw new FileNotFoundException("Input File unspecified");
        }
        else
            this.fileInput = fileInput;

        return instance;
    }

    public TransactionManager registerMinSupport(float minSup) {
        this.minSupport = minSup;
        minSupportRegistered = true;
        churn();
        return instance;
    }

    public TransactionManager createTransactionsTable() throws FileNotFoundException, RuntimeException {
        int transId, itemId, itemFrequency;
        if (fileInput == null)
            throw new RuntimeException("Input File unspecified");

        Scanner scanner = new Scanner(new File(fileInput));
        while (scanner.hasNextLine()) {
            Transaction transaction = null;
            if (!scanner.hasNext()) {
                break;
            }
            transId = scanner.nextInt();
            itemId = scanner.nextInt();
            itemFrequency = 0; // represent the frequency for each item present over all transactions

            ++noOfPurchases; // this would be the number of lines read?

            if (order1FreqMap.containsKey(itemId)) {
                itemFrequency = order1FreqMap.get(itemId);
            }

            itemFrequency++;
            order1FreqMap.put(itemId, itemFrequency);

            if(transactionTable.containsKey(transId)) {
                transaction = transactionTable.get(transId);
            } else {
                transaction = new Transaction(transId);
            }
            transaction.addItemToTransaction(itemId);

            transactionTable.put(transId, transaction);
        }
        scanner.close();
        return instance;
    }

    public int getNumberOfTransactions() {
        int count = 0;
        if (transactionTable != null) {
            count = transactionTable.size();
        }
        return count;
    }

    public Map<Integer, Integer> getFirstOrderFrequentItemsMap() {
        if (!minSupportRegistered) {
            return null;
        } else {
            return order1FreqMap;
        }
    }

    public void supportBasedPruning(FrequentKItemSets frequentKItemSets) {
        int removed = 0, tot = frequentKItemSets.getNumberOfBaskets();
        HashTreeManager htmanager = HashTreeManager.getInstance();

        for (Transaction t : transactionTable.values()) {
            if (t.getItems().getNumberOfItems()>=frequentKItemSets.getOrder()) {
                htmanager.updateCounts(frequentKItemSets, t);
            }
        }

        removed = htmanager.prune(frequentKItemSets, minSupport);
        System.out.println("Total items removed from order " + frequentKItemSets.getOrder() + " = " + removed + "/" + tot);
        System.out.println("Remaining entries in order " + frequentKItemSets.getOrder() + " : " + frequentKItemSets.getNumberOfBaskets());
    }

    private void churn() {
        int removed = 0, tot = order1FreqMap.size();
        for (Iterator<Map.Entry<Integer, Integer>> it = order1FreqMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Integer> entry = it.next();
            if (entry.getValue()<minSupport) {
                it.remove();
                ++removed;
            }
        }

        System.out.println("Total entries = " + noOfPurchases);
        System.out.println("Total no of transactions : " + transactionTable.size());
        for (Transaction t : transactionTable.values()) {
            System.out.println(t);
        }

        System.out.println("Total items removed = " + removed + "/" + tot);
        System.out.println("Remaining entries in order 1 : " + order1FreqMap.size());
        freqSetAvailable = true;
    }

    public boolean isFirstOrderSetAvailable() {
        return freqSetAvailable;
    }

    public float getMinSupport() {
        if (minSupportRegistered) {
            return minSupport;
        }
        else return -1;
    }

    public void markOrder1MapForDeletion() {
        if (freqSetAvailable) {
            this.freqSetAvailable = false;
            order1FreqMap.clear();
        }
    }

    public void resetTable() {
        if (instance!=null) {
            instance.transactionTable.clear();
            instance.transactionTable = null;
        }
    }

    public static void reset() {
        if (instance!=null) {
            instance.markOrder1MapForDeletion();
            instance.resetTable();
        }
        instance = null;
    }
}
