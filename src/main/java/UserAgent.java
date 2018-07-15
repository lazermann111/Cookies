public class UserAgent {
    private String agent;
    private String width;
    private String height;

    public UserAgent(String agent, String width, String height) {
        this.agent = agent;
        this.width = width;
        this.height = height;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}