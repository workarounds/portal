package in.workarounds.portal;

/**
 * Created by madki on 29/11/15.
 */
interface IntentType {
    int NO_TYPE = 0;
    int OPEN = 1;
    int CLOSE = 2;
    int HIDE = 3;
    int SHOW = 4;
    int SEND = 5;

    int CLOSE_MANAGER = 100;
    int SEND_TO_ALL = 101;
    int ACTIVITY_RESULT = 102;

    int START_ACTIVITY_FOR_RESULT = 110;

}
