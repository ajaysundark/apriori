package karup002;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by karup002 on 10/9/2016.
 *
 * Apriori algorithm
 */
public class Apriori {
    private static TransactionManager manager;
    private static boolean isInitialized = false;
    private FrequentKItemSets order1FreqSet;

    public boolean initialize() {
        manager = TransactionManager.getInstance();
        if (manager.isFirstOrderSetAvailable()) {
            Map<Integer, Integer> order1FreqMap = manager.getFirstOrderFrequentItemsMap();
            if (order1FreqMap != null && (order1FreqMap.size()>0)) {
                order1FreqSet = new FrequentKItemSets(1); // create first order frequentItemSet

                /* convert map of {itemId, Integers} to map of {itemId, Items}
                    I dunno why I am doing this! probably to maintain uniformity~!? */

                for (Integer itemId : order1FreqMap.keySet()) {
                    order1FreqSet.addBasket(itemId, new ItemSet(itemId));
                }
                isInitialized = true;
            }
        }

        return isInitialized;
    }

    public boolean generateFreqItemSets() {
        FrequentItemStore store = FrequentItemStore.getInstance();
        int orderK=1;
        int noOfTransactions = manager.getNumberOfTransactions();
        if (noOfTransactions>0) {
            FrequentKItemSets frequentKItemSets;
            FrequentKItemSets frequentKminus1ItemSets = order1FreqSet;
            store.addFktoCollection(order1FreqSet);

            while (true) {
                ++orderK;
                if (frequentKminus1ItemSets==null || frequentKminus1ItemSets.getNumberOfBaskets() == 0)
                    break;

                frequentKItemSets = new FrequentKItemSets(orderK);
                /* Candidate itemset generation & insertion at hashtree */
                aprioriGen(frequentKminus1ItemSets, frequentKItemSets);
                /* prune Ck and get Fk */
                manager.supportBasedPruning(frequentKItemSets);
                if (frequentKItemSets.getNumberOfBaskets()>0) {
                    System.out.println(frequentKItemSets);
                    System.out.println("------------------------------------------\n");
                }

                store.addFktoCollection(frequentKItemSets);
                frequentKminus1ItemSets = frequentKItemSets;
            } // end Fk generation
            TransactionManager.reset(); // we dont need you anymore!
        } // end check noOfTransactions
        return true;
    }

    private void aprioriGen(FrequentKItemSets fkminus1, FrequentKItemSets Fk) {
        int k = Fk.getOrder();

        HashTreeManager hashTreeManager = HashTreeManager.getInstance();

        if (k==2) {
             /* extend F1 to F2 : (Fk-1 - F1) method, with k being 1, so F1 - F1 method! :D
             *
             * we will be using here order1FreqMap instead of Set as it would be easier
             * for comparing each ItemSet to its consecutive itemSets based on ordering;
             * working with Set would have added pain of treating two iterators
             * */

            int key = 0;
            TreeMap<Integer, Integer> order1FreqMap = (TreeMap<Integer, Integer>) manager.getFirstOrderFrequentItemsMap();
            Integer[] indexArr = (Integer[]) order1FreqMap.keySet().toArray(new Integer[order1FreqMap.size()]); // This key set would be ordered in ascending order

            for (int i = 0; i < indexArr.length; i++) {
                for (int j = i+1; j < indexArr.length; j++) {
                    ItemSet itemSet = new ItemSet(indexArr[i]); // any frequent jthItem would be lexicographically greater than frequent ithItem
                    itemSet.addItem(indexArr[j]);

                    /* okay, so now we have created a 2ndOrderItemSet which is a candidateItemSet
                    *   push it to CkHashTree and Fk
                    * */

                    // Dont forget to add Ck = {key, supportCount} at HashTree & to Fk = {key, itemSet}
                    hashTreeManager.insertCkAtTree(k, key, itemSet);
                    Fk.addBasket(key, itemSet);
                    ++key; // maintain uniqueness of the key
                }
            }

            manager.markOrder1MapForDeletion();
        }
        else {
            /* (Fk-1 - Fk-1) method; merge candidates which have identical k-2 items */
            int key = 0;
            int orderKminus1 = fkminus1.getOrder();
            int mergePrefix = orderKminus1 - 1;
            Integer[] indexArr = (Integer[]) fkminus1.getBaskets().keySet().toArray(new Integer[fkminus1.getNumberOfBaskets()]); // This key set would maintain insertion order
            ItemSet ithItemSet, jthItemSet;

            /* fkminus1 has a series of itemSets and its keys (foreach itemSet), we need to create
             a similar structure for fk, with itemSets being set of keys!

             ;;

             remember that FrequentKItemSets.baskets is a LinkedHashMap and we are inserting
             in a groups together order, which is maintained. So, it is safe to assume that
             if we encounter a 'mismatch' in prefixes once, we do not need to check the entries after it.

             */
            for (int i = 0; i < indexArr.length; i++) {
                ithItemSet = fkminus1.getBasket(indexArr[i]);
                for (int j = i+1; j < indexArr.length; j++) {
                    jthItemSet = fkminus1.getBasket(indexArr[j]);
                    if (ithItemSet.equalUptoN(jthItemSet, mergePrefix)) {
                        ItemSet itemSet = new ItemSet(ithItemSet);
                        itemSet.addItem(jthItemSet.getItems().last());

                        /* then purge Ck based on infrequent subsets */
                        if(!hasInfreqSubsets(fkminus1, itemSet, orderKminus1)) {
                            hashTreeManager.insertCkAtTree(k, key, itemSet);
                            Fk.addBasket(key, itemSet);
                            ++key;
                        }
                    }
                    else break;
                }
            }
        }
    }

    private boolean hasInfreqSubsets(FrequentKItemSets fkminus1, ItemSet itemSet, int checkLen) {
        // before we tag a itemSet as frequent, check if all its (k-1)
        Integer[] itemArray = (Integer[]) itemSet.getItems().toArray(new Integer[itemSet.getNumberOfItems()]);

        /* Soln 1: using bloomfilter
        // if (<itemSet>subset).toStrings are present in fkminus1 bloomfilter
        StringBuilder sb = new StringBuilder(5*checkLen); // assume maximum itemId width be xxxx so, for each item say 8979, it needs 5 chars including ','


        for (int idx = checkLen; idx >= 0; idx--) {
            // mimic Set.toString()
            sb.append('[');
            for (int idy = 0; idy < idx; idy++) {
                sb.append(itemArray[idy]);
                sb.append(", ");
            }
            for (int idy = idx+1; idy <= checkLen; idy++) {
                sb.append(itemArray[idy]);
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append(']');

            if (fkminus1.definitelyNotInBasket(sb.toString()))
                return true; // do not add this superset - we found a subset not present in its Fkminus1 frequent list

            sb.setLength(0);
        }
        */

        /* Soln 2 : using containsValue */
        ItemSet subset = new ItemSet(checkLen);
        for (int idx = checkLen; idx >= 0 ; idx--) {
            for (int idy = 0; idy < idx; idy++) {
                subset.addItem(itemArray[idy]);
            }
            for (int idy = idx+1; idy <= checkLen; idy++) {
                subset.addItem(itemArray[idy]);
            }

            if (!fkminus1.getBaskets().containsValue(subset))
                return true;

            subset.getItems().clear();
        }

        return false;
    }



    public FrequentKItemSets getOrder1FreqSet() {
        return order1FreqSet;
    }
}
