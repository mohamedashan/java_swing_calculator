# Java Swing Calculator

A simple desktop calculator built with Java Swing.

## Features
- Add, subtract, multiply, divide
- Percent, plus/minus, backspace, clear
- Mouse and keyboard input
- Uses `BigDecimal`, so `0.1 + 0.2 = 0.3`
- Divide-by-zero handling

## Run
Requires Java 14+.

```bash
javac Calculator.java
java Calculator
```

## Keyboard
Digits, `+ - * /`, `.`, `%`, `Enter`/`=`, `Backspace`, `Esc` (clear).

Keyboard shortcuts use window-level bindings, so they continue to work after
clicking calculator buttons or while focus is on another control.
