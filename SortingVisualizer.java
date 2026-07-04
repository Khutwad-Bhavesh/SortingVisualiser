package sortingvisualizer;

import java.awt.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import sortingvisualizer.algorithms.*;
import sortingvisualizer.core.AbstractSorter;
import sortingvisualizer.core.SortContext;
import sortingvisualizer.ui.BarPanel;
import sortingvisualizer.ui.ControlPanel;
import sortingvisualizer.utils.Theme;

public class SortingVisualizer extends JFrame {

    private int N = 8;  // default 8 bars
    private static final int W = 1100, H = 680;

    private final SortContext  ctx     = new SortContext(N);
    private final BarPanel     barPanel;
    private final ControlPanel ctrl;

    private AbstractSorter[] sorters;
    private AbstractSorter   activeSorter;
    private JLabel            statsLabel;
    private Thread            sortingThread;

    public SortingVisualizer() {
        super("⚡ Sorting Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(W, H);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        barPanel = new BarPanel(ctx);
        ctrl     = new ControlPanel();
        ctrl.speedSlider.setValue(ctx.speed);

        buildSorters();
        generateArray();
        buildUI();
        wireListeners();
        setVisible(true);
    }

    private void buildSorters() {
        sorters = new AbstractSorter[]{
            new BubbleSorter   (ctx, barPanel),
            new SelectionSorter(ctx, barPanel),
            new InsertionSorter(ctx, barPanel),
            new MergeSorter    (ctx, barPanel),
            new QuickSorter    (ctx, barPanel),
            new ShellSorter    (ctx, barPanel),
        };
        activeSorter = sorters[0];
    }

    private void generateArray() {
        Random r = new Random();
        for (int i = 0; i < ctx.arr.length; i++) ctx.arr[i] = 20 + r.nextInt(380);
        System.arraycopy(new int[ctx.arr.length], 0, ctx.colorState, 0, ctx.arr.length);
        ctx.comparisons = 0;
        ctx.swaps = 0;
    }

    private void buildUI() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.PANEL_BG);
        titleBar.setPreferredSize(new Dimension(W, 46));

        JLabel title = new JLabel("  ⚡ SORTING VISUALIZER", SwingConstants.LEFT);
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(Theme.BAR_BASE);
        titleBar.add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 7));
        rightPanel.setBackground(Theme.PANEL_BG);

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statsLabel.setForeground(Theme.TEXT);
        rightPanel.add(statsLabel);


        // Small rounded square heart button linking to GitHub
        JButton heartBtn = ControlPanel.makeButton("❤", new Color(255, 75, 140));
        heartBtn.setPreferredSize(new Dimension(32, 32));
        heartBtn.addActionListener(e -> {
            try {
                String url = "https://github.com/Khutwad-Bhavesh/Khutwad-Bhavesh";
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("open", url).start();
                } else {
                    // Linux
                    new ProcessBuilder("xdg-open", url).start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        rightPanel.add(heartBtn);

        titleBar.add(rightPanel, BorderLayout.EAST);

        add(titleBar,  BorderLayout.NORTH);
        add(barPanel,  BorderLayout.CENTER);
        add(ctrl,      BorderLayout.SOUTH);

        updateStats();
    }

    private void wireListeners() {
        ctrl.algoBox.addActionListener(e -> {
            int idx = ctrl.algoBox.getSelectedIndex();
            activeSorter = sorters[idx];
            barPanel.setAlgoLabel(activeSorter.getName());
        });
        barPanel.setAlgoLabel(activeSorter.getName());

        ctrl.startBtn.addActionListener(e -> startSorting());
        ctrl.pauseBtn.addActionListener(e -> togglePause());
        ctrl.resetBtn.addActionListener(e -> resetAll());
        ctrl.barCountField.addActionListener(e -> resetAll());
        ctrl.speedSlider.addChangeListener(e -> ctx.speed = ctrl.speedSlider.getValue());

        ctrl.orderLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!ctx.descending) {
                    ctx.descending = true;
                    ctrl.orderLabel.setText("v DESC");
                } else {
                    ctx.descending = false;
                    ctrl.orderLabel.setText("^ ASC");
                }
                resetAll();
            }
        });

        ctrl.infoBtn.addActionListener(e -> showInfoDialog());
    }

    // ── Info Dialog ────────────────────────────────────────────
    private void showInfoDialog() {
        JDialog dialog = new JDialog(this, "Algorithm Info & Code Snippets", true);
        dialog.setSize(780, 560);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG);
        dialog.setLayout(new BorderLayout());

        // title
        JLabel title = new JLabel("  📖 Algorithm Reference", SwingConstants.LEFT);
        title.setFont(new Font("Monospaced", Font.BOLD, 16));
        title.setForeground(Theme.BAR_BASE);
        title.setOpaque(true);
        title.setBackground(Theme.PANEL_BG);
        title.setPreferredSize(new Dimension(780, 40));
        dialog.add(title, BorderLayout.NORTH);

        // tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Theme.PANEL_BG);
        tabs.setForeground(Theme.TEXT);
        tabs.setFont(new Font("Monospaced", Font.BOLD, 12));

        String[][] algorithms = {
            {"Bubble Sort",
             "Time: O(n²)  |  Space: O(1)  |  Stable: Yes\n\nCompares adjacent elements and swaps them if they are\nin the wrong order. Repeats until no swaps are needed.\nSimplest sorting algorithm but least efficient for large data.",
             "for (int i = 0; i < n-1; i++) {\n  for (int j = 0; j < n-i-1; j++) {\n    if (arr[j] > arr[j+1]) {\n      int tmp = arr[j];\n      arr[j] = arr[j+1];\n      arr[j+1] = tmp;\n    }\n  }\n}"},
            {"Selection Sort",
             "Time: O(n²)  |  Space: O(1)  |  Stable: No\n\nFinds the minimum element and places it at the beginning.\nRepeats for the remaining unsorted portion.\nMakes the minimum number of swaps.",
             "for (int i = 0; i < n-1; i++) {\n  int min = i;\n  for (int j = i+1; j < n; j++)\n    if (arr[j] < arr[min]) min = j;\n  int tmp = arr[min];\n  arr[min] = arr[i];\n  arr[i] = tmp;\n}"},
            {"Insertion Sort",
             "Time: O(n²)  |  Space: O(1)  |  Stable: Yes\n\nBuilds sorted array one element at a time.\nEfficient for small or nearly sorted datasets.\nWorks like sorting playing cards in your hand.",
             "for (int i = 1; i < n; i++) {\n  int key = arr[i], j = i-1;\n  while (j >= 0 && arr[j] > key) {\n    arr[j+1] = arr[j];\n    j--;\n  }\n  arr[j+1] = key;\n}"},
            {"Merge Sort",
             "Time: O(n log n)  |  Space: O(n)  |  Stable: Yes\n\nDivide-and-conquer algorithm. Splits array in half recursively,\nsorts each half, then merges them back together.\nGuaranteed O(n log n) in all cases.",
             "void mergeSort(int[] a, int l, int r) {\n  if (l < r) {\n    int m = (l+r)/2;\n    mergeSort(a, l, m);\n    mergeSort(a, m+1, r);\n    merge(a, l, m, r);\n  }\n}"},
            {"Quick Sort",
             "Time: O(n log n) avg  |  Space: O(log n)  |  Stable: No\n\nPicks a pivot element and partitions array around it.\nRecursively sorts left and right partitions.\nFastest in practice for most real-world data.",
             "void quickSort(int[] a, int l, int r) {\n  if (l < r) {\n    int p = partition(a, l, r);\n    quickSort(a, l, p-1);\n    quickSort(a, p+1, r);\n  }\n}"},
            {"Shell Sort",
             "Time: O(n log² n)  |  Space: O(1)  |  Stable: No\n\nGeneralization of insertion sort using a gap sequence.\nStarts with large gaps and reduces to 1.\nMuch faster than insertion sort for medium-sized arrays.",
             "for (int gap = n/2; gap > 0; gap /= 2) {\n  for (int i = gap; i < n; i++) {\n    int tmp = arr[i], j = i;\n    while (j >= gap && arr[j-gap] > tmp) {\n      arr[j] = arr[j-gap];\n      j -= gap;\n    }\n    arr[j] = tmp;\n  }\n}"}
        };

        for (String[] algo : algorithms) {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBackground(Theme.BG);
            panel.setBorder(new EmptyBorder(12, 14, 12, 14));

            // description
            JTextArea desc = new JTextArea(algo[1]);
            desc.setEditable(false);
            desc.setFont(new Font("Monospaced", Font.PLAIN, 13));
            desc.setForeground(Theme.TEXT);
            desc.setBackground(Theme.BG);
            desc.setLineWrap(true);
            desc.setWrapStyleWord(true);

            // code snippet
            JTextArea code = new JTextArea(algo[2]);
            code.setEditable(false);
            code.setFont(new Font("Monospaced", Font.PLAIN, 13));
            code.setForeground(new Color(0, 255, 140));
            code.setBackground(new Color(15, 15, 30));
            code.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 100), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            JLabel codeTitle = new JLabel("  Code Snippet:");
            codeTitle.setFont(new Font("Monospaced", Font.BOLD, 12));
            codeTitle.setForeground(new Color(0, 200, 100));

            JPanel codePanel = new JPanel(new BorderLayout(4, 4));
            codePanel.setBackground(Theme.BG);
            codePanel.add(codeTitle, BorderLayout.NORTH);
            codePanel.add(code, BorderLayout.CENTER);

            panel.add(desc,      BorderLayout.NORTH);
            panel.add(codePanel, BorderLayout.CENTER);

            tabs.addTab(algo[0], panel);
        }

        // close button
        JButton closeBtn = ControlPanel.makeButton("Close", new Color(255, 60, 130));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Theme.PANEL_BG);
        btnPanel.add(closeBtn);

        dialog.add(tabs,     BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── actions ────────────────────────────────────────────────
    private void startSorting() {
        if (ctx.sorting) return;
        ctx.sorting = true;
        ctx.paused  = false;
        ctx.comparisons = 0;
        ctx.swaps   = 0;
        System.arraycopy(new int[ctx.arr.length], 0, ctx.colorState, 0, ctx.arr.length);
        ctrl.onSortStart();

        if (sortingThread != null && sortingThread.isAlive()) {
            sortingThread.interrupt();
            try {
                sortingThread.join(50);
            } catch (InterruptedException ignored) {}
        }

        sortingThread = new Thread(() -> {
            try {
                activeSorter.sort();
                if (ctx.sorting) {
                    for (int i = 0; i < ctx.arr.length; i++) {
                        ctx.colorState[i] = 2;
                        barPanel.repaint();
                        Thread.sleep(6);
                    }
                }
            } catch (InterruptedException ignored) {
            } finally {
                ctx.sorting = false;
                SwingUtilities.invokeLater(() -> {
                    ctrl.onSortEnd();
                    updateStats();
                });
            }
        });
        sortingThread.start();

        new Timer(80, e -> {
            if (!ctx.sorting) ((Timer) e.getSource()).stop();
            updateStats();
        }).start();
    }

    private void togglePause() {
        ctx.paused = !ctx.paused;
        ctrl.pauseBtn.setText(ctx.paused ? "▶ RESUME" : "⏸ PAUSE");
    }

    private void resetAll() {
        ctx.sorting = false;
        ctx.paused  = false;
        if (sortingThread != null && sortingThread.isAlive()) {
            sortingThread.interrupt();
            try {
                sortingThread.join(50);
            } catch (InterruptedException ignored) {}
        }
        try {
            int val = Integer.parseInt(ctrl.barCountField.getText().trim());
            if (val >= 2 && val <= 200) {
                N = val;
            } else {
                ctrl.barCountField.setText(String.valueOf(N));
            }
        } catch (NumberFormatException ignored) {
            ctrl.barCountField.setText(String.valueOf(N));
        }
        ctx.resize(N);
        buildSorters();
        generateArray();
        ctrl.onSortEnd();
        updateStats();
        barPanel.repaint();
    }

    private void updateStats() {
        statsLabel.setText(String.format(
            "  ⚖ Comparisons: %,d    🔄 Swaps: %,d  ",
            ctx.comparisons, ctx.swaps));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(SortingVisualizer::new);
    }
}
