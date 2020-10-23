package com.undac.undaccomedor.server

class Server {
    companion object {

        private var HTTP: String="http://"
        public var IP_SERVER: String ="comedor.undac.edu.pe:8094"
        public var GET_MENU: String = "$HTTP$IP_SERVER/menu/getAppMenu"
        public var GET_HORARIO_RESER: String="$HTTP$IP_SERVER/horario/getAppHorary"
        public var SET_HOARARIO_RESER_SELECT: String="$HTTP$IP_SERVER/"
        public var SET_LOGIN :String="$HTTP$IP_SERVER/"
        public var GET_LGOIN: String ="$HTTP$IP_SERVER/"
        public var VALIDATE_LOGIN:String="$HTTP$IP_SERVER/validate/login/userApp";
        public var GET_NEWS:String ="$HTTP$IP_SERVER/novedadesApp"
        public var CHECK_MENU_RESRVATION_ENABLE="$HTTP$IP_SERVER/checkMenuReservationEnable"
        public var SET_RESERVATION="$HTTP$IP_SERVER/create/reservationApp"

        public var GET_HORARY_CANT_RESERVATION="$HTTP$IP_SERVER/horario/getAppCantReservation"
        public var CHECK_MEMU_ENABLE="$HTTP$IP_SERVER/menu/checkMenuEnableApp"

        public var CHANGE_PASSWORD="$HTTP$IP_SERVER/login/changePassword"

        public var GET_PROFILE="$HTTP$IP_SERVER/login/getProfileApp"

        public var GET_ALL_RESERVATION="$HTTP$IP_SERVER/reservation/getCantAllReservationApp"
        public var SUGERENCIA_CREATE="$HTTP$IP_SERVER/sugerencias/create/app"
        public var GET_MENU_ACTIVE="$HTTP$IP_SERVER/menu/getAppMenu/active/app"


        var HOME_GET_LIST_ITEMS_VIEW_="$HTTP$IP_SERVER//app/home/getListItemsView"
        var NEWS_GET_LATEST_NEWS_="$HTTP$IP_SERVER/app/news/getLatestNews"

        var RESERVATION_GET_RESERVATIONS_TODAY_="$HTTP$IP_SERVER/app/reservation/getMenuReservationsToday"

        var LOGIN_SAVE_NEW_INSTANCE_ID_TOKEN_="$HTTP$IP_SERVER/app/token_users_firebase/saveNewInstanceIdToken"

        var RESERVATION_SET_SCORE="$HTTP$IP_SERVER/app/reservation/setScore"

        var RESERVATION_SET_COMMENT="$HTTP$IP_SERVER/app/reservation/setComment"

        var HISTORY_RESERVATION_GET_LIST_FOR_JUSTIFICATION="$HTTP$IP_SERVER/app/history_reservation/getListForJustification"

        var MENU_GET_LIST_ALL="$HTTP$IP_SERVER/app/menu/getMenuAll"

        var MENU_GET_MENU_WEEK="$HTTP$IP_SERVER//app/menu/getDataBySpecificTypeMenuWeek"

        var MENU_GET_MENU="$HTTP$IP_SERVER/app/menu/getMenu"

        var HISTORY_RESERVATION_GET_RESERVATION="$HTTP$IP_SERVER/app/history_reservation/getReservation"

        var FIREBASE_SAVE_LOGGED_IN_LAST="$HTTP$IP_SERVER/app/token_users_firebase/saveDateLoggedInLast"

        var FIREBASE_SAVE_LOGOUT="$HTTP$IP_SERVER/app/token_users_firebase/saveDateLogout"

        var JUSTFICATION_GET_STATE_MESSAGE="$HTTP$IP_SERVER/app/justification/getStateMessage"

        var JUSTFICATION_GET_COUNT_FOR_JUSTIFICATION="$HTTP$IP_SERVER/app/justification/getCountForJustification"









    }
}