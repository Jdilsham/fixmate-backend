package com.fixmate.backend.service;

import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;

public interface NotificationService {
    void notifyCustomer(User customer, String message);
    void notifyProvider(ServiceProvider provider, String message);
}
