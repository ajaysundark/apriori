package karup002;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by karup002 on 10/9/2016.
 * FrequentKItemSets = {key, itemSet}
 */
public class FrequentKItemSets extends GenericKItemSets {
    private static String[] itemNames = {"", "Beer", "Bread", "Cola", "Diapers", "Eggs", "Milk"};

    FrequentKItemSets(int k) {
        super(k);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<Integer, ItemSet>> it = baskets.entrySet().iterator();
        sb.append("{");

        while (it.hasNext()) {
            // foreach basket
            Map.Entry<Integer, ItemSet> basket = it.next();
            if (basket == null) continue;

            sb.append(basket.getValue());
            sb.append(',');
        }
        sb.setLength(sb.length()-1);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof FrequentKItemSets) {
            FrequentKItemSets other = (FrequentKItemSets) obj;
            return other.getBaskets().equals(this.getBaskets());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
