package karup002;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by karup002 on 10/21/2016.
 *
 *
 */
public class KHashTree {
    private Map<Integer, Integer> CkMap; // {key, supportCount}
    private KHashTree child[];

    // at the example, hfrange = 3, so the branching was apparent as L,M,R
    // so we need to create an array<KHashTree> of size hfrange

    KHashTree(int branchFactor /*, int maxleafsize */) {
        child = new KHashTree[branchFactor]; // create a node with potential to have "branchFactor" no of branches
    }

    public void createCkMapPages(int order) {
        CkMap = new HashMap<>(order);
    }

    public int addCkInstance(int key) {
        int frequency = 0;
        if (CkMap.containsKey(key)) {
            frequency = CkMap.get(key);
            frequency++;
            CkMap.put(key, frequency);
        }
        else {
            CkMap.put(key, frequency);
        }
        return frequency;
    }

    public void addChild(int position, KHashTree childNode) {
        this.child[position] = childNode;
    }

    /* public void removeCk(int key) {
        if (null!=CkMap) CkMap.remove(key);
    } */

    public KHashTree getChildAtBranch(int branch /* or position*/) {
        return child[branch];
    }

    public Set<Integer> getCkMapkeys() {
        if (null==CkMap) return null;
        else return CkMap.keySet();
    }

    public Integer getSupportCount(int key) {
        if (null==CkMap) return Integer.valueOf(0); /* just avoiding a possible NPE */
        else return CkMap.get(key);
    }
}
