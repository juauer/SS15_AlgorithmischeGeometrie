package geometry.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BFPRT {
    public static <T extends Comparable<T>> T bfprt(List<T> list, int k) {
        if(list.isEmpty())
            return null;

        int size_list = list.size();

        if(size_list == 1)
            return list.get(k);

        int size_medians = size_list / 5 + (size_list % 5 == 0 ? 0 : 1);
        ArrayList<T> medians = new ArrayList<T>(size_medians);
        ArrayList<T> l = new ArrayList<T>(5);
        int i = 0;

        for(T t : list) {
            l.add(t);
            ++i;

            if(i == size_list || i % 5 == 0) {
                Collections.sort(l);
                medians.add(l.get(l.size() / 2));
                l.clear();
            }
        }

        T median = bfprt(medians, size_medians / 2);
        LinkedList<T> list_left = new LinkedList<T>();
        LinkedList<T> list_mid = new LinkedList<T>();
        LinkedList<T> list_right = new LinkedList<T>();

        for(T t : list)
            if(t.compareTo(median) < 0)
                list_left.add(t);
            else if(t.compareTo(median) == 0)
                list_mid.add(t);
            else
                list_right.add(t);

        int size_left = list_left.size();
        int size_mid = list_mid.size();

        if(k < size_left)
            return bfprt(list_left, k);
        else if(k < size_left + size_mid)
            return bfprt(list_mid, k - size_left);
        else
            return bfprt(list_right, k - size_left - size_mid);
    }

    public static <T extends Comparable<T>> T median(List<T> list) {
        return bfprt(list, list.size() / 2);
    }
}
