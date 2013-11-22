<!-- 
Author: Edwin Chase Noll
Purpose: Hackerati Application Assignment

This file displays data from a database 'traffic'. Filtering options include: 
database table, year, month, day, and hour.

A bar graph displaying the count (in this case vehicles on a highway) for each unit of time WITHIN the interval is displayed under the filtering options.
Under which is a list of those objects represented in the bar graph. The list includes individual vehicle times and speeds
Also on the heading the count over the ENTIRE graph and average speed over the graph is displayed
-->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<!-- Traffic System -->
<html>
    <head>
        <title>Traffic System</title>
        <link rel="stylesheet" type="text/css" href="CSS/custom.css" media="screen" />
    </head>
    <body class="p">
        <!-- Page actions to itself -->
        <form ACTION="trafficStats.php" method="post">
            
            <!-- Looks up all tables within 'traffic' database and display in combobox -->
            <label>Watch: </label> <select name="watch" onchange="this.form.submit()">
              <?php
                    //Database credentials
                    $dbServer = "localhost";
                    $dbUser = "ecn10";
                    $dbPass = "@bc123";
                    $database = "traffic";
					
                    //Connect to database
                    $con = mysql_connect($dbServer, $dbUser, $dbPass);
                    if(!$con)
                        die('Could Not Connect: ' . mysql_error());
                    mysql_select_db($database);
                    
                    //Show all tables
                    $find = mysql_query("SHOW TABLES FROM traffic");
					
                    //Cycle results
                    while($row = mysql_fetch_array($find))
                    {
                        //Include each table as an <option> in <select>
                        echo "<option value=\"".$row[0]."\"";
						
                        //Apply selected attribute to first or $_POST item
                        if (!isset($_POST['watch']) || $_POST['watch'] == $row[0])
                        {
                            $table = $row[0];
                            echo "selected";
                        }
                        echo ">".$row[0]."</option>\n";
                    }
                ?>
            </select>
			
            <!-- Looks up all years within 'traffic' database and display in combobox -->
            <label>Year: </label> <select name="year" onchange="this.form.submit()">
                <?php
				
                    //Select all rows of selected table
                    $find = mysql_query("SELECT * FROM " . $table);
					
                    //Array of unique years found
                    $yearsArray = array();
					
                    //Cycle results
                    while($row = mysql_fetch_array($find))
                    {
                        //Extract the year portion of the String
                        $yearValue = substr($row['date'], 0, 4);
						
                        //If the current year is not in the array, store it
                        if (!in_array($yearValue, $yearsArray))
                        {
                            array_push($yearsArray, $yearValue);
                        }
                    }
					
                    //Sort the array can be skipped IF database is garuanteed in order
                    array_multisort($yearsArray);
                 
                    //Cycle array
                    for($i = 0; $i < count($yearsArray); $i++)
                    {
                        //Include each year as an <option> in <select>
                        echo "<option value=\"".$yearsArray[$i]."\"";
                        
                        //Apply selected attribute to first or $_POST item
                        if ($i == 0 || (isset($_POST['year']) && $_POST['year'] == $yearsArray[$i]))
                        {
                            echo "selected";
                        }
                        echo ">".$yearsArray[$i]."</option>\n";
                    }
                    
                    //Set $_POST variable to an easier to write variable name or give it a value if no $_POST
                    if(isset($_POST['year']))
                        $postYear = $_POST['year'];
                    else
                        $postYear = $yearsArray[0];
                    
                    //Set $_POST variable to an easier to write variable name or give it a value if no $_POST
                    if(isset($_POST['month']))
                        $postMonth = $_POST['month'];
                    else 
                        $postMonth = "All";
						
                    //Set $_POST variable to an easier to write variable name or give it a value if no $_POST	
                    //If the higher time interval 'month' is 'All' force this to 'All'
                    if(isset($_POST['day']) && $postMonth != "All")
                        $postDay = $_POST['day'];
                    else 
                        $postDay = "All";
					
                    //Set $_POST variable to an easier to write variable name or give it a value if no $_POST	
                    //If the higher time interval 'day or month' is 'All' force this to 'All'
                    if(isset($_POST['hour']) && $postMonth != "All" && $postDay != "All")
                        $postHour = $_POST['hour'];
                    else 
                        $postHour = "All";
                ?>
            </select>
			
            <!-- List all months in combobox -->
            <label>Month: </label> <select name="month" onchange="this.form.submit()">
                <?php
                    //Call to funtion that writes the <option> html. The first argument is <option>Text and the second a check for the selected item
                    writeItems("All", $postMonth);
                    writeItems("Jan", $postMonth);
                    writeItems("Feb", $postMonth);
                    writeItems("Mar", $postMonth);
                    writeItems("Apr", $postMonth);
                    writeItems("May", $postMonth);
                    writeItems("Jun", $postMonth);
                    writeItems("Jul", $postMonth);
                    writeItems("Aug", $postMonth);
                    writeItems("Sep", $postMonth);
                    writeItems("Oct", $postMonth);
                    writeItems("Nov", $postMonth);
                    writeItems("Dec", $postMonth);
                ?>
            </select>
			
            <!-- List all days in combobox -->
            <label>Day: </label> <select name="day" onchange="this.form.submit()">
                <?php
                    //Call to funtion that writes the <option> html. The first argument is <option>Text and the second a check for the selected item
                    writeItems("All", $postDay);
            
                    //This portion set how many days are in the selected month. Skip if $postMonth = 'All' as Month must be selected to select a particular day
                    if ($postMonth != "All")
                    {
                        //Months with 31
                        if ($postMonth == "Jan" || $postMonth == "Mar" || $postMonth == "May" || $postMonth == "Jul" || $postMonth == "Aug" || $postMonth == "Oct" || $postMonth == "Dec")
                                $days = 31;
                        else if ($postMonth == "Feb")//Since February is so different
                        {
                            if ($postYear%4 == 0)//Check for leap year
                                $days = 29;
                            else
                                $days = 28;
                        }   
                        else
                            $days = 30; //All other 30 day months
							
                        //Cycle days and	
                        //Call to funtion that writes the <option> html. The first argument is <option>Text and the second a check for the selected item
                        for ($i = 1; $i <= $days; $i++)
                            writeItems($i, $postDay);
                    }
                ?>
            </select>
			
            <!-- List all hours (12 a.m. to 11 p.m.) in combobox -->
            <label>Hour: </label> <select name="hour" onchange="this.form.submit()">
                <?php
				
                    //Call to funtion that writes the <option> html. The first argument is <option>Text and the second a check for the selected item
                    writeItems("All", $postHour);
					
                    //Call to funtion that writes the <option> html. The first argument is <option>Text and the second a check for the selected item
                    //Skip if $postDay = 'All' as Day must be selected to select a particular hour
                    if ($postDay != "All")
                    {
                        //Write 12 a.m. isolated because not using military time
                        writeItems("12 a.m.", $postHour);
						
                        //Write a.m. hours with beginning '0' 
                        for ($i = 1; $i < 10; $i++)
                            writeItems("0".$i." a.m.", $postHour);
                        //Write a.m. hours with no beginning '0'
                        for ($i = 10; $i < 12; $i++)
                            writeItems($i." a.m.", $postHour);
                        //Write 12 p.m. isolated because not using military time
                        writeItems("12 p.m.", $postHour);
                        //Write p.m. hours with beginning '0'
                        for ($i = 1; $i < 10; $i++)
                            writeItems("0".$i." p.m.", $postHour);
                        //Write pm.m hours with no beginning '0'
                         for ($i = 10; $i < 12; $i++)
                            writeItems($i." p.m.", $postHour);
                    }
                ?>
            </select>
        </form>
        <?php
            //Initialize values for vertical axis
            $rowVal1 = 0;
            $rowVal2 = 0;
            $rowVal3 = 0;
            $rowVal4 = 0;
            $rowVal5 = 0;
            $rowVal6 = 0;
            $rowVal7 = 0;
            $rowVal8 = 0;
            $rowVal9 = 0;
            $rowVal10 = 0;
		
            //Set columns in graph
            if ($postMonth == "All")
                $columns = 12;
            else
            {
                if ($postDay == "All")
                {
                    if ($postMonth == "Jan" || $postMonth == "Mar" || $postMonth == "May" || $postMonth == "Jul" || $postMonth == "Aug" || $postMonth == "Oct" || $postMonth == "Dec")
                        $columns = 31;
                    else if ($postMonth == "Feb")
                    {
                        if ($postYear%4 == 0)
                            $columns = 29;
                        else
                            $columns = 28;
                    }
                    else
                        $columns = 30;
                }
                else
                {
                    if ($postHour == "All")
                        $columns = 24;
                    else
                        $columns = 60;
                }
            }
            //Holds counts for vehicles matching the criteria. Size 60 to handle data for months(12) days(28,29,30,31) hours(24) or minutes(60)
            $dataArray = array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            
            //Holds the date field for each record matching the criteria
            $timeArray = array();
			
            //Holds the speed field for each record matching the criteria
            $speedArray = array();
			
            //Select all rows of selected table
            $find = mysql_query("SELECT * FROM " . $table);
			
			
            while($row = mysql_fetch_array($find))
            {
                //Extract year from String
                $yearValue = substr($row['date'], 0, 4);
				
                //If same year as selected year
                if($yearValue == $postYear)
                {
                    //Extract month from String
                    $monthValue = substr($row['date'], 5, 2);
					
                    //Convert int value to text such as (01 ==> Jan)
                    switch($monthValue)
                    {
                        case(1):
                            $monthValue = "Jan";
                            break;
                        case(2):
                            $monthValue = "Feb";
                            break;
                        case(3):
                            $monthValue = "Mar";
                            break;
                        case(4):
                            $monthValue = "Apr";
                            break;
                        case(5):
                            $monthValue = "May";
                            break;
                        case(6):
                            $monthValue = "Jun";
                            break;
                        case(7):
                            $monthValue = "Jul";
                            break;
                        case(8):
                            $monthValue = "Aug";
                            break;
                        case(9):
                            $monthValue = "Sep";
                            break;
                        case(10):
                            $monthValue = "Oct";
                            break;
                        case(11):
                            $monthValue = "Nov";
                            break;
                        case(12):
                            $monthValue = "Dec";
                            break;
                        default :
                            $monthValue = "All";
                    }
		
                    //If include 'All' months or if same month as selected month
                    if($postMonth == "All" || $monthValue == $postMonth)
                    {
                        //If graph displays months store the date in $timeArray, speed in $speedArray, and increment the corresponding element in $dataArray
                        if ($postMonth == "All")
                        {
                            array_push($timeArray, $row['date']);
                            array_push($speedArray, $row['speed']);
                            switch($monthValue)
                            {
                                case("Jan"):
                                        $dataArray[0]++;
                                        break;
                                case("Feb"):
                                        $dataArray[1]++;
                                        break;
                                case("Mar"):
                                        $dataArray[2]++;
                                        break;
                                case("Apr"):
                                        $dataArray[3]++;
                                        break;
                                case("May"):
                                        $dataArray[4]++;
                                        break;
                                case("Jun"):
                                        $dataArray[5]++;
                                        break;
                                case("Jul"):
                                        $dataArray[6]++;
                                        break;
                                case("Aug"):
                                        $dataArray[7]++;
                                        break;
                                case("Sep"):
                                        $dataArray[8]++;
                                        break;
                                case("Oct"):
                                        $dataArray[9]++;
                                        break;
                                case("Nov"):
                                        $dataArray[10]++;
                                        break;
                                case("Dec"):
                                        $dataArray[11]++;
                                        break;
                            }
                        }
				                   
                        //Extract day from String
                        $dayValue = substr($row['date'], 8, 2);
						
                        //If not displaying months and if include 'All' days or if same day as selected day
                        if($postMonth != "All" && ($postDay == "All" || $dayValue == $postDay))
                        {
                            //If graph displays days store date in $timeArray, speed in $speedArray, and increment the corresponding element in $dataArray
                            if ($postDay == "All")
                            {
                                array_push($timeArray, $row['date']);
                                array_push($speedArray, $row['speed']);
                                switch($dayValue)
                                {
                                    case(1):
                                            $dataArray[0]++;
                                            break;
                                    case(2):
                                            $dataArray[1]++;
                                            break;
                                    case(3):
                                            $dataArray[2]++;
                                            break;
                                    case(4):
                                            $dataArray[3]++;
                                            break;
                                    case(5):
                                            $dataArray[4]++;
                                            break;
                                    case(6):
                                            $dataArray[5]++;
                                            break;
                                    case(7):
                                            $dataArray[6]++;
                                            break;
                                    case(8):
                                            $dataArray[7]++;
                                            break;
                                    case(9):
                                            $dataArray[8]++;
                                            break;
                                    case(10):
                                            $dataArray[9]++;
                                            break;
                                    case(11):
                                            $dataArray[10]++;
                                            break;
                                    case(12):
                                            $dataArray[11]++;
                                            break;
                                    case(13):
                                            $dataArray[12]++;
                                            break;
                                    case(14):
                                            $dataArray[13]++;
                                            break;
                                    case(15):
                                            $dataArray[14]++;
                                                break;
                                    case(16):
                                            $dataArray[15]++;
                                            break;
                                    case(17):
                                            $dataArray[16]++;
                                            break;
                                    case(18):
                                            $dataArray[17]++;
                                            break;
                                    case(19):
                                            $dataArray[18]++;
                                            break;
                                    case(20):
                                            $dataArray[19]++;
                                            break;
                                    case(21):
                                            $dataArray[20]++;
                                            break;
                                    case(22):
                                            $dataArray[21]++;
                                            break;
                                    case(23):
                                            $dataArray[22]++;
                                            break;
                                    case(24):
                                            $dataArray[23]++;
                                                break;
                                    case(25):
                                            $dataArray[24]++;
                                            break;
                                    case(26):
                                            $dataArray[25]++;
                                            break;
                                    case(27):
                                            $dataArray[26]++;
                                            break;
                                    case(28):
                                            $dataArray[27]++;
                                            break;
                                    case(29):
                                            $dataArray[28]++;
                                            break;
                                    case(30):
                                            $dataArray[29]++;
                                            break;
                                    case(31):
                                            $dataArray[30]++;
                                            break;
                                }
                            }
							
                            //Extract hour from String
                            $hourValue = substr($row['date'], 11, 2);
							
                            //Convert int value to text such as (00 ==> 12 a.m.)
                            switch($hourValue)
                            {
                                case(0):
                                        $hourValue = "12 a.m.";
                                        break;
                                case(1):
                                        $hourValue = "01 a.m.";
                                        break;
                                case(2):
                                        $hourValue = "02 a.m.";
                                        break;
                                case(3):
                                        $hourValue = "03 a.m.";
                                        break;
                                case(4):
                                        $hourValue = "04 a.m.";
                                        break;
                                case(5):
                                        $hourValue = "05 a.m.";
                                        break;
                                case(6):
                                        $hourValue = "06 a.m.";
                                        break;
                                case(7):
                                        $hourValue = "07 a.m.";
                                        break;
                                case(8):
                                        $hourValue = "08 a.m.";
                                        break;
                                case(9):
                                        $hourValue = "09 a.m.";
                                        break;
                                case(10):
                                        $hourValue = "10 a.m.";
                                        break;
                                case(11):
                                        $hourValue = "11 a.m.";
                                        break;
                                case(12):
                                        $hourValue = "12 p.m.";
                                        break;
                                case(13):
                                        $hourValue = "01 p.m.";
                                        break;
                                case(14):
                                        $hourValue = "02 p.m.";
                                        break;
                                case(15):
                                        $hourValue = "03 p.m.";
                                        break;
                                case(16):
                                        $hourValue = "04 p.m.";
                                        break;
                                case(17):
                                        $hourValue = "05 p.m.";
                                        break;
                                case(18):
                                        $hourValue = "06 p.m.";
                                        break;
                                case(19):
                                        $hourValue = "07 p.m.";
                                        break;
                                case(20):
                                        $hourValue = "08 p.m.";
                                        break;
                                case(21):
                                        $hourValue = "09 p.m.";
                                        break;
                                case(22):
                                        $hourValue = "10 p.m.";
                                        break;
                                case(23):
                                        $hourValue = "11 p.m.";
                                        break;
                                default :
                                        $hourValue = "All";
                        }

                        //If not displaying days and if include 'All' hours or if same hour as selected hour
                        if($postDay != "All" && ($postHour == "All" || $hourValue == $postHour))
                        {
                                $minuteValue = substr($row['date'], 14, 2);
                                //If graph displays hours store date in $timeArray, speed in $speedArray, and increment the corresponding element in $dataArray
                                if ($postHour == "All")
                                {
                                        array_push($timeArray, $row['date']);
                                        array_push($speedArray, $row['speed']);
                                        switch($hourValue)
                                        {
                                                case("12 a.m."):
                                                        $dataArray[0]++;
                                                        break;
                                                case("01 a.m."):
                                                        $dataArray[1]++;
                                                        break;
                                                case("02 a.m."):
                                                        $dataArray[2]++;
                                                        break;
                                                case("03 a.m."):
                                                        $dataArray[3]++;
                                                        break;
                                                case("04 a.m."):
                                                        $dataArray[4]++;
                                                        break;
                                                case("05 a.m."):
                                                        $dataArray[5]++;
                                                        break;
                                                case("06 a.m."):
                                                        $dataArray[6]++;
                                                        break;
                                                case("07 a.m."):
                                                        $dataArray[7]++;
                                                        break;
                                                case("08 a.m."):
                                                        $dataArray[8]++;
                                                        break;
                                                case("09 a.m."):
                                                        $dataArray[9]++;
                                                        break;
                                                case("10 a.m."):
                                                        $dataArray[10]++;
                                                        break;
                                                case("11 a.m."):
                                                        $dataArray[11]++;
                                                        break;
                                                case("12 p.m."):
                                                        $dataArray[12]++;
                                                        break;
                                                case("01 p.m."):
                                                        $dataArray[13]++;
                                                        break;
                                                case("02 p.m."):
                                                        $dataArray[14]++;
                                                        break;
                                                case("03 p.m."):
                                                        $dataArray[15]++;
                                                        break;
                                                case("04 p.m."):
                                                        $dataArray[16]++;
                                                        break;
                                                case("05 p.m."):
                                                        $dataArray[17]++;
                                                        break;
                                                case("06 p.m."):
                                                        $dataArray[18]++;
                                                        break;
                                                case("07 p.m."):
                                                        $dataArray[19]++;
                                                        break;
                                                case("08 p.m."):
                                                        $dataArray[20]++;
                                                        break;
                                                case("09 p.m."):
                                                        $dataArray[21]++;
                                                        break;
                                                case("10 p.m."):
                                                        $dataArray[22]++;
                                                        break;
                                                case("11 p.m."):
                                                        $dataArray[23]++;
                                                        break;
                                        }
                                }
                                else //If graph displays minutes store date in $timeArray, speed in $speedArray, and increment the corresponding element in $dataArray
                                {
                                    array_push($timeArray, $row['date']);
                                    array_push($speedArray, $row['speed']);

                                    //Extract minute from String
                                    $minuteValue = substr($row['date'], 14, 2);

                                    switch($minuteValue)
                                    {
                                            case(0):
                                                    $dataArray[0]++;
                                                    break;
                                            case(1):
                                                    $dataArray[1]++;
                                                    break;
                                            case(2):
                                                    $dataArray[2]++;
                                                    break;
                                            case(3):
                                                    $dataArray[3]++;
                                                    break;
                                            case(4):
                                                    $dataArray[4]++;
                                                    break;
                                            case(5):
                                                    $dataArray[5]++;
                                                    break;
                                            case(6):
                                                    $dataArray[6]++;
                                                    break;
                                            case(7):
                                                    $dataArray[7]++;
                                                    break;
                                            case(8):
                                                    $dataArray[8]++;
                                                    break;
                                            case(9):
                                                    $dataArray[9]++;
                                                    break;
                                            case(10):
                                                    $dataArray[10]++;
                                                    break;
                                            case(11):
                                                    $dataArray[11]++;
                                                    break;
                                            case(12):
                                                    $dataArray[12]++;
                                                    break;
                                            case(13):
                                                    $dataArray[13]++;
                                                    break;
                                            case(14):
                                                    $dataArray[14]++;
                                                    break;
                                            case(15):
                                                    $dataArray[15]++;
                                                    break;
                                            case(16):
                                                    $dataArray[16]++;
                                                    break;
                                            case(17):
                                                    $dataArray[17]++;
                                                    break;
                                            case(18):
                                                    $dataArray[18]++;
                                                    break;
                                            case(19):
                                                    $dataArray[19]++;
                                                    break;
                                            case(20):
                                                    $dataArray[20]++;
                                                    break;
                                            case(21):
                                                    $dataArray[21]++;
                                                    break;
                                            case(22):
                                                    $dataArray[22]++;
                                                    break;
                                            case(23):
                                                    $dataArray[23]++;
                                                    break;
                                            case(24):
                                                    $dataArray[24]++;
                                                    break;
                                            case(25):
                                                    $dataArray[25]++;
                                                    break;
                                            case(26):
                                                    $dataArray[26]++;
                                                    break;
                                            case(27):
                                                    $dataArray[27]++;
                                                    break;
                                            case(28):
                                                    $dataArray[28]++;
                                                    break;
                                            case(29):
                                                    $dataArray[29]++;
                                                    break;
                                            case(30):
                                                    $dataArray[30]++;
                                                    break;
                                            case(31):
                                                    $dataArray[31]++;
                                                    break;
                                            case(32):
                                                    $dataArray[32]++;
                                                    break;
                                            case(33):
                                                    $dataArray[33]++;
                                                    break;
                                            case(34):
                                                    $dataArray[34]++;
                                                    break;
                                            case(35):
                                                    $dataArray[35]++;
                                                    break;
                                            case(36):
                                                    $dataArray[36]++;
                                                    break;
                                            case(37):
                                                    $dataArray[37]++;
                                                    break;
                                            case(38):
                                                    $dataArray[38]++;
                                                    break;
                                            case(39):
                                                    $dataArray[39]++;
                                                    break;
                                            case(40):
                                                    $dataArray[40]++;
                                                    break;
                                            case(41):
                                                    $dataArray[41]++;
                                                    break;
                                            case(42):
                                                    $dataArray[42]++;
                                                    break;
                                            case(43):
                                                    $dataArray[43]++;
                                                    break;
                                            case(44):
                                                    $dataArray[44]++;
                                                    break;
                                            case(45):
                                                    $dataArray[45]++;
                                                    break;
                                            case(46):
                                                    $dataArray[46]++;
                                                    break;
                                            case(47):
                                                    $dataArray[47]++;
                                                    break;
                                            case(48):
                                                    $dataArray[48]++;
                                                    break;
                                            case(49):
                                                    $dataArray[49]++;
                                                    break;
                                            case(50):
                                                    $dataArray[50]++;
                                                    break;
                                            case(51):
                                                    $dataArray[51]++;
                                                    break;
                                            case(52):
                                                    $dataArray[52]++;
                                                    break;
                                            case(53):
                                                    $dataArray[53]++;
                                                    break;
                                            case(54):
                                                    $dataArray[54]++;
                                                    break;
                                            case(55):
                                                    $dataArray[55]++;
                                                    break;
                                            case(56):
                                                    $dataArray[56]++;
                                                    break;
                                            case(57):
                                                    $dataArray[57]++;
                                                    break;
                                            case(58):
                                                    $dataArray[58]++;
                                                    break;
                                            case(59):
                                                    $dataArray[59]++;
                                                    break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ?>
		
        <!-- Begin column graph -->
        <div class="nonScrollingDiv">
        <table class="bargraph">
          <tr class="barRow">
            <?php
                
                //Find max value in $dataArray 
                $max = max($dataArray);
					
                //If there is not data set the max to 10
                if ($max == 0)
                    $rowVal1 = 10;
                else //ceil the max value to the nearest 10 and set max vertical axis value
                    $rowVal1 = ceil(($max/10)) * 10;

                //Create 10 equal divisions for vertical axis values
                $rowVal2 = $rowVal1 - ($rowVal1/10);
                $rowVal3 = $rowVal2 - ($rowVal1/10);
                $rowVal4 = $rowVal3 - ($rowVal1/10);
                $rowVal5 = $rowVal4 - ($rowVal1/10);
                $rowVal6 = $rowVal5 - ($rowVal1/10);
                $rowVal7 = $rowVal6 - ($rowVal1/10);
                $rowVal8 = $rowVal7 - ($rowVal1/10);
                $rowVal9 = $rowVal8 - ($rowVal1/10);
                $rowVal10 = $rowVal9 - ($rowVal1/10);

                //Write vertical axis max value cell
                    echo "<td class=\"vAxis\">". $rowVal1 . "</td>";
                    
                //Call to funtion that writes the html for the column chart. The first argument is number of columns, the second the increment for the vertical axis (for calc proportional height), and the count (for calc height)
                for($i = 0; $i < $columns; $i++)
                        column($columns,$rowVal10,$dataArray[$i]);
            ?>
          </tr>
          <tr class="barRow">
  
            <!-- html for the remaining vertical axis values -->
            <td class="vAxis"><?php echo $rowVal2 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis"><?php echo $rowVal3 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis"><?php echo $rowVal4 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis"><?php echo $rowVal5 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis"><?php echo $rowVal6 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis"><?php echo $rowVal7 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis"><?php echo $rowVal8 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis"><?php echo $rowVal9 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis"><?php echo $rowVal10 ?></td>
          </tr>
          <tr class="barRow">
            <td class="vAxis">0</td>
          </tr>
          <tr class="barRow">
            <?php
			
                //Write horizontal axis values for months
                if ($postMonth == "All")
                {
                    echo "<td class=\"corner\">Mth</td>";
                    echo "<td class=\"hAxis\">Jan</td>";
                    echo "<td class=\"hAxis\">Feb</td>";
                    echo "<td class=\"hAxis\">Mar</td>";
                    echo "<td class=\"hAxis\">Apr</td>";
                    echo "<td class=\"hAxis\">May</td>";
                    echo "<td class=\"hAxis\">Jun</td>";
                    echo "<td class=\"hAxis\">Jul</td>";
                    echo "<td class=\"hAxis\">Aug</td>";
                    echo "<td class=\"hAxis\">Sep</td>";
                    echo "<td class=\"hAxis\">Oct</td>";
                    echo "<td class=\"hAxis\">Nov</td>";
                    echo "<td class=\"hAxis\">Dec</td>";
                }
                else if ($postDay == "All") //Write horizontal axis values for days
                {
                    echo "<td class=\"corner\">Day</td>";
                    for ($i = 1; $i <= $columns; $i++)
                        echo "<td class=\"hAxis\">".$i."</td>";
                }
                else if ($postHour == "All") //Write horizontal axis values for hours
                {
                    echo "<td class=\"corner\">Hr</td>";
                    echo "<td class=\"hAxis\">12</td>";
                    for ($i = 1; $i <= 12; $i++)
                        echo "<td class=\"hAxis\"td>".$i."</td>";
                    for ($i = 1; $i <= 11; $i++)
                        echo "<td class=\"hAxis\"td>".$i."</td>";
                }
                else //Write horizontal axis values for minutes
                {
                    echo "<td class=\"corner\">Min</td>";
                    for ($i = 0; $i < $columns; $i++)
                        echo "<td class=\"hAxis\"td>".$i."</td>";
                }
            ?>
          </tr>
        </table>
        </div>
        <!-- Write a tables for headings Time, Speed, Count Over Graph, and Average Speed Over Graph -->
        <div class="nonScrollingDiv">
        <table class="bargraph">
            <tr>
                <td class="headingLarge">
                    Time
                </td>
                <td class="headingSmall">
                    Speed
                </td>
                <td class="headingSmall">
                    Count: <?php echo count($speedArray); ?>
                </td>
                <td class="headingLarge">
                    Average Speed: <?php
					
                    //Calculate average speed over all represented data
                    $average = 0;
                    for ($i = 0; $i < count($speedArray); $i++)
                        $average += $speedArray[$i];
                    if(count($speedArray) != 0)
                        $average = $average/  count($speedArray);
                    else
                        $average = 0;
                    echo $average . " MPH";
                    ?>
                </td>
            </tr>
        </table>
        </div>
        <!-- Write tables listing times and speeds of represented data -->
        <div class="scrollingDiv">
        <table class="listChart">
             <?php
                for ($i = 0; $i < count($timeArray); $i++)
                    echo "<tr><td class=\"listItemLarge\">" . $timeArray[$i] . "</td><td class=\"listItemSmall\">" . $speedArray[$i] . "</td><td class=\"listItemSmall\"></td></td><td class=\"listItemLarge\"></td></tr>\n";
             ?>    
        </table>
        </div>
    </body>
</html>

<?php

//This function writes <option> tags that correspond to the <select> tag creating the contents of a Combo box
//First paramenter is the value of the <option> and the displayed text. Second paramenter checks against the first for the selected attribute
function writeItems($item, $selection)
{
    echo "<option value=\"" . $item . "\"";
    if (isset($selection) && $item == $selection)
    {
        echo ' selected';
    }
    echo ">". $item . "</option>\n";
}

//This funtion writes the column for the chart
//First parament is the number of columns. The second and third are used in calculating the height of each column
function column($col, $heightPer, $height)
{
  echo "<td class=\"columnContainerCell\" width=\"" . 95/$col . "%\" rowspan=\"11\">\n";
  echo "<table class=\"columnContainer\" height=\"" . (((100/11)/$heightPer)*($height)) . "%\">\n";
  echo "<tr>\n";
  echo "<td class=\"column\">";
  
  //Display the count in the bar
  //Displayed vertically as not to change column width
  if ($height != 0)
  {
      //echo "<font size=\"0\" color=\"white\">";
      $heightArray = str_split($height);
      for($i = 0; $i < count($heightArray); $i++)
            echo $heightArray[$i]."<br>";
      //echo "</font>";
  }
  echo "\n";
  echo "</td>\n";
  echo "</tr>\n";
  echo "</table>\n";
  echo "</td>\n"; 
}
?>