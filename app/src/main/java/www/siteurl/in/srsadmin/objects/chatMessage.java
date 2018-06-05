package www.siteurl.in.srsadmin.objects;

import java.io.Serializable;

/**
 * Created by siteurl on 18/4/18.
 */

public class chatMessage implements Serializable {

    private String personName;
    private String personMessage;
    private String date;
    private  String name;

    public chatMessage(String personName, String personMessage, String date, String name) {
        this.personName = personName;
        this.personMessage = personMessage;
        this.date = date;
        this.name = name;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonMessage() {
        return personMessage;
    }

    public void setPersonMessage(String personMessage) {
        this.personMessage = personMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
