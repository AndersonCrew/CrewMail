package com.dazone.crewemail.utils;

public class Urls {
    public static final String URL_CHECK_UPDATE = "http://mobileupdate.crewcloud.net/WebServiceMobile.asmx/Mobile_Version";
    public static final String URL_CHANGE_PASS = "/UI/WebService/WebServiceCenter.asmx/UpdatePassword";
    public static final String URL_CHECK_SSL = "http://mobileupdate.crewcloud.net/WebServiceMobile.asmx/SSL_Check";
    public static String URL_ROOT = "";

    public static String URL_DEFAULT_API = "http://www.crewcloud.net";

    public static final String URL_SIGN_UP = "http://www.crewcloud.net/UI/Center/MobileService.asmx/SendConfirmEmail";
    public static final String URL_INSERT_DEVICE = "/UI/MobileMail3/MobileDataService.asmx/InsertAndroidDevice";
    public static final String URL_GET_NOTIFICATION_SETTING = "/UI/MobileMail3/MobileDataService.asmx/GetAndroidDevice";
    public static final String LOGIN_DEVICE_RESTRICTION = "/UI/WebService/WebServiceCenter.asmx/CheckMobileDevicesAccessrestrictions";
    public static final String URL_DELETE_DEVICE = "/UI/MobileMail3/MobileDataService.asmx/DeleteAndroidDevice";
    public static final String URL_INSERT_PHONE_TOKEN = URL_DEFAULT_API + "/UI/WebService/WebServiceCenter.asmx/AddPhoneTokens";
    public static final String URL_CHECK_DEVICE_TOKEN = URL_DEFAULT_API +"/UI/WebService/WebServiceCenter.asmx/CheckPhoneToken";
    public static final String URL_CHECK_SESSION = URL_ROOT + "/UI/WebService/WebServiceCenter.asmx/CheckSessionUser";
    public static final String URL_GET_USER_INFO = URL_ROOT + "/UI/mobileworkingtime/WorkingTime_Service.asmx/GetUserInfo";
    public static final String URL_GET_LOGIN_V5 = URL_ROOT + "/UI/WebService/WebServiceCenter.asmx/Login_v5";
    public static final String URL_AUTO_LOGIN = "/UI/WebService/WebServiceCenter.asmx/AutoLogin";

    public static final String URL_GET_EMAIL_SIGN_UP = "/UI/note/serviceNote.asmx/GetEmailKey";


    public static final String URL_GET_EMAIL_MENU_LIST = "/UI/MobileMail3/MobileDataService.asmx/GetAllOfMailBoxs";

    public static final String URL_GET_MAIL_DETAIL = "/UI/MobileMail3/MobileDataService.asmx/GetMailContent";

    public static final String URL_GET_EMAIL_LIST = "/UI/MobileMail3/MobileDataService.asmx/GetListOfMails";
    public static final String URL_GET_EMAIL_TAG_LIST = "/UI/MobileMail3/MobileDataService.asmx/GetListOfMailsByTag";
    public static final String URL_UPDATE_EMAIL_READ_UNREAD = "/UI/MobileMail3/MobileDataService.asmx/UpdateReadDateOfMails";
    public static final String URL_DELETE_EMAIL = "/UI/MobileMail3/MobileDataService.asmx/DeleteMails";
    public static final String URL_MOVE_EMAIL_TO_TRASH = "/UI/MobileMail3/MobileDataService.asmx/MoveMailsToTrash";
    public static final String URL_UPDATE_EMAIL_IMPORTANT = "/UI/MobileMail3/MobileDataService.asmx/UpdateImportantOfMails";
    public static final String URL_COMPOSE_MAIL = "/UI/MobileMail3/MobileDataService.asmx/ComposeMail";
    public static final String URL_REMOVE_TEMP_FILE = "/UI/MobileMail3/MobileDataService.asmx/RemoveTemporaryFiles";
    public static final String URL_REPLY_MAIL = "/UI/MobileMail3/MobileDataService.asmx/ReplyMail";
    public static final String URL_FORWARD_MAIL = "/UI/MobileMail3/MobileDataService.asmx/ForwardMail";
    public static final String URL_GET_LIST_OF_MAIL_ACCOUNT = "/UI/MobileMail3/MobileDataService.asmx/GetListOfMailAccountsForServer";
    public static final String URL_MOVE_MAIL_TO_BOX = "/UI/MobileMail3/MobileDataService.asmx/MoveMailsToMailBox";
    public static final String URL_UPDATE_TAG = "/UI/MobileMail3/MobileDataService.asmx/UpdateTagOfMails";
    public static final String URL_DRAF_COMPOSE_MAIL = "/UI/MobileMail3/MobileDataService.asmx/SaveComposeMail";
    public static final String URL_DRAF_REPLY_MAIL = "/UI/MobileMail3/MobileDataService.asmx/SaveReplyMail";
    public static final String URL_DRAF_FORWARD_MAIL = "/UI/MobileMail3/MobileDataService.asmx/SaveForwardMail";
    public static final String URL_FRIEND_SERVICE_INFO_NEW = "/UI/WebService/WebServiceCenter.asmx/GetUser";
    public static final String URL_FILTER_ADDRESS_MAIL = "/UI/MobileMail3/MobileDataService.asmx/AddFilterForFromAddress";
    public static final String URL_FILTER_SENDER_MAIL = "/UI/MobileMail3/MobileDataService.asmx/AddFilterForSenderName";
    public static final String URL_CHECK_ACCOUNT = "/UI/MobileMail3/MobileDataService.asmx/GetWhetherToUseEmail";
    public static final String URL_GET_RECEIVE = "/UI/MobileMail3/MobileDataService.asmx/GetReceiversForMail";
    public static final String URL_CANCEL_SENT = "/UI/MobileMail3/MobileDataService.asmx/CancelSentMail";
    public static final String URL_CANCEL_RESERCATION = "/UI/MobileMail3/MobileDataService.asmx/CancelReservation";
    public static final String URL_CANCEL_RESERCATION_ENTRIE = "/UI/MobileMail3/MobileDataService.asmx/CancelEntireReservation";

    public static final String URL_DOWNLOAD_ATTACH = "/UI/MobileMail3/MobileFileDownload.ashx?";
    public static final String URL_GET_ALL_USER_WITH_BELONGS = "/UI/WebService/WebServiceCenter.asmx/GetAllUsersWithBelongs";
    public static final String URL_GET_ALL_USER = "/UI/WebService/WebServiceCenter.asmx/GetAllOfUsers";
    public static final String URL_GET_DEPARTMENT = "/UI/WebService/WebServiceCenter.asmx/GetDepartments";
    public static final String URL_GET_USERS_BY_DEPARTMENT = "/UI/WebService/WebServiceCenter.asmx/GetUsersByDepartment";
    public static final String URL_GET_CONTACT = "/ui/contacts/dataservice.asmx/Get_ContactsUserMobile";
    public static final String URL_GET_DEPARTMENT_MOD = "/UI/WebService/WebServiceCenter.asmx/GetDepartments_Mod";
    public static final String URL_GET_USER_MOD = "/UI/WebService/WebServiceCenter.asmx/GetAllUsersWithBelongs_Mod";
    public static final String URL_GET_ALL_USER_BE_LONGS = "/UI/WebService/WebServiceCenter.asmx/GetAllUsersWithBelongs";
}
