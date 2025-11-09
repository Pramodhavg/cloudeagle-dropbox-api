package io.cloudeagle.assessment;

// Add ALL of these imports
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.team.DbxTeamClientV2;
import com.dropbox.core.v2.team.ListMembersResult;
import com.dropbox.core.v2.team.TeamMemberInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Main {

    // 1. UPDATE THESE: Get these from your Dropbox App Console
    // (https://www.dropbox.com/developers/apps)
    private static final String APP_KEY = "YOUR_APP_KEY"; // Your Client ID
    private static final String APP_SECRET = "YOUR_APP_SECRET"; // Your Client Secret
    
    // 2. UPDATE THIS: This MUST match one of the Redirect URIs in your app settings
    private static final String REDIRECT_URI = "http://localhost:8080/auth-callback"; 

    public static void main(String[] args) throws IOException, DbxException {

        // --- Part 1: Authentication ---

        // DbxRequestConfig helps configure the API client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("cloudeagle-assessment-app").build();

        // DbxAppInfo holds your app's key and secret
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        // DbxWebAuth handles the OAuth 2.0 flow
        DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);

        // This helper builds the URL you need to visit to authorize the app
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withRedirectUri(REDIRECT_URI, null)
                // Add all the scopes you need, separated by spaces
                // From your template, you'll need at least these:
                .withTokenAccessType(DbxWebAuth.TokenAccessType.OFFLINE) // To get a refresh token
                .withScope("team_info.read team_data.member events.read") 
                .build();

        // 1. Generate the Authorization URL
        String authorizeUrl = webAuth.authorize(webAuthRequest);
        System.out.println("--- Dropbox Authentication ---");
        System.out.println("1. Go to this URL in your browser:");
        System.out.println(authorizeUrl);
        System.out.println();
        System.out.println("2. Click 'Allow' (you might have to log in first).");
        System.out.println("3. You will be redirected to a page (it might show a '404' or 'Unable to connect' error, this is OK).");
        System.out.println("4. Copy the entire URL from your browser's address bar.");
        System.out.println("5. Find the 'code=' part of the URL and paste just the code value here:");

        // 2. Wait for the user to paste the authorization code
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (code == null) {
            System.err.println("Error: No code provided.");
            return;
        }
        code = code.trim();

        // 3. Exchange the authorization code for an access token
        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finishFromCode(code, REDIRECT_URI);
        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.finishFromCode: " + ex.getMessage());
            return;
        }

        String accessToken = authFinish.getAccessToken();
        System.out.println("\n--- Authentication Successful! ---");
        System.out.println("Your Access Token: " + accessToken);
        System.out.println("----------------------------------\n");


        // --- Part 2: Make the API Call (Fetch list of all users) ---

        // Create the Dropbox Business (Team) client
        DbxTeamClientV2 client = new DbxTeamClientV2(config, accessToken);

        System.out.println("Fetching team members...");
        try {
            // This maps to the "/team/members/list" API endpoint
            ListMembersResult result = client.team().membersList();

            // The 'result' object contains a list of members
            for (TeamMemberInfo member : result.getMembers()) {
                System.out.println("--------------------");
                System.out.println("User: " + member.getProfile().getName().getDisplayName());
                System.out.println("Email: " + member.getProfile().getEmail());
                System.out.println("Role: " + member.getRole().toString());
            }

            // Note: If you have more users than the default limit, you'll need to
            // check 'result.getHasMore()' and call 'membersListContinue(result.getCursor())'
            // For this assignment, the first page is likely sufficient.

        } catch (DbxException ex) {
            System.err.println("Error making API call to /team/members/list: " + ex.getMessage());
        }
    }
}
