package com.example.navigation;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final int CREDENTIALS_FILE = R.raw.credentials;
//    private static final String CREDENTIALS_FILE_PATH = "/src/main/res/raw/credentials.json";


    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final Context ctx, final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        // Load client secrets.
//        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        InputStream in = ctx.getResources().openRawResource(CREDENTIALS_FILE);
        if (in == null) {
            Log.i("tag", "Name, Major");
            throw new FileNotFoundException("Resource not found: " + String.valueOf(CREDENTIALS_FILE));
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        Log.i("tag", "Name, Major");

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        Log.i("tag", "Name, Major");
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Log.i("tag", "Name, Major");
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static void test(final Context ctx) throws IOException, GeneralSecurityException {

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        final String spreadsheetId = "1vngVfBzGfGL8Wa8yDar1_cZCiaoK8orEYew8f-b_fE4";
        final String range = "Class Data!A1:A2";

        Log.i("tag", "Name, Major");
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(ctx, HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Log.i("tag", "Name, Major");
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        Log.i("tag", "Name, Major");
        List<List<Object>> values = response.getValues();
        Log.i("tag", "Name, Major");
        if (values == null || values.isEmpty()) {
//            System.out.println("No data found.");
            Log.i("tag", "No data found.");
        } else {
//            System.out.println("Name, Major");
            Log.i("tag", "Name, Major");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                Log.i("tag", row.get(0) + ", " + row.get(1));
            }
        }
    }

}