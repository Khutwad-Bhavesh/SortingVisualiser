package sortingvisualizer.algorithms;

import sortingvisualizer.core.AbstractSorter;
import sortingvisualizer.core.SortContext;
import javax.swing.JPanel;

/**
 * Shell Sort — O(n log² n) time, O(1) space.
 * Generalization of insertion sort using a decreasing gap sequence.
 */
public class ShellSorter extends AbstractSorter {

    public ShellSorter(SortContext ctx, JPanel canvas) {
        super(ctx, canvas);
    }

    @Override
    public String getName() { return "Shell Sort"; }

    @Override
    public void sort() throws InterruptedException {
        int n = ctx.arr.length;
        for (int gap = n / 2; gap > 0 && ctx.sorting; gap /= 2) {
            for (int i = gap; i < n && ctx.sorting; i++) {
                int tmp = ctx.arr[i];
                int j   = i;
                while (j >= gap && ctx.sorting) {
                    markCompare(j - gap, j);
                    if (outOfOrder(ctx.arr[j - gap], tmp)) {
                        ctx.colorState[j - gap] = 1;
                        ctx.colorState[j] = 1;
                        ctx.arr[j] = ctx.arr[j - gap];
                        ctx.swaps++;
                        repaintAndSleep();
                        ctx.colorState[j - gap] = 0;
                        ctx.colorState[j] = 0;
                        j -= gap;
                    } else {
                        break;
                    }
                }
                ctx.arr[j] = tmp;
                repaintAndSleep();
            }
        }
        for (int i = 0; i < n; i++) markSorted(i);
    }
}
