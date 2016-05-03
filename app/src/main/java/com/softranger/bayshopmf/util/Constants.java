package com.softranger.bayshopmf.util;

/**
 * Created by eduard on 29.04.16.
 */
public class Constants {

    public static class ApiResponse {
        public static final int RESPONSE_OK = 1;
        public static final int RESPONSE_FAILED = -1;
        public static final int RESPONSE_ERROR = 0;
        public static final int ARRAY_IS_EMPTY = 2;
    }

    public static class Api {
        private static final String URL = "http://md.bay-dev.tk/api/";
        private static final String STORAGE = "storage/";
        private static final String AUTH = "auth";

        public static String getAuthUrl() {
            return URL + AUTH;
        }

        public static String getStorageUrl() {
            return URL + STORAGE;
        }
    }
}
