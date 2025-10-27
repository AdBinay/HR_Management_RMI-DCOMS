import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The remote interface for the Human Resource Management (HRM) system.
 * This defines the methods that the client can invoke on the server.
 */
public interface HRMInterface extends Remote {

    String registerEmployee(String firstName, String lastName, String icPassport) throws RemoteException;

    Employee getEmployee(String icPassport) throws RemoteException;

    String updateProfile(String icPassport, String profileDetails, String familyDetails) throws RemoteException;

    String applyForLeave(String icPassport, String leaveDate, String reason) throws RemoteException;

    int getLeaveBalance(String icPassport) throws RemoteException;

    List<LeaveApplication> getLeaveStatus(String icPassport) throws RemoteException;

    String generateYearlyReport(String icPassport) throws RemoteException;
}
