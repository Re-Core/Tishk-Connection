package com.recore.tishkconnection.Model;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String uid, uimag, uname, content;
    private Object timeStam;

    public Comment() {
    }

    public Comment(String uid, String uimag, String uname, String content) {
        this.uid = uid;
        this.uimag = uimag;
        this.uname = uname;
        this.content = content;
        this.timeStam = ServerValue.TIMESTAMP;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimag() {
        return uimag;
    }

    public void setUimag(String uimag) {
        this.uimag = uimag;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getTimeStam() {
        return timeStam;
    }

    public void setTimeStam(Object timeStam) {
        this.timeStam = timeStam;
    }
}
