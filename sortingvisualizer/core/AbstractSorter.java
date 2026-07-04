package sortingvisualizer.core;

import javax.swing.JPanel;

/**
 * Base class for every sorting algorithm.
 * Subclasses only need to implement {@link #sort()}.
 * All visual feedback helpers live here.
 */
public abstract class AbstractSorter {

    protected final SortContext ctx;
    private   final JPanel      canvas;

    protected AbstractSorter(SortContext ctx, JPanel canvas) {
        this.ctx    = ctx;
        this.canvas = canvas;
    }

    public abstract void sort() throws InterruptedException;
    public abstract String getName();

    protected void markCompare(int i, int j) throws InterruptedException {
        int prevI = ctx.colorState[i];
        int prevJ = ctx.colorState[j];
        ctx.colorState[i] = 1;
        ctx.colorState[j] = 1;
        ctx.comparisons++;
        repaintAndSleep();
        ctx.colorState[i] = prevI;
        ctx.colorState[j] = prevJ;
    }

    protected void markSwap(int i, int j) throws InterruptedException {
        int tmp = ctx.arr[i];
        ctx.arr[i] = ctx.arr[j];
        ctx.arr[j] = tmp;
        int prevI = ctx.colorState[i];
        int prevJ = ctx.colorState[j];
        ctx.colorState[i] = 1;
        ctx.colorState[j] = 1;
        ctx.swaps++;
        repaintAndSleep();
        ctx.colorState[i] = prevJ;
        ctx.colorState[j] = prevI;
    }

    protected boolean outOfOrder(int a, int b) {
        return ctx.descending ? a < b : a > b;
    }

    protected void markSorted(int i) { ctx.colorState[i] = 2; }
    protected void markPivot(int i)  { ctx.colorState[i] = 3; }

    protected void repaintAndSleep() throws InterruptedException {
        canvas.repaint();
        // speed 1 = super slow (2000ms), speed 200 = fast (1ms)
        int ms = (int)(2000.0 / ctx.speed) - 9;
        Thread.sleep(Math.max(1, ms));
        while (ctx.paused && ctx.sorting) Thread.sleep(30);
    }
}
