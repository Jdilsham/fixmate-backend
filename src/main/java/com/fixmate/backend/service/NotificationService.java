package com.fixmate.backend.service;

@FunctionalInterface
public interface NotificationService {
    void notify(String destination, String message);
}
