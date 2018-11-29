package org.k;

/**
 * Created by Kenny on 18-2-5.
 */

public interface TCPListener {
    /** reflex code **/
    int REFLEX_ON_STATE = 0x0030;
    int REFLEX_ON_RECEIVE = 0x0031;
    int REFLEX_ON_SEND = 0x0032;
    int REFLEX_ON_ERROR = 0x0033;

    /**  error code **/
    int LOST_CONNECT = -500;
    int SEND_FAILED = -404;
    int RECEIVE_FAILED = -402;

    /**
     * 已经接受到数据将会调用这个监听
     */
    void onReceive(byte[] bytes, int length);

    /**
     * 已经发生的数据将会调用这个监听
     */
    void onSend(byte[] bytes);

    @Deprecated
    void onError(int errorCode, String errorMessage);

    @Deprecated
    void onState(String stateMessage);
}
