public class Runner {

    public static void main (String[] args) {
        Human human = new Human();
        Computer computer = new Computer();
        Position[][] hidGrid = new Position[10][10]; //'hid' because it isn't the view that we get

        human.username();
        computer.setOppName(human.getName());

        human.setWord();
        computer.setOppWord(human.getWord());

        computer.setWord();
        human.setOppWord(computer.getWord());

        human.scatterWord(hidGrid);
        computer.scatterWord(hidGrid);

        human.setLandmines(hidGrid);

        human.firstSpot(hidGrid);
        computer.firstSpot(hidGrid);

        System.out.println("STARTING BOARD: ");
        human.printCurrentGrid(human.makeHumView(hidGrid));

        while (human.getChances() > 0 && computer.getChances() > 0) {
            human.turn(2, hidGrid);
            computer.setOwnHitShips(human.getLettersDiscovered());
            if (human.getChances() > 0 && computer.getChances() > 0) {
                computer.turn(2, hidGrid);
            }
        }
    }

}