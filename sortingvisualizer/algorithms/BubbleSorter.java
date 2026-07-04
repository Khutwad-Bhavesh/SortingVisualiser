package sortingvisualizer.algorithms;

import sortingvisualizer.core.AbstractSorter;
import sortingvisualizer.core.SortContext;
import javax.swing.JPanel;

/**
 * Bubble Sort — O(n²) time, O(1) space.
 * Repeatedly swaps adjacent elements that are out of order.
 */
public class BubbleSorter extends AbstractSorter {

    public BubbleSorter(SortContext ctx, JPanel canvas) {
        super(ctx, canvas);
    }

    @Override
    public String getName() { return "Bubble Sort"; }

    @Override
    public void sort() throws InterruptedException {
        int n = ctx.arr.length;
        for (int i = 0; i < n - 1 && ctx.sorting; i++) {
            for (int j = 0; j < n - i - 1 && ctx.sorting; j++) {
                markCompare(j, j + 1);
                if (outOfOrder(ctx.arr[j], ctx.arr[j + 1])) markSwap(j, j + 1);
            }
            markSorted(n - i - 1);
        }
        markSorted(0);
    }
}
