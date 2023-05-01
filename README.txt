Title: Scheduler
Purpose: This is my project for QAM2 TASK 1 for C195 at WGU
Author: Patrick Barnhardt
Contact: pbarnh1@wgu.edu
Date: 11/30/2022
Version: 3.0
IDE: IntelliJ IDEA 2021.1.3 (Community Edition)
JDK: 17.0.1
JavaFX: 11.0.2
MySQL: mysql-connector-java-8.0.25
How to run: Upon running the Main configuration a login screen will be shown.  The user must have valid login credentials, username and password, that match the local database.  Once entered, the user can press login to enter the main screen.
Description of additional report: Total Appointments by User counts the number of appointments per user_ID and lists the username, count and userID.  I had SQL handle the bulk of the work by creating a JOIN between the appointments and users tables based on user_ID. SQL also handles the counts.
Notes: There is extra code that has been commented out regarding the appointment alerts, I originally assumed these should be per-user.  I was having issues with the current userID, and found out that for the sake of this project, showing the alerts to all users is acceptable.