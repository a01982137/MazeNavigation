/* RAT MAZE TRAVERSAL 
 * Ruby Sun
 * 13/12/2022
 * Java, Neon.1 Release (4.6.1)
 * =======================================================
 * Problem Definition: Required to find the shortest path in a maze, given a rat, a cheese and the exit
 * 	(must show the shortest path from rat to cheese and from the cheese to the exit + output the amount of steps it'd take)
 * Input: read a file, user inputs whether they want to repeat the program or not 
 * Output: the amount of steps from rat to cheese, cheese to exit, show the original maze and the path the rat will take, anything to 
 * 	make it more easy for user to understand (eg. a legend)
 * Process: create a matrix of the imported file's maze
 * 	within the grid, recursively test out every path via backtracking
 * 	add 1 to the direction of choice when going to next recursion to find path
 */
import java.io.*;
import java.util.*; 
public class RubyMain {												//RubyMain class
	/* main method
	 * Procedural method is called automatically, used to organized the calling of other methods in the class 
	 * 
	 * List of local variables
	 * maze - array used to hold maze size 8 by 12 from file <type String[][]>
	 * matrix - matrix version of the maze <type int [][]>
	 * visited - tracks the areas that have already been visited <type int[][]>
	 * rodent - the type of rodent found in the maze <type String>
	 * run - holds user input to repeat the program <type String>
	 * o - objects used to gain access to the private variables in class Obtain <type Object>
	 * 
	 * @param args <type String>
	 * @throws IOException
	 * @return void
	 */
	public static void main(String[] args)throws IOException {		//main method					
		String[][] maze=new String[8][12];
		int[][] matrix=new int[8][12];
		int[][] visited = new int[8][12];
		int minLength;
		String rodent="Rat", run;
		Obtain o = new Obtain();
		System.out.println("This is the maze traversal program.\n");
		System.out.println("A rodent is stuck in a maze with a cheese and exit! \nThis program will show the shortest path for the rat to get the cheese \nand then escape through the exit!\n");
		System.out.println("Do you want to run the maze traversal program? Y/N");
		run=getInput();												//User input to check whether they want to run the program or not	
		while(run.equals("y")){										//runs the program if user wants to, allows it to repeat
			maze=getMaze();
			o.setRat(locate(maze, "r ", "m "));						//getting coordinates of the rat, cheese and exit from class Obtain
			o.setCheese(locate(maze, "c ", null));
			o.setExit(locate(maze, "x ", null));
			System.out.println("ORIGINAL MAZE\n======================");
			if(checkRCE(maze)>1||o.getRat()[0]==-1||o.getExit()[0]==-1||o.getCheese()[0]==-1){					//checks if there are any missing/extra rats, cheese or exits
				legend(rodent);																					//printing legend and the original maze
				printArr(maze);
				sadRat();	
				if(o.getRat()[0]==-1)																			//checks if a rat is missing
					System.out.println("You're missing a rat/mouse :(");

				if(o.getExit()[0]==-1)																			//checks if the exit is missing
					System.out.println("You're missing an exit :(");					

				if(o.getCheese()[0]==-1)																		//checks if the cheese is missing
					System.out.println("You're missing a cheese :(");
			}																									//close if(o.getRat()[0]==-1||o.getExit()[0]==-1||o.getCheese()[0]==-1)
			else{																								//if there are no missing/extra rats, cheese or exits, continue with the program
				if(maze[o.getRat()[0]][o.getRat()[1]].toLowerCase().equals("r "))								//checks if the letter at the rodent position is "r "
					rodent="Rat";																				//if the letter is r, it sets rodent to the String "rat"													
				else{																							//if the letter was not r (open else)
					rodent="Mouse";																				//rodent is a mouse instead
				}																								//end else
				legend(rodent);																					//printing legend and the original maze
				printArr(maze);
				matrix=convertMatrix(maze);
				visited[o.getRat()[0]][o.getRat()[1]] = 1; 														//RAT'S POSITION MARKED ON MATRIX
				System.out.println("\n\n"+rodent.toUpperCase()+" > CHEESE\n======================");
				minLength=pathLength(matrix, o.getRat()[0], o.getRat()[1], o.getCheese()[0], o.getCheese()[1],  maze,visited, rodent);			
				if(minLength<0){																									//checks if there is a viable path from rat to cheese
					System.out.println("There is no path to the cheese");
				}																													//end if(minLength<0){
				else{																												//if there is a path found, run this block of code
					System.out.println("\nIt will take the "+rodent+" "+minLength+" moves to get to the cheese\n");
					maze=getMaze();																									//reset the maze so the first path doesn't show
					maze[o.getRat()[0]][o.getRat()[1]]=". "; 																		//setting rat's positional marker to the path marker because it's no longer there
					System.out.println("\nCHEESE > EXIT\n======================");
					minLength=pathLength(matrix, o.getCheese()[0], o.getCheese()[1], o.getExit()[0], o.getExit()[1], maze,visited, rodent);
					if(minLength<0){																								//checks if there is a path from cheese to exit											
						System.out.println("There is no path to the exit");
					}																												//end if(minLength<0){
					else{																											//if there is a path, run this code 
						System.out.println("\nIt will take the "+rodent+" "+minLength+" moves to get from the cheese to the exit");
						System.out.println("\nYay, "+rodent+" got the cheese!\n");
						rat();
					}
				}																													//end else
			}																														//end else 
			System.out.println("Do you want to repeat the program? Y/N");
			run=getInput();																											//get user input to see if they want to repeat the program
		}																															//end while(run.equals("y"))
		System.out.println("Thanks for using the rodent maze traversal program! \nProgram ended");
	}																																//end main method

	/* checkInput method
	 * This procedural method checks user input
	 * 
	 * @param 	in - String entered by user <type String>
	 * @throws IOException
	 * @return void
	 */
	public static String checkInput(String in)throws IOException{				//checkInput method
		while(true) {															//repeats if user input is invalid
			switch (in){														//switch case to check user input
			case "n": case "y":												//if user input is valid, return the input
				return in;
			default:															//default for all other inputs (invalid inputs)
				System.out.println("Please enter a valid response (Y/N)");
				in=getInput();													//get user input
			}
		} 																		//end while(true)
	}																			//end checkInput

	/* getInput method
	 * This functional method gets user input
	 * 
	 * List of local variables
	 * br - a BufferedReader object used for keyboard input <type BufferedReader>;
	 * r - used to hold the input while it's being checked <type String>
	 * s - used to get user input <type String>
	 * 
	 * @param none
	 * @throws IOException
	 * @return r - the user input <type String>
	 */
	public static String getInput()throws IOException{									//getInput method
		BufferedReader br = new BufferedReader (new InputStreamReader(System.in));	
		String r;
		String s=br.readLine().toLowerCase();											//gets and converts user input to lower case
		r=checkInput(s);																
		return r;																		
	}																					//end getInput method

	/* legend method
	 * This procedural method prints out the maze's legend
	 * 
	 * List of local variables
	 * r - String that holds the corresponding letter for the type of rodent <type String>
	 * 
	 * @param animal - the type of rodent <type String>
	 * @return void
	 */
	public static void legend(String animal){														//legend method
		String r;
		if(animal.equals("Mouse"))																	//if / else checking the string to determine whether to use M or R for the legend						
			r="M";
		else{
			r="R";
		}

		System.out.println("LEGEND\n------\n"+animal+": "+r+"\nCheese: C\nExit: X\nBarrier: B\nPath: .\nPath taken: u");
		System.out.println("======================");
	}																								//end legend method

	/* getMaze method --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * This functional method gets the maze from a file and places it into a 2d String array
	 * 
	 * List of local variables
	 * sc - a Scanner object used to read a file <type Scanner>
	 * arrMaze - holds the maze that is read from file <type String[][]>
	 * line - gets the line from the file <type String[]>
	 * 
	 * @param none
	 * @throws FileNotFoundException
	 * @return arrMaze - array that holds the maze <type String[][]>
	--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- */
	public static String[][] getMaze() throws FileNotFoundException{					//getMaze method
		/*******************************************************************************************************
		 * *******************************************************************************************************
		 *METHOD FOR THE FILE PATH vvv
		 ********************************************************************************************************
		 *******************************************************************************************************/
		Scanner sc = new Scanner(new BufferedReader(new FileReader("mazefile0.txt")));	//CHANGE FILE HERE CHANGE FILE HERE CHANGE FILE HERE CHANGE FILE HERE CHANGE FILE HERE CHANGE FILE HERE CHANGE FILE HERE CHANGE FILE HERE
		String[][] arrMaze = new String [8][12];
		while(sc.hasNextLine()) {														//while loop as long as there is a line within the file
			for (int i=0; i<arrMaze.length; i++) {										//loops through the columns of arrMaze
				String[] line = sc.nextLine().trim().split(" ");						//removes spaces from the maze, sets each char in the file to one index in line[]
				for (int j=0; j<line.length; j++) {										//loops through the row of each column 
					arrMaze[i][j]=line[j]+" ";											//set each array index to one char from the line
				}																		//end for(int j=0; j<line.length; j++)
			}																			//end for (int i=0; i<arrMaze.length; i++)
		}																				//end while(sc.hasNextLine())
		sc.close();																		//close the scanner after finishing with file
		return arrMaze;
	}																					//end getMaze

	/* printArr method
	 * This procedural method prints a string array
	 * 
	 * @param none
	 * @return void
	 */
	public static void printArr(String[][] arr){										//printArr method
		for (int i=0; i<arr.length; i++) {												//loops through array columns
			if(i>0)																		//checks if it is past column 0 so there is no space on the top and no space on the bottom when the array prints
				System.out.println();
			for (int j=0; j<arr[i].length; j++) {										//loops through items in the rows, prints out the contents									
				System.out.print(arr[i][j]);
			}																			//end for (int j=0; j<arr[i].length; j++)
		}																				//end for (int i=0; i<arr.length; i++)
	}																					//end printArr method

	/* locate method
	 * This functional method finds the location of a given string within an array
	 * 
	 * List of local variables
	 * location - the coordinates of the String found in array <type int[]>
	 * 
	 * @param 	 in - the maze array <type String[][]>
	 * 			 a - string 1 to check for <type String>
	 * 			 b - string 2 to check for <type String>
	 * 
	 * @return location - the array holding coordinates of the String params to be found <type int[]>
	 */
	public static int[] locate(String[][]arr, String a, String b){							//locate method
		int[] location={-1,-1};
		for (int i=0; i<arr.length; i++) {													//loops through array columns
			for (int j=0; j<arr[i].length; j++) {											//loops through items in the row
				if(arr[i][j].toLowerCase().equals(a)||arr[i][j].toLowerCase().equals(b)){	//checking and converting each item to lowercase, if it matches the string, send the column and row num to location array
					location[0]=i;
					location[1]=j;
				}
			}																				//end for (int j=0; j<arr[i].length; j++)
		}																					//end for (int i=0; i<arr.length; i++)
		return location;
	}																						//end locate

	/* checkRCE method
	 * This functional method finds the amount of times a string appears in an array
	 * 
	 * List of local variables
	 * counter1 - counter for the string r <type int>
	 * counter2 - counter for the string c <type int>
	 * counter3 - counter for the string x <type int>
	 * 
	 * @param 	arr - the maze array <type String[]>
	 * @return a number, used to check program validity (type int)
	 */
	public static int checkRCE(String[][]arr){												//checkRCE method
		int counter1=0;
		int counter2=0;
		int counter3=0;
		for (int i=0; i<arr.length; i++) {													//looping through the 2d array
			for (int j=0; j<arr[i].length; j++) {											//start for(int j=0; j<arr[i].length; j++)
				if(arr[i][j].toLowerCase().equals("r ")||arr[i][j].equals("m ")){			//group of if/else if statements checking the amount of times a string occurs within the array
					counter1++;																//increment each corresponding character by 1 for each time it occurs
				}																			//end (arr[i][j].toLowerCase().equals("r ")||arr[i][j].equals("m "))
				else if(arr[i][j].toLowerCase().equals("c ")){
					counter2++;
				}																			//end else if (arr[i][j].toLowerCase().equals("c "))
				else if(arr[i][j].toLowerCase().equals("x ")){
					counter3++;
				}																			//end else if(arr[i][j].toLowerCase().equals("x "))
			}																				//end for(int j=0; j<arr[i].length; j++)
		}																					//end for(int i=0; i<arr.length; i++)
		if(counter1>1)																		//group of if statements outputting message if there are too many occurrences of the strings
			System.out.println("You can only have 1 rodent ");
		if(counter2>1)
			System.out.println("You can only have 1 cheese");
		if(counter3>1)
			System.out.println("You can only have 1 exit");

		if(counter1>1||counter2>1||counter3>1)												//if statement checking if there are too many occurrences of any string
			return 2;																		
		return 1;
	}																						//end checkRCE

	/* convertMatrix method
	 * This functional method checks converts a string array into a matrix
	 * 
	 * List of local variables
	 * iArr - int array for the matrix <type int[][]>
	 * 
	 * @param 	arr - the array holding the maze <type String[][]>
	 * @return iArr - the matrix version of the maze param <type int[][]>
	 */
	public static int[][] convertMatrix(String[][]arr){										//convertMatrix method
		int[][] iArr=new int[8][12];
		for (int i=0; i<arr.length; i++) {													//for loops to compare against original array to find walls
			for (int j=0; j<arr[i].length; j++) {
				if(arr[i][j].toLowerCase().equals("b ")){									//if/else to check and mark down barriers and the path 
					iArr[i][j]=0;															//(barrier is marked with 0 and the path is marked with 1)
				}																			//end (arr[i][j].toLowerCase().equals("b ")){
				else{
					iArr[i][j]=1;
				}																			//end else
			}																				//(int j=0; j<arr[i].length; j++)
		}																					//end (int i=0; i<arr.length; i++)
		return iArr;
	}																						//end convertMatrix

	/* pathLength method
	 * This functional method returns the minimum path it will take to find the destination 
	 * 
	 * List of local variables
	 * minPath - the variable holding the minimum amount of steps taken <type int>
	 * 
	 * @param 	mat - the matrix version of the maze <type int[][]>
	 * 			i - the x value of the starting position <type int>
	 * 			j - the y value of the starting position <type int>
	 * 			x - the x value of the destination <type int>
	 * 			y - the y valueo f the destination <type int>
	 * 			arr - the maze array <type String arr[][]>
	 * 			visited - array used to hold the cells that have been "visited" or tried <type int[][]>
	 * 			animal - the type of rodent used <type String>
	 * 
	 * @return minPath - the minimum amount of steps taken to get to destination <type int> or -1 if there is no path <type int>
	 */
	public static int pathLength(int[][] mat, int i, int j, int x, int y, String[][] arr, int[][] visited, String animal){		//pathLength method
		int minPath;
		minPath = shortestPath(mat, visited, i, j, x, y, Integer.MAX_VALUE, 0, arr, -1, animal);						//calling shortestPath to find minimum amount of steps
		if (minPath != Integer.MAX_VALUE) {																		//checks if there was a path found
			shortestPath(mat, visited, i, j, x, y, Integer.MAX_VALUE, 0, arr, minPath, animal);
			return minPath;
		}																										//end if(minPath != Integer.MAX_VALUE)
		return -1;
	}																											//end pathLength

	/* shortestPath method
	 * This functional recursive method finds a path and returns the shortest path. It prints out the path if it is the shortest, and tries every path
	 * 
	 * @param 	mat - the matrix version of the maze <type int[][]>
	 * 			visited - array used to hold the cells that have been "visited" or tried <type int[][]>
	 * 			i - the x value of the starting position <type int>
	 * 			j - the y value of the starting position <type int>
	 * 			x - the x value of the destination <type int>
	 * 			y - the y valueo f the destination <type int>
	 * 			minPath - updates the amount of steps taken within every attempt, stores the lowest <type int>
	 * 			dist - amount of steps taken in current attempt <type int>
	 * 			arr - the maze array <type String arr[][]>
	 * 			min - the minimum value needed to find destination <type int>
	 * 			animal = the type of rodent it is <type String>
	 * 
	 * @return minPath - the smallest number of steps it will take to find the destination <type int>
	 */
	public static int shortestPath(int[][] mat, int[][] visited, int i, int j, int x, int y, int minPath, int dist, String[][] arr, int min, String animal){		//shortestPath method
		if (i == x && j == y) {																														//checks if it is at the destination																				
			if(dist==min&&minPath>0){																												//checks if it is the shortest possible path
				minPath=-1;																															//if it is, it prints out the legend and the path as well as sets minPath to -1 so multiple paths won't print
				legend(animal);
				printPath(visited, arr);
				return minPath;
			}																																		//if(dist==min&&minPath>0)
			return Integer.min(dist, minPath);
		}																																			//end if (i == x && j == y)
		visited[i][j] = 2;																															//mark i, j as visited
		//if statements checking if you can go in a direction. If you can, recursively call shortestPath (in the new position). Also adds 1 to current dist
		if (checkValid(mat, visited, i + 1, j)){																									//check if you can go down																							
			minPath = shortestPath(mat, visited, i + 1, j, x, y, minPath, dist + 1, arr, min, animal);
		}																														
		if (checkValid(mat, visited, i, j + 1)){																									//check if you can go right
			minPath = shortestPath(mat, visited, i,	j + 1, x, y, minPath, dist + 1, arr, min, animal);
		}
		if (checkValid(mat, visited, i - 1, j)){																									//check if you can go up
			minPath = shortestPath(mat, visited, i - 1, j, x, y, minPath, dist + 1, arr, min, animal);
		}
		if (checkValid(mat, visited, i, j - 1)){																									//check if you can go to the left
			minPath = shortestPath(mat, visited, i, j - 1, x, y, minPath, dist + 1, arr, min, animal);
		}
		visited[i][j] = 0;																															//backtrack and unmark i,j as visited
		return minPath;
	}																																				//end shortestPath

	/* checkValid method
	 * This functional method checks if a move is valid or not (checks for barriers)
	 * 
	 * @param 	mat - the matrix version of the maze <type int[][]>
	 * 			visited - marks whether a cell is visited or not <type int[][]>
	 * 			x - the x coordinate of current position <type int>
	 * 			y - the y coordinate of current position <type int>
	 * @return true/false
	 */
	public static boolean checkValid(int[][] mat, int[][] visited, int x, int y) {								//checkValid method
		return (x>=0 && x<mat.length && y>=0 && y<mat[0].length) && mat[x][y]==1 && visited[x][y]!=2;			//checks for x and y's position and whether or not it will be a valid space, checks if there is a barrier and checks if it is visited or not
	}																											//end checkValid method

	/* printPath method
	 * This procedural method outputs the path taken by the rat/mouse
	 * 
	 * @param 	visited - the path that the rat took <type int[][]>
	 * 			arr - the maze <type String[][]>
	 * @return void
	 */
	static void printPath(int[][] visited, String[][] arr) {													//printPath method
		for(int i=0;i<visited.length;i++){																		//for loop looping through the 2d array, finding the path that the rat took
			for(int j=0;j<visited[i].length;j++){																//if a position is marked as visited, the cell is marked with the path in the maze
				if(visited[i][j]>1){
					arr[i][j]="u ";
				}																								//end if(visited[i][j]>1){
			}																									//end for(int j=0;j<visited[i].length;j++){	
		}																										//end for(int i=0;i<visited.length;i++){	
		printArr(arr);																							//prints out the maze and path
	}																											//end printPath method

	/* rat method
	 * This procedural method outputs an ASCII art rat who has cheese
	 * 
	 * @return void
	 */
	static void rat(){																							//rat method
		System.out.println("                         _     __,..---'''-._                   ';-,");
		System.out.println("                   ,    _/_),-'`             '-.                   `\\");
		System.out.println("    __            \\|.-;`    -_)                 '.                 ||");
		System.out.println("  .'o O'-.        /`   <   ,                      \\               .'/");
		System.out.println(" / O o_.-`|       '.___,__/                  .-'   \\_         _.-'.'");
		System.out.println("/O_.-'  O |          |\\  \\      \\          /`       _`'''''''`_.-'");
		System.out.println("| o   o  o|             _/;--._, >         |  --.__/ `'''''''`");
		System.out.println("|o   o O.-`           (((-'  __//`'-......-;\\      )");
		System.out.println("| O _.-'                   (((-'       __//  '--. /");
		System.out.println("'--`                                 (((-'    __//");
		System.out.println("                                            (((-'");
	}																											//end rat

	/* sadRat method
	 * This procedural method outputs an sad ASCII art rat who has no cheese
	 * 
	 * @return void
	 */
	static void sadRat(){																						//sadRat method
		System.out.println("  q-p	  q-p");
		System.out.println(" /   \\	 /\\\"/\\");
		System.out.println("(     )	(`=*=')");
		System.out.println(" `-(-'	 ^---^`-._");
		System.out.println("    ) 		");
		System.out.println("");
		System.out.println("");
	}																											//end sadRat

}//end RubyMain class

/*	Obtain class
 * 
 * <Class>
 * This class contains the private variables for the location of the rat, cheese and exit. 
 * <List of identifiers>
 * let rat represent a private variable with the coordinates of the rat <type int[]>
 * let exit represent a private variable with the coordinates of the exit <type int[]>
 * let cheese represent a private variable with the coordinates of the cheese <type int[]>
 */
class Obtain{										//class Obtain
	private int[] rat=new int[2];
	private int[] exit=new int[2];
	private int[] cheese=new int[2];

	/* setter sets the coordinate of the rat
	 * @param ratPos - the position of the rat <type int[]>
	 * @return void
	 */
	public void setRat(int[] ratPos){
		rat=ratPos;
	}

	/* getter returns the position of the rat
	 * 
	 * @return rat - the position of the rat <type int[]>
	 */
	public int[] getRat(){
		return rat;
	}

	/* setter sets the coordinate of the exit
	 * @param exitPos - the position of the exit <type int[]>
	 * @return void
	 */
	public void setExit(int[] exitPos){
		exit=exitPos;
	}

	/* getter returns the position of the exit
	 * 
	 * @return exit - the position of the exit <type int[]>
	 */
	public int[] getExit(){
		return exit;
	}

	/* setter sets the coordinate of the cheese
	 * @param cheesePos - the position of the cheese <type int[]>
	 * @return void
	 */
	public void setCheese(int[] cheesePos){
		cheese=cheesePos;
	}

	/* getter returns the position of the cheese
	 * 
	 * @return cheese - the position of the cheese <type int[]>
	 */
	public int[] getCheese(){
		return cheese;
	}
}												//end Obtain
