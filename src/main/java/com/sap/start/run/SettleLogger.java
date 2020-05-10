package com.sap.start.run;


public class SettleLogger implements com.jcraft.jsch.Logger {
    public boolean isEnabled(int level) {
        return true;
    }

    public void log(int level, String msg) {
        System.out.println(msg);
    }
}
