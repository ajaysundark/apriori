package karup002;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karup002 on 10/24/2016.
 * Collection of all FrequentItemSets
 */
public class FrequentItemStore {
    private static FrequentItemStore ourInstance = new FrequentItemStore();
    private Map<Integer,FrequentKItemSets> frequentItemSetCollection;

    public static FrequentItemStore getInstance() {
        return ourInstance;
    }

    private FrequentItemStore() {
        frequentItemSetCollection = new HashMap<>();
    }

    public void addFktoCollection(FrequentKItemSets Fk) {
        this.frequentItemSetCollection.put(Fk.getOrder(), Fk);
    }
}
