import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class GrassFire{

    //Grid holds information regarding the randomized map, offering a visual represenetation of the problem
    public static char[][] grid;
    //Grass holds information regarding which squares were "burned," as well as their level, represented as an integer.
    public static int[][] grass;
    //Frontier keeps track of all the nodes on the outskirts of the fire, so we know which nodes to burn next.
    public static Stack<int[]> frontier;

    //These are global variables representing the size of the grid.
    public static int xbounds;
    public static int ybounds;

    //These variables store the starting and finishing tile locations
    public static int startingx;
    public static int startingy;
    public static int finishx;
    public static int finishy;

    public static int wait;


    public static void main(String[] args){

        wait = 2000;
        frontier = new Stack<int[]>();
        xbounds = 0;
        ybounds = 0;

        Scanner s = new Scanner(System.in);

        //Welcome screen
        System.out.println();
        System.out.println("= ".repeat(33));
        System.out.println();
        System.out.println("Welcome to this demonstration of the Grassfire traversal algorithm.\n");
        System.out.println("= ".repeat(33));
        System.out.println();
        try { Thread.sleep(wait); } catch (InterruptedException e) {}

        //Gaining inputs to generate a random map
        System.out.println("\nWhat size of map would you like? format: \"x y\". Please enter values between 8 and 15 only.");
        do { 
            ybounds = s.nextInt();
            xbounds = s.nextInt();
            if (xbounds < 8 || ybounds < 8 || xbounds > 15 || ybounds > 15)
                System.out.println("Please enter values only between 8 and 15.");
        } while (xbounds < 8 || ybounds < 8 || xbounds > 15 || ybounds > 15);

        System.out.println("\nPlease enter the desired percentage of walls (between 10 and 20)");
        float walls = 0;
        do {
            walls = s.nextFloat();
            if (walls < 10 || walls > 20)
            System.out.println("Please enter a number between 10 and 20.");
        } while (walls < 10 || walls > 20);

        System.out.println("\nPlease enter a number inbetween 1 and " + ybounds + " to initialize the starting node.");
        do {
            startingy = s.nextInt();
            if (startingy < 1 || startingy > ybounds)
                System.out.println("Please enter a number inbetween 1 and " + ybounds);
        } while (startingy < 1 || startingy > ybounds);
        startingx = 0;
        startingy--;

        System.out.println("\nPlease enter coordinates for the finish node in the format \"x y\".");
        System.out.println("!! X must be greater than " + Math.round((ybounds * 2 / 3) + .5) + " and less than " + ybounds + " !!");
        System.out.println("!! Y must be greater than " + Math.round((xbounds / 2)) + " and less than " + xbounds + " !!");
        do {
            finishy = s.nextInt();
            finishx = s.nextInt();
            if (finishy < Math.round((ybounds * 2 / 3)+.5) || finishy > ybounds || finishx < Math.round(xbounds / 2) || finishx > xbounds)
                System.out.println("Please enter a valid coordinate.");
        } while (finishy < Math.round((ybounds * 2 / 3)+.5) || finishy > ybounds || finishx < Math.round(xbounds / 2) || finishx > xbounds);

        finishy--;
        finishx--;

        //Here I gain the coordinates of the finishing square, while simletaneously generating a randomized grid
        int[] finish = generateGrid(xbounds, ybounds, Math.round(walls));

        //I push the coordinates of the finishing node to frontier, so we can keep track of which nodes to "burn"
        frontier.push(finish);

        //Printing the non - traversed node
        try { Thread.sleep(wait); } catch (InterruptedException e) {}
        System.out.println("-  ".repeat(ybounds + 1));
        System.out.println("Here is your randomly generated map: ");
        printGrid();

        try { Thread.sleep(wait); } catch (InterruptedException e) {}

        //Using a grassfire algorithm to find the best path
        grassfire(0);

        //Once the grassfire algorithm shows is the best path to take, we can simply traverse one of the best paths.
        traversePath(startingx, startingy);

        //Print the result.
        try { Thread.sleep(wait); } catch (InterruptedException e) {}
        System.out.println("-  ".repeat(ybounds + 1));
        System.out.println("Here is the optimal solution: ");
        try { Thread.sleep(wait); } catch (InterruptedException e) {}
        printGrid();
    }

    public static void traversePath(int x, int y){

        boolean possible = false;
        //If we have reached the final square, we can end recursion
        if (grid[x][y] == 'F')
            return;
        
        //If the square given is not the starting node, we can change it to "*" to denote it has been travelled to.
        if (grid[x][y] != 'S')
            grid[x][y] = '*';

        //We need to check 4 squares, and pick the cheapest one to travel to.
        
        int up = 9999999;
        int down = 9999999;
        int left = 9999999;
        int right = 9999999;

        int min = 9999999;

        if (x + 1 < xbounds && grass[x + 1][y] != -1){
            down = grass[x + 1][y];
            min = Math.min(min, down);
            possible = true;
        }
        if (x - 1 >= 0 && grass[x - 1][y] != -1){
            up = grass[x - 1][y];
            min = Math.min(min, up);
            possible = true;
        }
        if (y + 1 < ybounds && grass[x][y + 1] != -1){
            right = grass[x][y + 1];
            min = Math.min(min, right);
            possible = true;
        }
        if (y - 1 >= 0 && grass[x][y - 1] != -1){
            left = grass[x][y - 1];
            min = Math.min(min, left);
            possible = true;
        }

        //If there are no viable moves, we return.
        if (possible == false){
            return;
        }

        if (up == min){
            traversePath(x - 1, y);
        }
        else if (down == min){
            traversePath(x + 1, y);
        }
        else if (left == min){
            traversePath(x, y - 1);
        }
        else if (right == min){
            traversePath(x, y + 1);
        }
        else{
            System.out.println("Error");
            return;
        }
    }

    public static int[] generateGrid(int x, int y, int wallPercent){
        
        grid = new char[x][y];
        grass = new int[x][y];

        Random r = new Random();
        int roll = 0, x1, x2, y1, y2;
        char tile = ' ';

        for(int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                r.setSeed(System.nanoTime());
                roll = r.nextInt(100);

                if (roll < wallPercent)
                    tile = 'X';
                else
                    tile = ' ';

                grid[i][j] = tile;
                grass[i][j] = -1;
            }
        }

        // do {
        // //Set the start tile
        // r.setSeed(System.nanoTime());
        // x1 = r.nextInt(x);
        // r.setSeed(System.nanoTime());
        // y1 = r.nextInt(y);

        // //Set a finish tile
        // r.setSeed(System.nanoTime());
        // x2 = r.nextInt(x);
        // r.setSeed(System.nanoTime());
        // y2 = r.nextInt(y);

        // } while (x1 == x2 && y1 == y2);

        // grid[x1][y1] = 'S';
        // grid[x2][y2] = 'F';

        // int[] finish = {x2, y2};
        // startingx = x1;
        // startingy = y1;

        grid[startingx][startingy] = 'S';
        grid[finishx][finishy] = 'F';

        int[] finish = {finishx, finishy};

        return finish;
    }

    private static void printGrid(){
        System.out.println("-  ".repeat(ybounds + 1));
        for (int i = 0; i < xbounds; i++){
            System.out.print("|");
            for (int j = 0; j < ybounds; j++){
                System.out.print(" " + grid[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("-  ".repeat(ybounds + 1));
    }

    private static void printGrass(){
        System.out.println("- \t".repeat(2 * xbounds + 1));
        for (int i = 0; i < xbounds; i++){
            System.out.print("|");
            for (int j = 0; j < ybounds; j++){
                System.out.print("\t" + grass[i][j] + "\t");
            }
            System.out.print("|");
            System.out.println();
            System.out.println();
        }
        System.out.println("- \t".repeat(2 * xbounds + 1));
    }

    private static void grassfire(int level) {

        //Grassfire will "burn" one layer surrounding it at a time, given a set of outskirt nodes
        Stack<int[]> addToFrontier = new Stack<int[]>();

        //I iterate through each element in the burn pile, updating the grass array, and adding new 
        // squares to the frontier for the next level of recursion.
        for (int i = frontier.size(); i > 0; i--){

            int[] coords = frontier.pop();
            int x = coords[0];
            int y = coords[1];

            grass[x][y] = level;

            //x + 1
            if (x + 1 < xbounds && grass[x + 1][y] == -1 && grid[x + 1][y] != 'X'){
                int[] c = {x + 1, y};
                addToFrontier.push(c);
            }

            //x - 1
            if (x - 1 >= 0 && grass[x - 1][y] == -1 && grid[x - 1][y] != 'X'){
                int[] c = {x - 1, y};
                addToFrontier.push(c);
            }

            //y + 1
            if (y + 1 < ybounds && grass[x][y + 1] == -1 && grid[x][y + 1] != 'X'){
                int[] c = {x, y + 1};
                addToFrontier.push(c);
            }

            //y - 1
            if (y - 1 >= 0 && grass[x][y - 1] == -1 && grid[x][y - 1] != 'X'){
                int[] c = {x, y - 1};
                addToFrontier.push(c);
            }
        }

        for (int i = addToFrontier.size(); i > 0; i--){
            frontier.push(addToFrontier.pop());
        }

        //If we have no more tiles in our frontier, we know that we're done.
        if (frontier.size() == 0)
            return;
        else{
            grassfire(level + 1);
        }
    }
}