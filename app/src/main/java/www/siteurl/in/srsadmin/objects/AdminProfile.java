package www.siteurl.in.srsadmin.objects;

import java.io.Serializable;

/**
 * Created by siteurl on 9/4/18.
 */

public class AdminProfile implements Serializable {

    private String mName;
    private String mEmail;
    private String mPhone;
    private String mAddress;

    public AdminProfile(String mName, String mEmail, String mPhone, String mAddress) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.mPhone = mPhone;
        this.mAddress = mAddress;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
