package dk.aau.cs.idq.others;

import java.util.*;

class hash {

    public Map<BitSet, Integer> map = null;

    public BitSet tranToBitSet(List<Integer> path) {

        BitSet bitSet = new BitSet();

        for (int i = 0; i < path.size(); i++) {
            bitSet.set(path.get(i));
        }

        return bitSet;
    }

    public int get(List<Integer> path) {
        BitSet bitSet = tranToBitSet(path);

        if (map.get(bitSet) == null) {
            return 0;
        }
        else {
            return map.get(bitSet);
        }
    }

    public void put(List<Integer> path, int v) {
        BitSet bitSet = tranToBitSet(path);

        if (map.get(bitSet) == null) {
            map.put(bitSet, v);
        }
        else {
            int t = map.get(bitSet);
            map.put(bitSet, v + t);
        }
    }

    public hash() {
        map = new HashMap<>();
    }
}
