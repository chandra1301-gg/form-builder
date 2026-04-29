package com.hare.formbuilder.enums;

public enum ChoiceType {
    SHORT_ANSWER("short answer"),
    PARAGRAPH("paragraph"),
    DATE("date"),
    MULTIPLE_CHOICE("multiple choice"),
    DROPDOWN("dropdown"),
    CHECKBOXES("checkboxes");

    private final String value;

    ChoiceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean requiresChoices() {
        return this == MULTIPLE_CHOICE || this == DROPDOWN || this == CHECKBOXES;
    }
}