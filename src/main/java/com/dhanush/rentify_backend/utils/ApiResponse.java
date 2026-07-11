package com.dhanush.rentify_backend.utils;

public class ApiResponse<T> {

    private int status;
    private T data;

    public ApiResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}