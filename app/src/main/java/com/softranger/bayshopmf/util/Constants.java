package com.softranger.bayshopmf.util;

import java.io.File;

/**
 * Created by eduard on 29.04.16.
 */
public class Constants {

    public static final String USA = "us";
    public static final String UK = "gb";
    public static final String DE = "de";

    public static Character[] ALPHABET = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    public static final int IN_PROGRESS = 1, FINISHED = 2, NOT_REQUESTED = 0;

    public static class ApiResponse {
        public static final int RESPONSE_OK = 1;
        public static final int RESPONSE_FAILED = -1;
        public static final int RESPONSE_ERROR = 0;
    }

    public static class ParcelStatus {
        public static final String IN_STOCK = "in-stock";
        public static final String IN_PROCESSING = "processing";
        public static final String LIVE = "live";
        public static final String PACKED = "packed";
        public static final String SENT = "sent";
        public static final String RECEIVED = "received";
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
        private static final String PARCELS = "parcels/";
        private static final String PARCEL_STEP = "parcel-step/";
        private static final String DELETE_BOX = "delete-box/";

        /**
         * POST
         * @return authentication to bay shop url
         */
        public static String urlAuth() {
            return URL + AUTH;
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
    }

    public static class ListToShow {
        public static final String IN_STOCK = "in stock items";
        public static final String AWAITING_ARRIVAL = "awaiting arrival";
        public static final String IN_PROCESSING = "in processing";
        public static final String IN_FORMING = "in forming";
        public static final String AWAITING_SENDING = "awaiting sending";
        public static final String SENT = "sent";
        public static final String RECEIVED = "received";
    }
}
