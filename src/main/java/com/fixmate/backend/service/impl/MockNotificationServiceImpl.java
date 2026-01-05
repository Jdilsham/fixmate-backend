package com.fixmate.backend.service.impl;

import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class MockNotificationServiceImpl implements NotificationService {

    @Override
    public void notifyCustomer(User customer, String message){
        System.out.println("Notifying Customer " + customer.getEmail() + "->" + message);
    }

    @Override
    public void notifyProvider(ServiceProvider provider, String message){
        System.out.println("Notifying Provider" + provider.getUser().getEmail() + "->" + message);
    }
}
