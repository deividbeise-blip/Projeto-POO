import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private User user;
    private Task task;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public Notification(User user, Task task, String message, Boolean isRead, LocalDateTime createdAt) {
        this.user = user;
        this.task = task;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}
