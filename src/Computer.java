import java.util.*;

public class Computer extends Player {

    int counter = 1; //used to make the computer more or less likely to do things at different times

    private String[] lastCode = {"none", "null"}; //[0]: direction or 'none'; [1]: 'blocked' or 'ship' or 'mine' or 'null'
    // conveys information discovered during the last action
    private String lastAction;

    private int ownHitShips;

    //options the computer will select from when guessing
    private String[] possGuesses = {"angle", "helix", "deity", "court", "sunny", "apple", "piano" +
            "", "kicks", "owlet", "smart", "darns", "world", "plain", "orbit", "clans" +
            "", "faded", "found", "crown", "think", "scene", "angel", "xylem", "death", "foils", "scorn" +
            "", "dunce", "pains", "kooks", "towel", "party", "snark", "chair", "wordy", "boats", "pound" +
            "", "trove", "finds", "wakes", "thunk", "soars", "gales", "axiom", "exalt", "trunk", "sandy" +
            "", "place", "guess", "board", "lowly", "farms", "snark", "block", "shout", "towed", "phone" +
            "", "coded", "fried", "crows", "shank", "eight"};

    public Computer() {
        super ("Computer");
        this.lastAction = "gameStarts";
        this.ownHitShips = 0;
    }

    public void scatterWord(Position[][] hidGrid) {
        int x = (int)(10.0 * Math.random());
        int y = (int)(10.0 * Math.random());

        for (int i = 0; i < this.getWord().length(); i++) {
            if (hidGrid[x][y].getStatus().equals("empty")) {
                Position pos = new Position();

                pos.setLetter(getWord().substring(i, i + 1));
                pos.setStatus("hidCompShip");

                hidGrid[x][y] = pos;
            } else {
                i--; //go back to place that letter again if the first suggestion for where to put it is taken
            }
            x = (int)(10.0 * Math.random());
            y = (int)(10.0 * Math.random());
        }
    }

    public void firstSpot(Position[][] hidGrid) {
        int x = (int)(8.0 * Math.random()) + 1; //limits it from landing first on an edge (for the sake of mobility)
        int y = (int)(8.0 * Math.random()) + 1;

        while (!hidGrid[x][y].getStatus().equals("empty") || hidGrid[x][y].getPlayerIsPresent().equals("hum")) {
            x = (int)(8.0 * Math.random()) + 1;
            y = (int)(8.0 * Math.random()) + 1;
        }

        hidGrid[x][y].setPlayerIsPresent("comp");
        this.setxPos(x);
        this.setyPos(y);
    }

    public void turn (int numActions, Position[][] hidGrid) {
        String[] choices = {"move", "board", "save", "guess", "extend", "regain a chance"};

        while (numActions > 0) {
            //First it will assess the situation and pick an action from the array above as well as a direction to
            //potentially move or board towards.
            counter++;

            int[] possibilities = think(hidGrid); //creates an array of 50 ints, representing the six choices above;
            //frequency of each is fairly logical given the current circumstances
            int decision = possibilities[(int)(50.0 * Math.random())]; //random choice from this array

            String direction = pointer(hidGrid); //picks a random direction which is not out-of-bounds

            if (getLastCode()[1].equals("blocked")) {
                direction = getLastCode()[0];
            } //if it tried to move last time and was blocked, the direction will be switched to the one taken last time

            String stat = spatialAwareness(hidGrid, direction);
            //a small message regarding the position one spot away in the selected direction

            if (!(counter % 4 == 0) && stat.equals("hidHumShip")) {
                decision = 1;
            } //more likely to decide to board if it has chosen a direction where one spot away there is a hidden letter

            //If it has decided to move and it would be objectively dumb to move to the position one spot away in the
            // selected direction, it will decide on a different direction.
            while (decision == 0 && (stat.equals("ownShip") || stat.equals("mine")) || stat.equals("foundHumShip") || stat.equals("notAgain") || stat.equals("hum")) {
                counter++; //if it is surrounded by visited positions, it will only be able to move once 'notAgain'
                // is not the message; 'notAgain' becomes or doesn't become the message based on the counter
                direction = pointer(hidGrid);
                stat = spatialAwareness(hidGrid, direction);
            }

            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                System.out.println("Insomnia");
            }

            System.out.println("Computer decides to " + choices[decision] + ".");
            setLastAction(choices[decision]); //setting up for the next action

            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                System.out.println("Insomnia");
            }

            switch (choices[decision]) {
                case "move":
                    String[] moveCode = {direction, "null"}; //moveCode will be set as the lastCode at the end of the action

                    if (getLastCode()[1].equals("blocked")) {
                        String same = direction;
                        while (direction.equals(same)) {
                            direction = pointer(hidGrid);
                        }
                    }
                    //if it has decided to move after being blocked previously, it will choose a different direction now

                    int mx = chooseDirection(direction)[0]; //new coordinates (move x and move y)
                    int my = chooseDirection(direction)[1];

                    if ((hidGrid[mx][my].getStatus().equals("visited") || hidGrid[mx][my].getStatus().equals("empty")) && hidGrid[mx][my].getPlayerIsPresent().equals("empty")) {
                        hidGrid[this.getxPos()][this.getyPos()].setPlayerIsPresent("empty");
                        setxPos(mx);
                        setyPos(my);
                        hidGrid[this.getxPos()][this.getyPos()].setPlayerIsPresent("comp");
                    } else {
                        System.out.println("Tries to move " + direction + " -- spot is taken.");
                        moveCode[1] = "blocked";
                    }

                    setLastCode(moveCode);
                    break;
                case "board":
                    if (!getLastCode()[0].equals("none")) { //aka if lastCode()[0] holds a direction
                        String experienced = hidGrid[chooseDirection(getLastCode()[0])[0]][chooseDirection(getLastCode()[0])[1]].getStatus();
                        //experienced will be the status of the position one spot away in that direction

                        if (experienced.equals("empty") || experienced.equals("hidHumShip") || experienced.equals("hidLandmine")) {
                            direction = getLastCode()[0];
                        } //if it is empty or if something is hidden it will agree to go in that direction, but if it's already
                        //found something there it'll use a different direction
                    }

                    String[] boardCode = {direction, "null"}; //boardCode will become lastCode at the end of this action

                    int bx = chooseDirection(direction)[0]; //new coordinates (board x and board y)
                    int by = chooseDirection(direction)[1];

                    System.out.println("Trying to board " + direction + "...");
                    if (hidGrid[bx][by].getStatus().equals("empty")) {
                        System.out.println("There is nothing to board at the selected spot. Action is over.");
                    } else {
                        switch (hidGrid[bx][by].getStatus()) {
                            case "hidHumShip":
                                System.out.println("Computer has just boarded a ship! The letter here is " + hidGrid[bx][by].getLetter().toUpperCase() + ".");
                                hidGrid[bx][by].setStatus("foundHumShip");

                                String[] soFar = getOppLetters(); //used to easily write to individual values in the array
                                soFar[getLettersDiscovered()] = hidGrid[bx][by].getLetter();
                                setOppLetters(soFar);

                                setLettersDiscovered(getLettersDiscovered() + 1);
                                boardCode[1] = "ship";
                                break;
                            case "hidLandmine":
                                System.out.println("Computer just boarded a mine. Computer must lose a chance to guess the opponent's word.");
                                hidGrid[bx][by].setStatus("foundLandmine");

                                this.setChances(getChances() - 1);
                                System.out.println("Chance lost. Chances left: " + getChances());
                                if (getChances() == 0) {
                                    System.out.println("Last chance used. Game over --" + getOppName() + " wins.");
                                    numActions = 1;
                                }

                                boardCode[1] = "mine";
                                break;
                            default:
                                System.out.println("Boarding attempt unproductive.");
                                break;
                        }
                        setLastCode(boardCode);
                    }
                    break;
                case "save":
                    saveAnAction();
                    System.out.println("Saved an action. Computer's saved actions: " + getActionsSaved());
                    break;
                case "guess":
                    System.out.println("The computer is guessing the opponent's word.");
                    boolean success = guess(generateGuess().toUpperCase());

                    if (success) {
                        System.out.println("Success! Computer has guessed the correct word!");
                        System.out.println("Game over -- computer wins!");
                        numActions = 1; // turn will not continue to next action
                        setChances(0); // game will not continue to next turn in Runner
                    } else {
                        setChances(getChances() - 1);
                        System.out.println("That is incorrect. Computer now has " + getChances() + " chances remaining.");
                        if (getChances() == 0) {
                            System.out.println("Last chance used. Game over --" + getOppName() + " wins.");
                            numActions = 1; //turn will continue to next action
                        }
                    }
                    break;
                case "extend":
                    //At the end of each action, numActions decreases by 1, so 1 extra will be added to numActions here
                    //in addition to any extension because extending doesn't take up an action.

                    int request = getActionsSaved() - (int)(getActionsSaved() * Math.random());
                    //picks random non-zero amount of saved-up actions to use

                    numActions += request + 1;
                    for (int i = 0; i < request; i++) {
                        loseAnAction(); //of saved-up actions
                    }

                    System.out.println("Turn extended by " + request + " action(s). Saved action(s): " + getActionsSaved());
                    break;
                case "regain a chance":
                    regainChance();
                    break;
            }
            numActions--;
            if (getChances() > 0) { //if there are zero chances the game is over
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    System.out.println("Insomnia");
                }

                this.printCurrentGrid(makeHumView(hidGrid));
                System.out.println(numActions + " action(s) left.");
            }
            if (getChances() > 0 && numActions == 0) {
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    System.out.println("Insomnia");
                }

                System.out.println(getOppName() + "'s turn.");
            }
        }
    }

    public int[] think(Position[][] hidGrid) {
        //This is how the computer will decide which action to make. Essentially, it will pull one of the six choices
        //from a digital hat (the returned array from this function), but we are going to assess five different
        //aspects of the current situation and make it so that more reasonable decisions are more likely to be picked.
        int[] possibilities = new int[50];

        //These variables represent the number of 0's, the number of 1's and so on in the final array. 0 represents
        //moving, 1 boarding, 2 saving, and so on.
        int num0 = 0; //move
        int num1 = 0; //board
        int num2 = 0; //save
        int num3 = 0; //guess
        int num4 = 0; //extend
        int num5 = 0; //regain a chance

        //This is just a quick check to make sure that if a direction is in the lastCode (which will be used if so) that
        //it will be changed to 'none' if it directs the computer off the grid.
        if (!getLastCode()[0].equals("none")) {
            if (Arrays.equals(chooseDirection(getLastCode()[0]), new int[] {getxPos(), getyPos()})) {
                setLastCode(new String[]{"none", getLastCode()[1]});
            }
        }

        //FIRST EXAMINED ASPECT: How many of the human player's letters have been discovered?
        switch(getLettersDiscovered()) {
            case 0:
            case 1: //FEW
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num1 += 8; //very likely to board if spatialAwareness recommends it and few letters have been discovered
                    num0 += 1; //moving is just feasible
                    num2 += 1; //saving is just feasible
                } else {
                    if (getActionsSaved() > 3 && getChances() < 3) { //if has few chances and 4 or more actions saved-up:
                        num5 += 6; // likely to regain a chance as that is possible and advisable
                        num0 += 3; // possibly going to move
                        num2++; // saving is just feasible
                    } else {
                        num0 += 6; //likely to move
                        num2 += 2; //saving is possible
                        num4 += 2; //extension is possible
                    }
                }
                break;
            case 2:
            case 3: //MEDIUM AMOUNT
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num1 += 7; //still most likely to board if advised to do so by spatialAwareness with 2 or 3 known letters
                    num0 += 2; //moving is possible
                    num2 += 1; //saving is just feasible
                } else {
                    if (getActionsSaved() > 3 && getChances() < 3) { //if has few chances and 4 or more actions saved-up:
                        num5 += 6; //it's pretty smart to regain a chance
                        num0 += 3; //moving is reasonable
                        num2++; //saving is just feasible
                    } else {
                        num0 += 5; //moving is fairly smart
                        num2 += 2; //saving is possible
                        num4 += 2; //extension is possible
                        num3++; //guessing is just feasible
                    }
                }
                break;
            case 4:
            case 5: //MANY (chance of boarding won't show any increase directly resulting from this aspect)
                if (getChances() < 3) { //if it has less than 3 chances left to guess the opponent's word:
                    if (getActionsSaved() > 3) { //if it has more than 3 actions saved-up
                        num5 += 7; //it will be likely to choose to regain a chance to guess
                        num2++; //saving is just feasible
                    } else { //if it doesn't have enough actions saved to regain a chance to guess
                        num2 += 8; //it would be very smart to save
                    }
                    num3++; //guessing is just feasible
                    num4++; //extension is just feasible
                } else { //if it has 3 or more chances left to guess the opponent's word
                    if (getActionsSaved() > 3) { //if it has 4 or more actions saved-up:
                        num5 += 2; //regaining a chance is possible
                        num2 -= 2; //and saving is less likely than if not
                    }
                    num4 += 4; //extension is fairly likely
                    num3 += 3; //guessing is reasonable
                    num0++; //moving is just feasible
                    num2 += 2; //saving is possible
                }
                break;
        }

        //SECOND EXAMINED ASPECT: How many saved-up actions does the computer player have?
        switch(getActionsSaved()) {
            case 0: //NONE
                if (getLettersDiscovered() > 2) { //if it has discovered more than 2 letters
                    num3++; //guessing is just feasible
                    num0 += 3; //moving is reasonable
                } else { // if it has discovered less than 2 letters
                    num0 += 4; //moving is fairly likely
                }
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num2 += 4; //saving is fairly likely
                    num1 += 2; //boarding is possible if advised by spatialAwareness
                } else {
                    num2 += 6; //saving is pretty smart in general with few actions saved
                }
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: //A MEDIUM AMOUNT
                num0 += 7; //moving is most likely
                num2++; //saving is just feasible
                num4 += 2; //extending is possible
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num1 += 2; //boarding is possible if so advised by spatialAwareness
                    num0--; //moving becomes slightly less likely
                    num2--; //as does saving
                }
                if (getLettersDiscovered() > 3) { //if more than 3 of the human's letters have been found
                    num3++; //guessing is just feasible
                    num0--; //again, moving and
                    num2--; //saving become slightly less likely
                }
                if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                    num5 += 2; //regaining is possible but not absolutely necessary
                    num0--; //moving is slightly less likely
                    num4--; //extending is slightly less likely
                }
                break;
            default: //A LOT
                num4 += 5; //extending is fairly smart
                num5 += 3; //regaining is reasonable
                num0 += 2; //moving is possible
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num1++; //boarding is just feasible if advised by spatialAwareness
                    num0--; //moving becomes slightly less possible
                }
                if (getLettersDiscovered() > 3) { //if more than 3 letters have been discovered
                    num3++; //guessing is just feasible
                    num0--; //moving becomes slightly less possible
                }
                break;
        }

        //THIRD EXAMINED ASPECT: How many of the computer's letters have been found?
        switch(getOwnHitShips()) {
            case 0:
            case 1: //A MINIMAL NUMBER
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num1 += 8; //boarding is very likely if advised by spatialAwareness
                    num0 += 2; //moving is possible
                } else {
                    num0 += 6; //moving is pretty smart
                    num2 += 3; //saving is reasonable
                    num4++; //extending is just feasible
                    if (getLettersDiscovered() > 3) { //if more than 3 of the human's letters have been guessed
                        num3++;//guessing is just feasible
                        num0--; //moving becomes slightly less likely
                    }
                    if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                        num5++; //regaining is just feasible
                        num0--; //moving becomes slightly less likely
                    }
                }
                break;
            case 2:
            case 3: //A MODERATE AMOUNT
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num1 += 7; //boarding is most likely if advised by spatialAwareness
                    num0++; //moving is just feasible
                    num2++; //saving is just feasible
                    num4++; //extending is just feasible
                } else { //if boarding is not advised by spatialAwareness
                    num0 += 3; //moving is reasonable
                    num2 += 4; //saving is fairly likely
                    num4 += 3; //extending is reasonable
                }
                if (getLettersDiscovered() > 3) { //if more than 3 letters have been discovered
                    num3++; //guessing is just feasible
                    num0--; //moving is a little less smart
                }
                if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                    num5++; //regaining is just feasible
                    num2--; //saving is not so helpful
                }
                break;
            case 4:
            case 5: //MOST TO ALL
                num4 += 6; //extending is pretty smart
                num0 += 3; //moving is reasonable
                num3++; //guessing is just feasible
                if (getActionsSaved() < 1) { //if no actions are saved-up
                    num2 += 2; //saving is possible
                    num4 -= 2; //extending will be turned into moving during later processing and moving will be less helpful
                }
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num1 += 4; //boarding is fairly likely if advised by spatialAwareness
                    num4 -= 2; //extending can be sacrificed for boarding
                    num0 -= 2; //moving will also not be favoured
                }
                if (getLettersDiscovered() > 3) { //if more than 3 of the human's letters have been found
                    num3 += 2; //guessing is possible
                    num0--; //moving is not as smart
                    num1--; //boarding is not as smart
                }
                if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                    num5 += 3; //regaining is reasonable
                    num4 -= 3; //extending, not so good
                }
                break;
        }

        //FOURTH EXAMINED ASPECT: How many chances does the computer player have left to guess the human's word?
        switch(getChances()) {
            case 0:
            case 1: //HARDLY ANY
                if (getActionsSaved() > 3) { //if more than 3 actions are saved-up
                    num5 += 8; //regaining is very likely
                    num4 += 2; //extending is possible
                } else { // if less than 3 actions are saved-up
                    num0 += 3; //moving is reasonable
                    num2 += 5; //saving is fairly smart
                    num4 += 2; //extending is possible
                    if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board") && getLettersDiscovered() < 4) {
                        num1 += 2; //boarding is possible if advised by spatialAwareness
                        num0 -= 2; //moving is possible
                    }
                    if (getLettersDiscovered() > 3) { //if more than 3 letters have been discovered
                        num3 += 2; //guessing is possible
                        num2--; //saving is slightly less helpful
                        num4--; //extending is also less helpful
                    }
                }
                break;
            case 2:
            case 3: //A FEW
                num0 += 4; //moving is fairly likely
                num2 += 3; //saving is reasonable
                num4 += 3; //extending is reasonable
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board") && getLettersDiscovered() < 3) {
                    num1 += 2; //boarding is possible if advised by spatialAwareness
                    num0 -= 2; //moving is not particularly advised
                }
                if (getLettersDiscovered() == 5) { //if all the human's letters have already been discovered
                    num3 += 2; //guessing is possible
                    num0--; //moving is not so helpful
                    num2--; //saving is not so helpful
                }
                if (getActionsSaved() > 3) { //if more than 3 actions are saved-up
                    num5 += 2; //regaining is possible
                    num2 -= 2; //saving is less smart
                }
                break;
            case 4:
            case 5:
            default: //MANY
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num3 += 7; //guessing is most likely
                    num1 += 3; //boarding is reasonable if advised by spatialAwareness
                } else {
                    num0 += 5; //moving is fairly smart
                    num2 += 3; //saving is reasonable
                    num4 += 2; //extending is possible
                }
                break;
        }

        //FIFTH EXAMINED ASPECT: What was the last action the computer took and what happened?
        switch (getLastAction()) {
            case "gameStarts":
            case "save":
            case "guess":
                //fresh start of sorts
                num0 += 6; //moving is pretty smart
                num2 += 3; //saving is reasonable
                num4++; //extending is just feasible
                if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                    num5 += 2; //regaining is possible
                    num2--; //saving is less helpful
                    num0--; //moving is also less helpful
                }
                break;
            case "move":
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    if (getLettersDiscovered() == 5) { //if all of the letters have already been found
                        num0 += 3; //moving is reasonable
                        num2 += 4; //saving is fairly likely
                        num4 += 2; //extending is possible
                        num3++; //guessing is just feasible
                        if (getActionsSaved() > 3) { //with more than 3 actions saved
                            num5 += 3; //regaining is reasonable
                            num4 -= 2; //extending is less helpful
                            num2--; //saving is less helpful
                        }
                    } else {
                        num1 += 8; //boarding is very likely if advised by spatialAwareness
                        num2++; //saving is just feasible
                        num4++; //extending is just feasible
                        if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                            num5 += 3; //regaining is reasonable
                            num2--; //saving is not so helpful
                            num4--; //extending isn't as smart
                            num1--; //boarding could be risky
                        }
                    }
                } else {
                    num0 += 5; //moving is fairly smart
                    num2 += 4; //saving is fairly likely
                    num4++; //extending is just feasible
                    if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                        num5 += 2; //regaining is possible
                        num2--; //saving is less helpful
                        num0--; //moving is less helpful
                    }
                }
                break;
            case "board":
                switch(getLastCode()[1]) {
                    case "ship":
                        //slightly more confident than usual
                        num0 += 5; //moving is fairly smart
                        num2 += 2; //saving is possible
                        num4 += 2; //extending is possible
                        num3++; //guessing is just feasible
                        if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                            num5 += 2; //regaining is possible
                            num2--; //saving is not as helpful
                            num0--; //moving is not as helpful
                        }
                        if (getLettersDiscovered() > 4) { //if 4 or more letters have been discovered
                            num3++; //guessing is just feasible
                            num0--; //moving is not as helpful
                        }
                        break;
                    case "mine":
                        //slightly less confident than usual
                        num0 += 6; //moving is pretty smart
                        num2 += 3; //saving is reasonable
                        num4++; //extending is just feasible
                        if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                            num5++; //regaining is just feasible
                            num0--; //moving is not as likely
                        }
                        break;
                    default:
                        num0 += 6; //moving is pretty smart
                        num2 += 2; //saving is possible
                        num4 += 2; //extending is possible
                        if (getActionsSaved() > 3 && getChances() < 3) { //with less than 3 chances and 4 or more actions saved-up
                            num5++; //regaining is just feasible
                            num0--; //moving is not as helpful
                        }
                        break;
                }
                break;
            case "extend":
                num0 += 9; //moving is extremely smart
                num2++; //saving is just feasible
                if (getActionsSaved() > 3) { //with more than 3 actions saved-up
                    num5++; //regaining is just feasible
                    num2--; //saving is not so constructive
                }
                if (getLettersDiscovered() > 3) { //with more than 3 of the humans's letters found
                    num3++; //guessing is just feasible
                    num0--; //moving is not as helpful as usual
                }
                if (spatialAwareness(hidGrid, getLastCode()[0]).equals("board")) {
                    num1 += 2; //boarding is possible if advised by spatialAwareness
                    num0 -= 2; //moving is less helpful
                }
                break;
            case "regain a chance":
                num0 += 3; //moving is reasonable
                num1++; //boarding is just feasible
                num2 += 3; //saving is reasonable
                num4 += 3; //extending is reasonable
                if (getLettersDiscovered() > 3) { //with 4 or more of the human's letters discovered
                    num3 += 2; //guessing is possible
                    num0--; //moving is less helpful
                    num1--; //boarding is more likely to lead to discovering mines than letters
                }
                if (getChances() < 3 && getActionsSaved() > 3) { //with less than 3 chances and 4 or more actions saved-up
                    num5 += 3; //regaining is reasonable
                    num0--; //moving is less advisable
                    num4 -= 2; //extending isn't as helpful
                }
                break;
        }

        //This is a way of 'putting the chances into the hat' -- there is a for loop for each possible action and
        //the number of that action accumulated during the examination of the five aspects will be added to the
        //array to be chosen from randomly.

        int i = 0; //the index in possibilities that has been reached so far
        for (int a = 0; a < num0; a++) {
            possibilities[i] = 0;
            i++;
        }
        for (int b = 0; b < num1; b++) {
            possibilities[i] = 1;
            i++;
        }
        for (int c = 0; c < num2; c++) {
            possibilities[i] = 2;
            i++;
        }
        for (int d = 0; d < num3; d++) {
            if (getLettersDiscovered() == 0 && counter % 15 != 0) {
                //if no letters have been found the improbability of choosing to guess the word is exaggerated
                possibilities[i] = 0;
            } else {
                possibilities[i] = 3;
            }
            i++;
        }
        for (int e = 0; e < num4; e++) {
            if (getActionsSaved() > 0) { //impossible to extend if no actions have been saved
                possibilities[i] = 4;
            } else {
                possibilities[i] = 0;
            }
            i++;
        }
        for (int f = 0; f < num5 && i < 50; f++) {
            possibilities[i] = 5;
            i++;
        }

        return possibilities;
    }

    public String spatialAwareness(Position[][] hidGrid, String direction) {
        //This function simulates the general awareness the human has for the positions immediately surrounding their
        //player. It includes a moderate aversion to moving to previously visited positions, an inclination to board
        //positions which one has recently discovered hide something and a complete disinclination to board positions
        //holding something already found or holding the human player.
        String message = "";
        int xIQ = chooseDirection(direction)[0];
        int yIQ = chooseDirection(direction)[1];

        String stat = hidGrid[xIQ][yIQ].getStatus();

        if (getLastCode()[1].equals("blocked")) {
            message = "board";
        }

        if (!stat.equals("empty") && !stat.equals("hidHumShip") && !stat.equals("hidLandmine") && !stat.equals("visited")) {
            message = "null";
        }

        if (stat.equals("hidCompShip") || stat.equals("foundCompShip")) {
            message = "ownShip";
        }

        if (stat.equals("foundLandmine")) {
            message = "mine";
        }

        if (stat.equals("foundHumShip")) {
            message = stat;
        }

        if (stat.equals("visited") && !(counter % 6 == 0)) {
            message = "notAgain";
        }

        if (hidGrid[xIQ][yIQ].getPlayerIsPresent().equals("hum")) {
            message = "hum";
        }
        return message; //returns a short piece of information about a position one spot away in a particular direction
    }

    public String intuition(Position[][] hidGrid) {
        //This function only runs when the computer player is not on an edge of the grid. Because the computer typically
        //has a difficult time orienting itself late in the game in the middle of mostly visited positions, this
        //increases its chances of boarding a human letter earlier on -- as if it just has a 'gut feeling' something is
        //there. If it is next to a human letter and this function is run, there is a 50% chance it will have a 'gut feeling'.

        String gutFeeling = "idk";
        String[] directions = {"N", "E", "S", "W", "NE", "NW", "SE", "SW"};
        String stat = ""; //status of an indicated position

        for (int i = 0; i < 8; i++) {
            switch(directions[i]) {
                case "N":
                    stat = hidGrid[getxPos()][getyPos() - 1].getStatus();
                    break;
                case "E":
                    stat = hidGrid[getxPos() + 1][getyPos()].getStatus();
                    break;
                case "S":
                    stat = hidGrid[getxPos()][getyPos() + 1].getStatus();
                    break;
                case "W":
                    stat = hidGrid[getxPos() - 1][getyPos()].getStatus();
                    break;
                case "NE":
                    stat = hidGrid[getxPos() + 1][getyPos() - 1].getStatus();
                    break;
                case "NW":
                    stat = hidGrid[getxPos() - 1][getyPos() - 1].getStatus();
                    break;
                case "SE":
                    stat = hidGrid[getxPos() + 1][getyPos() + 1].getStatus();
                    break;
                case "SW":
                    stat = hidGrid[getxPos() - 1][getyPos() + 1].getStatus();
                    break;
            }

            if (stat.equals("hidHumShip")) {
                int x = (int) (2.0 * Math.random());
                if (x % 2 == 0) {
                    gutFeeling = directions[i];
                }
            }
        }
        return gutFeeling; //returns a direction or 'idk'
    }

    public boolean inBoundaries(String direction) {
        //used when the computer selects a direction to make sure it is in bounds before it tries to move or checks status, etc.

        boolean goAhead = true;
        switch (direction) {
            case "N":
                if (getyPos() == 0) {
                    goAhead = false;
                }
                break;
            case "E":
                if (getxPos() == 9) {
                    goAhead = false;
                }
                break;
            case "S":
                if (getyPos() == 9) {
                    goAhead = false;
                }
                break;
            case "W":
                if (getxPos() == 0) {
                    goAhead = false;
                }
                break;
            case "NE":
                if (getyPos() == 0 || getxPos() == 9) {
                    goAhead = false;
                }
                break;
            case "NW":
                if (getyPos() == 0 || getxPos() == 0) {
                    goAhead = false;
                }
                break;
            case "SE":
                if (getyPos() == 9 || getxPos() == 9) {
                    goAhead = false;
                }
                break;
            case "SW":
                if (getyPos() == 9 || getxPos() == 0) {
                    goAhead = false;
                }
                break;
        }
        return goAhead;
    }

    public String pointer (Position[][] hidGrid) {
        String[] directions = {"N", "E", "S", "W", "NE", "NW", "SE", "SW"};

        boolean goAhead = false; //<-- whether or not it is possible to move in a selected direction
        boolean free = true; //<-- whether or not the computer is away from all edges

        String suggestion = "N"; //just to initialize

        while (!goAhead) { //picks random directions until it is possible to move or board in a suggested direction
            suggestion = directions[(int) (8.0 * Math.random())];
            goAhead = inBoundaries(suggestion);
        }

        for (int i = 0; i < 8; i++) { //checks if the computer is away from all edges
            if (!inBoundaries(directions[i])) {
                free = false;
            }
        }

        if (free) { //if it IS away from all edges it can use the intuition function
            String thought = intuition(hidGrid);
            if (!thought.equals("idk")) {
                suggestion = thought;
            }
        }

        return suggestion; //returns a direction for moving or boarding
    }

    public String generateGuess() {
        //The computer first builds a long string (allTogetherNow) of all the words from its list of possible guesses
        //which include all of the letters which it has discovered so far.

        StringBuilder allTogetherNow = new StringBuilder();

        for (int i = 0; i < getPossGuesses().length; i++) {
            //This process will be repeated once for each word in the list of words the computer could use as a guess.

            boolean[] confirmations = new boolean[] {true, true, true, true, true, true};
            //The confirmations each represent whether individual letters from the set of discovered letters are in
            //the current word in question. The first is true but doesn't represent a letter because it will always
            //check the previous confirmation so that once one letter discovered isn't in the word it can't be marked
            //as viable.

            for (int j = 1; j < 6; j++) {
                String currentLetter = getOppLetters()[j - 1];

                boolean isIn = false;
                for (int k = 0; k < 5; k++) {
                    //'1' is the default placeholder for a letter that hasn't yet been discovered, so if the current
                    //letter is '1' it will be marked as in the word; if others aren't in the word it won't be marked
                    //as viable. If no letters have been discovered all words will be viable.
                    if (currentLetter.equals(getPossGuesses()[i].substring(k, k + 1)) || (confirmations[j - 1] && currentLetter.equals("1"))) {
                        isIn = true;
                    }
                }
                confirmations[j] = isIn;
            }

            //If all letters are marked as in the word the word will be added to allTogetherNow.
            if (confirmations[1] && confirmations[2] && confirmations[3] && confirmations[4] && confirmations[5]) {
                allTogetherNow.append(getPossGuesses()[i]);
            }
        }

        //Next, it will turn this string into an array of strings (viables), so that each word is on its own again but
        //now in a collection of only those words from the list which might work.

        String[] viables = new String[allTogetherNow.length()/5]; //All words are five letters long
        for (int i = 0; i < viables.length; i++) {
            viables[i] = allTogetherNow.substring(i*5, i*5 + 5);
        }

        //Finally, it will pick a random word from this list of equally viable words as its guess.

        int justPick = (int)((viables.length) * Math.random());
        return viables[justPick];
    }

    //getters and setters below

    public String[] getLastCode() {
        return lastCode;
    }

    public void setLastCode(String[] lastCode) {
        this.lastCode = lastCode;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public int getOwnHitShips() {
        return ownHitShips;
    }

    public void setOwnHitShips(int ownHitShips) {
        this.ownHitShips = ownHitShips;
    }

    public String[] getPossGuesses() {
        return possGuesses;
    }
}