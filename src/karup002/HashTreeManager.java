package karup002;

import java.util.*;

/**
 * Created by karup002 on 10/21/2016.
 */
public class HashTreeManager {
    private int hfrange;
    private KHashTree root; // tree control block -> for each K, we will have root stored here
    private static HashTreeManager treeManager = new HashTreeManager();

    public static HashTreeManager getInstance() {
        return treeManager;
    }

    private HashTreeManager() {}

    public int getHfrange() {
        return hfrange;
    }

    public void setHfrange(int hfrange) {
        this.hfrange = hfrange;
    }

    public boolean insertCkAtTree(int order, int itemSetKey, ItemSet itemSet) {
        KHashTree head, node = null;

        if (null==root) {
            root = new KHashTree(hfrange);
        }

        head=root;
        Iterator<Integer> itemItr = itemSet.getItems().iterator();
        Integer itemId;
        int pos;
        while (itemItr.hasNext()) {
            itemId = itemItr.next();
            pos = hashFunction(itemId);
            if( (node=head.getChildAtBranch(pos))==null ) {
                node = new KHashTree(hfrange);
                head.addChild(pos, node);
            }
            head=node;
        }

        if(null==node.getCkMapkeys()) {
            node.createCkMapPages(order);
        }

        node.addCkInstance(itemSetKey);
        return true;
    }

    public boolean updateCounts(FrequentKItemSets fk, Transaction transaction) {
        KHashTree node = null;
        Set<Integer> mapKeys;

        if (null==root)
            return false;

        /* generate subsets from each transaction */
        ItemSet itemSet = transaction.getItems();
        Integer[] itemArray = (Integer[]) itemSet.getItems().toArray(new Integer[itemSet.getNumberOfItems()]);;
        Long allMasks = (long) (1 << itemArray.length);
        for (long i = 0; i < allMasks; i++)
        { // I think better if we could use Long.compareUnsigned here?
            ItemSet sub = new ItemSet();
            for (int j = 0; j < itemArray.length; j++) {
                if ((i & (1 << j)) > 0) {
                    sub.addItem(itemArray[j]);
                }
            }
            node = (fk.getOrder()==sub.getNumberOfItems()) ? findNode(root, sub) : null;
            if (null!=node) {
                mapKeys = node.getCkMapkeys();
                if (null!=mapKeys)
                {
                    for (int key : mapKeys) {
                        ItemSet retrievedSet = fk.getBasket(key);
                        if(null!=retrievedSet && retrievedSet.equals(sub))
                            node.addCkInstance(key);
                    }
                }
            }
        }
        return true;
    }

    public int prune(FrequentKItemSets fk, float minSupport) {
        int order = fk.getOrder();
        int removeCount = 0;
        KHashTree head, node = null;
        Set<Integer> mapKeys;

        if (null==root) return 0;

        Set<Map.Entry<Integer, Object>> entries = fk.getBaskets().entrySet();
        Iterator entryIterator = entries.iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Integer, Object> entry = (Map.Entry<Integer, Object>) entryIterator.next();
            Integer fkKey = entry.getKey();
            ItemSet itemSet = (ItemSet) entry.getValue();

            head=root;
            Iterator<Integer> itemItr = itemSet.getItems().iterator();
            Integer itemId;
            int pos;
            while (itemItr.hasNext()) {
                itemId = itemItr.next();
                pos = hashFunction(itemId);
                if( (node=head.getChildAtBranch(pos))==null ) {
                    continue;
                }
                head=node;
            }

            if (null!=node) {
                mapKeys = node.getCkMapkeys();
                if (null!=mapKeys)
                {
                    for (Iterator<Integer> iterator = mapKeys.iterator();iterator.hasNext();) {
                        int key = iterator.next();
                        ItemSet retrievedSet = fk.getBasket(key);
                        if(null!=retrievedSet && retrievedSet.equals(itemSet))
                            if(node.getSupportCount(key)<minSupport) {
                                entryIterator.remove();
                                removeCount++;
                            }
                        iterator.remove(); // once we confirm the validity of Ck, we dont need the instance at tree anymore
                    }
                    if (mapKeys.size()==0) mapKeys=null; // set this null so gc could take care of this map instance
                }
            }
        }
        return removeCount;
    }

    public KHashTree findNode(KHashTree root, ItemSet itemSet) {
        KHashTree node = null;
        KHashTree head = root;
        int branch, itemId;
        Iterator<Integer> itemIterator = itemSet.getItems().iterator();
        while (itemIterator.hasNext() && null!=head) {
            itemId = itemIterator.next();
            branch = hashFunction(itemId);
            node = head.getChildAtBranch(branch);
            head = node;
        }
        return node;
    }

    private int hashFunction(int itemId) {
        // decide which branch
        return (itemId%hfrange);
    }
}
