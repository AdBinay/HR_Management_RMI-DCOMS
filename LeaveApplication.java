import java.io.Serializable;

/**
 * Represents a Leave Application. Must be Serializable.
 */
public class LeaveApplication implements Serializable {
    
    private static final long serialVersionUID = 2L;

    private String employeeIcPassport;
    private String leaveDate;
    private String reason;
    private String status;

    public LeaveApplication(String employeeIcPassport, String leaveDate, String reason) {
        this.employeeIcPassport = employeeIcPassport;
        this.leaveDate = leaveDate;
        this.reason = reason;
    }

    // --- Getters and Setters ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getLeaveDate() { return leaveDate; }
    public String getReason() { return reason; }
    public String getEmployeeIcPassport() { return employeeIcPassport; }


    @Override
    public String toString() {
        return "Leave [Date: " + leaveDate + ", Reason: " + reason + ", Status: " + status + "]";
    }
}
