/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chase
 */
public class randomCars
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    { 
        String zone = "CST";
        String fileName = "hwy27Data.txt";
        int min = 500;
        int max = 1500;
        int year = 2012;
        boolean append = false;
		
        List<Calendar> month = new ArrayList<Calendar>();
        Random rand = new Random();
        int cars;
        cars = rand.nextInt(max - min) + min;
        
        for (int i = 2; i > 0; i--)
        {
            randomMonth(year, 12, zone, 31, cars, fileName, append);
            append = true;
            randomMonth(year, 11, zone, 30, cars, fileName, append);
            randomMonth(year, 10, zone, 31, cars, fileName, append);
            randomMonth(year, 9, zone, 30, cars, fileName, append);
            randomMonth(year, 8, zone, 31, cars, fileName, append);
            randomMonth(year, 7, zone, 31, cars, fileName, append);
            randomMonth(year, 6, zone, 30, cars, fileName, append);
            randomMonth(year, 5, zone, 31, cars, fileName, append);
            randomMonth(year, 4, zone, 30, cars, fileName, append);
            randomMonth(year, 3, zone, 31, cars, fileName, append);
            if(year%4 == 0)
                randomMonth(year, 2, zone, 29, cars, fileName, append);
            else
                randomMonth(year, 2, zone, 28, cars, fileName, append);
            randomMonth(year, 1, zone, 31, cars, fileName, append);
            year--;
            cars = rand.nextInt(1000) + 500;
        }
        
        
    }
    
    public static void randomMonth(int year, int month, String zone, int maxDay, int cars, String fileName, boolean app)
    {
        Random speed = new Random();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        List<Calendar> monthList = new ArrayList<Calendar>();
        
        for (int i = 0; i < cars; i++)
        {
            Calendar randDate = randomDate(year, month, zone, 31);
            monthList.add(randDate);
        }
        Collections.sort(monthList);
        PrintWriter output = null;
        try
        {
            output = new PrintWriter(new FileOutputStream(new File(fileName), app));
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(randomCars.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(app)
            output.append("\n");
        for (int i = cars - 1; i >= 0; i--)
        {
            output.write(sdf.format(monthList.get(i).getTime()).toString() + ";" + (speed.nextInt(20) + 45));
            if (i > 0)
                output.write("\n");
        }
        output.close();
    }
    public static Calendar randomDate(int year, int month, String zone, int maxDay)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        Random rand = new Random();
        int day;
        day = rand.nextInt(maxDay) + 1;
        int hr;
        hr = rand.nextInt(24);
        int min;
        min = rand.nextInt(59);
        int sec;
        sec = rand.nextInt(59);
        String dateText = year + "/" + month + "/";
        if (day < 10)
            dateText += "0";
        dateText += day + " ";
        if (hr < 10)
            dateText += "0";
        dateText += hr + ":";
        if (min < 10)
            dateText += "0";
        dateText += min + ":";
        if (sec < 10)
            dateText += "0";
        dateText += sec + " ";
        dateText += zone;
   
        Calendar date = Calendar.getInstance();
        try
        {
            date.setTime(sdf.parse(dateText));
        } catch (ParseException ex)
        {
            Logger.getLogger(randomCars.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        return date;
    }
    
}
