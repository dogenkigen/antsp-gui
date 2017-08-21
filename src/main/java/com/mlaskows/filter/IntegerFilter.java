package com.mlaskows.filter;

import java.util.function.UnaryOperator;

import static javafx.scene.control.TextFormatter.Change;

public class IntegerFilter implements UnaryOperator<Change> {

    @Override
    public Change apply(Change change) {
        String newText = change.getControlNewText();
        if (newText.matches("-?([1-9][0-9]*)?")) {
            return change;
        }
        return null;
    }

}
