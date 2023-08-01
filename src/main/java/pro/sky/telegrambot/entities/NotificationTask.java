package pro.sky.telegrambot.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name ="notification_tasks")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "chat_id")
    private long chatId;
    @Column(name = "text")
    private String text;
    @Column(name = "notification_date")
    private LocalDateTime notificationDate;

    public NotificationTask(long id, long chatId, String text, LocalDateTime notificationDate) {
        this.id = id;
        this.chatId = chatId;
        this.text = text;
        this.notificationDate = notificationDate;
    }

    public NotificationTask() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", text='" + text + '\'' +
                ", notificationDate=" + notificationDate +
                '}';
    }
}

