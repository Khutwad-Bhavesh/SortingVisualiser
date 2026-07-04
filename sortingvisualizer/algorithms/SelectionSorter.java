package sortingvisualizer.algorithms;

import sortingvisualizer.core.AbstractSorter;
import sortingvisualizer.core.SortContext;
import javax.swing.JPanel;

/**
 * Selection Sort — O(n²) time, O(1) space.
 * Finds the minimum element and places it at the front each pass.
 */
public class SelectionSorter extends AbstractSorter {

    public SelectionSorter(SortContext ctx, JPanel canvas) {
        super(ctx, canvas);
    }

    @Override
    public String getName() { return "Selection Sort"; }

    @Override
    public void sort() throws InterruptedException {
        int n = ctx.arr.length;
        for (int i = 0; i < n - 1 && ctx.sorting; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n && ctx.sorting; j++) {
                markCompare(minIdx, j);
                if (outOfOrder(ctx.arr[minIdx], ctx.arr[j])) minIdx = j;
            }
            if (minIdx != i) markSwap(i, minIdx);
            markSorted(i);
        }
        markSorted(n - 1);
    }
}
