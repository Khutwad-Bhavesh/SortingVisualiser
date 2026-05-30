package sortingvisualizer.ui;

import sortingvisualizer.core.SortContext;
import sortingvisualizer.utils.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Custom JPanel that renders the animated bar chart.
 * Reads directly from {@link SortContext} on every repaint.
 */
public class BarPanel extends JPanel {

    private static final int BAR_AREA_TOP    = 80;
    private static final int BAR_AREA_BOTTOM = 520;

    private final SortContext ctx;
    private       String      algoLabel = "";

    public BarPanel(SortContext ctx) {
        this.ctx = ctx;
        setBackground(Theme.BG);
    }

    public void setAlgoLabel(String label) { this.algoLabel = label; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawBars(g2);
        drawBaseLine(g2);
        drawAlgoLabel(g2);
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 12));
        g2.setStroke(new BasicStroke(1));
        for (int y = BAR_AREA_TOP; y <= BAR_AREA_BOTTOM; y += 40)
            g2.drawLine(0, y, getWidth(), y);
    }

    private void drawBars(Graphics2D g2) {
        int n     = ctx.arr.length;
        float barW = (float) getWidth() / n;

        for (int i = 0; i < n; i++) {
            int barH = (int) ((ctx.arr[i] / 400.0) * (BAR_AREA_BOTTOM - BAR_AREA_TOP));
            int x    = (int) (i * barW);
            int y    = BAR_AREA_BOTTOM - barH;
            int bw   = Math.max(1, (int) barW - 1);

            Color base = Theme.forState(ctx.colorState[i]);

            // soft glow halo
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 40));
            g2.fillRect(x - 1, y - 2, bw + 2, barH + 4);

            // gradient bar body
            GradientPaint gp = new GradientPaint(
                x, y,                 base.brighter(),
                x, BAR_AREA_BOTTOM,   base.darker().darker());
            g2.setPaint(gp);
            g2.fillRect(x, y, bw, barH);

            // top white highlight
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillRect(x, y, bw, 2);
        }
    }

    private void drawBaseLine(Graphics2D g2) {
        Color c = Theme.BAR_BASE;
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, BAR_AREA_BOTTOM, getWidth(), BAR_AREA_BOTTOM);
    }

    private void drawAlgoLabel(Graphics2D g2) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 15));
        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawString(algoLabel.toUpperCase(), 14, BAR_AREA_TOP - 10);
    }
}
