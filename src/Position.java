public class Position {

    private String status;
    private String playerIsPresent;
    private String letter;
    private String humView;

    public Position() {
        this.letter = "1"; //only used if letter is set in by a player

        this.status = "empty";
        this.playerIsPresent = "empty";
        this.humView = "\uD83D\uDD35";
    }

    public void setStatus(String status) {
        this.status = status;

        switch (status) {
            case "empty":
            case "hidLandmine":
            case "hidCompShip":
                setHumView("\uD83D\uDD35");
                break;
            case "hidHumShip":
                setHumView(this.getLetter());
                break;
            case "foundHumShip":
                setHumView("\uD83D\uDCA5");
                break;
            case "foundCompShip":
                setHumView(this.getLetter().toUpperCase());
                break;
            case "foundLandmine":
                setHumView("\uD83D\uDCA3");
                break;
            case "visited":
                setHumView("⚫");
                break;
        }

    }

    public void setPlayerIsPresent(String playerIsPresent) {
        this.playerIsPresent = playerIsPresent;
        switch (playerIsPresent) {
            case "hum":
                setHumView("⛵");
                break;
            case "comp":
                setHumView("\uD83D\uDEA2");
                break;
            default: //a player is leaving the position
                if (getStatus().equals("empty")) {
                    setStatus("visited");
                } else {
                    setStatus(getStatus()); //sets correct view according to status
                }
                break;
        }
    }

    //simple getters and setters below

    public String getStatus() {
        return status;
    }

    public String getPlayerIsPresent() {
        return playerIsPresent;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getLetter() {
        return letter;
    }

    public String getHumView() {
        return humView;
    }

    public void setHumView(String humView) {
        this.humView = humView;
    }
}