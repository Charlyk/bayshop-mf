package com.softranger.bayshopmf.util;

/**
 * Created by eduard on 29.04.16.
 */
public class Constants {

    public static final String USA = "usa";
    public static final String UK = "uk";
    public static final String DE = "de";

    public static Character[] ALPHABET = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    public static final int IN_PROGRESS = 1, FINISHED = 2, NOT_REQUESTED = 0;

    public static class ApiResponse {
        public static final int RESPONSE_OK = 1;
        public static final int RESPONSE_FAILED = -1;
        public static final int RESPONSE_ERROR = 0;
        public static final int RESONSE_UNAUTHORIZED = -2;
    }

    public static class ParcelStatus {
        public static final String IN_STOCK = "in-stock";
    }

    public static class Api {
        private static final String URL = "http://md.bay-dev.tk/api/";
        private static final String STORAGE = "storage/";
        private static final String MF_LIST = "mf-list/";
        private static final String IN_STOCK = "in-stock/";
        private static final String WAITING_MF_LIST = "waiting-mf-list/";
        private static final String MF_DECLARATION = "mf-storage-declaration/";
        private static final String WAITING_MF = "waiting-mf/";
        private static final String EDIT = "edit/";
        private static final String AUTH = "auth";
        public static final String OPTION_PHOTO = "photo";
        public static final String OPTION_CHECK = "verification";

        public static String getAuthUrl() {
            return URL + AUTH;
        }

        public static String getStorageUrl() {
            return URL + STORAGE;
        }

        public static String getMfList(String id) {
            return URL + MF_LIST + id;
        }

        public static String getWaitingMfList(String storage) {
            return URL + WAITING_MF_LIST + storage;
        }

        public static String getWaitingMfItem(String waitingItemId) {
            return URL + WAITING_MF + waitingItemId;
        }

        public static String editWaitingMfItem(String itemId) {
            return URL + WAITING_MF + EDIT + itemId;
        }

        public static String addWaitingMfItem() {
            return URL + WAITING_MF + "add";
        }

        public static String getInStockItems(String storage) {
            return URL + MF_LIST + IN_STOCK + storage;
        }

        public static String getMfDeclarationUrl(String mfId) {
            return URL + MF_DECLARATION + mfId;
        }

        public static String getAdditioalPhotoUrl() {
            return URL + STORAGE;
        }
    }

    public static class ListToShow {
        public static final String IN_STOCK = "in stock items";
        public static final String AWAITING_ARRIVAL = "awaiting arrival";
        public static final String IN_PROCESSING = "in processing";
    }
}
