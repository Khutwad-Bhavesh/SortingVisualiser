package sortingvisualizer.core;


/**
 * Shared mutable state passed to every sorting algorithm.
 * Acts as the "bridge" between algorithms and the visualizer UI.
 */
public class SortContext {

    public int[] arr;
    public int[] colorState;// 0=base 1=compare 2=sorted 3=pivot
    public volatile boolean sorting;
    public volatile boolean paused;
    public int  comparisons;
    public int  swaps;
    public int  speed;                 // slider value 10–110
    public boolean descending = false; // false = ascending, true = descending

    public SortContext(int size) {
        arr        = new int[size];
        colorState = new int[size];
        sorting    = false;
        paused     = false;
        speed      = 70;
    }

   public void resize(int newSize) {
        arr        = new int[newSize];
        colorState = new int[newSize];
    }
}
