package com.example.booksearch;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//Class for handling networking functions in the App
public final class NetworkingUtils {

    public static final String LOG_TAG = NetworkingUtils.class.getSimpleName();

    //private constructor because this class shouldn't be instantiated.
    private NetworkingUtils() {
    }


    //Method that combines the helper methods to return a list of Books from a given url
    public static List<Book> fetchBookData(String requestURL) {
        //Create Url object
        URL url = createUrl(requestURL);
        String jsonResponse = null;

        //Make http request on the url and get store the json response
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "There was a problem making HTTP request", e);
        }

        //Create an Books list to store the new List created
        List<Book> fetchedBooks = extractBookData(jsonResponse);
        Log.e(LOG_TAG, "fetching data" + jsonResponse);
        return fetchedBooks;
    }


    //Method that extracts the data from the JSON Response
    public static List<Book> extractBookData(String JSONResponse) {
        //if the response is empty or null, return null since we need to return an object type

        if (TextUtils.isEmpty(JSONResponse)) {
            return null;
        }

        //Empty array list to store the data to  new extracted Book objects
        List<Book> books = new ArrayList<>();

        //Try to parse the JSON if there is an JSONException catch the error so the app doesn't crash
        try {

            //String for author
            String authors;

            JSONObject rootJSONObject = new JSONObject(JSONResponse);
            JSONArray jsonItemsArray = rootJSONObject.getJSONArray("items");

            //Looping through the ITEMS Array and getting the data
            for (int i = 0; i < jsonItemsArray.length(); i++) {
                JSONObject jsonItemsObject = jsonItemsArray.getJSONObject(i);
                JSONObject jsonVolumeInfo = jsonItemsObject.getJSONObject("volumeInfo");
                if(jsonVolumeInfo.has("authors")) {
                    JSONArray jsonauthorsArray = jsonVolumeInfo.getJSONArray("authors");
                    authors = jsonauthorsArray.getString(0);
                }
                else {
                    authors = "Author not found";
                }

                //Get the books data
                JSONObject jsonImageLinks = jsonVolumeInfo.getJSONObject("imageLinks");
                JSONObject jsonsalesInfoObject = jsonItemsObject.getJSONObject("saleInfo");
                String title = jsonVolumeInfo.getString("title");
                String whereToBuy;

                //checking to see if the object at the given index even has a buyLink
                if (jsonsalesInfoObject.has("buyLink")) {
                    whereToBuy = jsonsalesInfoObject.getString("buyLink");
                } else {
                    whereToBuy = "";
                }

                books.add(new Book(title, authors, whereToBuy));

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing Book JSON results", e);
        }

        return books;

    }

    //Method used to Create an URL from an String
    public static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error making URL");
        }
        return url;
    }

    //Method used to make an HTTP Request with a given url and return the JSON response
    public static String makeHTTPRequest(URL url) throws IOException {

        //initalize our empty JSON reponse
        String JSONresponse = "";

        //If the URL is null return from this function
        if (url == null) {
            return JSONresponse;
        }

        //Don't refer to any object yet but we need the variable
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            //Setting the time we're willing to wait for the server to send the next byte. If time expires a timeout error will occur
            urlConnection.setReadTimeout(10000 /*Milliseconds*/);
            //Setting hte time we're willing to wait for communication to be established if no communcation is established before the timout a socketTimoutException occurs.
            urlConnection.setConnectTimeout(15000 /*Milliseconds*/);
            //Specifies the action we wan't to take on the URL
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the connection was successfulthen read from the input stream and parse the JSON
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                JSONresponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response Code" + urlConnection.getResponseCode());

            }


        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem getting Book JSON Results from URL", e);

        }

        //finally after reading all the data from the input stream error or not, destroy the connection and close the stream to save memory.
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JSONresponse;


    }

    //Method to parse the InputStream to a long JSON String that we can use
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (inputStream != null) {

            //Creating buffered reader to read text stream more efficiently, reader takes in an input stream as arguements.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = bufferedReader.readLine();

            //While the reader still has something to read
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }

        //Return the total JSONresponse which is the stringBuilders result
        return stringBuilder.toString();

    }

}
