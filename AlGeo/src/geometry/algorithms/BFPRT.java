package geometry.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class BFPRT {
    public static <T> T bfprt(List<T> list, int k, Comparator<T> comparator) {
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
                Collections.sort(l, comparator);
                medians.add(l.get(l.size() / 2));
                l.clear();
            }
        }

        T median = bfprt(medians, size_medians / 2, comparator);
        LinkedList<T> list_left = new LinkedList<T>();
        LinkedList<T> list_mid = new LinkedList<T>();
        LinkedList<T> list_right = new LinkedList<T>();

        for(T t : list)
            if(comparator.compare(t, median) < 0)
                list_left.add(t);
            else if(comparator.compare(t, median) == 0)
                list_mid.add(t);
            else
                list_right.add(t);

        int size_left = list_left.size();
        int size_mid = list_mid.size();

        if(k < size_left)
            return bfprt(list_left, k, comparator);
        else if(k < size_left + size_mid)
            return bfprt(list_mid, k - size_left, comparator);
        else
            return bfprt(list_right, k - size_left - size_mid, comparator);
    }

    public static <T extends Comparable<T>> T bfprt(List<T> list, int k) {
        return bfprt(list, k, new Comparator<T>() {

            @Override
            public int compare(T t1, T t2) {
                return t1.compareTo(t2);
            }
        });
    }

    public static <T> T median(List<T> list, Comparator<T> comparator) {
        return bfprt(list, list.size() / 2, comparator);
    }

    public static <T extends Comparable<T>> T median(List<T> list) {
        return bfprt(list, list.size() / 2);
    }
}
