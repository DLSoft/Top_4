package com.dizzylipton.top_4;

import android.graphics.Bitmap;

/**
 * Created by Patrick on 12/18/2014.
 */
public class Top4Contact {

    public Top4Contact() { }

    public Top4Contact(Integer contactId, String contactName, String contactNum, Bitmap contactAvatar) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactNumber = contactNum;
        this.contactAvatar = contactAvatar;
    }

    private Integer contactId;
    private String contactName;
    private String contactNumber;
    private Bitmap contactAvatar;

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Bitmap getContactAvatar() {
        return contactAvatar;
    }

    public void setContactAvatar(Bitmap contactAvatar) {
        this.contactAvatar = contactAvatar;
    }


}
