package karup002;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karup002 on 10/24/2016.
 * Collection of all FrequentItemSets
 */
public class FrequentItemStore {
    private static FrequentItemStore ourInstance = new FrequentItemStore();
    private List<Map<ItemSet,Integer>> scb;
    private Map<Integer,FrequentKItemSets> frequentItemSetCollection; /* k -> {Fk} */
    private boolean ready;

    public int getMaxorder() {
        return maxorder;
    }

    private int maxorder;

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public static FrequentItemStore getInstance() {
        return ourInstance;
    }

    private FrequentItemStore() {
        frequentItemSetCollection = new HashMap<>();
        scb = new ArrayList<>();
        generateScMapforOrderK(2); // keep dummy for zero matching
        maxorder = 0;
    }

    public Map<ItemSet, Integer> generateScMapforOrderK(int initSize) {
        Map<ItemSet, Integer> scmap = new HashMap<>(initSize/2);
        this.scb.add(scmap);
        return scmap;
    }

    public void attachScMapforOrder1(Map<ItemSet,Integer> firstOrderMap) {
        if (firstOrderMap!=null)
            this.scb.add(firstOrderMap);
    }


    public void addFktoCollection(FrequentKItemSets Fk) {
        this.frequentItemSetCollection.put(Fk.getOrder(), Fk);
        if (Fk.getOrder()>maxorder) maxorder = Fk.getOrder();
    }


    public FrequentKItemSets getFrequentItemSetFromCollection(int order) {
        return this.frequentItemSetCollection.get(order);
    }

    public int getSupportCountForItemSet(ItemSet itemSet) {
        int sc = 0;
        Map<ItemSet,Integer> listentry = scb.get(itemSet.getNumberOfItems());
        if(null!=listentry.get(itemSet)) sc = listentry.get(itemSet);
        return sc;
    }

    public void pruneItemSet(ItemSet itemSet) {
        // we can't remove from collection with reverse lookup, so rather we set support count as zero
        Map<ItemSet,Integer> listentry = scb.get(itemSet.getNumberOfItems());
        listentry.put(itemSet, 0);
    }
}
