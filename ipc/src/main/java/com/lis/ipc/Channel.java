package com.lis.ipc;

public class Channel {
    private static final Channel instance = new Channel();

    public static Channel getInstance() {
        return instance;
    }

}
