import lejos.nxt.*;

public class MapSystem {
	
    //Attributes
	static int cellSize;		            //size of cells
	static int[][] map;                 	//map to be completed
    static int[][] C;                       //number of times a cell had been scanned
    static double[][] P;                    //probability of a cell being occupied
    
    static int[] limit = new int[2]; 		//highest coordinates in map
	static int[] position = new int[2];	    //robot's position
	static int i = 1;						//used to switch between axes of position and limit

	static int heading = 1;			        //plus or minus 1 depending on which direction the robot is facing
	static int direction = 1;			    //direction 1-4 of where the robot is facing (1 is forward, 4 is left).
	
    static int dest;                        //distance to nearest obstacle/wall
    static int obstaclePos;                 //detected position of obstacle along an axis
    static boolean obstacle;                //whether an obstacle is detected (true) or not (false)
    static int totalCells;                  //number of cells
    static int unknownObjs = 4;             //number of obstacles not found
        
    //Sonar Sensor Object
	static UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
    
    //Constructor
    MapSystem(int d, int c, int r) {
        //input map dimensions
        map = new int[c][r];
        P = new double[c][r];
        C = new int[c][r];
        
        //add map limits                            
        limit[0] = (c - 1);
        limit[1] = (r - 1);
        
        //set other variables
        totalCells = (c * r);
        cellSize = d;
        
        us.continuous();        //turn on sonar
    }
    
    /* Works out the probability of the cell
     * ahead being occupied.
     */
    public static double basicProb() {
        double x = ( (unknownObjs / totalCells) * 100);
        return x;
    }

    /* Scans the cell to the ahead of 
     * the robot by turning its head
     */
    public static boolean scanAhead() {
        Motor.A.rotateTo(0);            //rotate to front
                
        dest = us.getDistance();        //scan
        updateMap();
        
        if(dest < 30) {                 //if directly ahead
            return true;
        } else {
            return false;
        }
        
    }
    
    /* Scans the cell to the left of 
     * the robot by turning its head
     */
    public static boolean scanLeft() {
        Motor.A.rotateTo(-650);         //rotate to left
        leftTurn();                     //tell map system
        
        dest = us.getDistance();        //scan
        updateMap();         
        
        Motor.A.rotateTo(0);            //rotate to front
        rightTurn();                    //tell map system
       
        if(dest < 30) {                 //if directly ahead
            return true;
        } else {
            return false;
        }
        
    }
    
    /* Scans the cell to the right of 
     * the robot by turning its head
     */
    public static boolean scanRight() {
        Motor.A.rotateTo(650);          //rotate to right
        rightTurn();                    //tell map system
        
        dest = us.getDistance();        //scan 
        updateMap();
        
        Motor.A.rotateTo(0);            //rotate to front
        leftTurn();                     //tell map system
        
        if(dest < 30) {                 //if directly ahead
            return true;
        } else {
            return false;
        }                  
    }
    
    /* Updates the current position of the robot 
     * based on it's current movement axis and 
     * heading.
     */
    public static void updatePosition() {
        position[i] = position[i] + heading;
        
        //incase of some error
        if (position[i] > limit[i]) {
            position[i] = limit[i];
        } else if (position[i] < 0) {
            position[i] = 0;
        }
    }
    
    
	/* How the map system recognises a 
	 * right turn.
	 */
	public static void rightTurn() {
        //change direction
		if (direction == 4){		//if max direction
			direction = 1;		    //reset
		} else {
			direction++;		    //change direction
		}
		
		if (direction < 3) {	    //if facing forward
			heading = 1;			
		} else  {
			heading = -1;
		}
		
		if(i == 0) {                //y axis is 1, x is 0
			i = 1;		          
		} else {
			i = 0;
		}
	}
	
	/* How the map system recognises a 
	 * left turn.
	 */
	public static void leftTurn() {
		if (direction == 1){        //if min direction
			direction = 4;          //reset
		} else {
			direction--;            //change direction
		}
		
		if (direction < 3) {
			heading = 1;
		} else  {
			heading = -1;
		}
		
		if(i == 0) {
			i = 1;		
		} else {
			i = 0;
		}
	}
	
	/* Updates the specific part of the map the
     * robot thinks there is an obstacle. 
	 * Increments map[][] where there is an obstacle and
     * decrements where there isn't up to the obstacle.
     * Increments C[][] whenever that cell is updated.
     * Passes to occupancyGrid when map is updated.
	 */  
	public static void updateBlock() {
		
		if (i == 1) {								                    //if x axis
			if (obstaclePos >= 0 && obstaclePos <= limit[i]) {          //if obstacle within map limits
                obstacle = true;                                        //obstacle at this position        
                occupancyGrid(position[0], obstaclePos, obstacle);      //update map
            } //else dont add obstacle, just update to wall
            
            obstacle = false;                                           //now update where no obstacle
			if (heading == 1) {
				for (int j = position[i]; j < obstaclePos; j++) {		//add in empty spaces up to obstacle
					occupancyGrid(position[0], j, obstacle);
				}
			} else if (heading == -1) {
				for (int j = position[i]; j > obstaclePos; j--) {
					occupancyGrid(position[0], j, obstacle);
				}
			}
		} else if (i == 0) {								            //if x axis
            if (obstaclePos >= 0 && obstaclePos <= limit[i]) {          //if obstacle within map limits
                obstacle = true;
                occupancyGrid(obstaclePos, position[1], obstacle);
            }  //else dont add obstacle, just update to wall
            
            obstacle = false;
			if (heading == 1) {
				for (int j = position[i]; j < obstaclePos; j++) {		//add in empty spaces up to obstacle
                    occupancyGrid(j, position[1], obstacle);
				}
			} else if (heading == -1) {
				for (int j = position[i]; j > obstaclePos; j--) {
                    occupancyGrid(j, position[1], obstacle);
				}
			}
		}	
	}
	
    /* Calculates the probability of a cell
     * in the map being occupied.
     */
    public static void occupancyGrid(int x, int y, boolean obstacle) {
        if (obstacle) {
            map[x][y]++;
        } else {
            map[x][y]--;
        }
        C[x][y]++;
        P[x][y] = ((double)map[x][y] + (double)C[x][y]) / (2 * (double)C[x][y]);  
    }
    
	/* Calculates the distance in coordinates
	 * to obstacle, then the actual coordinate
	 * of the obstacle then puts it into the
	 * map using updateBlock().
	 */
	public static void updateMap() {
       
		float d = (float)dest / (float)cellSize;	        //get occupied position distance from robot
		int x = (int) Math.ceil(d);	            //then get as integer (if x is 2, obstacle is in the second block away from robot)
        if (dest % cellSize == 0) {
            x++;                                //x incremented as cannot round up with mod 0
        }
        
        obstaclePos = position[i] + (x * heading);     //get map position of obstacle
        
		if(obstaclePos > limit[i]) {	        //if obstacle is wall
			obstaclePos = limit[i] + 1;
		} else if (obstaclePos < 0) {          //if other wall
            obstaclePos = -1;           
        }
        
        updateBlock();
	}

	/* Prints current occupancy grid to lcd 
     * screen.
     */
    public static void printMap(int c, int r) {
        LCD.clear();
        for(int k = r - 1; k >= 0; i++) {
			for(int j = 0; j < c; j++) {
				if (P[j][k] >= 0.5) {                //if theres probably an object
                    System.out.print("O");
                } else if (C[j][k] > 0 && P[j][k] < 0.5) {         //if theres probably not an object
                    System.out.print("~");                    
                } else {                            //if it gets confused
                    System.out.print("?");
                }
			}
			System.out.print('\n');                 //next layer
		}   
    }
	
	/* Puts raw occupancy grid into a string for 
     * bluetooth.
     */
	public static String getMap(int c, int r) {
        StringBuilder sb = new StringBuilder();     //used to append map data to string
        
		for(int k = r - 1; k >= 0; i++) {
			for(int j = 0; j < c; j++) {
                sb.append(P[j][k]);                 //append probability to string       
			}
			sb.append("\n");                        //next layer
		}
        //create string
		String s = sb.toString();                
		return s;

	}
}