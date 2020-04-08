import java.util.*;

public class Human extends Player {
    Scanner hsc = new Scanner(System.in);

    public Human () {
        super("Human");
    }

    public void username() {
        System.out.println("Enter a username:");
        String userName = hsc.nextLine();

        System.out.println("Hello and welcome, " + userName + ". Would you like to read the directions?\n        Type " +
                "'yes' if so or anything else to continue straight on to the game.");

        if (hsc.nextLine().toLowerCase().equals("yes")) {
            directions();
        } else {
            System.out.println("Continuing without directions.\n");
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                System.out.println("Insomnia");
            }
        }
        setName(userName);
    }

    public void directions () {
        System.out.println("Okay!\n");

        try {
            Thread.sleep(900);
        } catch (InterruptedException e) {
            System.out.println("Insomnia");
        }

        System.out.println("This game involves two players: a human and a computer.\n");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Insomnia");
        }

        System.out.println("SET-UP\n" +
                "        Each player starts with a five-letter word and five chances to guess the other player's word.\n" +
                "        The game is played on a 10 x 10 grid; most spots will be marked \uD83D\uDD35. When the players receive their\n" +
                "        words, they will each scatter the letters from their word around the grid, where they will be able\n" +
                "        to view them as a lowercase letter.                                                        [continues]\n");

        try {
            Thread.sleep(7100);
        } catch (InterruptedException e) {
            System.out.println("Insomnia");
        }

        System.out.println("TURNS\n" +
                "        The players take turns executing ACTIONS. One turn generally consists of two actions.\n" +
                "        For each action, a player has the choice to:\n" +
                "              a.) MOVE one spot over -- vertically, horizontally or diagonally;\n" +
                "              b.) attempt to BOARD a spot one movement away from them vertically, horizontally or diagonally;\n" +
                "              c.) SAVE an action;\n" +
                "              d.) EXTEND their turn using saved-up actions;\n" +
                "              e.) attempt to GUESS their opponent's word;\n" +
                "              or\n" +
                "              f.) REGAIN A CHANCE to guess their opponent's word using 4 saved-up actions.         [continues]\n");

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            System.out.println("Insomnia");
        }

        System.out.println("BOARDING\n" +
                "        If a player boards a spot containing one of their opponent's letters, it becomes visible as a\n" +
                "        capital letter and they will have an easier time guessing the opponent's word.             [continues]\n");

        try {
            Thread.sleep(4300);
        } catch (InterruptedException e) {
            System.out.println("Insomnia");
        }

        System.out.println("        However, there is a risk involved in boarding.\n" +
                "        A player will be notified upon choosing to board a spot whether or not there is something present;\n" +
                "        if so, it could be a letter -- or it could be a MINE. \uD83D\uDCA3                                   [continues]\n");

        try {
            Thread.sleep(4800);
        } catch (InterruptedException e) {
            System.out.println("Insomnia");
        }

        System.out.println("        If a player boards a mine, they lose a chance to guess the opponent's word. The number of mines\n" +
                "        randomly scattered around the board depends on the level selected by the human player.     [continues]\n");

        try {
            Thread.sleep(5500);
        } catch (InterruptedException e) {
            System.out.println("Insomnia");
        }

        System.out.println("SYMBOLS\n" +
                "        The symbols on the grid are as follows:\n" +
                "             \uD83D\uDD35 - a blank spot, or the location of either a hidden letter or mine\n" +
                "             ⚫ - a blank spot previously vacated by a player\n" +
                "             \uD83D\uDCA3 - a previously discovered mine\n" +
                "             \uD83D\uDCA5 - the site of a letter of one's own which has been discovered\n" +
                "             ⛵ - the human player\n" +
                "             \uD83D\uDEA2 - the computer player\n" +
                "\n" +
                "        and capital or lowercase letters designating, respectively, letters of the opponent's word\n" +
                "        which have been discovered or letters of one's own word which have not.                        [over]\n");

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            System.out.println("Insomnia");
        }
    }

    public void scatterWord(Position[][] hidGrid) {
        System.out.println("Your word is '" + getWord() + "'.\n");
        System.out.println("Would you like to scatter your own letters?\n        Type 'scatter' if so, or" +
                " type anything else to have it done for you.");

        boolean automatic = true;
        if (hsc.next().equals("scatter")) {
            automatic = false;
        }

        for (int i = 0; i < this.getWord().length(); i++) {
            int x = (int)(10.0 * Math.random());
            int y = (int)(10.0 * Math.random());

            if (automatic) {
                Position pos = new Position();
                String letter = getWord().substring(i, i + 1);
                pos.setLetter(letter);
                pos.setStatus("hidHumShip");
                if (hidGrid[x][y] == null) { //human letters are the only positions in the grid right now
                    hidGrid[x][y] = pos;
                    System.out.println("Automatically scattering letter " + letter + "...");
                } else {
                    i--; //i dictates which letter of the word; so if there's already a letter there it will be redone
                }

                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    System.out.println("Insomnia");
                }

            } else {
                System.out.println("You are now going to place the letter " + getWord().substring(i, i + 1) + ". Select " +
                        "column first, from 1 to 10.");
                x = coordinateHandling(x);

                System.out.println("Now, select a row, again from 1 to 10.");
                y = coordinateHandling(y);

                Position pos = new Position();
                pos.setLetter(getWord().substring(i, i + 1));
                pos.setStatus("hidHumShip");

                if (hidGrid[x][y] != null) {
                    System.out.println("You have already placed a letter in that spot. It will be placed randomly for you.");
                    x = (int) (10.0 * Math.random());
                    y = (int) (10.0 * Math.random());
                }
                hidGrid[x][y] = pos;
            }
        }
        System.out.println("Word scattered.\n");

        for (int i = 0; i < hidGrid.length; i++) {
            for (int j = 0; j < hidGrid[i].length; j++) {
                if (hidGrid[i][j] == null) {
                    hidGrid[i][j] = new Position();
                }
            } //creates the rest of the hidGrid
        }
    }

    public int coordinateHandling(int num) {
        //a function to ensure that all entries are integers within bounds
        if (hsc.hasNextInt()) {
            int w = hsc.nextInt();
            if (w <= 1 || w >= 11)  {
                System.out.println("That didn't work. Please try again -- make sure your entry is a number between one and ten.");
                return coordinateHandling(num);
            }
            num = w;
        } else {
            System.out.println("That didn't work. Please try again -- make sure your entry is a number between one and ten.");
            return coordinateHandling(num);
        }
        return num;
    }

    public void firstSpot(Position[][] hidGrid) {
        //Allows the human player to pick the first position on which they want to land; during this turn only, they
        //can land on a position of any status, so that they pick only once and with no knowledge of the board. If they
        //land on something hidden, when they move off the spot it will be blue, not black.

        int x = 0; //just to initialize
        int y = 0;
        System.out.println("\nPick a spot from 1 to 10 on which to land first. You will have to move spot by spot " +
                "from here for the rest of the game.");
        System.out.println("Column: ");
        x = coordinateHandling(x);
        this.setxPos(x);

        System.out.println("Row: ");
        y = coordinateHandling(y);
        this.setyPos(y);

        hidGrid[x][y].setPlayerIsPresent("hum");
    }

    public void setLandmines(Position[][] hidGrid) {
        int numMines;
        System.out.println("Type 'easy', 'medium' or 'hard' to select game difficulty");
        //Level mostly only affects number of mines except that the computer is more likely to board a human letter
        //than a mine so as the percentage of mines among hidden things increases, the computer also gets better at
        //playing compared to the human.

        String levelRequest = psc.next();
        switch (levelRequest) {
            case "easy":
                System.out.println("Your level is EASY (there are 5 mines).");
                numMines = 5;
                break;
            case "medium":
                System.out.println("Your level is MEDIUM (there are 10 mines).");
                numMines = 10;
                break;
            case "hard":
                System.out.println("Your level is HARD (there are 15 mines).");
                numMines = 15;
                break;
            default:
                System.out.println("Please try again.");
                setLandmines(hidGrid);
                numMines = 0;
                break;
        }

        int randomX = (int)(10.0 * Math.random());
        int randomY = (int)(10.0 * Math.random());

        for (int i = 0; i < numMines; i++) {
            Position pos = new Position();
            pos.setStatus("hidLandmine");

            if (hidGrid[randomX][randomY].getStatus().equals("empty") && hidGrid[randomX][randomY].getPlayerIsPresent().equals("empty")) {
                hidGrid[randomX][randomY] = pos;
            }

            randomX = (int)(10.0 * Math.random());
            randomY = (int)(10.0 * Math.random());
        }
    }

    public void turn (int numActions, Position[][] hidGrid) {
        while (numActions > 0) {
            System.out.println("\nFor this action, your choices are to MOVE, BOARD a spot next to you, SAVE an action," +
                    " EXTEND your turn, attempt to GUESS the opponent's word or\nREGAIN a chance to do so using 4 saved actions. " +
                    "\n        Please choose by typing 'move', 'board' or 'save', 'extend', 'guess' or 'regain'.'");

            switch (hsc.next().toLowerCase()) {
                case "move":
                    System.out.println("You have chosen to move. Please indicate compass direction by typing" +
                            " 'N', 'E', 'S', 'W', 'NE', 'NW', 'SE' or 'SW'.");
                    int[] mIQ = chooseDirection(hsc.next().toUpperCase());
                    //move in question (mIQ) becomes an array holding x and y coordinates adjusted from the current position
                    //based on the direction selected by the human player

                    String statusmIQ = hidGrid[mIQ[0]][mIQ[1]].getStatus();
                    if ((statusmIQ.equals("visited") || statusmIQ.equals("empty")) && hidGrid[mIQ[0]][mIQ[1]].getPlayerIsPresent().equals("empty")) {
                        hidGrid[this.getxPos()][this.getyPos()].setPlayerIsPresent("empty");
                        setxPos(mIQ[0]);
                        setyPos(mIQ[1]);
                        hidGrid[this.getxPos()][this.getyPos()].setPlayerIsPresent("hum");
                    } else {
                        System.out.println("There is something in the way here. You cannot move to this spot.");
                    }
                    break;
                case "board":
                    System.out.println("You have chosen to board. \nPlease indicate compass direction by typing " +
                            "'N', 'E', 'S', 'W', 'NE', 'NW', 'SE' or 'SW'.");
                    int[] bIQ = chooseDirection(hsc.next().toUpperCase());
                    //board in question (bIQ) becomes an array holding x and y coordinates adjusted from the current position
                    //based on the direction selected by the human player

                    String statusbIQ = hidGrid[bIQ[0]][bIQ[1]].getStatus();
                    if (statusbIQ.equals("empty") || statusbIQ.equals("visited")) {
                        System.out.println("There is nothing to board at the selected spot. Your action is over.");
                    } else {
                        System.out.println("There is something here to board. Would you like to proceed with boarding? \n     " +
                                "   Please type 'yes' to proceed or anything else to stop here.");

                        if(hsc.next().toLowerCase().equals("yes")) {
                            switch (statusbIQ) {
                                case "hidHumShip":
                                case "foundHumShip":
                                    System.out.println("You are trying to board your own letter. Action terminated without result.");
                                    numActions++; //this action will finish and numActions will decrease by one but this was used for human
                                    //error or not understanding the game; this is a second chance
                                    break;
                                case "hidCompShip":
                                    System.out.println("Congratulations, you have boarded a ship! The letter here is " + hidGrid[bIQ[0]][bIQ[1]].getLetter() + ".");

                                    String[] soFar = getOppLetters(); //created in order to easily write to one value at a time in the array
                                    soFar[getLettersDiscovered()] = hidGrid[bIQ[0]][bIQ[1]].getLetter();
                                    setOppLetters(soFar);

                                    setLettersDiscovered(getLettersDiscovered() + 1);
                                    hidGrid[bIQ[0]][bIQ[1]].setStatus("foundCompShip");
                                    break;
                                case "foundCompShip":
                                    System.out.println("You have already boarded this ship. The letter here is " + hidGrid[bIQ[0]][bIQ[1]].getLetter() + ".");
                                    numActions++;
                                    break;
                                case "hidLandmine":
                                    hidGrid[bIQ[0]][bIQ[1]].setStatus("foundLandmine");
                                    setChances(getChances() - 1);
                                    System.out.println("Unfortunately, you have just boarded a mine. You " +
                                            "lose a chance to guess your opponent's word. Chances left: " + getChances());
                                    break;
                                case "foundLandmine": //may be done as some self-destructive event when the player no longer wants to play
                                    System.out.println("You have already found this mine. \n        Type 'A' to end the game or type " +
                                            "anything else to terminate this action without result.");

                                    if ("A".equals(hsc.next())) {
                                        System.out.println("Game over.");
                                        setChances(0); //game will not continue to next turn in runner
                                        numActions = 1; //turn will not continue to next action
                                    } else {
                                        System.out.println("Action terminated without result.");
                                        numActions++;
                                    }
                                    break;
                                default:
                                    System.out.println("Something went amiss.");
                                    break;
                            }
                        } else {
                            System.out.println("Action terminated."); //if they decided not to board
                        }
                    }
                    break;
                case "save":
                    saveAnAction();
                    System.out.println("Action saved. Saved action(s): " + getActionsSaved());
                    break;
                case "extend":
                    System.out.println("You have " + getActionsSaved() + " action(s) saved.");

                    if (getActionsSaved() > 0) {
                        System.out.println("Type the number of saved action(s) you would like to add to this turn.");
                        //At the end of each action, numActions decreases by 1, so 1 extra will be added to numActions here
                        //in addition to any extension because extending doesn't take up an action.

                        if (hsc.hasNextInt()) {
                            int request = hsc.nextInt();

                            if (request <= getActionsSaved()) {
                                numActions += request + 1;
                                for (int i = 0; i < request; i++) {
                                    loseAnAction(); //saved-up ones being spent
                                }
                                System.out.println("Your turn has been successfully extended.");

                            } else {
                                System.out.println("You do not have enough actions saved to extend your \nturn by that " +
                                        "number. Action terminated without result.");
                                numActions++;
                            }
                        } else {
                            System.out.println("Error encountered. By default, one action has been added to your turn.");
                            numActions += 2;
                        }
                    } else {
                        System.out.println("You cannot extend your turn.");
                        numActions++;
                    }
                    break;
                case "guess":
                    System.out.println("You have chosen to attempt to guess the opponent's word. \nCareful: you will not get another chance to make this attempt. You have " + getChances() + " chances remaining. Type your guess below.");

                    try {
                        Thread.sleep(1300);
                    } catch (InterruptedException e) {
                        System.out.println("Insomnia");
                    }

                    boolean success = guess(hsc.next().toUpperCase());
                    if (success) {
                        System.out.println("Congratulations! You have guessed the computer's word!");
                        System.out.println("Game over -- " + getName() + " wins!");
                        numActions = 1; //turn will not continue to next action
                        setChances(0); //game will not continue to next turn in Runner
                    } else {
                        setChances(getChances() - 1);

                        System.out.println("Apologies. That is incorrect. You now have " + getChances() + " chances remaining.");

                        try {
                            Thread.sleep(900);
                        } catch (InterruptedException e) {
                            System.out.println("Insomnia");
                        }

                        if (getChances() == 0) {
                            System.out.println("That was your last chance. Game over -- computer wins.");
                            numActions = 1; // turn will not continue to next action
                        }
                    }
                    break;
                case "regain":
                    regainChance();
                    break;
                default:
                    System.out.println("Error encountered. Please try again.");
                    turn(numActions, hidGrid);
                    break;
            }
            numActions--;

            if (getChances() > 0) {
                printCurrentGrid(makeHumView(hidGrid));
                System.out.println(numActions + " action(s) left.");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Insomnia");
                }
            }
        }
        if (getChances() > 0) {
            System.out.println("Computer's turn.");
        }
    }
}