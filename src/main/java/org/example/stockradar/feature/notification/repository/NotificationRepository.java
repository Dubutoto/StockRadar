package org.example.stockradar.feature.notification.repository;

import org.example.stockradar.feature.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Hyun7en
 */

public interface NotificationRepository extends JpaRepository<Notification,Long> {

}
