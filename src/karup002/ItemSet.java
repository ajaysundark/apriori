package karup002;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by karup002 on 10/9/2016.
 *
 * ItemSet = TreeSet{itemIds} - this could represent a single transaction of Items
 * eg. {Bread, Milk, Diapers}
 */
public class ItemSet implements Comparable {
    /*
    * items is a treeSet because we want the items to be ordered
    * for convenience during freqItemSetGeneration
    * */

    private TreeSet<Integer> items;

    public ItemSet(Integer item) {
        items = new TreeSet<Integer>();
        addItem(item);
    }

    public ItemSet(ItemSet itemSet) {
        items = new TreeSet<Integer>((Collection<? extends Integer>) itemSet.getItems());
    }

    public ItemSet() {
        items = new TreeSet<Integer>();
    }

    public int addItem(Integer item) {
        items.add(item);
        return (items.size());
    }

    public int removeLastItem() {
        return items.pollLast();
    }

    public int removeItem(Integer item) {
        items.remove(item);
        return (items.size());
    }

    public TreeSet<Integer> getItems() {
        return items;
    }

    public int getNumberOfItems() {
        return items.size();
    }
    
    @Override
    public String toString() {
        return this.items.toString();
    }
    
    @Override
    public int compareTo(Object o) {
        /* we assume that we get non-null and valid ItemSet instances always */
        ItemSet other = (ItemSet) o;
        int thisK = this.getNumberOfItems();
        int otherK = other.getNumberOfItems();

        /* this method should ideally be used to compare only two itemSets with same order(k) only */
        if (thisK < otherK) return -1;
        else if (thisK > otherK) return 1;
        else {
            int diff = 0;
            Iterator<Integer> thisItr = this.items.iterator();
            Iterator<Integer> otherItr = other.getItems().iterator();

            while (thisItr.hasNext()) {
                diff = thisItr.next() - otherItr.next();
                if (diff != 0) return diff;
            }
        }

        return 0;
    }

    public boolean equalUptoN(ItemSet other, int N) {
        if ((other==null) || this.getNumberOfItems()<N)
            return false;

        int i = 0;
        Iterator<Integer> thisItr = this.items.iterator();
        Iterator<Integer> otherItr = other.items.iterator();
        while (thisItr.hasNext() && otherItr.hasNext() && i<N) {
            if (thisItr.next()!=otherItr.next()) return false;
            ++i;
        }
        return true;
    }

    public boolean copyFirstNtoOther(ItemSet other, int N) {
        if ((other==null) || this.getNumberOfItems()<N)
            return false;

        int i = 0;
        Iterator<Integer> thisItr = this.items.iterator();
        while (thisItr.hasNext() && i<N) {
            other.addItem(thisItr.next());
            ++i;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if (!(obj instanceof ItemSet)) return false;

        ItemSet otherIS = (ItemSet) obj;
        if (this.getNumberOfItems()!=otherIS.getNumberOfItems()) return false;

        return (this.items.equals(otherIS.items));
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}

