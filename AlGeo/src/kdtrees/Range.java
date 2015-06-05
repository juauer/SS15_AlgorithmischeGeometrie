package kdtrees;

import java.util.Vector;

public class Range {

    Vector<Tupel> values = new Vector<Tupel>();

    public Range(Tupel... list) {
        for (Tupel r : list) {
            this.values.add(r);
        }
    }

    public void removeValueAtDimension(int depth) {
        values.remove(depth % values.size());
     }

    @Override
    protected Object clone() {
        Range newRange = new Range();
        newRange.values = new Vector<Tupel>(this.values);
        return newRange;
    }
}
