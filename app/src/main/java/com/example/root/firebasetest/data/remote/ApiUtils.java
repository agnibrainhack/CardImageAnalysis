package com.example.root.firebasetest.data.remote;

/**
 * Created by root on 29/12/17.
 */


public class ApiUtils {
    private ApiUtils(){}
    public static final String BASE_URL = "http://nilagnik.pythonanywhere.com/";
    public static APIService getAPIService(){
        return RetofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
