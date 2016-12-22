package com.android.internal.telephony;

/**
 * Created by SourabhSadhu on 12-12-2016.
 */

public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}
