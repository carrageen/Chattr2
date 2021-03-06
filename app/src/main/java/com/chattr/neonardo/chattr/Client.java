package com.chattr.neonardo.chattr;

public class Client extends CancerClient {

    private final ChatMain cm;
    public Client(ChatMain cm) {
        super();
        this.cm = cm;
    }

    protected void onMessageIncoming(String msg){
        cm.displayMessage(msg);
    }
}