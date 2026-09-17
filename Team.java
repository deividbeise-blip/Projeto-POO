public class Team {
    private Long id;
    private String name;
    private String inviteToken;
    private User master;

    public Team(String name, String inviteToken, User master) {
        this.name = name;
        this.inviteToken = inviteToken;
        this.master = master;
    }
}
