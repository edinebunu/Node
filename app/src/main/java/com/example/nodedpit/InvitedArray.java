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

    public static void add(String item)
    {
        mInvited.add(item);
    }

    public static void remove(String item){
        mInvited.remove(item);
    }

    public static void setmInvited(ArrayList<String> invited) {
        mInvited = invited;
    }
}
