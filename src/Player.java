import java.util.*;

public class Player {
    Scanner psc = new Scanner(System.in);

    private String[] wordArray = {"angle", "helix", "deity", "court", "sunny", "apple", "piano" +
            "", "kicks", "owlet", "smart", "darns", "world", "plain", "orbit", "clans" +
            "", "faded", "found", "crown", "think", "scene"}; //used only for initial word selection

    private int xPos;
    private int yPos;

    private String name;
    private String word;

    private String oppName;
    private String oppWord;

    private int actionsSaved;
    private int chances; //to guess the opponent's word

    private int lettersDiscovered;
    private String[] lettersFound = {"1", "1", "1", "1", "1"}; //switches as they are discovered

    public Player(String name) {
        this.name = name;
        this.actionsSaved = 0;
        this.chances = 5;
        this.lettersDiscovered = 0;
    }

    public void printCurrentGrid(String[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            System.out.println(" ");
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[j][i] + " ");
                if (!grid[j][i].equals("\uD83D\uDD35") && !grid[j][i].equals("⚫") && !grid[j][i].equals("⛵")
                        && !grid[j][i].equals("\uD83D\uDEA2") && !grid[j][i].equals("\uD83D\uDCA3")
                        && !grid[j][i].equals("\uD83D\uDCA5")) {
                    System.out.print(" ");
                }
            }
        }
    }

    public void regainChance() {
        if (getActionsSaved() >= 4) {
            for (int i = 0; i < 4; i++) {
                loseAnAction();
            }
            setChances(getChances() + 1);
            System.out.println("Regained a chance successfully. Chances left: " + getChances());
        } else {
            System.out.println("Not enough actions saved. Action terminated.");
        }
    }

    public boolean guess (String word) {
        System.out.println("The suggestion was " + word + ".");
        return (word.equals(getOppWord().toUpperCase()));
    }

    public int[] chooseDirection(String indicator) {
        //Returns the new coordinates of a player's position if they move in a particular direction, or the same
        //coordinates if that direction would put them off the grid.

        int curX = this.getxPos();
        int curY = this.getyPos();
        int xIQ;
        int yIQ;
        switch(indicator) {
            case "N":
                xIQ = curX;
                yIQ = curY - 1;
                break;
            case "E":
                xIQ = curX + 1;
                yIQ = curY;
                break;
            case "S":
                xIQ = curX;
                yIQ = curY + 1;
                break;
            case "W":
                xIQ = curX - 1;
                yIQ = curY;
                break;
            case "NE":
                xIQ = curX + 1;
                yIQ = curY - 1;
                break;
            case "NW":
                xIQ = curX - 1;
                yIQ = curY - 1;
                break;
            case "SE":
                xIQ = curX + 1;
                yIQ = curY + 1;
                break;
            case "SW":
                xIQ = curX - 1;
                yIQ = curY + 1;
                break;
            default:
                xIQ = curX;
                yIQ = curY;
                break;
        }
        if (xIQ > -1 && xIQ < 10 && yIQ > -1 && yIQ < 10) {
            return new int[]{xIQ, yIQ};
        } else {
            return new int[]{curX, curY};
        }
    }

    public String[][] makeHumView (Position[][] grid) {
        String[][] done = new String[10][10];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                done[i][j] = grid[i][j].getHumView();
            }
        }
        return done;
    }

    public void saveAnAction() {
        this.actionsSaved++;
    }

    public void loseAnAction() {
        this.actionsSaved--;
    }

    //getters and setters below

    public String[] getWordArray() {
        return wordArray;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWord() {
        return word;
    }

    public void setWord() {
        this.word = this.wordArray[(int)(getWordArray().length * Math.random())];
    }

    public String getOppName() {
        return oppName;
    }

    public void setOppName(String oppName) {
        this.oppName = oppName;
    }

    public String getOppWord() {
        return this.oppWord;
    }

    public void setOppWord(String word) {
        this.oppWord = word;
    }

    public int getActionsSaved() {
        return actionsSaved;
    }

    public int getChances() {
        return chances;
    }

    public void setChances(int chances) {
        this.chances = chances;
    }

    public int getLettersDiscovered() {
        return lettersDiscovered;
    }

    public void setLettersDiscovered(int lettersDiscovered) {
        this.lettersDiscovered = lettersDiscovered;
    }

    public String[] getOppLetters() {
        return lettersFound;
    }

    public void setOppLetters(String[] oppLetters) {
        this.lettersFound = oppLetters;
    }
}