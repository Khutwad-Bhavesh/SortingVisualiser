package sortingvisualizer.algorithms;

import sortingvisualizer.core.AbstractSorter;
import sortingvisualizer.core.SortContext;
import javax.swing.JPanel;

/**
 * Merge Sort — O(n log n) time, O(n) space.
 * Divide-and-conquer: splits array in half recursively, then merges.
 */
public class MergeSorter extends AbstractSorter {

    public MergeSorter(SortContext ctx, JPanel canvas) {
        super(ctx, canvas);
    }

    @Override
    public String getName() { return "Merge Sort"; }

    @Override
    public void sort() throws InterruptedException {
        mergeSort(0, ctx.arr.length - 1);
    }

    private void mergeSort(int l, int r) throws InterruptedException {
        if (l >= r || !ctx.sorting) return;
        int m = (l + r) / 2;
        mergeSort(l, m);
        mergeSort(m + 1, r);
        merge(l, m, r);
    }

    private void merge(int l, int m, int r) throws InterruptedException {
        int[] tmp = new int[r - l + 1];
        int i = l, j = m + 1, k = 0;

        while (i <= m && j <= r && ctx.sorting) {
            ctx.comparisons++;
            if (ctx.descending ? ctx.arr[i] >= ctx.arr[j] : ctx.arr[i] <= ctx.arr[j]) tmp[k++] = ctx.arr[i++];
            else                           tmp[k++] = ctx.arr[j++];
        }
        while (i <= m) tmp[k++] = ctx.arr[i++];
        while (j <= r)  tmp[k++] = ctx.arr[j++];

        for (int x = 0; x < tmp.length && ctx.sorting; x++) {
            ctx.arr[l + x]        = tmp[x];
            ctx.colorState[l + x] = 1;
            ctx.swaps++;
            repaintAndSleep();
            ctx.colorState[l + x] = 0;
        }
    }
}
