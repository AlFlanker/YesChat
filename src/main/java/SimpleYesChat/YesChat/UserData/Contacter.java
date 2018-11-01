package SimpleYesChat.YesChat.UserData;

import SimpleYesChat.YesChat.Messages.answers.StatusContacter;

public class Contacter {
//    @JsonIgnore
    private String id;
    private String name;
    private String town;
    private String step;
    private String trmode;
    private String raiting;
    private StatusContacter isOnline;

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getTrmode() {
        return trmode;
    }

    public void setTrmode(String trmode) {
        this.trmode = trmode;
    }

    public String getRaiting() {
        return raiting;
    }

    public void setRaiting(String raiting) {
        this.raiting = raiting;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusContacter getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(StatusContacter isOnline) {
        this.isOnline = isOnline;
    }

    public Contacter() {

    }

    @Override
    public String toString() {
        return "Contacter{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", town='" + town + '\'' +
                ", step='" + step + '\'' +
                ", trmode='" + trmode + '\'' +
                ", raiting='" + raiting + '\'' +
                '}';
    }
}
