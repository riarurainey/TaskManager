package model;

public enum Status {

    NEW("NEW"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE");

    private final String value;

    public String getValue() {
        return value;
    }

    Status(String value) {
        this.value = value;
    }
}
