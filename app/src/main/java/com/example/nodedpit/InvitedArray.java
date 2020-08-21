package com.example.nodedpit;

import java.util.ArrayList;

public class InvitedArray {

    private static ArrayList<String> mInvited = new ArrayList<>();

    public static ArrayList<String> getmInvited() {
        return mInvited;
    }

    public static void clear(){
        mInvited.clear();
    }

    public static void setmInvited(ArrayList<String> invited) {
        mInvited = invited;
    }
}
