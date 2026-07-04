package sortingvisualizer.utils;

import java.awt.Color;

/**
 * Centralized neon color palette.
 * Change colors here — they update everywhere automatically.
 */
public final class Theme {
    private Theme() {}

    public static final Color BG       = new Color(10,  10,  20);
    public static final Color PANEL_BG = new Color(20,  20,  40);
    public static final Color TEXT     = new Color(200, 210, 255);

    // bar states
    public static final Color BAR_BASE = new Color(0,   200, 255);  // cyan
    public static final Color COMPARE  = new Color(255,  60, 130);  // pink
    public static final Color SORTED   = new Color(0,   255, 140);  // green
    public static final Color PIVOT    = new Color(255, 200,   0);  // yellow

    /** Resolves a colorState integer to the matching Color. */
    public static Color forState(int state) {
        return switch (state) {
            case 1  -> COMPARE;
            case 2  -> SORTED;
            case 3  -> PIVOT;
            default -> BAR_BASE;
        };
    }
}
