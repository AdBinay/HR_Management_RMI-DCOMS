import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Employee. Must be Serializable to be passed over RMI.
 */
public class Employee implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private String icPassport;
    private String profileDetails;
    private String familyDetails;
    private int leaveBalance;
    private List<LeaveApplication> leaveHistory;

    public Employee(String firstName, String lastName, String icPassport) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.icPassport = icPassport;
        this.leaveHistory = new ArrayList<>();
    }

    // --- Getters and Setters ---

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getIcPassport() { return icPassport; }

    public String getProfileDetails() { return profileDetails; }
    public void setProfileDetails(String profileDetails) { this.profileDetails = profileDetails; }

    public String getFamilyDetails() { return familyDetails; }
    public void setFamilyDetails(String familyDetails) { this.familyDetails = familyDetails; }

    public int getLeaveBalance() { return leaveBalance; }
    public void setLeaveBalance(int leaveBalance) { this.leaveBalance = leaveBalance; }

    public List<LeaveApplication> getLeaveHistory() { return leaveHistory; }
    public void setLeaveHistory(List<LeaveApplication> history) { this.leaveHistory = history; }
    public void addLeaveApplication(LeaveApplication app) { this.leaveHistory.add(app); }

    @Override
    public String toString() {
        return "Employee [Name: " + firstName + " " + lastName + ", IC/Passport: " + icPassport + "]";
    }
}
