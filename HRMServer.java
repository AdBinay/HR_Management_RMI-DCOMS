import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The server-side implementation of the HRMInterface.
 * Connects to a MySQL database using JDBC.
 */
public class HRMServer extends UnicastRemoteObject implements HRMInterface {

    // --- Database Connection Details ---
    // ⚠️ UPDATE THESE WITH YOUR MYSQL DETAILS ⚠️
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hrm_db";
    private static final String DB_USER = "root"; // Or 'hrm_user' if you created one
    private static final String DB_PASS = "2000";     // Your MySQL password

    protected HRMServer() throws RemoteException {
        super();
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure the .jar is in the classpath.");
            throw new RemoteException("Server error: JDBC Driver not found.", e);
        }
    }

    /**
     * Helper method to get a new database connection.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    @Override
    public synchronized String registerEmployee(String firstName, String lastName, String icPassport) throws RemoteException {
        String sql = "INSERT INTO employees (first_name, last_name, ic_passport, leave_balance) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Check if employee already exists
            if (getEmployee(icPassport) != null) {
                return "Error: Employee with IC/Passport " + icPassport + " already exists.";
            }

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, icPassport);
            pstmt.setInt(4, 20); // Default leave balance
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Registered new employee: " + icPassport);
                return "Success: Employee " + firstName + " " + lastName + " registered.";
            } else {
                return "Error: Registration failed.";
            }
        } catch (SQLException e) {
            System.err.println("SQL Error (registerEmployee): " + e.getMessage());
            throw new RemoteException("Server database error.", e);
        }
    }

    @Override
    public synchronized Employee getEmployee(String icPassport) throws RemoteException {
        String sql = "SELECT * FROM employees WHERE ic_passport = ?";
        Employee emp = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, icPassport);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                emp = new Employee(rs.getString("first_name"), rs.getString("last_name"), rs.getString("ic_passport"));
                emp.setProfileDetails(rs.getString("profile_details"));
                emp.setFamilyDetails(rs.getString("family_details"));
                emp.setLeaveBalance(rs.getInt("leave_balance"));
                
                // Now, fetch leave history
                emp.setLeaveHistory(getLeaveStatus(icPassport));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error (getEmployee): " + e.getMessage());
            throw new RemoteException("Server database error.", e);
        }
        return emp; // Returns null if not found
    }

    @Override
    public synchronized String updateProfile(String icPassport, String profileDetails, String familyDetails) throws RemoteException {
        String sql = "UPDATE employees SET profile_details = ?, family_details = ? WHERE ic_passport = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, profileDetails);
            pstmt.setString(2, familyDetails);
            pstmt.setString(3, icPassport);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated profile for: " + icPassport);
                return "Success: Profile updated.";
            } else {
                return "Error: Employee not found or profile unchanged.";
            }
        } catch (SQLException e) {
            System.err.println("SQL Error (updateProfile): " + e.getMessage());
            throw new RemoteException("Server database error.", e);
        }
    }

    @Override
    public synchronized String applyForLeave(String icPassport, String leaveDate, String reason) throws RemoteException {
        // 1. Check leave balance
        Employee emp = getEmployee(icPassport);
        if (emp == null) return "Error: Employee not found.";
        if (emp.getLeaveBalance() <= 0) return "Error: No leave balance remaining.";

        // 2. Insert new leave application
        String sqlInsert = "INSERT INTO leave_applications (employee_ic, leave_date, reason, status) VALUES (?, ?, ?, ?)";
        // 3. Update employee's leave balance
        String sqlUpdate = "UPDATE employees SET leave_balance = ? WHERE ic_passport = ?";
        
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert leave app
            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                pstmtInsert.setString(1, icPassport);
                pstmtInsert.setString(2, leaveDate);
                pstmtInsert.setString(3, reason);
                pstmtInsert.setString(4, "Approved"); // Auto-approving for simplicity
                pstmtInsert.executeUpdate();
            }

            // Update leave balance
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                int newBalance = emp.getLeaveBalance() - 1;
                pstmtUpdate.setInt(1, newBalance);
                pstmtUpdate.setString(2, icPassport);
                pstmtUpdate.executeUpdate();
            }

            conn.commit(); // Commit transaction
            System.out.println("Processed leave application for: " + icPassport);
            return "Success: Leave applied and approved. New balance: " + (emp.getLeaveBalance() - 1);

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("SQL Error (applyForLeave): " + e.getMessage());
            throw new RemoteException("Server database error.", e);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public synchronized int getLeaveBalance(String icPassport) throws RemoteException {
        Employee emp = getEmployee(icPassport);
        return (emp != null) ? emp.getLeaveBalance() : -1;
    }

    @Override
    public synchronized List<LeaveApplication> getLeaveStatus(String icPassport) throws RemoteException {
        String sql = "SELECT * FROM leave_applications WHERE employee_ic = ?";
        List<LeaveApplication> history = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, icPassport);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LeaveApplication app = new LeaveApplication(
                    rs.getString("employee_ic"),
                    rs.getString("leave_date"),
                    rs.getString("reason")
                );
                app.setStatus(rs.getString("status"));
                history.add(app);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error (getLeaveStatus): " + e.getMessage());
            throw new RemoteException("Server database error.", e);
        }
        return history;
    }

    @Override
    public synchronized String generateYearlyReport(String icPassport) throws RemoteException {
        Employee emp = getEmployee(icPassport);
        if (emp == null) {
            return "Error: Employee not found.";
        }
        
        System.out.println("Generating report for: " + icPassport);
        
        StringBuilder report = new StringBuilder();
        report.append("--- Yearly Report for ").append(emp.getFirstName()).append(" ").append(emp.getLastName()).append(" ---\n");
        report.append("IC/Passport: ").append(emp.getIcPassport()).append("\n\n");
        
        report.append("Profile Details: \n").append(emp.getProfileDetails()).append("\n\n");
        report.append("Family Details: \n").append(emp.getFamilyDetails()).append("\n\n");
        
        report.append("Current Leave Balance: ").append(emp.getLeaveBalance()).append("\n\n");

        report.append("Leave History: \n");
        if (emp.getLeaveHistory().isEmpty()) {
            report.append("  No leave taken this year.\n");
        } else {
            for (LeaveApplication app : emp.getLeaveHistory()) {
                report.append("  - ").append(app.toString()).append("\n");
            }
        }
        report.append("\n--- End of Report ---");
        
        return report.toString();
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry started.");

            HRMServer server = new HRMServer();
            Naming.rebind("//localhost/HRMService", server);

            System.out.println("HRM Server is running and bound to 'HRMService'...");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
