/*
 * COMP329 Assignment One
 * File Purpose: To run testing methods and use as variable storage
 */
 
//import statements here
import lejos.nxt.*;
import lejos.robotics.*;
 
public class Calibrate {
 
    //global variables here - templates for now
    static void    someVarHere   = 0   ;
    static int     someIntVar    = 0   ;
    static String  someStringVar = ""  ;
    static boolean isEmpty       = true;

    Calibrate(int a, int b, String c) {
        //constructor class
    }
    
    private static void chkArenaSize() {
      //variable to store movement data
      Pose  pObj       = new Pose(0.0, 0.0, 0);

      TouchSensor lBmp = new TouchSensor(SensorPort.S2);
      TouchSensor rBmp = new TouchSensor(SensorPort.S1);

      Motor.B.forward();
      Motor.C.forward();

      //wait until it bumps into something
      while(!lBmp.isPressed() || !rBmp.isPressed() ) {
      }

      Motor.B.stop();
      Motor.C.stop();
      


    }
    private static void chkFwdMovement(MotorObj mV) {
        //Should test forward movement so that there are no leaning sideways
        //method should be recursive to allow changing variables whilst running
    }
    
    private static void chkTurningCircles(MotorObj mV) {
        //series of tests to test 90, 180 and 270 degree turns are done right
        //method should be recursive to allow changing variables whilst running
    }
    
    private static void chkSonar() {
        //tests the strength/turning capabilities of the sonar 
        //method should be recursive to allow changing variables whilst running
    }
    
    private static void chkBumper() {
        //tests the functionality of the bumpers
        //method should be recursive to allow changing variables whilst running
    }
    
    //Maybe more stuff here but I can't think of any
    
    
    //methods for calling values stored here - should follow template below
    public static int getIntVar() return intVar;
    
    //methods for getting values of Objects used in Calibrate, instead of
    //blank variable declarations in other classes, such as Mover and Mapper
    public static MotorObj getMotorVar() return mV;
    public static SonarObj getSonarVar() return sV;
    
    
    //EndOfFile
 }
