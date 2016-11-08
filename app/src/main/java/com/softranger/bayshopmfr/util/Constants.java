package com.softranger.bayshopmfr.util;

/**
 * Created by eduard on 29.04.16.
 */
public class Constants {

    public static final String US = "us";
    public static final String GB = "gb";
    public static final String DE = "de";

    public static final String USD_SYMBOL = "$";
    public static final String EUR_SYMBOL = "€";
    public static final String GBP_SYMBOL = "£";

    public static final String USA = "usa";
    public static final String UK = "uk";
    public static final String PARCELS = "PARCELS";

    public static final int IN_PROGRESS = 1, FINISHED = 2, NOT_REQUESTED = 0;

    public static final String ACTION_CHANGE_ADDRESS = "change delivery address";
    public static final String USD = "usd";

    public static class ApiResponse {
        public static final int RESPONSE_OK = 1;
        public static final int RESPONSE_FAILED = -1;
        public static final int RESPONSE_ERROR = 0;
        public static final String OK_MESSAGE = "Ok";
    }

    public static class ParcelStatus {
        public static final String AWAITING_ARRIVAL = "waitingMf";
        public static final String IN_STOCK = "mf";
        public static final String IN_PROCESSING = "processing";
        public static final String LIVE = "live";
        public static final String PACKED = "packed";
        public static final String SENT = "sent";
        public static final String RECEIVED = "received";
        public static final String LOCAL_DEPO = "local-depo";
        public static final String TAKEN_TO_DELIVERY = "taken-to-delivery";
        public static final String CUSTOMS_HELD = "customs-held";
        public static final String DEPT = "dept";
        public static final String HELD_BY_PROHIBITION = "held-by-prohibition";
        public static final String AWAITING_DECLARATION = "awaiting-declaration";
        public static final String PACKING = "packing";
        public static final String HELD_BY_USER = "held-by-user";
        public static final String DAMAGE_RECORDED = "held-by-damage";
    }

    public static class Api {
        public static final String TRACK_URL = "https://gdeposylka.ru/api/v4/";
        //        public static final String BASE_URL = "http://bayshop.com";
//        public static final String BASE_API_URL = "http://bayshop.com/api/";
        public static final String BASE_URL = "http://bay-test.tk";
        public static final String BASE_API_URL = "http://bay-test.tk/api/";
        private static final String AUTH = "auth/";
        private static final String MEMBER = "member/";
        private static final String PARCELS_COUNTER = "parcels-counter/";

        /**
         * POST
         * @return authentication to bay shop url
         */
        public static String urlAuth() {
            return BASE_API_URL + AUTH;
        }

        /**
         * GET
         * Create an url to obtain all parcels quantity in each status
         * @return strung url to access get how many parcels are in each status
         */
        public static String urlParcelsCounter() {
            return BASE_API_URL + MEMBER + PARCELS_COUNTER;
        }
    }

    public enum  Period {

        one("one"), all("all"), week("7"), month("1");

        private String mStringPeriod;

        Period(String stringPeriod) {
            mStringPeriod = stringPeriod;
        }


        @Override
        public String toString() {
            return mStringPeriod;
        }
    }
}
