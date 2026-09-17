import java.time.LocalDateTime;

public class VerificationToken {
    private Long id;
    private User user;
    private String token;
    private String type;
    private LocalDateTime expiresAt;
    private Boolean used;

    public VerificationToken(User user, String token, String type, LocalDateTime expiresAt, Boolean used) {
        this.user = user;
        this.token = token;
        this.type = type;
        this.expiresAt = expiresAt;
        this.used = used;
    }
}
