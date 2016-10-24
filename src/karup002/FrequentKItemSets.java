package karup002;

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.hash.*;

/**
 * Created by karup002 on 10/9/2016.
 * FrequentKItemSets = {key, itemSet}
 */
public class FrequentKItemSets extends GenericKItemSets {

    // guava bloom
    private BloomFilter<CharSequence> blm = null;
    private boolean _isBlmReady = false;

    private static String[] itemNames = {"", "Beer", "Bread", "Cola", "Diapers", "Eggs", "Milk"};

    FrequentKItemSets(int k) {
        super(k);
    }

    @Override
    public int addBasket(Integer key, Object value) {
        assert (value instanceof ItemSet);
        return super.addBasket(key,value);
    }

    public ItemSet getBasket(Integer key) {
        return (ItemSet)super.baskets.get(key);
    }

    public boolean definitelyNotInBasket(ItemSet itemSet) {
        return definitelyNotInBasket(itemSet.toString());
    }

    public boolean definitelyNotInBasket(String itemSetAsString) {
        if (!_isBlmReady) {
            blm = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8) , super.getNumberOfBaskets() );
            for (Object basketEntry : baskets.values()) {
                blm.put( ((ItemSet)basketEntry).toString() );
            }
            _isBlmReady = true;
        }

        return (!blm.mightContain(itemSetAsString));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<Integer, Object>> it = baskets.entrySet().iterator();
        sb.append("Frequent ItemSet of order " + k +": \n" + " size : " + baskets.size() + "\n\t{\n");

        while (it.hasNext()) {
            // foreach basket
            Map.Entry<Integer, Object> basket = it.next();
            if (basket == null) continue;

            sb.append("\t\t{ ");
            for (Integer item : ((ItemSet)basket.getValue()).getItems()) {
                sb.append(itemNames[item] + ", ");
            }

            sb.setLength(sb.length()-2);
            sb.append(" }\n");
        }

        sb.append("\t}");
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
}
