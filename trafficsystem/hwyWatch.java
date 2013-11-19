package trafficsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Author: Edwin Chase Noll
 * Purpose: Hackerati Application Assignment
 *
 * This file contains the hwyWatch class
 * It includes constructors, setters, getters
 * Database connection and table creation methods
 * And a collect data method
 * 
 */

public class hwyWatch
{
    //For parallel collecting of hwyWatches
    private Thread thread;
     
    //Database Connection
    private Connection conn;
    
    // JDBC driver name and database URL
    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    private final String DB_SERVER = "jdbc:mysql://localhost/traffic";
    
    //Database credentials and name
    private final String DB_USER = "ecn10";
    private final String DB_PASS = "@bc123";
    private String TABLENAME;
    private Calendar LASTCHECK;
    private Calendar STARTDATE;
    private String DATAFILE;
    private long SLEEPTIME;
    boolean RUNNING;
    private int RECORDS;
    boolean MONITOR;
    
    //Constructor sets LASTCHECK as current date/time/zone
    public hwyWatch(long time, String table, String fileName)
    {
        conn = null;
        TABLENAME = table;
        LASTCHECK = Calendar.getInstance();
        STARTDATE = LASTCHECK;
        DATAFILE = fileName;
        SLEEPTIME = time;
        RUNNING = false;
        MONITOR = false;
        setThread();
    }
    
    //Constructor sets LASTCHECK to user selected values
    public hwyWatch(long time, String table, String fileName, int year, int month, int day, int hourOfDay, int minute, int seconds, TimeZone zone)
    {
        conn = null;
        TABLENAME = table;
        LASTCHECK = Calendar.getInstance();
        LASTCHECK.set(year, month, day, hourOfDay, minute, seconds);
        LASTCHECK.setTimeZone(zone);
        STARTDATE = LASTCHECK;
        DATAFILE = fileName;
        SLEEPTIME = time;
        RUNNING = false;
        MONITOR = false;
        setThread();
    }
    
    //This function connects to database 'traffic'
    private void dbConnect()
    {
        boolean tableSuccess = false;
        
        //Attempt to connect to database
        try
        {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Connect
            conn = DriverManager.getConnection(DB_SERVER,DB_USER,DB_PASS);
        }
        catch(SQLException se)//Handle errors for JDBC
        {
            System.out.println("JDBC error: Connection FAILED!");
            return;
        }
        catch(Exception e)//Handle errors for Class.forName
        {
            System.out.println("JDBC driver registration error: Connection FAILED!");
            return;
        }
        
        //Create tables if not already exist
        tableSuccess = createTable(TABLENAME);
        if(!tableSuccess)
            System.out.println("Creating table FAILED!");
    }
    
    
    //This function attempts to create a table in the database or finds it existing 
    public boolean createTable(String table)
    {
        Statement execute = null;
        
        //Attempt to create connection statement
        try
        {
            execute = conn.createStatement();
        } catch (SQLException ex)
        {
            Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //SQL for creating required table with given name
        String sql = "CREATE TABLE IF NOT EXISTS " + table + "(id INT NOT NULL AUTO_INCREMENT, date VARCHAR(23), speed INT, PRIMARY KEY(id))";
        int result = 0;
        
        //Attempt to execute statement
        try
        {
            result = execute.executeUpdate(sql);
            
            //Creation successful return true
            return true;
        } catch (SQLException ex)//If failed check for tables already exist
        {
            Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex);
            DatabaseMetaData dbm = null;
            
            //Attempt to get database metaData
            try
            {
                dbm = conn.getMetaData();
            } catch (SQLException ex1)
            {
                Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex1);
                return false;
            }
            // check if table is there
            ResultSet tables = null;
            
            //Attempt to find table name in metadata
            try
            {
                tables = dbm.getTables(null, null, table, null);
            } catch (SQLException ex1)
            {
                Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex1);
                return false;
            }
            
            try
            {
                //If table is found return true
                if (tables.next()) 
                {
                    return true;
                }
                else
                {
                    return false;
                }
            } catch (SQLException ex1)
            {
                Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex1);
                return false;
            }
        }
    }
 
    //This function calls wait() on the thread time parameter determins over a period or indefinitly(0)
    public void stopRun(long time)
    {
        synchronized (hwyWatch.this) 
        {
            //If collected stopped by user or period
            while(!RUNNING || time != 0)
            {
                //Attempt to wait
                try
                {
                    //If monitoring display that waiting is about to occured
                    if (MONITOR)
                        System.out.println(Calendar.getInstance().getTime() + " " + TABLENAME + " Waiting " + time);
                    
                    //Wait
                    this.wait(time);
                    
                    //Set time to 0 to kick period waiting out of the loop
                    time = 0;
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
         
    //This function wakes a waiting thread
    public void resumeRun()
    { 
        synchronized (hwyWatch.this) 
        {
            this.notify();
        }
    } 
    
    //This function creates new thread
    private void setThread()
    {
        thread = new Thread() 
        {
            @Override
            public void run() 
            {
                //Initially the watch is running
                RUNNING = true;

                //Connect to database
                dbConnect();

                while(true)
                {
                    try
                    {
                        //If running collect data
                        if (RUNNING)
                            collectData();
                        else//Wait indefinitly
                            stopRun(0);

                    } catch (IOException ex)
                    {
                        Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //Wait for period
                    stopRun(SLEEPTIME);
                }
            }
       };
    }
    
    
    //Return thread
    public Thread getThread()
    {
        return thread;
    }
    
    
    //Sets TABLENAME
    public void setTable(String table)
    {
        TABLENAME = table;
    }
    
    //returns TABLENAME
    public String getTable()
    {
        return TABLENAME;
    }
    
    //Sets LASTCHECK to current data/time/zone
    public void setLastCheck()
    {
        LASTCHECK = Calendar.getInstance();
    }
    
    //Sets LASTCHECK to user selected values
    public void setLastCheck(int year, int month, int date, int hourOfDay, int minute, int second, TimeZone timeZone)
    {
        LASTCHECK.set(year, month, date, hourOfDay, minute, second);
        LASTCHECK.setTimeZone(timeZone);
    }
    
    //Sets LASTCHECK to String from DATAFILE
    public void setLastCheck(Calendar date)
    {
        LASTCHECK = date;
    }
    
    //Returns LASTCHECK date/time
    public Calendar getLastCheck()
    {
        return LASTCHECK;
    }
    
    //Sets STARTDATE to current data/time/zone
    public void setStartDate()
    {
        STARTDATE = Calendar.getInstance();
    }
    
    //Sets STARTDATE to user selected values
    public void setStartDate(int year, int month, int date, int hourOfDay, int minute, int second, TimeZone timeZone)
    {
        STARTDATE.set(year, month, date, hourOfDay, minute, second);
        STARTDATE.setTimeZone(timeZone);
    }
    
    //Sets STARTDATE to String from DATAFILE
    public void setStartDate(Calendar date)
    {
        STARTDATE = date;
    }
    
    //Returns STARTDATE date/time
    public Calendar getStartDate()
    {
        return STARTDATE;
    }
    //Sets DATAFILE name
    public void setDataFile(String fileName)
    {
        DATAFILE = fileName;
    }
    
    //Returns DATAFILE value
    public String getDataFile()
    {
        return DATAFILE;
    }
    
    //Sets SLEEPTIME
    public void setSleepTime(long time)
    {
        SLEEPTIME = time;
    }
    
    //Returns SLEEPTIME
    public long getSleepTime()
    {
        return SLEEPTIME;
    }
    
    //Sets RECORDS
    public void setRecords(int records)
    {
        RECORDS = records;
    }
    
    //Returns RECORDS
    public int getRecords()
    {
        return RECORDS;
    }
    
    //Set MONITIOR flag
    public void setMonitor(boolean set)
    {
        MONITOR = set;
    }
    
    //Returns MONITOR flag
    public boolean getMonitor()
    {
        return MONITOR;
    }
    //Open file and store in database records AFTER STARTTIME
    public void collectData() throws FileNotFoundException, IOException
    {
        //If monitoring display that waiting is about to occured
        if (MONITOR)
            System.out.println(Calendar.getInstance().getTime() + " " + TABLENAME + " Collecting ");
            
        
        //Initialize vehicleDate and endDate as current
        Calendar vehicleDate = Calendar.getInstance();
        //For this setup, the latest date should be read first so store the value for next collection
        Calendar endDate = Calendar.getInstance();
        //Can't initialize endDate with Calendar.getTime so initial above and set dired below
        endDate.setTime(STARTDATE.getTime());
        int speed;
        
        //Open file from which to read data
        Scanner reader = new Scanner (new File(DATAFILE));
        
        //Delimiter between data/time stamp and speed
        reader.useDelimiter(";");
        
        //Format of file dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        Statement execute = null;
        String sql;
        
        //Reset record from last collection
        RECORDS = 0;
        int result = 0;
        int records = 0;       
                
        //Attempt to create a database statement
        try
        {
            execute = conn.createStatement();
        } catch (SQLException ex)
        {
            Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Attempt to parse the string from file
        try
        {
            //Get and parse the string from file
            vehicleDate.setTime(sdf.parse(reader.next()));
        } catch (ParseException ex)
        {
            Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Skip over the ;
        reader.findInLine(";");
        
        //Get and parse the speed from file
        speed = Integer.parseInt(reader.nextLine());
        
        //While the dates are after the start date. Stop reading otherwise. This means the file needs to be in order most recent times first
        while (vehicleDate.after(STARTDATE))
        {
            //SQL insertion statement
            sql = "INSERT INTO " + TABLENAME + " (date, speed) VALUES (\"" + sdf.format(vehicleDate.getTime()) + "\", \"" + speed + "\")";
   
            //Attempt to execute insertion
            try
            {
                result = execute.executeUpdate(sql);
                if (MONITOR && records%1000 == 0)
                    System.out.println(TABLENAME + " Collected " + records + " records.");
                
            } catch (SQLException ex)
            {
                Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //If inserted increment records check if endTime needs to be updated
            if(result == 1)
            {
                records++;
                if(vehicleDate.after(endDate))
                    endDate.setTime(vehicleDate.getTime());
            }
            else//There was an error display the record if file that failed
            {
                System.out.println("\n------------------------------------\n" + TABLENAME + " @ " + vehicleDate.getTime() + "\n------------------------------------");
                vehicleDate.add(Calendar.SECOND, 1);
                endDate.setTime(vehicleDate.getTime());
                break;
            }
            
            //If not end of file go ahead and read the next record
            if(reader.hasNext())
            {
                //Attempt to parse the string from file
                try
                {
                    //Get and parse the string from file
                    vehicleDate.setTime(sdf.parse(reader.next()));
                } catch (ParseException ex)
                {
                    Logger.getLogger(hwyWatch.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Skip over the ;
                reader.findInLine(";");
                
                //Get and parse the speed from file
                speed = Integer.parseInt(reader.nextLine());
            }
            else//Date is before last collected end. Stop reading
                break;   
        }
        
        //update RECORDS for stats
        setRecords(records);
        
        //update STARTDATE for next collection
        setStartDate(endDate);

        //update LASTCHECK time to now
        setLastCheck(Calendar.getInstance());
        
        //Close file
        reader.close();
    }
}
