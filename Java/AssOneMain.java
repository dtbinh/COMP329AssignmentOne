/*
 * COMP329 Assignment One
 * File Purpose: To start the program and link with other files
 */
 
//import statements here
import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.nxt.Button;
import lejos.nxt.Motor;
 
 
public class AssOneMain {
 
  //global variables here - templates for now
  static int       cellSize      = 0            ; //size of cells
  static int       columns       = 0            ; //grid X axis
  static int       numOfRowCells = 0            ; //grid Y axis
  static int       loop1         = 0            ;
  static int       loop2         = 0            ;
  static BtStuff   btObj         = new BtStuff(); //var to connect to RConsole
  static boolean   objAhead      = false        ; //true if front cell is full
  static boolean   objLeft       = false        ; //true if left  cell is full
  static boolean   objRight      = false        ; //true if right cell is full
  static Movement  movObj; //given value in main
  static MapSystem mapObj; //given value in main

  public static void main(String[] args) {
    //grid values for mapping
    columns       = 4 ;
    numOfRowCells = 5 ;
    cellSize      = 30;

    //connect to other classes
    mapObj = new MapSystem(cellSize, columns, numOfRowCells);
    movObj = new Movement (cellSize, columns, numOfRowCells);
    
    //connect to RConsole
    btObj.startBtConn();
    System.out.println("Press Any Button");
    Button.waitForAnyPress();
    LCD.clear();
            
    //for each column
    for(loop1 = 0; loop1 < columns; loop1++) {
      //for each cell within the column
      for(loop2 = 0; loop2 < numOfRowCells; loop2++) {
        movRow(); //scan and move method
      }
      if(loop1 == (columns - 1) ) {
          break;
      }
      if(loop1 % 2 == 0) { //if the robot moves along the Y axis positively
        movCol(true) ;     //turn right into the new column
      } else {
        movCol(false);     //turn left  into the new column
      }
    }
    
    String[] output = mapObj.getMap(columns, numOfRowCells);
    Button.waitForAnyPress();
    btObj.stringToRCon(output[0] );
    btObj.stringToRCon(output[1] );
    btObj.stringToRCon(output[2] );
    
  }
  
  //Method scans input and maps it; if obstacle is in front, left and right,
  //backpedal.  If there is a space adjacent but an obstacle at the front,
  //turns to move around the obstacle using the available space.
  public static void movRow() {
    double nextCell = mapObj.basicProb(); //work out probability
    //output to RConsole through BlueTooth
    //btObj.stringToRCon("Object Probability in next Cell: %2f" + nextCell);
    
   // mapObj.printMap(columns, numOfRowCells);

    //sonar scan around of the robot
    boolean[] scanResults = new boolean[3];
    scanResults = mapObj.scanAll();
    mapObj.updateMap(scanResults);
    //btObj.stringToRCon("Scan Results: " + scanResults[0] + ", " + scanResults[1] + ", " + scanResults[2]);
    //mapObj.printMap(columns, numOfRowCells);
        
    if(scanResults[1] ) { //if there's an obstacle in front
      //if there's an obstacle to the left & right.  i.e. a dead end
      if(scanResults[0] && scanResults[2] ) {
        turnAround(scanResults[2], mapObj, movObj); //move backwards
      } else {
        //call movAround() method to navigate around the obstacle
        movAround( scanResults[1], mapObj, movObj);
      }
    } else {
      movObj.nextCell(mapObj); //move forward a cell
    }
  }
  
  //Method to shift columns (X Axis)
  public static void movCol(boolean b) {
    movObj.turn(b, mapObj); //rotate 90 degrees - true = right; false = left
    movRow()              ; //move forward a cell
    movObj.turn(b, mapObj); //rotate 90 degrees - true = right; false = left
      
  }
    
  //Method to move around an obstacle - runs when obstacle is in front
  public static void movAround(boolean r, MapSystem ms, Movement mv) {
    mv.turn(r, ms) ; //true = right turn; false = left turn
    movRow()       ; //scan and move forward if necessary
    mv.turn(!r, ms); //invert the boolean to turn the other way
    
    //move forward twice to pass the obstacle
    movRow();
    ++loop2;
    movRow();
    ++loop2;
    
    mv.turn(!r, ms);                     //turn to face original column
    boolean extend = mapObj.scanAhead(); //flag to check object is passed
    while(extend) {
      mv.turn( r, ms) ;          //turn to move along the axis
      movObj.nextCell(mapObj);
      ++loop2;
      mv.turn(!r, ms) ;          //face the original column
      if(!mapObj.scanAhead() ) { //if an obstacle is not found anymore
        extend = false;          //break the while loop
      } else {
      }
    }
    movRow();       //move into the original column
    mv.turn(r, ms); //face the correct way to continue the patrol
  }
    
  //method runs when robot is in a dead end and has to back up and go around
  public static void turnAround(boolean r, MapSystem ms, Movement mv) {
    mv.turn(ms); //180 degree turn
    movRow()   ; //move forward

    //check for empty adjacent space
    boolean[] scanResults = new boolean[3];
    scanResults = mapObj.scanAll();
    //Move to said empty space
    if(scanResults[0] && !scanResults[2]) { //Obstacle on the Left
      mv.turn(scanResults[0],  ms);   //turn right
      movRow();
    } else { //Obstacle on the Right
      mv.turn(!scanResults[2], ms);  //turn left
      movRow();
      mv.turn(!scanResults[2], ms);
      movRow();
    //move forward 2 cells, scanning as we go
    //if an obstacle is still on the same axis as the first
    //    class it as a different obstacle
    //    move forward a cell
    //    scan again - recursive
    //move back to the correct column
        
    //if at a 'limit cell', turn left instead of right
        
     }
    
    }
}
//EndOfFile
