public class Task {
    private Long id;
    private String title;
    private String description;
    private String status;
    private User user;

    public Task(String title, String description, String status, User user) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.user = user;
    }
}
