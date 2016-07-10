package com.softranger.bayshopmf.util;

import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by eduard on 29.04.16.
 */
public class Constants {

    public static final String US = "us";
    public static final String GB = "gb";
    public static final String DE = "de";

    public static final String USA = "usa";
    public static final String UK = "uk";

    public static Character[] ALPHABET = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    public static final int IN_PROGRESS = 1, FINISHED = 2, NOT_REQUESTED = 0;

    public static final String ACTION_CHANGE_ADDRESS = "change delivery address";

    public static class ApiResponse {
        public static final int RESPONSE_OK = 1;
        public static final int RESPONSE_FAILED = -1;
        public static final int RESPONSE_ERROR = 0;
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
        private static final String AUTH = "auth/";
        public static final String OPTION_PHOTO = "photo";
        public static final String OPTION_CHECK = "verification";
        public static final String OPTION_PERIOD = "?period=";
        private static final String PARCELS = "parcels/";
        private static final String VIEW = "view/";
        private static final String PARCEL_STEP = "parcel-step/";
        private static final String DELETE_BOX = "delete-box/";
        private static final String MEMBER_ADDRESS = "member-address/";
        private static final String MEMBER_ADDRESS_ADD = "member-address-add/";
        private static final String PHONE_CODES = "phone-formats";
        private static final String REGISTER = "register/";
        private static final String RESTORE = "restore/";
        private static final String MEMBER = "member/";
        private static final String PERSONAL_DATA = "personal-data/";
        private static final String MAIL_OPTIONS = "mail-options/";
        private static final String BALANCE = "balance/";
        private static final String PARCELS_COUNTER = "parcels-counter/";

        /**
         * POST
         * @return authentication to bay shop url
         */
        public static String urlAuth() {
            return URL + AUTH;
        }


        /**
         * GET/POST
         * Create an url used to retreive or update user data
         * @return string url to access or change user data
         */
        public static String urlPersonalData() {
            return URL + MEMBER + PERSONAL_DATA;
        }

        /**
         * GET
         * Create an url to obtain all parcels quantity in each status
         * @return strung url to access get how many parcels are in each status
         */
        public static String urlParcelsCounter() {
            return URL + MEMBER + PARCELS_COUNTER;
        }

        /**
         * GET/POST
         * Create an url used to access user mail settings
         * @return string url to access or update user mail options
         */
        public static String urlMailOptions() {
            return URL + MEMBER + MAIL_OPTIONS;
        }

        /**
         * POST
         * Create an url used to register a user in Bay data base
         * @return url which will receive registration data
         */
        public static String urlRegister() {
            return URL + AUTH + REGISTER;
        }


        /**
         * POST
         * Create an url used to restore user password
         * @return url which receives user email and send a restore password request
         */
        public static String urlRestorePassword() {
            return URL + AUTH + RESTORE;
        }

        /**
         * POST
         * Create an url to delete a box from a PUS parcel
         * @param packageId in which you want to delete the box
         * @param boxId for box you want to delete
         * @return url to delete the box with given parameters
         */
        public static String urlDeleteBoxFromParcel(String packageId, String boxId) {
            return URL + PARCELS.replace("s", "") + DELETE_BOX + packageId + File.separator + boxId;
        }

        /**
         * GET
         * Build an url to access parcel building
         * @param step building steps 1 - 6
         * @return url to the given step
         */
        public static String urlBuildStep(int step) {
            return URL + PARCEL_STEP + step;
        }

        /**
         * POST
         * Build an url to add items to a parcel
         * @param step building steps 1 - 6
         * @param parcelId PUS parcel id to which you want to add boxes
         * @return url to the given step
         */
        public static String urlBuildStep(int step, String parcelId) {
            return URL + PARCEL_STEP + step + File.separator + parcelId;
        }

        /**
         * GET
         * Create an url to get parcels after they leave In Stock status
         * @param depot for which to get the parcels (US, GB, DE)
         * @param parcelStatus status you want to access
         * @return a url to obtain a list of parcels
         */
        public static String urlOutgoing(String depot, String parcelStatus) {
            return URL + PARCELS + depot + "/" + parcelStatus;
        }

        /**
         * Hz ce za url este aista))
         * @return
         */
        public static String urlStorage() {
            return URL + STORAGE;
        }

        /**
         * GET
         * Create an url to access the details for given in stock item id
         * @param id for which you want to get details
         * @return url to obtain item details
         */
        public static String urlDetailedInStock(String id) {
            return URL + MF_LIST + id;
        }

        /**
         * GET
         * Create an url to obtain a list of waiting arrival items
         * @param depot for which you need to get the list
         * @return url to awaiting arrival list for given storage
         */
        public static String urlWaitingArrival(String depot) {
            return URL + WAITING_MF_LIST + depot;
        }

        /**
         * GET
         * Create an url to obtain details for an awaiting arrival item
         * @param waitingItemId for which you need the details
         * @return an url to access given item details
         */
        public static String urlWaitingArrivalDetails(String waitingItemId) {
            return URL + WAITING_MF + waitingItemId;
        }

        /**
         * POST
         * Create an url used to edit awaiting arrival item details
         * @param itemId for which you want to edit details
         * @return url to item for which you need to edit details
         */
        public static String urlEditWaitingArrivalItem(String itemId) {
            return URL + WAITING_MF + EDIT + itemId;
        }

        /**
         * POST
         * Create a url to add an awaiting arrival item
         * @return url to which to send a new awaiting arrival item
         */
        public static String urlAddWaitingArrivalItem() {
            return URL + WAITING_MF + "add";
        }

        /**
         * GET
         * Create an url to obtain in stock items
         * @param storage for which to get items
         * @return a url to access all in stock items for given storage
         */
        public static String urlInStockItems(String storage) {
            return URL + MF_LIST + IN_STOCK + storage;
        }

        /**
         * GET
         * Create an url to access the declaration for an In stock item
         * @param mfId for which to obtain declaration
         * @return
         */
        public static String urlMfDeclaration(String mfId) {
            return URL + MF_DECLARATION + mfId;
        }

        /**
         * POST
         * Create an url to send a request for additional photos
         * @return url to request photos for a product
         */
        public static String urlAdditionalPhoto() {
            return URL + STORAGE;
        }

        /**
         * GET
         * Create an url to obtain a parcel details
         * @param parcelId for which to get details
         * @return url to access given parcel details from server
         */
        public static String urlViewParcelDetails(String parcelId) {
            return URL + PARCELS + VIEW + parcelId;
        }

        /**
         * POST
         * Create an url used to either add new or edit address
         * @param addressId for address you want to edit or null if you need to add new address
         * @return url to edit or add a new address
         */
        public static String urlAddNewAddress(@Nullable String addressId) {
            if (addressId == null) {
                return URL + MEMBER_ADDRESS_ADD;
            } else {
                return URL + MEMBER_ADDRESS_ADD + addressId;
            }
        }

        /**
         * GET
         * Create an url to get an address details
         * @param addressId for which you need details
         * @return url to get address details
         */
        public static String urlGetAddress(String addressId) {
            return URL + MEMBER_ADDRESS + addressId;
        }

        /**
         * GET
         * Create an url to obtain phonecode and countries ids
         * @return url to access phone codes from server
         */
        public static String urlGetPhoneCodes() {
            return URL + MEMBER_ADDRESS + PHONE_CODES;
        }

        /**
         * GET
         * Create an url to obtain user payment
         * @param period
         * @return
         */
        public static String urlUserBalance(Period period) {
            return URL + BALANCE + OPTION_PERIOD + period.getPeriod();
        }

        /**
         *
         * @return
         */
        public static String urlAddressesList() {
            return URL + MEMBER_ADDRESS;
        }
    }

    public enum  Period {

        one, all, week, month;

        public String getPeriod() {
            switch (this) {
                case one: return ONE;
                case month: return MONTH;
                case week: return WEEK;
                default: return ALL;
            }
        }
        public final String ONE = "one";
        public final String ALL = "all";
        public final String WEEK = "6";
        public final String MONTH = "1";
    }
}
