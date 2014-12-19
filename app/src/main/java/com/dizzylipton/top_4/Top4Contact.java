package com.dizzylipton.top_4;

import android.graphics.Bitmap;

/**
 * Created by Patrick on 12/18/2014.
 */
public class Top4Contact {

    public Top4Contact() {

    }

    public Top4Contact(Integer contactId, String contactName, Integer contactNum, Bitmap contactAvatar) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactNumber = contactNum;
        this.contactAvatar = contactAvatar;
    }

    Integer contactId;
    String contactName;
    Integer contactNumber;
    Bitmap contactAvatar;

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

    public Integer getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Integer contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Bitmap getContactAvatar() {
        return contactAvatar;
    }

    public void setContactAvatar(Bitmap contactAvatar) {
        this.contactAvatar = contactAvatar;
    }


}
