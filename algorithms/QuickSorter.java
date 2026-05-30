package sortingvisualizer.algorithms;

import sortingvisualizer.core.AbstractSorter;
import sortingvisualizer.core.SortContext;
import javax.swing.JPanel;

/**
 * Quick Sort — O(n log n) avg, O(n²) worst, O(log n) space.
 * Picks a pivot, partitions around it, then recurses on each side.
 */
public class QuickSorter extends AbstractSorter {

    public QuickSorter(SortContext ctx, JPanel canvas) {
        super(ctx, canvas);
    }

    @Override
    public String getName() { return "Quick Sort"; }

    @Override
    public void sort() throws InterruptedException {
        quickSort(0, ctx.arr.length - 1);
    }

    private void quickSort(int l, int r) throws InterruptedException {
        if (l >= r || !ctx.sorting) return;
        int p = partition(l, r);
        markSorted(p);
        quickSort(l, p - 1);
        quickSort(p + 1, r);
    }

    private int partition(int l, int r) throws InterruptedException {
        int pivot = ctx.arr[r];
        markPivot(r);
        int i = l - 1;
        for (int j = l; j < r && ctx.sorting; j++) {
            markCompare(j, r);
            if (ctx.descending ? ctx.arr[j] >= pivot : ctx.arr[j] <= pivot) {
                i++;
                markSwap(i, j);
            }
        }
        markSwap(i + 1, r);
        ctx.colorState[r] = 0;
        return i + 1;
    }
}
