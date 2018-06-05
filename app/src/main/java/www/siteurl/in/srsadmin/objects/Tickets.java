package www.siteurl.in.srsadmin.objects;

/**
 * Created by siteurl on 9/4/18.
 */

public class Tickets {
    String ticketId;
    String prodName;
    String ticketDesc;
    String date;
    String status;
    String ticket_subject;
    String call_back_date;
    String assigned_to;
    String assigned_from;
    String updated_at;
    String userName;
    String userPhone;


    public Tickets(String ticketId, String prodName, String ticketDesc, String date, String status, String ticket_subject, String call_back_date, String assigned_to, String assigned_from, String updated_at, String userName, String userPhone) {
        this.ticketId = ticketId;
        this.prodName = prodName;
        this.ticketDesc = ticketDesc;
        this.date = date;
        this.status = status;
        this.ticket_subject = ticket_subject;
        this.call_back_date = call_back_date;
        this.assigned_to = assigned_to;
        this.assigned_from = assigned_from;
        this.updated_at = updated_at;
        this.userName = userName;
        this.userPhone = userPhone;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getTicketDesc() {
        return ticketDesc;
    }

    public void setTicketDesc(String ticketDesc) {
        this.ticketDesc = ticketDesc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTicket_subject() {
        return ticket_subject;
    }

    public void setTicket_subject(String ticket_subject) {
        this.ticket_subject = ticket_subject;
    }

    public String getCall_back_date() {
        return call_back_date;
    }

    public void setCall_back_date(String call_back_date) {
        this.call_back_date = call_back_date;
    }

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getAssigned_from() {
        return assigned_from;
    }

    public void setAssigned_from(String assigned_from) {
        this.assigned_from = assigned_from;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
