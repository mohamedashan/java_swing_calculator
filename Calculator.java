import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * A simple calculator built with Java Swing.
 * Uses BigDecimal so results like 0.1 + 0.2 come out as 0.3.
 * Supports mouse and keyboard input.
 *
 * Compile: javac Calculator.java
 * Run:     java Calculator
 */
public class Calculator extends JFrame {

    private static final Color BG        = new Color(0x1E1E24);
    private static final Color NUM_BG    = new Color(0x3A3A44);
    private static final Color FUNC_BG   = new Color(0x55555F);
    private static final Color OP_BG     = new Color(0xFF9F0A);
    private static final Color EQ_BG     = new Color(0x30B050);
    private static final Color TEXT      = Color.WHITE;

    private final JLabel history = new JLabel(" ", SwingConstants.RIGHT);
    private final JLabel display = new JLabel("0", SwingConstants.RIGHT);

    private BigDecimal left = null;      // stored left operand
    private String operator = null;      // + - × ÷
    private boolean startNewNumber = true;
    private boolean error = false;

    public Calculator() {
        super("Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(buildDisplay(), BorderLayout.NORTH);
        add(buildButtons(), BorderLayout.CENTER);
        setupKeyboard();

        setSize(340, 520);
        setMinimumSize(new Dimension(280, 420));
        setLocationRelativeTo(null);
        setFocusable(true);
    }

    // ---------- UI ----------

    private JPanel buildDisplay() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(16, 16, 8, 16));

        history.setForeground(new Color(0x9A9AA5));
        history.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        display.setForeground(TEXT);
        display.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 44));

        panel.add(history);
        panel.add(display);
        panel.setPreferredSize(new Dimension(0, 110));
        return panel;
    }

    private JPanel buildButtons() {
        JPanel grid = new JPanel(new GridLayout(5, 4, 8, 8));
        grid.setBackground(BG);
        grid.setBorder(new EmptyBorder(8, 12, 12, 12));

        String[] labels = {
            "C", "⌫", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "−",
            "1", "2", "3", "+",
            "±", "0", ".", "="
        };

        for (String label : labels) {
            grid.add(makeButton(label));
        }
        return grid;
    }

    private JButton makeButton(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setFocusable(false);          // keep keyboard focus on the frame
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        switch (label) {
            case "C", "⌫", "%", "±" -> b.setBackground(FUNC_BG);
            case "÷", "×", "−", "+" -> b.setBackground(OP_BG);
            case "="                -> b.setBackground(EQ_BG);
            default                 -> b.setBackground(NUM_BG);
        }

        b.addActionListener(e -> handle(label));
        return b;
    }

    // ---------- Keyboard ----------

    private void setupKeyboard() {
        // Window-level bindings work even when a button or another child owns focus.
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        for (char digit = '0'; digit <= '9'; digit++) {
            bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(digit), String.valueOf(digit));
        }
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke('.'), ".");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(','), ".");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke('+'), "+");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke('-'), "−");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke('*'), "×");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke('/'), "÷");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke('%'), "%");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke('='), "=");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "=");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "⌫");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "C");
    }

    private void bindKey(InputMap inputMap, ActionMap actionMap, KeyStroke keyStroke, String action) {
        String actionName = "key-" + action + "-" + keyStroke;
        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                handle(action);
            }
        });
    }

    // ---------- Logic ----------

    private void handle(String key) {
        if (error && !key.equals("C")) {
            clearAll();               // recover from an error on any input
        }

        switch (key) {
            case "C" -> clearAll();
            case "⌫" -> backspace();
            case "±" -> negate();
            case "%" -> percent();
            case "." -> inputDot();
            case "=" -> equals();
            case "+", "−", "×", "÷" -> setOperator(key);
            default -> inputDigit(key);
        }
    }

    private void inputDigit(String d) {
        String text = display.getText();
        if (startNewNumber || text.equals("0")) {
            setDisplay(d);
        } else if (text.replace("-", "").replace(".", "").length() < 15) {
            setDisplay(text + d);
        }
        startNewNumber = false;
    }

    private void inputDot() {
        if (startNewNumber) {
            setDisplay("0.");
            startNewNumber = false;
        } else if (!display.getText().contains(".")) {
            setDisplay(display.getText() + ".");
        }
    }

    private void backspace() {
        if (startNewNumber) return;
        String text = display.getText();
        if (text.length() <= 1 || (text.length() == 2 && text.startsWith("-"))) {
            setDisplay("0");
            startNewNumber = true;
        } else {
            setDisplay(text.substring(0, text.length() - 1));
        }
    }

    private void negate() {
        String text = display.getText();
        if (text.equals("0")) return;
        setDisplay(text.startsWith("-") ? text.substring(1) : "-" + text);
    }

    private void percent() {
        BigDecimal value = current().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);
        setDisplay(format(value));
        startNewNumber = true;
    }

    private void setOperator(String op) {
        // Chain operations: 2 + 3 + ... computes 2 + 3 first.
        if (operator != null && !startNewNumber) {
            if (!compute()) return;
        } else if (operator == null) {
            left = current();
        }
        operator = op;
        startNewNumber = true;
        history.setText(format(left) + " " + op);
    }

    private void equals() {
        if (operator == null) return;
        String expr = format(left) + " " + operator + " " + format(current()) + " =";
        if (compute()) {
            history.setText(expr);
            operator = null;
            left = null;
            startNewNumber = true;
        }
    }

    /** Applies the pending operator to left and the display value. Result goes to display and left. */
    private boolean compute() {
        BigDecimal right = current();
        BigDecimal result;

        switch (operator) {
            case "+" -> result = left.add(right);
            case "−" -> result = left.subtract(right);
            case "×" -> result = left.multiply(right);
            case "÷" -> {
                if (right.signum() == 0) {
                    showError("Cannot divide by 0");
                    return false;
                }
                result = left.divide(right, MathContext.DECIMAL64);
            }
            default -> { return false; }
        }

        left = result;
        setDisplay(format(result));
        startNewNumber = true;
        return true;
    }

    private void clearAll() {
        left = null;
        operator = null;
        startNewNumber = true;
        error = false;
        history.setText(" ");
        setDisplay("0");
    }

    private void showError(String message) {
        error = true;
        left = null;
        operator = null;
        startNewNumber = true;
        history.setText(message);
        setDisplay("Error");
    }

    // ---------- Helpers ----------

    private BigDecimal current() {
        try {
            return new BigDecimal(display.getText());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private static String format(BigDecimal value) {
        BigDecimal stripped = value.stripTrailingZeros();
        if (stripped.signum() == 0) return "0";
        return stripped.toPlainString();
    }

    private void setDisplay(String text) {
        display.setText(text);
        // Shrink the font for long numbers so they stay visible.
        int size = text.length() > 14 ? 24 : text.length() > 10 ? 32 : 44;
        display.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
    }

    // ---------- Main ----------

    public static void main(String[] args) {
        try {
            // Cross-platform look & feel so button colors render the same everywhere.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
    }
}
