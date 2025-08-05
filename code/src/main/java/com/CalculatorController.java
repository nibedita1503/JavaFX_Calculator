package com.calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CalculatorController {

    @FXML
    private Label txt_result;

    private final StringBuilder input = new StringBuilder();

    // Handles button clicks (numbers, operators)
    @FXML
    private void handleButtonClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        String value = button.getText();
        input.append(value);
        txt_result.setText(input.toString());
    }

    // Clears the current input
    @FXML
    private void handleClear(ActionEvent event) {
        input.setLength(0);
        txt_result.setText("");
    }

    // Handles equal button click to evaluate the expression
    @FXML
    private void handleEqualButton(ActionEvent event) {
        try {
            String expression = input.toString();

            // If there's no input, do nothing
            if (expression.isEmpty()) {
                txt_result.setText("");
                return;
            }

            // Evaluate the expression
            double result = eval(expression);
            txt_result.setText(String.valueOf(result));
            input.setLength(0);  // Clear input after evaluation
        } catch (Exception e) {
            txt_result.setText("Error");
        }
    }

    // Basic expression evaluator (only handles +, -, *, /)
    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() { ch = (++pos < expression.length()) ? expression.charAt(pos) : -1; }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Invalid");
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Invalid");
                }

                return x;
            }
        }.parse();
    }
}
