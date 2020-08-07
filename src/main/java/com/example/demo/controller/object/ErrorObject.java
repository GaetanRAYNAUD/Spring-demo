package com.example.demo.controller.object;

public class ErrorObject<T> {
    private ErrorCode error;

    private T meta;

    public ErrorObject() {
    }

    public ErrorObject(ErrorCode error) {
        this.error = error;
    }

    public ErrorObject(ErrorCode error, T meta) {
        this.error = error;
        this.meta = meta;
    }

    public ErrorCode getError() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
    }

    public T getMeta() {
        return meta;
    }

    public void setMeta(T meta) {
        this.meta = meta;
    }
}
