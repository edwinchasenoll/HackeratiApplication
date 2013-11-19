package trafficsystem;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

/**
 *
 * Author: Edwin Chase Noll
 * Purpose: Hackerati Application Assignment
 *
 * This file manages the collection of data from a highway monitoring system
 * Instances of collecting are not saved. Two are hard coded and start automatically in this version.
 * Options include:
 * Creating a new collection process
 * Starting an existing collection process
 * Stopping an existing collection process
 * Changing the period of the collection
 * Changing the date/time in which to collect data AFTER
 * Changing the database table in which to store the data 
 * Changing the file from which to collect the data. Since another system generates this data FILE MUST EXIST.
 * Viewing the stats from the last collection
 * Monitoring the times of the collection and waiting. Other actions cannot be performed while monitoring.
 * 
 */
 
public class TrafficSystem
{
    static final Scanner console = new Scanner(System.in);
    static final Console screen = System.console();
    
    //Holds list of available watches for selection in menus
    static List<hwyWatch> myWatches = new ArrayList<hwyWatch>();

    //Times in milliseconds for wait();
    static final long oneSecond = 1000;
    static final long oneMinute = oneSecond * 60;
    static final long oneHour = oneMinute * 60;
    static final long oneDay = oneHour * 24;
    
    public static void main(String[] args)
    {
        //Initialize hwyWatch with collect period, table name, file name, time will be set to the beginning of time
        hwyWatch hwy27 = new hwyWatch(oneHour, "hwy27", "hwy27Data.txt", 0, 0, 0, 0, 0, 0, TimeZone.getTimeZone("America/New York"));
		myWatches.add(hwy27);
        
        //Start collection process
        hwy27.getThread().start();

        //Initialize hwyWatch with collect period, table name, file name, time will be set to the beginning of time
        hwyWatch hwy44 = new hwyWatch(oneMinute, "hwy44", "hwy44Data.txt", 0, 0, 0, 0, 0, 0, TimeZone.getTimeZone("America/New York"));
        myWatches.add(hwy44);
		
        //Start collection process
        hwy44.getThread().start();
        
		//Display the main menue to the user
		mainMenu();
		
        //Returns to this point when user has exited all menus. Exit program (all collecting stops)
        System.exit(1);
    }
    
	
    //This function displays the main menu
    //Show available watches
    //Add a new watch
    //Monitor ALL watches
    //Or exit. User can only exit by menu commands from this menu
    public static void mainMenu()
    {
        String input = "";
        String commands[] = {"show", "new", "monitor", "exit"};
        boolean exit = false;
		
        //Loop for returning from a deeper menu
        do
        {
            //Loop for incorrect input
            do
            {
                System.out.println("Type \"show\" to see existing watches. Type \"new\" to add new watch");
                System.out.print("\"monitor\" to monitor all activity or \"exit\": ");
				
                //Get user command
                input = console.nextLine();
            }while (Arrays.asList(commands).indexOf(input) < 0);//Does not equal any available commands

            //If exiting prompt for conformation
            if ("exit".equals(input))
            {
                System.out.print("Are you sure? Tpye \"yes\" to exit: ");
                input = console.nextLine();
				
                //Set exit flag if confirmed. If not display menu again
                if ("yes".equals(input))
                    exit = true;
            }
			
            //If show command display next menu
            if ("show".equals(input))
                showWatches();
			
            //If new command display next menu
            if("new".equals(input))
                newWatch();
            
            //If new command display next menu. Argument is for which watch in the array myWatches (-1 will monitor all)
            if("monitor".equals(input))
                monitor(-1);

        }while(exit == false);//Redisplay menu
    }
    
    //This function displays the watches menu
    //Displays available watches
    //Or back. Return to main menu
    public static void showWatches()
    {
        String input;
        boolean found = false;	
        //Loop for returning from a deeper menu
        do
        {
            System.out.println("\nWatch Names");
            System.out.println("-----------------");
            for(int i = 0; i < myWatches.size(); i++)
                System.out.println(myWatches.get(i).getTable() + " ");
            System.out.println("-----------------");
            System.out.print("\nType watch name to see options \"back\" for main menu: ");

            //Get user command
            input = console.nextLine();

            //Skip existence check if returning to main menu
            if (!"back".equals(input))
            {
                //Cycle array. Look for array name matching command
                for(int i = 0; i < myWatches.size(); i++)
                {
                    //If found, set the element and end the search
                    if (input.equals(myWatches.get(i).getTable()))
                    {
                        found = true;//Found, set flag
                        
                        //Display the menu for appropriate watch optons
                        watchOptions(i);
                        break;
                    }
                }
                found = false;//Reset flag for command found in array
            }
        }while(!"back".equals(input));//Redisplay menu
        
    }
    
    //This function displays the menu for creating a new watch
    //Prompts user for a period (second, minute, hour, or day)
    //Prompts user for a table name (will be created if not already)
    //Prompts user for a file name from which to read (must exist)
    //Prompts a user for a time in file to collect after
    //Prompts to start the watch
    //Or q. Return to main menu
    public static void newWatch()
    {
        String input = "";
        long period = oneMinute;
        String table = "";
        String file = "";
        String commands[] = {"second", "minute", "hour", "day", "q"};
        //Loop for incorrect input
        do
        {
            System.out.println("Enter \"second\" \"minute\" \"hour\" or \"day\" for period or \"q\" to quit: ");
            input = console.nextLine();
        }while (Arrays.asList(commands).indexOf(input) < 0);//Does not equal any available commands
        
        //If not quitting
        if (!"q".equals(input))
        {
            //Set period
            if ("second".equals(input))
                period = oneSecond;
            else if ("minute".equals(input))
                period = oneMinute;
            else if ("hour".equals(input))
                period = oneHour;
            else if ("day".equals(input))
                period = oneDay;

            System.out.print("Enter table name or \"q\" to quit: ");
            table = console.nextLine();
            
            //If not quitting
            if (!"q".equals(table))
            {
                System.out.print("Enter new file name (must exists) or \"q\" to quit: ");
                file = console.nextLine();
                
                //If not quitting. If input is good watch will be created beyond this point
                if(!"q".equals(file))
                {
                    //Attempt to open inputted file to check for existence
                    try
                    {
                        //Open file
                        Scanner reader = new Scanner (new File(file));
                        
                        //Exist. Close
                        reader.close();
                        
                        //Initialize hwyWatch with collect period, table name, file name, time will be set to construction time
                        hwyWatch watch = new hwyWatch(period, table, file);
                        myWatches.add(watch);
                        
                        //Call function to prompt for time from which to collect after
                        userTime(myWatches.size() - 1);

                        //Loop for incorrect input
                        do
                        {
                            System.out.print("Start " + table + "?(\"yes\" or \"no\"): ");
                            input = console.nextLine();
                        }while(!"yes".equals(input) && !"no".equals(input));//Does not equal any available commands

                        //Start watch
                        if ("yes".equals(input))
                            watch.getThread().start();
                        
                    } catch (FileNotFoundException ex)
                    {
                        System.out.println("Error Creating watch.");//Error opening file. Did not create watch. Display error.
                    }
                }
            }
        }
    }
    
    //This function begins the monitoring of all or selected watches
    //Provide disclaimer to user that monitoring data may not be available until next even occurs
    //Prompt to enter monitoring
    public static void monitor(int element)
    {
        String input = "";
        //Loop for incorrect input
        do
        {
            System.out.println("Once monitor starts, type 'q' then enter any time to stop.");
            System.out.print("Data will be available once a watch is not waiting Continue? \"yes\" or \"no\": ");
            input = console.nextLine();
        }while (!"yes".equals(input) && !"no".equals(input));//Does not equal any available commands
        
        //If enter monitoring
        if ("yes".equals(input))
        {
            //Monitor all
            if (element == -1)
            {
                for(int i = 0; i < myWatches.size(); i++)
                    myWatches.get(i).setMonitor(true);
            }
            else//Monitor selected
                myWatches.get(element).setMonitor(true);
        }
        
        //Loop for incorrect input
        do
        {
            System.out.println("Type 'q' then enter to quit: ");
            input = console.nextLine();
        }while(!"q".equals(input));//Does not equal any available commands

        //Quit monitoring all
        if (element == -1)
        {
            for(int i = 0; i < myWatches.size(); i++)
                myWatches.get(i).setMonitor(false);
        }
        else//Quit monitoring selected
            myWatches.get(element).setMonitor(false);
    }
    
    //This function displays the watch options menu
    //Starts watches
    //Stops watches
    //Change period
    //Change time
    //Change database table
    //Change file from which to read
    //View stats
    //Monitor single watch
    //Or back to watches menu.
    public static void watchOptions(int element)
    {
        String input = "";
        String commands[] = {"start", "stop", "period", "time", "table", "file", "stats", "monitor", "back"};
        
        //Loop for returning from deeper menu
        do
        {
            //Loop for incorrect input
            do
            {
                System.out.println("\nOptions:");
                System.out.println("--------------------------------------------------------------");
                System.out.println("start - starts the watch if currently stopped.");
                System.out.println("stop - stops the watch if currently running.");
                System.out.println("period - set the collection period.");
                System.out.println("time - set beggining date of data to collect.");
                System.out.println("table - set the database table name.");
                System.out.println("file - set the file name from which to collect.");
                System.out.println("stats - view stat of last collection.");
                System.out.println("monitor - monitor this watch");
                System.out.println("back - return to watch list");
                System.out.println("--------------------------------------------------------------");
                System.out.print("\nEnter command for " + myWatches.get(element).getTable() + ": ");
                input = console.nextLine();
              
            }while (Arrays.asList(commands).indexOf(input) < 0);//Does not equal any available commands

            //If start call start function
            if ("start".equals(input))
                userStart(element);

            //If stop call stop function
            if ("stop".equals(input))
                userStop(element);

            //If period call period function
            if ("period".equals(input))
                userPeriod(element);

            //If time call time function
            if("time".equals(input))
                userTime(element);

            //If tables call table function
            if("table".equals(input))
                userTable(element);

            //If file call file function
            if ("file".equals(input))
                userFile(element);

            //If stats call stats function
            if ("stats".equals(input))
                userStats(element);

            //If monitor call monitor function
            if("monitor".equals(input))
                monitor(element);
            
        }while(!"back".equals(input));
    }
    
    
    //This function starts the collecting process by starting the Thread or setting a run flag and waking
    public static void userStart(int element)
    {
        //If the Thread has never been started start
        if(!myWatches.get(element).getThread().isAlive())
        {
            myWatches.get(element).getThread().start();
           
            System.out.println(myWatches.get(element).getTable() + " started.");
        }
        else if(!myWatches.get(element).RUNNING)//Thread is started, running flag false. Set true and wake
        {
            myWatches.get(element).RUNNING = true;
            myWatches.get(element).resumeRun();
            
            System.out.println(myWatches.get(element).getTable() + " started.");
        }
        else//Already running
        {
            System.out.println(myWatches.get(element).getTable() + " is already started and running.");
        }
    }
    
    public static void userStop(int element)
    {  
        //Set running flag to false. If currently collecting will finish before wait
        myWatches.get(element).RUNNING = false;
      
        System.out.println(myWatches.get(element).getTable() + " stopped.");
    }
    
    
    //This function allows the user to change the period of collecting
    //Periods can be: every second, minute, hour, or day
    //Back returns to watch options menu
    public static void userPeriod(int element)
    {
        String period = "";
        String commands[] = {"second", "minute", "hour", "day", "back"};
        do
        {
            System.out.print("Enter \"second\" \"minute\" \"hour\" or \"day\" for period or \"back\": ");
            period = console.nextLine();
        }while (Arrays.asList(commands).indexOf(period) < 0);//Does not equal any available commands
        
        //Reset period by calling object setSleepTime method
        if ("second".equals(period))
            myWatches.get(element).setSleepTime(oneSecond);
        else if ("minute".equals(period))
            myWatches.get(element).setSleepTime(oneMinute);
        else if ("hour".equals(period))
            myWatches.get(element).setSleepTime(oneHour);
        else if ("day".equals(period))
            myWatches.get(element).setSleepTime(oneDay);
        else if ("back".equals(period))
            return;
        
        //Collect once period has be set
        myWatches.get(element).resumeRun();
        
        System.out.println(myWatches.get(element).getTable() + " period set.");
    }
    
    //This function allows the user to change the time to collect after ignor dates before in file
    //Time must be entered in yyyy/MM/dd HH:mm:ss z format such as 2013/01/01 08:00:00 CST
    //Now set the time to the current date, time, and zone. Performing this during collection stops the current collection
    //Reset sets all fields to 0 to collect all data in file
    //Back returns to watch options menu
    public static void userTime(int element)
    {   
        String time = "";
        Calendar chosenTime = Calendar.getInstance();
        boolean set = true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        
        //Loop for incorrect format enterd
        do
        {
            System.out.print("Enter time in the form \"yyyy/MM/dd HH:mm:ss z\" or \"now\" for current date/time\n"
                    + "\"reset\" for 0000/00/00 00:00:00 EST or \"back\": ");
            time = console.nextLine();
            
            //Set time to now
            if("now".equals(time))
            {
                chosenTime = Calendar.getInstance();
                set = true;
            }
            else if ("reset".equals(time))//Reset time
            {
                try
                {
                    chosenTime.setTime(sdf.parse("0000/00/00 00:00:00 EST"));
                    set = true;
                } catch (ParseException ex)
                {
                    set = false;
                }
            }
            else if ("back".equals(time))//Return to watch options menu
            {
                return;
            }
            else//Format good. Attempt to set
            {
                try
                {
                    chosenTime.setTime(sdf.parse(time));
                    set = true;
                } catch (ParseException ex)
                {
                    set = false;
                }
            }
        }while (!set);//Setting time unsuccessful
        
        //Call object setStartDate method
        myWatches.get(element).setStartDate(chosenTime);
        
        //Collect after time has be set
        myWatches.get(element).resumeRun();
        
        System.out.println(myWatches.get(element).getTable() + " Collect From Time set.");
    }
    
    //This function allows the user to change the fileName from which to collect data
    //This file is provided by another system, therefore, much already exist
    public static void userFile(int element)
    {
        String input = "";
        System.out.print("Enter new file name (must exists) or \"back\": ");
        input = console.nextLine();
        
        //If back return to watch options menu
        if("back".equals(input))
        {
            return;
        }
        try//Attempt to open file to check existence
        {
            Scanner reader = new Scanner (new File(input));
            reader.close();//Exist. Close.
            
            //Call object setDataFile method
            myWatches.get(element).setDataFile(input);
            System.out.println(myWatches.get(element).getTable() + " new file set.");
        } catch (FileNotFoundException ex)
        {
            System.out.println("Error Opening File.");//Error opening file. New value not set.
        }
    }
    
    //This function allows the user to change the table in which to store the data
    //May exist or not. Will be created if not
    //Must follow sql naming rules
    public static void userTable(int element)
    {
        boolean tableSuccess = false;
        String table = "";
     
        System.out.print("Enter new table name or \"back\": ");
        table = console.nextLine();

        //If back return to watch optons menu
        if("back".equals(table))
        {
            return;
        }
        else
        {
            //Call objects createTable method
            tableSuccess = myWatches.get(element).createTable(table);
            if(!tableSuccess)
                System.out.println("Creating table FAILED!");
            else
                myWatches.get(element).setTable(table);
        }

        System.out.println(myWatches.get(element).getTable() + " new table set.");
    }
    
    
    //The function displays the stats from the last collection
    public static void userStats(int element)
    {
        String confirm = "";
        System.out.println("Database Table     : " + myWatches.get(element).getTable());
        System.out.println("Data File          : " + myWatches.get(element).getDataFile());
        System.out.println("Collect Every      : " + myWatches.get(element).getSleepTime() + " milliseconds.");
        System.out.println("Collect Dates After: " + myWatches.get(element).getStartDate().getTime());
        System.out.println("Last Collected     : " + myWatches.get(element).getLastCheck().getTime() + ". Records stored: " + myWatches.get(element).getRecords());
        System.out.println("Currently Running  : " + myWatches.get(element).RUNNING);
        do
        {
            System.out.print("\nDone:  \"c\" to confirm: ");
            confirm = console.nextLine();
        }while(!"c".equals(confirm));
        
    }
}
