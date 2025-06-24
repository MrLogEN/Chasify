package cz.charwot.chasify.models;

public enum Status {
    OPEN("open"),
    IN_PROGRESS("in_progress"),
    DONE("done");

    private final String dbValue;

    Status(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String toString() {
        return dbValue;
    }

    public static Status fromDb(String dbValue) {
        for (Status status : values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + dbValue);
    }
}

