This file contains information reguarding the accompaning:

hwyWatch.java
hwy27.txt
hwy44.txt
TrafficSystem.java
trafficStats.php

hwyWatch.java includes database information.
WampSever2.2e phpMyAdmin was used for this database.
user ecn10 with a password of @bc123.
The database name is traffic.
Any adjustments needed can be done in hwyWatch.java member variables.

hwy27.txt and hwy44.txt include random data from a highway.
Both files contain between 20,000 and 30,000 values.
Initial collection to a database could take a while.
Values must be sorted most recent to least recent and in the form. 
	2012/12/31 23:48:32 CST;46
	2012/12/31 23:46:31 CST;56
	2012/12/31 22:56:12 CST;48
	2012/12/31 22:47:32 CST;61
What I have provided is.
When reading the file the date/time field is read as a String delimited by the ;
Then the speed.
To simulate data coming in add more recent entries above and in remaining in order most recent to least recent.
These files and any additional hwyWatches that are created are assumed to be created and generated by another system
I've only include two files for the hard coded objects

TrafficSystem.java initiates the the two hard coded hwyWatch objects hwy44 and hwy27.
Once your database is setup and are running TrafficSystem.java collection should start automattically 
Remeber it could take a while if the file contents remain as I have provided.
hwy44 collects every minute and
hwy27 collects every hour 
There is a menu system to change the period as well as other properties
All is explained in file comments and the menu system while running the program
For quick accessment of collecting and waiting (monitor in the menus) is of course quicker when using small periods
hwy27 especially you may want to change.
New hwyWatches can be created however there is no system emplemented to restart them once the program has been terminated
However the database tables created from any new hwyWatches will remain.
If creating a new hwyWatch with a non-exitsting file name for reading data will fail because as above...
These files and any additional files used by hwyWatches that are created are assumed to be created and generated by another system
I've only include two files for the hard coded objects.
New hwyWatches can collect from these two files however if this is not desired new files must be created

********************************************************

I WILL INCLUDE randomCars.java TO ASSIST WITH CREATING DATA FILE IF DESIRED
HOWEVER THESE WERE ONLY CREATED FOR ME TO GENERATE THE DATA
AND NOT INTENDED TO BE PART OF THE APPLICATION PROCESS AS THEY
ARE QUICK WRITTEN AND UNPOLISHED

THANK YOU

********************************************************
trafficStats.php displays data over a selected interval
options include a hwyWatch text is its database table name
years are all years found withing the tables
months all
days all
hours all
minutes all

An initial table will be selected and an initial year will be selected
months, days, hours and, minutes have an "All" option
A month must be selected to be able to select a day
A day must be selected to be able to select an hour
Any value of all forces the smaller time internals to also be All.
The upper red bar chart displays COUNT (cars passing by the monitor) over one unit of time smaller than the selected interval
For instance if hwy44, 2012, Jan is selected and days and hours are All
Then All the days in Jan are represented in the graph
The list below in blue display the individual records represented in the bar graph (Time and Speed)
Other data here are the count over the graph and the average speed over the graph