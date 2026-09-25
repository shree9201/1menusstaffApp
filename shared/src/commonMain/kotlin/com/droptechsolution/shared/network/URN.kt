package com.droptechsolution.shared.network

object URN {
//    val SERVER: String = "https://1menus.com/app"
    val SERVER: String = "https://api.1menus.com/"
    val SERVER2: String = "https://api.1menu.com"
    val OUTLET_INFO = "/API/outletInfo"
//    val STAFF_LOGIN ="/application_service/staffLogin"
    val STAFF_LOGIN ="staffLogin"
    val STAFF_LOGOUT = "logout"
    val STAFF_LIST = "getStaffList"
    val STAFF_ADD = "staff/add"
    val STAFF_UPDATE = "staff/update"
    val STAFF_DELETE = "staff/delete"
    val STAFF_ATTENDANCES = "staffAttendances"
    val CHANGE_PASSWORD = "changePassword"
    val UPDATE_PROFILE = "updateProfile"
    val SEND_NOTIFICATION = "/sendPushNotification"

    //Services
    val GET_ROOM_REQUESTS = "getRoomRequests"
    val GET_OUTLET_SERVICES ="getOutletServices"
    val GET_REQUEST_DETAILS = "getRequestDetails"
    val UPDATE_REQUEST = "updateRequest"
    val SERVICE_STATUS_COUNT = "serviceStatusCount"
    val GET_ACTIVITY_LIST = "getActivityList"
    val SERVICE_VIEW = "Service/view/id"
    val SERVICE_CATEGORY = "Service/category/id"
    val SERVICE_UPDATE = "Service/update"
    val SERVICE_DELETE = "Service/delete"
    val MASTER_DATA = "masterData"
}