package cz.charwot.chasify.utils;

public class Result<T, E> {

    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public static <T, E> Result<T, E> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T, E> Result<T, E> err(E error) {
        return new Result<>(null, error);
    }

    public boolean isOk() {
        return value != null;
    }

    public boolean isErr() {
        return error != null;
    }

    public T unwrap() {
        if (value == null) throw new IllegalStateException("Called unwrap on an error result");
        return value;
    }

    public E unwrapErr() {
        if (error == null) throw new IllegalStateException("Called unwrapErr on a success result");
        return error;
    }

    public T getOrElse(T fallback) {
        return value != null ? value : fallback;
    }
}

