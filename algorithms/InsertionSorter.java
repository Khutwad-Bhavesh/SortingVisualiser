package sortingvisualizer.algorithms;

import sortingvisualizer.core.AbstractSorter;
import sortingvisualizer.core.SortContext;
import javax.swing.JPanel;

/**
 * Insertion Sort — O(n²) time, O(1) space.
 * Builds the sorted array one element at a time by inserting into position.
 */
public class InsertionSorter extends AbstractSorter {

    public InsertionSorter(SortContext ctx, JPanel canvas) {
        super(ctx, canvas);
    }

    @Override
    public String getName() { return "Insertion Sort"; }

    @Override
    public void sort() throws InterruptedException {
        int n = ctx.arr.length;
        markSorted(0);
        for (int i = 1; i < n && ctx.sorting; i++) {
            int key = ctx.arr[i];
            int j   = i - 1;
            while (j >= 0 && ctx.sorting) {
                markCompare(j, j + 1);
                if (outOfOrder(ctx.arr[j], key)) {
                    ctx.colorState[j] = 1;
                    ctx.colorState[j + 1] = 1;
                    ctx.arr[j + 1] = ctx.arr[j];
                    ctx.swaps++;
                    ctx.soundPlayer.playSwap();
                    repaintAndSleep();
                    ctx.colorState[j] = 0;
                    ctx.colorState[j + 1] = 0;
                    j--;
                } else {
                    break;
                }
            }
            ctx.arr[j + 1] = key;
            markSorted(i);
            repaintAndSleep();
        }
    }
}
