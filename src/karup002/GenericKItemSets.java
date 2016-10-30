package karup002;

import java.util.*;

/**
 * Created by karup002 on 10/15/2016.
 *
 * Item = individual entity (of type Integer) in a transaction : eg. Bread
 * ItemSet = {Bread, Milk} <- represent a single basket (in e.g., it's of order k=2)
 * baskets = {itemSets}
 */
public abstract class GenericKItemSets {
    protected final int k; // order of ItemSets

    protected Map<Integer, ItemSet> baskets;

    GenericKItemSets(int k) {
        this.k = k;
        baskets = new LinkedHashMap<Integer, ItemSet>(k);
    }

    public int getOrder() {
        return k;
    }

    public int addBasket(Integer key, ItemSet value) {
        this.baskets.put(key, value);
        return this.baskets.size();
    }

    public ItemSet getBasket(Integer key) {
        return baskets.get(key);
    }

    public int removeBasket(Integer key) {
        this.baskets.remove(key);
        return this.baskets.size();
    }

    public Map<Integer, ItemSet> getBaskets() {
        return baskets;
    }

    public int getNumberOfBaskets() {
        return baskets.size();
    }
}
