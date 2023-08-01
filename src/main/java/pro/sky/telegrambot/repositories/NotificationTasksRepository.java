package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.sky.telegrambot.entities.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTasksRepository extends JpaRepository<NotificationTask, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM  notification_tasks WHERE notification_date = :time ")
    List<NotificationTask> findActualTasks(@Param("time") LocalDateTime time);
}
