package com.example.project_web.service;

import com.example.project_web.entity.Notification;
import com.example.project_web.entity.User;
import com.example.project_web.entity.Enrollment;
import com.example.project_web.repository.NotificationRepository;
import com.example.project_web.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public void sendNotificationToClass(Long classId, String title, String message) {
        List<Enrollment> enrollments = enrollmentRepository.findBySubjectClassId(classId);
        for (Enrollment en : enrollments) {
            if (en.getStudent().getUser() != null) {
                createNotification(en.getStudent().getUser(), title, message);
            }
        }
    }

    public Notification createNotification(User user, String title, String message) {
        Notification notification = new Notification(user, title, message);
        return notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}
