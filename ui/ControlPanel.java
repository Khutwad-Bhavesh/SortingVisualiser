package sortingvisualizer.ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import sortingvisualizer.utils.Theme;

/**
 * Bottom control bar: algorithm selector, Start / Pause / Reset buttons,
 * speed slider, bar count input, order toggle, info button, github button.
 */
public class ControlPanel extends JPanel {

    public  final JComboBox<String> algoBox;
    public  final JButton           startBtn;
    public  final JButton           pauseBtn;
    public  final JButton           resetBtn;
    public  final JButton           infoBtn;
    public  final JSlider           speedSlider;
    public  final JLabel            orderLabel;
    public  final JTextField        barCountField;

    private static final String[] ALGORITHMS = {
        "Bubble Sort", "Selection Sort", "Insertion Sort",
        "Merge Sort",  "Quick Sort",     "Shell Sort"
    };

    public ControlPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 12, 10));
        setBackground(Theme.PANEL_BG);
        setPreferredSize(new Dimension(1100, 90));

        algoBox       = buildCombo();
        startBtn      = makeButton("▶ START",    new Color(0,   200, 255));
        pauseBtn      = makeButton("⏸ PAUSE",    new Color(255, 200,   0));
        resetBtn      = makeButton("↺ RESET",    new Color(255,  60, 130));
        infoBtn       = makeButton("📖 INFO",    new Color(0,   200, 100));
        speedSlider   = buildSlider();
        orderLabel    = buildOrderLabel();
        barCountField = buildBarField();

        JLabel speedLbl = styledLabel("Speed:");
        JLabel barLbl   = styledLabel("Bars:");
        add(algoBox);
        add(startBtn);
        add(pauseBtn);
        add(resetBtn);
        add(infoBtn);
        add(orderLabel);
        add(speedLbl);
        add(speedSlider);
        add(barLbl);
        add(barCountField);

        pauseBtn.setEnabled(false);
    }

    // ── builders ──────────────────────────────────────────────

    private JComboBox<String> buildCombo() {
        JComboBox<String> cb = new JComboBox<>(ALGORITHMS);
        cb.setBackground(new Color(30, 30, 55));
        cb.setForeground(Theme.BAR_BASE);
        cb.setFont(new Font("Monospaced", Font.BOLD, 13));
        cb.setPreferredSize(new Dimension(160, 36));
        cb.setBorder(BorderFactory.createLineBorder(Theme.BAR_BASE, 1));
        return cb;
    }

    private JSlider buildSlider() {
        JSlider s = new JSlider(1, 200, 3); // default super slow = 10
        s.setBackground(Theme.PANEL_BG);
        s.setForeground(Theme.TEXT);
        s.setPreferredSize(new Dimension(120, 30));
        return s;
    }

    private JLabel buildOrderLabel() {
        JLabel lbl = new JLabel("^ ASC");
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        lbl.setForeground(new Color(160, 80, 255));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 80, 255), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return lbl;
    }

    private JTextField buildBarField() {
        JTextField tf = new JTextField("8", 3);
        tf.setBackground(new Color(30, 30, 55));
        tf.setForeground(new Color(160, 80, 255));
        tf.setFont(new Font("Monospaced", Font.BOLD, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 80, 255), 1),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return tf;
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.TEXT);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        return lbl;
    }



    public static JButton makeButton(String text, Color accent) {
        JButton b = new JButton(text) {
            private boolean hovered = false;
            
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color fill;
                if (!isEnabled()) {
                    fill = new Color(30, 30, 45);
                } else if (getModel().isPressed()) {
                    fill = accent.darker().darker();
                } else if (hovered) {
                    fill = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35);
                } else {
                    fill = new Color(15, 15, 30);
                }
                
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                
                Color border = isEnabled() 
                    ? (hovered ? accent.brighter() : accent) 
                    : new Color(70, 70, 90);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(hovered ? 2.0f : 1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 10, 10));
                
                g2.setColor(isEnabled() ? (hovered ? Color.WHITE : Theme.TEXT) : new Color(100, 100, 120));
                g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String buttonText = getText();
                g2.drawString(buttonText,
                    (getWidth()  - fm.stringWidth(buttonText)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(110, 36));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public void onSortStart() {
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        algoBox.setEnabled(false);
    }

    public void onSortEnd() {
        startBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        pauseBtn.setText("⏸ PAUSE");
        algoBox.setEnabled(true);
    }
}
