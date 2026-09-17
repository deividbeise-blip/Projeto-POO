import java.time.LocalDateTime;

public class TeamMember {
    private Long id;
    private User user;
    private Team team;
    private LocalDateTime joinedAt;

    public TeamMember(User user, Team team, LocalDateTime joinedAt) {
        this.user = user;
        this.team = team;
        this.joinedAt = joinedAt;
    }
}
