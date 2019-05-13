package com.huiwang.net;

public class API {

    public static String key="";
    public static String orgno="";
    public static String uid="";
    public static boolean Debug=false;

    public static final String BASE_URL = "https://apitest.xmfree.net"+":8118";


    public static final String LOGIN_URL = BASE_URL + "/communication/communicationuseradd";
    public static final String USER_QUERY_URL = BASE_URL + "/communication/communicationuserqurey";
    public static final String APPLY_USER_URL = BASE_URL + "/communication/communicaionuserapply";
    public static final String APPLY_AGREE_URL = BASE_URL + "/communication/communicaionuserapplyagree";
    public static final String APPLY_REFUSE_URL = BASE_URL + "/communication/communicaionuserapplyrefuse";
    public static final String SEND_TEXT_URL = BASE_URL + "/communication/communicationsendtextmessage";
    public static final String SEND_VOID_URL = BASE_URL + "/communication/communicationsendvoicemessage";
    public static final String APPLY_VOID_URL = BASE_URL + "/communication/communicationphoneagree";
    public static final String AGREE_VOID_URL = BASE_URL + "/communication/communicationlineagree";
    public static final String REFUSE_VOID_URL = BASE_URL + "/communication/communicationlinerefuse";
    public static final String GET_FRIEND_LIST_URL = BASE_URL + "/communication/getfriendlist";
    public static final String GET_FRIENDAPPLY_LIST_URL = BASE_URL + "/communication/getfriendapplylist";




}
