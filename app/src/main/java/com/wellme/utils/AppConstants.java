package com.wellme.utils;



public interface AppConstants {

    //String BASE_URL_NEW = "http://173.212.250.62:3023/api/"; // For server url
    //String IMAGE_URL_NEW = "http://173.212.250.62:3023"; // For server url
    String BASE_URL_NEW = "http://173.212.250.62:3024/api/";
    String IMAGE_URL_NEW = "http://173.212.250.62:3024";
    String IMAGE_URL_NEW1 = IMAGE_URL_NEW+"/media/user_image/";
    //String SOCKET_URL = "http://173.212.250.62:3023"; // For server url
    String SOCKET_URL = "http://173.212.250.62:3024";
    String SOCKET_IMAGE_URL = IMAGE_URL_NEW+"/media/user_image/chat/";




    /**
     * HTTP request method types
     */
    int POST_TYPE = 1, GET_TYPE = 0, PUT_TYPE = 3, DELETE_TYPE = 4;
    String LOGIN_API = "login_user";
    String REGISTER_API = "register_user";
}
