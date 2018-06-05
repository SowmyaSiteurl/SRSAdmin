package www.siteurl.in.srsadmin.objects;

import java.io.Serializable;

/**
 * Created by siteurl on 10/4/18.
 */

public class    StaffList implements Serializable {

    String user_id,name,email,password,user_group_id,phone_no,address,gps_location;

    public StaffList(String user_id, String name, String email, String password, String user_group_id, String phone_no, String address, String gps_location) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.user_group_id = user_group_id;
        this.phone_no = phone_no;
        this.address = address;
        this.gps_location = gps_location;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUser_group_id() {
        return user_group_id;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getAddress() {
        return address;
    }

    public String getGps_location() {
        return gps_location;
    }
}
