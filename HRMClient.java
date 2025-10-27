import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

/**
 * A Java Swing GUI client for the RMI-based HRM system.
 */
public class HRMClient extends JFrame {

    private HRMInterface hrmService;
    private String currentEmployeeIC; // Store logged-in employee's IC

    // --- Panels ---
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel hrPanel;
    private JPanel employeePanel;

    // --- Login Components ---
    private JTextField icField;
    private JButton loginButton;
    private JButton showHrButton;

    // --- HR Components ---
    private JTextField hrRegFirstName, hrRegLastName, hrRegIC;
    private JButton hrRegisterButton;
    private JTextField hrReportIC;
    private JButton hrGenerateReportButton;
    private JTextArea hrReportArea;
    private JButton hrLogoutButton;

    // --- Employee Components ---
    private JTextArea empProfileArea, empFamilyArea;
    private JButton empUpdateProfileButton;
    private JLabel empLeaveBalanceLabel;
    private JTextField empLeaveDate;
    private JTextArea empLeaveReason;
    private JButton empApplyLeaveButton;
    private JTextArea empLeaveStatusArea;
    private JButton empRefreshLeaveButton;
    private JButton empLogoutButton;

    public HRMClient(HRMInterface service) {
        this.hrmService = service;

        setTitle("BHEL Human Resource Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create the different "pages"
        createLoginPanel();
        createHRPanel();
        createEmployeePanel();

        // Add pages to the main panel
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(hrPanel, "HR_MENU");
        mainPanel.add(employeePanel, "EMP_MENU");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN"); // Start on login page
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(new JLabel("Welcome to BHEL HRM"), gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        loginPanel.add(new JLabel("IC/Passport:"), gbc);

        gbc.gridx = 1;
        icField = new JTextField(20);
        loginPanel.add(icField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loginButton = new JButton("Login as Employee");
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        showHrButton = new JButton("Login as HR Staff");
        loginPanel.add(showHrButton, gbc);

        // --- Action Listeners ---
        loginButton.addActionListener(this::performLogin);
        showHrButton.addActionListener(e -> cardLayout.show(mainPanel, "HR_MENU"));
    }

    private void createHRPanel() {
        hrPanel = new JPanel(new BorderLayout(10, 10));
        hrPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Registration Tab ---
        JPanel regPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; regPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; hrRegFirstName = new JTextField(20); regPanel.add(hrRegFirstName, gbc);
        gbc.gridx = 0; gbc.gridy = 1; regPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; hrRegLastName = new JTextField(20); regPanel.add(hrRegLastName, gbc);
        gbc.gridx = 0; gbc.gridy = 2; regPanel.add(new JLabel("IC/Passport:"), gbc);
        gbc.gridx = 1; hrRegIC = new JTextField(20); regPanel.add(hrRegIC, gbc);
        gbc.gridx = 1; gbc.gridy = 3; hrRegisterButton = new JButton("Register Employee"); regPanel.add(hrRegisterButton, gbc);
        
        tabbedPane.addTab("Register Employee", regPanel);

        // --- Report Tab ---
        JPanel reportPanel = new JPanel(new BorderLayout(5, 5));
        JPanel reportTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reportTopPanel.add(new JLabel("Employee IC/Passport:"));
        hrReportIC = new JTextField(15);
        reportTopPanel.add(hrReportIC);
        hrGenerateReportButton = new JButton("Generate Report");
        reportTopPanel.add(hrGenerateReportButton);
        reportPanel.add(reportTopPanel, BorderLayout.NORTH);

        hrReportArea = new JTextArea();
        hrReportArea.setEditable(false);
        hrReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportPanel.add(new JScrollPane(hrReportArea), BorderLayout.CENTER);
        
        tabbedPane.addTab("Generate Report", reportPanel);

        hrPanel.add(tabbedPane, BorderLayout.CENTER);
        
        hrLogoutButton = new JButton("Back to Login");
        hrPanel.add(hrLogoutButton, BorderLayout.SOUTH);

        // --- Action Listeners ---
        hrRegisterButton.addActionListener(this::performRegistration);
        hrGenerateReportButton.addActionListener(this::generateReport);
        hrLogoutButton.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
    }

    private void createEmployeePanel() {
        employeePanel = new JPanel(new BorderLayout(10, 10));
        employeePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Profile Tab ---
        JPanel profilePanel = new JPanel(new BorderLayout(5, 5));
        JPanel profileFieldsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        profileFieldsPanel.add(new JLabel("Profile Details:"));
        empProfileArea = new JTextArea(5, 30);
        profileFieldsPanel.add(new JScrollPane(empProfileArea));
        
        profileFieldsPanel.add(new JLabel("Family Details:"));
        empFamilyArea = new JTextArea(5, 30);
        profileFieldsPanel.add(new JScrollPane(empFamilyArea));
        
        profilePanel.add(profileFieldsPanel, BorderLayout.CENTER);
        empUpdateProfileButton = new JButton("Update Profile");
        profilePanel.add(empUpdateProfileButton, BorderLayout.SOUTH);
        
        tabbedPane.addTab("My Profile", profilePanel);

        // --- Leave Tab ---
        JPanel leavePanel = new JPanel(new BorderLayout(5, 5));
        
        JPanel leaveApplyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        empLeaveBalanceLabel = new JLabel("Current Leave Balance: N/A");
        leaveApplyPanel.add(empLeaveBalanceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; leaveApplyPanel.add(new JLabel("Leave Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; empLeaveDate = new JTextField(15); leaveApplyPanel.add(empLeaveDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2; leaveApplyPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1; empLeaveReason = new JTextArea(3, 20);
        leaveApplyPanel.add(new JScrollPane(empLeaveReason), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        empApplyLeaveButton = new JButton("Apply for Leave");
        leaveApplyPanel.add(empApplyLeaveButton, gbc);

        leavePanel.add(leaveApplyPanel, BorderLayout.NORTH);

        JPanel leaveStatusPanel = new JPanel(new BorderLayout(5, 5));
        leaveStatusPanel.add(new JLabel("My Leave Applications:"), BorderLayout.NORTH);
        empLeaveStatusArea = new JTextArea();
        empLeaveStatusArea.setEditable(false);
        leaveStatusPanel.add(new JScrollPane(empLeaveStatusArea), BorderLayout.CENTER);
        empRefreshLeaveButton = new JButton("Refresh Status");
        leaveStatusPanel.add(empRefreshLeaveButton, BorderLayout.SOUTH);

        leavePanel.add(leaveStatusPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Leave Management", leavePanel);

        employeePanel.add(tabbedPane, BorderLayout.CENTER);
        
        empLogoutButton = new JButton("Logout");
        employeePanel.add(empLogoutButton, BorderLayout.SOUTH);

        // --- Action Listeners ---
        empUpdateProfileButton.addActionListener(this::updateProfile);
        empApplyLeaveButton.addActionListener(this::applyForLeave);
        empRefreshLeaveButton.addActionListener(e -> loadEmployeeData(this.currentEmployeeIC)); // Reload
        empLogoutButton.addActionListener(this::performLogout);
    }

    // --- RMI Action Methods ---

    private void performLogin(ActionEvent e) {
        String ic = icField.getText().trim();
        if (ic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an IC/Passport number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Employee emp = hrmService.getEmployee(ic);
            if (emp != null) {
                this.currentEmployeeIC = ic;
                loadEmployeeData(ic); // Load data into employee panel
                cardLayout.show(mainPanel, "EMP_MENU");
                setTitle("HRM System - Welcome " + emp.getFirstName());
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException ex) {
            showConnectionError(ex);
        }
    }
    
    private void performLogout(ActionEvent e) {
        this.currentEmployeeIC = null;
        icField.setText("");
        setTitle("BHEL Human Resource Management System");
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void performRegistration(ActionEvent e) {
        String fName = hrRegFirstName.getText().trim();
        String lName = hrRegLastName.getText().trim();
        String ic = hrRegIC.getText().trim();

        if (fName.isEmpty() || lName.isEmpty() || ic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String result = hrmService.registerEmployee(fName, lName, ic);
            JOptionPane.showMessageDialog(this, result, "Registration", JOptionPane.INFORMATION_MESSAGE);
            if (result.startsWith("Success")) {
                hrRegFirstName.setText("");
                hrRegLastName.setText("");
                hrRegIC.setText("");
            }
        } catch (RemoteException ex) {
            showConnectionError(ex);
        }
    }
    
    private void generateReport(ActionEvent e) {
        String ic = hrReportIC.getText().trim();
        if (ic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an IC/Passport number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String report = hrmService.generateYearlyReport(ic);
            hrReportArea.setText(report);
        } catch (RemoteException ex) {
            showConnectionError(ex);
        }
    }

    private void loadEmployeeData(String ic) {
        try {
            Employee emp = hrmService.getEmployee(ic);
            if (emp == null) {
                JOptionPane.showMessageDialog(this, "Could not reload employee data.", "Error", JOptionPane.ERROR_MESSAGE);
                performLogout(null);
                return;
            }
            
            // Profile Tab
            empProfileArea.setText(emp.getProfileDetails());
            empFamilyArea.setText(emp.getFamilyDetails());
            
            // Leave Tab
            empLeaveBalanceLabel.setText("Current Leave Balance: " + emp.getLeaveBalance());
            
            // Leave Status
            empLeaveStatusArea.setText(""); // Clear old data
            List<LeaveApplication> history = emp.getLeaveHistory();
            if (history.isEmpty()) {
                empLeaveStatusArea.setText("No leave applications found.");
            } else {
                for (LeaveApplication app : history) {
                    empLeaveStatusArea.append(app.toString() + "\n");
                }
            }
        } catch (RemoteException ex) {
            showConnectionError(ex);
        }
    }

    private void updateProfile(ActionEvent e) {
        String profile = empProfileArea.getText();
        String family = empFamilyArea.getText();
        
        try {
            String result = hrmService.updateProfile(currentEmployeeIC, profile, family);
            JOptionPane.showMessageDialog(this, result, "Profile Update", JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException ex) {
            showConnectionError(ex);
        }
    }
    
    private void applyForLeave(ActionEvent e) {
        String date = empLeaveDate.getText().trim();
        String reason = empLeaveReason.getText().trim();
        
        if (date.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date and Reason are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String result = hrmService.applyForLeave(currentEmployeeIC, date, reason);
            JOptionPane.showMessageDialog(this, result, "Leave Application", JOptionPane.INFORMATION_MESSAGE);
            if (result.startsWith("Success")) {
                empLeaveDate.setText("");
                empLeaveReason.setText("");
                loadEmployeeData(currentEmployeeIC); // Reload all data
            }
        } catch (RemoteException ex) {
            showConnectionError(ex);
        }
    }

    private void showConnectionError(RemoteException ex) {
        JOptionPane.showMessageDialog(this,
                "Connection error: Could not reach the RMI server.\n" + ex.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
    }


    public static void main(String[] args) {
        try {
            // Look up the remote object from the RMI registry
            HRMInterface hrmService = (HRMInterface) Naming.lookup("//localhost/HRMService");

            // Start the Swing GUI on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                new HRMClient(hrmService).setVisible(true);
            });

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Could not connect to HRM Server. Make sure the server is running.", 
                "Client Startup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
