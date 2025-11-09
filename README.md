This project consists of a Java application that securely connects to the Dropbox Business API using OAuth 2.0 and fetches data from a team's account.

This program is built with Java, using Apache Maven for dependency management and the official dropbox-core-sdk.

Features:
Implements the complete OAuth 2.0 Authorization Code Flow for a command-line application.

Securely exchanges an authorization code for an access token and refresh token.

Uses the official Dropbox Java SDK to make authenticated API calls.

Fetches and displays the list of all users (team members) in a Dropbox Business account.

Prerequisites
To run this project, you will need:

Java JDK 11 or higher

Apache Maven (3.6+ recommended)

A Dropbox Business (Team) trial account

A Dropbox App created in the Dropbox App Console

Setup Instructions:
You must configure the application with your own Dropbox App credentials before running it.

1. Configure Your Dropbox App
Go to the Dropbox App Console and create a new app.

Choose an API: Select "Scoped Access".

Type of access: Select "Full Dropbox" or "Team Member File Access".

Name your app: (e.g., "CloudEagle-Assessment").

Once created, go to the Permissions tab and add the following scopes:

team_info.read

team_data.member

events.read

Go to the Settings tab.

Find the Redirect URIs section and add the following URL:

http://localhost:8080/auth-callback

From this same page, copy your App key and App secret.

2. Update the Java Code
Open the file: src/main/java/io/cloudeagle/assessment/Main.java

Find these three constant variables at the top of the file and replace them with your values.

Java

public class Main {
    // 1. UPDATE THESE: Get these from your Dropbox App Console
    private static final String APP_KEY = "YOUR_APP_KEY_HERE"; // Your Client ID
    private static final String APP_SECRET = "YOUR_APP_SECRET_HERE"; // Your Client Secret
    
    // 2. UPDATE THIS: This MUST match the Redirect URI from Step 7
    private static final String REDIRECT_URI = "http://localhost:8080/auth-callback"; 
    
    // ... rest of the code
}
How to Run
Open your terminal or command prompt and navigate to the project's root directory (where the pom.xml file is).

Compile the project and download all dependencies using Maven:

Bash

mvn clean install
Once the build is successful, run the application:

Bash

mvn exec:java
Execution Flow
After running the command, follow the steps in your terminal:

The console will print a long URL. 1. Go to this URL in your browser: https://www.dropbox.com/oauth2/authorize?client_id=...

Copy this URL and paste it into your browser.

Log in to your Dropbox Business account and click "Allow" on the permissions screen.

Your browser will be redirected to http://localhost:8080.... The page will likely show an error like "This site can’t be reached." This is normal and expected.

Copy the entire URL from your browser's address bar. It will look like this: http://localhost:8080/auth-callback?code=ABC123xyz...&state=...

Find the authorization code parameter in the URL.

Paste only the code value (e.g., ABC123xyz...) back into your terminal and press Enter.

The program will authenticate and then print the list of all team members and their roles to the console.

Example Output:

--- Authentication Successful! ---
Your Access Token: sl.B...[a very long string of characters]...
----------------------------------

Fetching team members...
--------------------
User: Janani (Admin)
Email: janani2@gmail.com
Role: TeamAdmin
--------------------
User: Test User
Email: test.user@cloudeagle.com
Role: MemberOnly
