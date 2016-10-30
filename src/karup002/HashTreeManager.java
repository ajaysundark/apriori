package karup002;

import java.util.*;

/**
 * Created by karup002 on 10/21/2016.
 */
public class HashTreeManager {
    private int hfrange;
    private KHashTree root;
    private static HashTreeManager treeManager = new HashTreeManager();
    private int maxLeafSize;

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

    public boolean insertCkAtTree(int itemSetKey, ItemSet itemSet) {
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
            node.createCkMapPages(maxLeafSize);
        }

        node.addCkInstance(itemSetKey);
        return true;
    }

    public void goDeep(KHashTree startNode, int startIdx, int endWidth, Integer[] itemArray,
                       ItemSet subset, FrequentKItemSets fk) {
        int nextW = endWidth+1;

        if (startNode==null) return;

        if (nextW==itemArray.length) {
            Set<Integer> mapKeys;
            mapKeys = startNode.getCkMapkeys();
            if (null!=mapKeys)
            {
                for (int key : mapKeys) {
                    ItemSet retrievedSet = fk.getBasket(key);
                    if(null!=retrievedSet && retrievedSet.equals(subset))
                        startNode.addCkInstance(key);
                }
            }
            return;
        }

        for (int i = startIdx+1; i <= nextW; i++) {
            KHashTree node = startNode.getChildAtBranch(hashFunction(itemArray[i]));
            if (null!=node) {
                subset.addItem(itemArray[i]);
                goDeep( node, i, nextW, itemArray, subset, fk);
                subset.removeLastItem();
            }
        }
    }

    public boolean updateCounts(FrequentKItemSets fk, Transaction transaction) {
        KHashTree node = null;
        Set<Integer> mapKeys;

        if (null==root)
            return false;

        /* generate subsets from each transaction */
        ItemSet itemSet = transaction.getItems();
        Integer[] itemArray = (Integer[]) itemSet.getItems().toArray(new Integer[itemSet.getNumberOfItems()]);

        int endIdx = (itemArray.length-fk.getOrder());
        ItemSet subset = new ItemSet();
        for( int startidx=0;startidx<=endIdx; ++startidx ) {
            node = root.getChildAtBranch(hashFunction(itemArray[startidx]));
            if (null!=node) {
                subset.addItem(itemArray[startidx]);
                goDeep( node, startidx, endIdx, itemArray, subset, fk);
                subset.removeLastItem();
            }
        }
        return true;
    }

    public int prune(FrequentKItemSets fk, float minSupport) {
        int removeCount = 0;
        KHashTree head, node = null;
        Set<Integer> mapKeys;
        FrequentItemStore store = FrequentItemStore.getInstance();
        Map<ItemSet,Integer> scMap = store.generateScMapforOrderK( fk.getNumberOfBaskets() );

        if (null==root) return 0;

        Set<Map.Entry<Integer, ItemSet>> entries = fk.getBaskets().entrySet();
        Iterator entryIterator = entries.iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Integer, ItemSet> entry = (Map.Entry<Integer, ItemSet>) entryIterator.next();
            ItemSet itemSet = entry.getValue();

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
                        if(null!=retrievedSet && retrievedSet.equals(itemSet)) {
                            int nodeSc = node.getSupportCount(key);
                            if (nodeSc < minSupport) {
                                entryIterator.remove();
                                removeCount++;
                            }
                            else {
                                scMap.put(itemSet, nodeSc);
                            }

                            iterator.remove(); // once we confirm the validity of Ck, we dont need the instance at tree anymore
                        }
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

    public void setMaxLeafSize(int maxLeafSize) {
        this.maxLeafSize = maxLeafSize;
    }
}
