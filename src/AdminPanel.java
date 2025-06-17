import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Admin panel for the Course Management System GUI.
 */
public class AdminPanel extends JPanel {
    private CourseManagementGUI mainFrame;
    private JTabbedPane tabbedPane;
    
    // Header component for greeting
    private JLabel greetingLabel;
    
    // Courses tab components
    private JPanel coursesPanel;
    private JTable coursesTable;
    private JButton addCourseButton;
    private JButton updateCourseButton;
    private JButton deleteCourseButton;
    
    // Users tab components
    private JPanel usersPanel;
    private JTable usersTable;
    
    // User Management tab components
    private JPanel userManagementPanel;
    private JComboBox<String> userTypeCombo;
    private JButton addUserButton;
    private JButton updateUserButton;
    private JButton deleteUserButton;
    private JTable managementUsersTable;
    
    // Assign courses tab components
    private JPanel assignPanel;
    private JComboBox<String> instructorCombo;
    private JComboBox<String> courseCombo;
    private JButton assignButton;
    
    // Feedbacks tab components
    private JPanel feedbacksPanel;
    private JTable feedbacksTable;
    private JTextArea feedbackDetailsArea;
    
    // Reports tab components
    private JPanel upcomingCoursesPanel;
    private JTable upcomingCoursesTable;
    private JPanel endingCoursesPanel;
    private JTable endingCoursesTable;
    
    /**
     * Constructor for the AdminPanel.
     * 
     * @param mainFrame The main GUI frame.
     */
    public AdminPanel(CourseManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create header panel with title and greeting
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        // Add greeting label (will be populated when admin logs in)
        greetingLabel = new JLabel("", JLabel.CENTER);
        greetingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(greetingLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create tabs
        createCoursesTab();
        createUsersTab();
        createUserManagementTab();
        createAssignCoursesTab();
        createFeedbacksTab();
        createUpcomingCoursesTab();
        createEndingCoursesTab();
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("Manage Courses", coursesPanel);
        tabbedPane.addTab("View Users", usersPanel);
        tabbedPane.addTab("Manage Users", userManagementPanel);
        tabbedPane.addTab("Assign Courses", assignPanel);
        tabbedPane.addTab("Feedbacks", feedbacksPanel);
        tabbedPane.addTab("Upcoming Courses", upcomingCoursesPanel);
        tabbedPane.addTab("Ending Courses", endingCoursesPanel);
        
        // Create logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            mainFrame.switchPanel("Login");
        });
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(logoutButton);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Updates the greeting message with the admin's name.
     * 
     * @param adminName The name of the logged-in admin.
     */
    public void setAdminName(String adminName) {
        greetingLabel.setText("Hello, " + adminName + "!");
    }
    
    /**
     * Create the courses management tab.
     */
    private void createCoursesTab() {
        coursesPanel = new JPanel(new BorderLayout());
        
        // Create table model and table
        String[] columnNames = {"Course Name", "Instructors", "Room", "Branch", "Price", "Start Date", "End Date", "Description"};
        Object[][] data = new Object[0][8]; // Empty data initially
        
        coursesTable = new JTable(data, columnNames);
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Set row height to accommodate multiple lines of text
        coursesTable.setRowHeight(coursesTable.getRowHeight() + 10);
        // Allow word wrapping in cells
        coursesTable.getColumnModel().getColumn(1).setCellRenderer(new MultiLineCellRenderer());
        coursesTable.getColumnModel().getColumn(7).setCellRenderer(new MultiLineCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        
        // Create a details panel to show instructors and students
        JPanel detailsPanel = new JPanel(new GridLayout(1, 2));
        
        // Instructors panel
        JPanel instructorsPanel = new JPanel(new BorderLayout());
        JTextArea instructorsArea = new JTextArea(3, 20);
        instructorsArea.setEditable(false);
        JScrollPane instructorsScroll = new JScrollPane(instructorsArea);
        instructorsScroll.setBorder(BorderFactory.createTitledBorder("Instructors for Selected Course"));
        instructorsPanel.add(instructorsScroll, BorderLayout.CENTER);
        
        // Students panel (will be populated when a course is selected)
        JPanel studentsPanel = new JPanel(new BorderLayout());
        JTextArea studentsArea = new JTextArea(3, 20);
        studentsArea.setEditable(false);
        JScrollPane studentsScroll = new JScrollPane(studentsArea);
        studentsScroll.setBorder(BorderFactory.createTitledBorder("Enrolled Students for Selected Course"));
        studentsPanel.add(studentsScroll, BorderLayout.CENTER);
        
        // Add both panels to details panel
        detailsPanel.add(instructorsPanel);
        detailsPanel.add(studentsPanel);
        
        // Listen for row selection to update details
        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = coursesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String instructors = (String) coursesTable.getValueAt(selectedRow, 1);
                    String courseName = (String) coursesTable.getValueAt(selectedRow, 0);
                    instructorsArea.setText("Course: " + courseName + "\n\nInstructors:\n" + formatInstructorsList(instructors));
                    
                    // Find enrolled students for this course
                    StringBuilder studentsText = new StringBuilder();
                    studentsText.append("Course: ").append(courseName).append("\n\nEnrolled Students:\n");
                    
                    boolean foundStudents = false;
                    List<User> users = mainFrame.getAdmin().getUsers();
                    for (User user : users) {
                        if (user instanceof Student) {
                            Student student = (Student) user;
                            ArrayList<String> enrolledCourses = student.getCourses();
                            if (enrolledCourses.contains(courseName)) {
                                foundStudents = true;
                                int courseIndex = enrolledCourses.indexOf(courseName);
                                Float grade = (courseIndex >= 0 && courseIndex < student.getNumericGrades().size()) ? 
                                              student.getNumericGrades().get(courseIndex) : null;
                                String gradeStr = (grade != null && grade > 0) ? String.format("%.1f", grade) : "No grade";
                                studentsText.append("• ").append(student.getStudentName())
                                           .append(" (").append(student.getUsername()).append(") - Grade: ")
                                           .append(gradeStr).append("\n");
                            }
                        }
                    }
                    
                    if (!foundStudents) {
                        studentsText.append("No students enrolled in this course.");
                    }
                    
                    studentsArea.setText(studentsText.toString());
                }
            }
        });
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        addCourseButton = new JButton("Add Course");
        updateCourseButton = new JButton("Update Course");
        deleteCourseButton = new JButton("Delete Course");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addCourseButton);
        buttonPanel.add(updateCourseButton);
        buttonPanel.add(deleteCourseButton);
        buttonPanel.add(refreshButton);
        
        // Add action listeners
        addCourseButton.addActionListener(e -> addCourse());
        updateCourseButton.addActionListener(e -> updateCourse());
        deleteCourseButton.addActionListener(e -> deleteCourse());
        refreshButton.addActionListener(e -> refreshCoursesTable());
        
        // Add components to panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, detailsPanel);
        splitPane.setResizeWeight(0.7); // Give more space to the table
        
        coursesPanel.add(splitPane, BorderLayout.CENTER);
        coursesPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize the table
        refreshCoursesTable();
    }
    
    /**
     * Format the instructors list for better display
     */
    private String formatInstructorsList(String instructors) {
        if (instructors == null || instructors.isEmpty() || instructors.equals("None")) {
            return "No instructors assigned";
        }
        
        StringBuilder formatted = new StringBuilder();
        String[] instructorArray = instructors.split(",");
        
        for (String instructor : instructorArray) {
            formatted.append("• ").append(instructor.trim()).append("\n");
        }
        
        return formatted.toString();
    }
    
    /**
     * Create the users tab.
     */
    private void createUsersTab() {
        usersPanel = new JPanel(new BorderLayout());
        
        // Create table model and table
        String[] columnNames = {"Username", "Name", "User Type"};
        Object[][] data = new Object[0][3]; // Empty data initially
        
        usersTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshUsersTable());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        usersPanel.add(scrollPane, BorderLayout.CENTER);
        usersPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize the table
        refreshUsersTable();
    }
    
    /**
     * Create the user management tab.
     */
    private void createUserManagementTab() {
        userManagementPanel = new JPanel(new BorderLayout());
        
        // Create top panel with user type selection
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select User Type:"));
        userTypeCombo = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"});
        userTypeCombo.addActionListener(e -> refreshManagementUsersTable());
        topPanel.add(userTypeCombo);
        
        // Create table for user management
        String[] columnNames = {"Username", "Name", "User Type"};
        Object[][] data = new Object[0][3]; // Empty data initially
        
        managementUsersTable = new JTable(data, columnNames);
        managementUsersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(managementUsersTable);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        addUserButton = new JButton("Add User");
        updateUserButton = new JButton("Update User");
        deleteUserButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addUserButton);
        buttonPanel.add(updateUserButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(refreshButton);
        
        // Add action listeners
        addUserButton.addActionListener(e -> addUser());
        updateUserButton.addActionListener(e -> updateUser());
        deleteUserButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> refreshManagementUsersTable());
        
        // Add components to panel
        userManagementPanel.add(topPanel, BorderLayout.NORTH);
        userManagementPanel.add(scrollPane, BorderLayout.CENTER);
        userManagementPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize the table
        refreshManagementUsersTable();
    }
    
    /**
     * Create the assign courses tab.
     */
    private void createAssignCoursesTab() {
        assignPanel = new JPanel(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Instructor dropdown
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Instructor:"), gbc);
        
        gbc.gridx = 1;
        instructorCombo = new JComboBox<>();
        formPanel.add(instructorCombo, gbc);
        
        // Course dropdown
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Select Course:"), gbc);
        
        gbc.gridx = 1;
        courseCombo = new JComboBox<>();
        formPanel.add(courseCombo, gbc);
        
        // Assign button
        assignButton = new JButton("Assign Course");
        assignButton.addActionListener(e -> assignCourse());
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh Lists");
        refreshButton.addActionListener(e -> {
            refreshInstructorList();
            refreshCourseList();
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(assignButton);
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        assignPanel.add(formPanel, BorderLayout.CENTER);
        assignPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize the dropdowns
        refreshInstructorList();
        refreshCourseList();
    }
    
    /**
     * Create the feedbacks tab to display student feedback
     */
    private void createFeedbacksTab() {
        feedbacksPanel = new JPanel(new BorderLayout());
        
        // Create split pane for table and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Create table panel (top)
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Student Name", "Course Name", "Instructor Name", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        feedbacksTable = new JTable(model);
        feedbacksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        feedbacksTable.setRowHeight(25);
        
        // Make the table columns automatically adjust their width
        feedbacksTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        feedbacksTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        feedbacksTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        feedbacksTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        
        // Add selection listener to update the details area
        feedbacksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateFeedbackDetails();
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(feedbacksTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Create details panel (bottom)
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Feedback Details"));
        
        feedbackDetailsArea = new JTextArea(10, 30);
        feedbackDetailsArea.setEditable(false);
        feedbackDetailsArea.setLineWrap(true);
        feedbackDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(feedbackDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Add panels to split pane
        splitPane.setTopComponent(tablePanel);
        splitPane.setBottomComponent(detailsPanel);
        splitPane.setResizeWeight(0.6); // 60% to top, 40% to bottom
        
        // Button panel with refresh and fix buttons
        JPanel buttonPanel = new JPanel();
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh Feedbacks");
        refreshButton.addActionListener(e -> refreshFeedbacksTable());
        buttonPanel.add(refreshButton);
        
        // Button to reset feedback file
        JButton resetButton = new JButton("Reset Feedback File");
        resetButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "This will delete all existing feedback entries.\nAre you sure you want to continue?",
                "Confirm Reset", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (DataManager.resetFeedbackFile()) {
                    JOptionPane.showMessageDialog(
                        this, 
                        "Feedback file has been reset successfully. New feedback entries will be correctly encoded.",
                        "Reset Success", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshFeedbacksTable();
                } else {
                    JOptionPane.showMessageDialog(
                        this, 
                        "Failed to reset feedback file. Check console for details.",
                        "Reset Failed", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        buttonPanel.add(resetButton);
        
        // Button to convert feedback file
        JButton convertButton = new JButton("Convert Feedback File to UTF-8");
        convertButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "This will attempt to convert the corrupted feedback file to UTF-8 format.\n" +
                "Some data might be lost if it's severely corrupted.\n" +
                "Are you sure you want to continue?",
                "Confirm Conversion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (DataManager.convertFeedbackFileToUTF8()) {
                    JOptionPane.showMessageDialog(
                        this, 
                        "Feedback file has been converted to UTF-8 successfully.",
                        "Conversion Success", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshFeedbacksTable();
                } else {
                    JOptionPane.showMessageDialog(
                        this, 
                        "Failed to convert feedback file. Check console for details.",
                        "Conversion Failed", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        buttonPanel.add(convertButton);
        
        // Add components to panel
        feedbacksPanel.add(splitPane, BorderLayout.CENTER);
        feedbacksPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize the table
        refreshFeedbacksTable();
    }
    
    /**
     * Update the feedback details area with the selected feedback
     */
    private void updateFeedbackDetails() {
        int selectedRow = feedbacksTable.getSelectedRow();
        if (selectedRow == -1) {
            feedbackDetailsArea.setText("No feedback selected.");
            return;
        }
        
        // Get the feedback data from the table model
        String studentName = (String) feedbacksTable.getValueAt(selectedRow, 0);
        String courseName = (String) feedbacksTable.getValueAt(selectedRow, 1);
        String instructorName = (String) feedbacksTable.getValueAt(selectedRow, 2);
        String date = (String) feedbacksTable.getValueAt(selectedRow, 3);
        
        // Get the feedback message from the stored data
        String feedbackMessage = getFeedbackMessage(selectedRow);
        
        // Build the detailed view
        StringBuilder details = new StringBuilder();
        details.append("Date: ").append(date).append("\n\n");
        details.append("Course: ").append(courseName).append("\n");
        details.append("Instructor: ").append(instructorName).append("\n");
        details.append("Student: ").append(studentName).append("\n\n");
        details.append("Feedback:\n").append(feedbackMessage);
        
        feedbackDetailsArea.setText(details.toString());
    }
    
    /**
     * Get the feedback message for the selected row
     */
    private String getFeedbackMessage(int selectedRow) {
        // This method assumes we're using the tag in the table model to store additional data
        if (selectedRow >= 0 && feedbacksTable.getModel() instanceof DefaultTableModel) {
            DefaultTableModel model = (DefaultTableModel) feedbacksTable.getModel();
            if (model.getRowCount() > selectedRow) {
                // The message is stored in the hidden column (index 4)
                if (model.getColumnCount() > 4) {
                    Object message = model.getValueAt(selectedRow, 4);
                    if (message != null) {
                        return message.toString();
                    }
                }
                
                // If not in the model, try to get it from the file based on the visible data
                String studentName = (String) model.getValueAt(selectedRow, 0);
                String courseName = (String) model.getValueAt(selectedRow, 1);
                String date = (String) model.getValueAt(selectedRow, 3);
                
                // Try to find in the feedbacks.txt file
                try {
                    File file = new File("data/feedbacks.txt");
                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] parts = line.split("\\|");
                                if (parts.length >= 6 && 
                                    parts[0].equals(date) && 
                                    parts[1].equals(studentName) && 
                                    parts[3].equals(courseName)) {
                                    return parts[5].trim();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return "Feedback message not available.";
    }
    
    /**
     * Refresh the feedbacks table with data from the feedbacks file
     */
    private void refreshFeedbacksTable() {
        DefaultTableModel model = (DefaultTableModel) feedbacksTable.getModel();
        model.setRowCount(0); // Clear existing rows
        
        File file = new File("data/feedbacks.txt");
        if (!file.exists()) {
            feedbackDetailsArea.setText("No feedback file found.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    String timestamp = parts[0];
                    String studentName = parts[1];
                    String studentUsername = parts[2];
                    String courseName = parts[3];
                    String instructorName = parts[4];
                    String feedbackMessage = parts[5].trim(); // Trim the feedback message
                    
                    // Add row to the table
                    Object[] rowData = {
                        studentName,
                        courseName,
                        instructorName,
                        timestamp,
                        feedbackMessage // Hidden column for the message
                    };
                    model.addRow(rowData);
                    
                    // Debug output - uncomment if needed
                    // System.out.println("Added feedback: " + feedbackMessage);
                }
            }
            
            // Select the first row if available
            if (feedbacksTable.getRowCount() > 0) {
                feedbacksTable.setRowSelectionInterval(0, 0);
                updateFeedbackDetails();
            } else {
                feedbackDetailsArea.setText("No feedback available.");
            }
            
        } catch (IOException e) {
            feedbackDetailsArea.setText("Error loading feedback: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
    
    /**
     * Handles adding a new course.
     */
    private void addCourse() {
        // Create custom dialog for course input
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Course");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Course name
        formPanel.add(new JLabel("Course Name:"));
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField);
        
        // Instructor (optional, can be assigned later)
        formPanel.add(new JLabel("Instructor(s) (optional):"));
        JTextField instructorField = new JTextField(20);
        formPanel.add(instructorField);
        
        // Room
        formPanel.add(new JLabel("Room:"));
        JTextField roomField = new JTextField(20);
        formPanel.add(roomField);
        
        // Branch
        formPanel.add(new JLabel("Branch:"));
        JTextField branchField = new JTextField(20);
        formPanel.add(branchField);
        
        // Price
        formPanel.add(new JLabel("Price:"));
        JTextField priceField = new JTextField(20);
        formPanel.add(priceField);
        
        // Start date (YYYY-MM-DD)
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        JTextField startDateField = new JTextField(20);
        formPanel.add(startDateField);
        
        // End date (YYYY-MM-DD)
        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        JTextField endDateField = new JTextField(20);
        formPanel.add(endDateField);
        
        // Description
        formPanel.add(new JLabel("Description:"));
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Action listeners
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String instructor = instructorField.getText().trim();
                String room = roomField.getText().trim();
                String branch = branchField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String startDate = startDateField.getText().trim();
                String endDate = endDateField.getText().trim();
                String description = descriptionArea.getText().trim();
                
                // Validate inputs
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Course name cannot be empty!", 
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (room.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Room cannot be empty!", 
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (branch.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Branch cannot be empty!", 
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (price < 0) {
                    JOptionPane.showMessageDialog(dialog, "Price cannot be negative!", 
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate date format
                if (!isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {
                    JOptionPane.showMessageDialog(dialog, "Dates must be in YYYY-MM-DD format!", 
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create the new course
                Course newCourse = new Course(name, instructor.isEmpty() ? "None" : instructor, 
                                             room, branch, price, startDate, endDate, description);
                
                // Add the course
                mainFrame.getCourseManager().addCourse(newCourse);
                
                // Refresh the tables
                refreshCoursesTable();
                refreshUpcomingCoursesTable();
                refreshEndingCoursesTable();
                
                // Close the dialog
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this, "Course added successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price format. Please enter a valid number!", 
                                            "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding course: " + ex.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    /**
     * Validates if a string is in YYYY-MM-DD format.
     * 
     * @param date The date string to validate.
     * @return true if valid, false otherwise.
     */
    private boolean isValidDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Update an existing course.
     */
    private void updateCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a course to update",
                "Update Course Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String courseName = (String) coursesTable.getValueAt(selectedRow, 0);
        
        JTextField nameField = new JTextField(courseName);
        JTextField instructorField = new JTextField((String) coursesTable.getValueAt(selectedRow, 1));
        JTextField startDateField = new JTextField((String) coursesTable.getValueAt(selectedRow, 2));
        JTextField endDateField = new JTextField((String) coursesTable.getValueAt(selectedRow, 3));
        JTextField descriptionField = new JTextField((String) coursesTable.getValueAt(selectedRow, 4));
        
        Object[] message = {
            "Course Name:", nameField,
            "Instructor:", instructorField,
            "Start Date (YYYY-MM-DD):", startDateField,
            "End Date (YYYY-MM-DD):", endDateField,
            "Description:", descriptionField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Update Course", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String instructor = instructorField.getText().trim();
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();
            String description = descriptionField.getText().trim();
            
            if (newName.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Course name, start date, and end date are required",
                    "Update Course Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                mainFrame.getCourseManager().updateCourse(courseName, newName, instructor, startDate, endDate, description);
                refreshCoursesTable();
                refreshCourseList();
                JOptionPane.showMessageDialog(this, "Course updated successfully!");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update course: " + e.getMessage(),
                    "Update Course Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Delete a course.
     */
    private void deleteCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a course to delete",
                "Delete Course Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String courseName = (String) coursesTable.getValueAt(selectedRow, 0);
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the course '" + courseName + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            try {
                mainFrame.getCourseManager().deleteCourse(courseName);
                refreshCoursesTable();
                refreshCourseList();
                JOptionPane.showMessageDialog(this, "Course deleted successfully!");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete course: " + e.getMessage(),
                    "Delete Course Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Assign a course to an instructor.
     */
    private void assignCourse() {
        String instructorSelection = (String) instructorCombo.getSelectedItem();
        String courseName = (String) courseCombo.getSelectedItem();
        
        if (instructorSelection == null || courseName == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select both an instructor and a course",
                "Assign Course Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extract username from the format "Name (username)"
        String instructorUsername = extractUsername(instructorSelection);
        if (instructorUsername == null) {
            JOptionPane.showMessageDialog(this, 
                "Invalid instructor selection format",
                "Assign Course Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Admin admin = mainFrame.getAdmin();
        boolean found = false;
        
        for (User user : admin.getUsers()) {
            if (user instanceof Instructor && user.getUsername().equals(instructorUsername)) {
                found = true;
                Instructor instructor = (Instructor) user;
                instructor.assignCourse(courseName);
                
                // Also update the course record with instructor's name
                CourseManager manager = mainFrame.getCourseManager();
                ArrayList<Course> courses = manager.getCourses();
                for (Course course : courses) {
                    if (course.getName().equals(courseName)) {
                        course.addInstructor(instructor.getInstructorName());
                        DataManager.saveCourses(courses); // Save course changes
                        break;
                    }
                }
                
                admin.saveUsers(); // Save the user changes
                JOptionPane.showMessageDialog(this, 
                    "Course '" + courseName + "' assigned to instructor " + instructor.getInstructorName());
                refreshCoursesTable(); // Refresh to show updated instructor assignment
                break;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, 
                "Instructor with username '" + instructorUsername + "' not found",
                "Assign Course Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Extract username from the format "Name (username)".
     * 
     * @param selection The selected item from the combo box.
     * @return The extracted username or null if format is invalid.
     */
    private String extractUsername(String selection) {
        if (selection == null) return null;
        
        int startIndex = selection.lastIndexOf("(");
        int endIndex = selection.lastIndexOf(")");
        
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return selection.substring(startIndex + 1, endIndex);
        }
        
        return null;
    }
    
    /**
     * Refresh the courses table.
     */
    private void refreshCoursesTable() {
        ArrayList<Course> courses = mainFrame.getCourseManager().getCourses();
        
        Object[][] data = new Object[courses.size()][8];
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            data[i][0] = course.getName();
            data[i][1] = course.getInstructorName();
            data[i][2] = course.getRoom();
            data[i][3] = course.getBranch();
            data[i][4] = String.format("%.2f", course.getPrice());
            data[i][5] = course.getStartDate();
            data[i][6] = course.getEndDate();
            data[i][7] = course.getDescription();
        }
        
        coursesTable.setModel(new DefaultTableModel(data, new String[]{
            "Course Name", "Instructors", "Room", "Branch", "Price", "Start Date", "End Date", "Description"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        });
        
        // Set custom renderer for instructors column and description column
        coursesTable.getColumnModel().getColumn(1).setCellRenderer(new MultiLineCellRenderer());
        coursesTable.getColumnModel().getColumn(7).setCellRenderer(new MultiLineCellRenderer());
    }
    
    /**
     * Refresh the users table.
     */
    private void refreshUsersTable() {
        Admin admin = mainFrame.getAdmin();
        List<User> users = admin.getUsers();
        
        Object[][] data = new Object[users.size() + 1][3]; // +1 for the main admin
        
        // Add main admin
        data[0][0] = admin.getUsername();
        data[0][1] = admin.getAdminName();
        data[0][2] = "Admin";
        
        // Add other users
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i+1][0] = user.getUsername();
            
            if (user instanceof Admin) {
                data[i+1][1] = ((Admin) user).getAdminName();
                data[i+1][2] = "Admin";
            } else if (user instanceof Instructor) {
                data[i+1][1] = ((Instructor) user).getInstructorName();
                data[i+1][2] = "Instructor";
            } else if (user instanceof Student) {
                data[i+1][1] = ((Student) user).getStudentName();
                data[i+1][2] = "Student";
            }
        }
        
        usersTable.setModel(new javax.swing.table.DefaultTableModel(data, 
            new String[]{"Username", "Name", "User Type"}));
    }
    
    /**
     * Refresh the instructor dropdown list.
     */
    private void refreshInstructorList() {
        Admin admin = mainFrame.getAdmin();
        instructorCombo.removeAllItems();
        
        for (User user : admin.getUsers()) {
            if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                instructorCombo.addItem(instructor.getInstructorName() + " (" + instructor.getUsername() + ")");
            }
        }
    }
    
    /**
     * Refresh the course dropdown list.
     */
    private void refreshCourseList() {
        CourseManager manager = mainFrame.getCourseManager();
        ArrayList<Course> courses = manager.getCourses();
        
        courseCombo.removeAllItems();
        
        for (Course course : courses) {
            courseCombo.addItem(course.getName());
        }
    }
    
    /**
     * Add a new user based on selected type.
     */
    private void addUser() {
        String userType = (String) userTypeCombo.getSelectedItem();
        
        if (userType.equals("Student")) {
            addStudent();
        } else if (userType.equals("Instructor")) {
            addInstructor();
        } else if (userType.equals("Admin")) {
            addAdmin();
        }
    }
    
    /**
     * Add a new student.
     */
    private void addStudent() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField nameField = new JTextField();
        
        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Student Name:", nameField
        };
        
        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Student", 
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String name = nameField.getText().trim();
            
            // Check for empty fields
            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if username already exists
            for (User user : mainFrame.getAdmin().getUsers()) {
                if (user.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            try {
                Student newStudent = new Student(username, password, name);
                
                // Ask if admin wants to enroll student in courses
                int enrollResult = JOptionPane.showConfirmDialog(this, 
                                                              "Do you want to enroll this student in any courses?", 
                                                              "Course Enrollment", 
                                                              JOptionPane.YES_NO_OPTION);
                
                if (enrollResult == JOptionPane.YES_OPTION) {
                    enrollStudentInCourses(newStudent);
                }
                
                mainFrame.getAdmin().addUser(newStudent);
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                refreshManagementUsersTable();
                refreshUsersTable();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Helper method to enroll a student in courses.
     */
    private void enrollStudentInCourses(Student student) {
        ArrayList<Course> availableCourses = mainFrame.getCourseManager().getCourses();
        
        if (availableCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses available for enrollment.");
            return;
        }
        
        // Create an array of course names for the selection dialog
        String[] courseNames = new String[availableCourses.size()];
        for (int i = 0; i < availableCourses.size(); i++) {
            courseNames[i] = availableCourses.get(i).getName();
        }
        
        // Show a multi-selection dialog
        JList<String> courseList = new JList<>(courseNames);
        courseList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        
        int result = JOptionPane.showConfirmDialog(this, scrollPane, 
                                                "Select Courses", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            int[] selectedIndices = courseList.getSelectedIndices();
            for (int index : selectedIndices) {
                String courseName = courseNames[index];
                student.addCourse(courseName, 0.0f, "N/A");
            }
        }
    }
    
    /**
     * Add a new instructor.
     */
    private void addInstructor() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField nameField = new JTextField();
        
        // Get available courses for specialization
        CourseManager courseManager = mainFrame.getCourseManager();
        ArrayList<Course> courses = courseManager.getCourses();
        
        String[] specializationOptions;
        if (courses.isEmpty()) {
            specializationOptions = new String[]{"None"};
        } else {
            specializationOptions = new String[courses.size() + 1];
            specializationOptions[0] = "None";
            for (int i = 0; i < courses.size(); i++) {
                specializationOptions[i + 1] = courses.get(i).getName();
            }
        }
        
        JComboBox<String> specializationCombo = new JComboBox<>(specializationOptions);
        
        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Instructor Name:", nameField,
            "Specialization:", specializationCombo
        };
        
        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Instructor", 
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String name = nameField.getText().trim();
            String specialization = (String) specializationCombo.getSelectedItem();
            
            // Check for empty fields
            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username, password, and name are required!", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if username already exists
            for (User user : mainFrame.getAdmin().getUsers()) {
                if (user.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            try {
                Instructor newInstructor = new Instructor(username, password, name, specialization);
                mainFrame.getAdmin().addUser(newInstructor);
                JOptionPane.showMessageDialog(this, "Instructor added successfully!");
                refreshManagementUsersTable();
                refreshUsersTable();
                refreshInstructorList();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding instructor: " + e.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Add a new admin.
     */
    private void addAdmin() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField nameField = new JTextField();
        
        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Admin Name:", nameField
        };
        
        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Admin", 
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String name = nameField.getText().trim();
            
            // Check for empty fields
            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if username already exists
            for (User user : mainFrame.getAdmin().getUsers()) {
                if (user.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            try {
                Admin newAdmin = new Admin(username, password, name);
                mainFrame.getAdmin().addUser(newAdmin);
                JOptionPane.showMessageDialog(this, "Admin added successfully!");
                refreshManagementUsersTable();
                refreshUsersTable();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding admin: " + e.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Update an existing user.
     */
    private void updateUser() {
        int selectedRow = managementUsersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String username = (String) managementUsersTable.getValueAt(selectedRow, 0);
        String userType = (String) managementUsersTable.getValueAt(selectedRow, 2);
        
        // Find the user in the list
        User userToUpdate = null;
        for (User user : mainFrame.getAdmin().getUsers()) {
            if (user.getUsername().equals(username)) {
                userToUpdate = user;
                break;
            }
        }
        
        if (userToUpdate == null) {
            JOptionPane.showMessageDialog(this, "User not found.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Open appropriate update dialog based on user type
        if (userType.equals("Student")) {
            updateStudent((Student) userToUpdate);
        } else if (userType.equals("Instructor")) {
            updateInstructor((Instructor) userToUpdate);
        } else if (userType.equals("Admin")) {
            updateAdmin((Admin) userToUpdate);
        }
    }
    
    /**
     * Update a student.
     */
    private void updateStudent(Student student) {
        JTextField usernameField = new JTextField(student.getUsername());
        JTextField passwordField = new JTextField(student.getPassword());
        JTextField nameField = new JTextField(student.getStudentName());
        
        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Student Name:", nameField
        };
        
        int result = JOptionPane.showConfirmDialog(this, fields, "Update Student", 
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String newUsername = usernameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            String newName = nameField.getText().trim();
            
            // Check for empty fields
            if (newUsername.isEmpty() || newPassword.isEmpty() || newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if username already exists (if changed)
            if (!newUsername.equals(student.getUsername())) {
                for (User user : mainFrame.getAdmin().getUsers()) {
                    if (user != student && user.getUsername().equals(newUsername)) {
                        JOptionPane.showMessageDialog(this, "Username already exists!", 
                                                     "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            try {
                student.setUsername(newUsername);
                student.setPassword(newPassword);
                student.setStudentName(newName);
                
                mainFrame.getAdmin().saveUsers();
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
                refreshManagementUsersTable();
                refreshUsersTable();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Update an instructor.
     */
    private void updateInstructor(Instructor instructor) {
        JTextField usernameField = new JTextField(instructor.getUsername());
        JTextField passwordField = new JTextField(instructor.getPassword());
        JTextField nameField = new JTextField(instructor.getInstructorName());
        
        // Get and display currently assigned courses (read-only)
        JTextField assignedCoursesField = new JTextField(instructor.getAssignedCourses());
        assignedCoursesField.setEditable(false);
        
        // Get available courses for specialization
        CourseManager courseManager = mainFrame.getCourseManager();
        ArrayList<Course> courses = courseManager.getCourses();
        
        String[] specializationOptions;
        if (courses.isEmpty()) {
            specializationOptions = new String[]{"None"};
        } else {
            specializationOptions = new String[courses.size() + 1];
            specializationOptions[0] = "None";
            for (int i = 0; i < courses.size(); i++) {
                specializationOptions[i + 1] = courses.get(i).getName();
            }
        }
        
        JComboBox<String> specializationCombo = new JComboBox<>(specializationOptions);
        
        // Try to set the current specialization as selected
        String currentSpecialization = instructor.getAssignedCourses().split(",")[0].trim(); // Get first specialization
        if (!currentSpecialization.isEmpty() && !currentSpecialization.equals("None")) {
            for (int i = 0; i < specializationCombo.getItemCount(); i++) {
                if (specializationCombo.getItemAt(i).equals(currentSpecialization)) {
                    specializationCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Instructor Name:", nameField,
            "Currently Assigned Courses:", assignedCoursesField,
            "Update Specialization:", specializationCombo
        };
        
        int result = JOptionPane.showConfirmDialog(this, fields, "Update Instructor", 
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String newUsername = usernameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            String newName = nameField.getText().trim();
            String newSpecialization = (String) specializationCombo.getSelectedItem();
            
            // Check for empty fields
            if (newUsername.isEmpty() || newPassword.isEmpty() || newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username, password, and name are required!", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if username already exists (if changed)
            if (!newUsername.equals(instructor.getUsername())) {
                for (User user : mainFrame.getAdmin().getUsers()) {
                    if (user != instructor && user.getUsername().equals(newUsername)) {
                        JOptionPane.showMessageDialog(this, "Username already exists!", 
                                                     "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            try {
                instructor.setUsername(newUsername);
                instructor.setPassword(newPassword);
                instructor.setInstructorName(newName);
                
                // Ask if user wants to replace all existing assignments with new specialization
                if (!newSpecialization.equals("None")) {
                    int updateResponse = JOptionPane.showConfirmDialog(this,
                        "Do you want to replace all existing course assignments with the new specialization?",
                        "Update Assignments",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (updateResponse == JOptionPane.YES_OPTION) {
                        instructor.setAssignedCourses(newSpecialization);
                    } else {
                        // Add the new specialization to existing assignments if not already present
                        String currentAssignments = instructor.getAssignedCourses();
                        if (currentAssignments.isEmpty() || currentAssignments.equals("None")) {
                            instructor.setAssignedCourses(newSpecialization);
                        } else if (!currentAssignments.contains(newSpecialization)) {
                            instructor.setAssignedCourses(currentAssignments + ", " + newSpecialization);
                        }
                    }
                }
                
                mainFrame.getAdmin().saveUsers();
                JOptionPane.showMessageDialog(this, "Instructor updated successfully!");
                refreshManagementUsersTable();
                refreshUsersTable();
                refreshInstructorList();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating instructor: " + e.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Update an admin.
     */
    private void updateAdmin(Admin adminToUpdate) {
        JTextField usernameField = new JTextField(adminToUpdate.getUsername());
        JTextField passwordField = new JTextField(adminToUpdate.getPassword());
        JTextField nameField = new JTextField(adminToUpdate.getAdminName());
        
        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Admin Name:", nameField
        };
        
        int result = JOptionPane.showConfirmDialog(this, fields, "Update Admin", 
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String newUsername = usernameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            String newName = nameField.getText().trim();
            
            // Check for empty fields
            if (newUsername.isEmpty() || newPassword.isEmpty() || newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if username already exists (if changed)
            if (!newUsername.equals(adminToUpdate.getUsername())) {
                for (User user : mainFrame.getAdmin().getUsers()) {
                    if (user != adminToUpdate && user.getUsername().equals(newUsername)) {
                        JOptionPane.showMessageDialog(this, "Username already exists!", 
                                                     "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            try {
                adminToUpdate.setUsername(newUsername);
                adminToUpdate.setPassword(newPassword);
                adminToUpdate.setAdminName(newName);
                
                mainFrame.getAdmin().saveUsers();
                JOptionPane.showMessageDialog(this, "Admin updated successfully!");
                refreshManagementUsersTable();
                refreshUsersTable();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating admin: " + e.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Delete a user.
     */
    private void deleteUser() {
        int selectedRow = managementUsersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String username = (String) managementUsersTable.getValueAt(selectedRow, 0);
        String name = (String) managementUsersTable.getValueAt(selectedRow, 1);
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, 
                                                 "Are you sure you want to delete user '" + username + "' (" + name + ")?", 
                                                 "Confirm Deletion", 
                                                 JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            mainFrame.getAdmin().deleteUser(username);
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
            refreshManagementUsersTable();
            refreshUsersTable();
            refreshInstructorList();
        }
    }
    
    /**
     * Refresh the users table in the User Management tab.
     */
    private void refreshManagementUsersTable() {
        String userType = (String) userTypeCombo.getSelectedItem();
        List<User> allUsers = mainFrame.getAdmin().getUsers();
        List<Object[]> filteredUsers = new ArrayList<>();
        
        for (User user : allUsers) {
            String currentType = "";
            String name = "";
            
            if (user instanceof Student) {
                currentType = "Student";
                name = ((Student) user).getStudentName();
            } else if (user instanceof Instructor) {
                currentType = "Instructor";
                name = ((Instructor) user).getInstructorName();
            } else if (user instanceof Admin) {
                currentType = "Admin";
                name = ((Admin) user).getAdminName();
            }
            
            if (userType.equals(currentType)) {
                filteredUsers.add(new Object[]{user.getUsername(), name, currentType});
            }
        }
        
        // Create and set the table model
        DefaultTableModel model = new DefaultTableModel(
            filteredUsers.toArray(new Object[0][0]),
            new String[]{"Username", "Name", "User Type"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        
        managementUsersTable.setModel(model);
    }
    
    /**
     * Create the upcoming courses tab showing courses starting within 30 days.
     */
    private void createUpcomingCoursesTab() {
        upcomingCoursesPanel = new JPanel(new BorderLayout());
        
        // Create title label
        JLabel titleLabel = new JLabel("Courses Starting Within 30 Days", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Create table model and table
        String[] columnNames = {"Course Name", "Instructor(s)", "Room", "Branch", "Price", "Start Date"};
        Object[][] data = new Object[0][6]; // Empty data initially
        
        upcomingCoursesTable = new JTable(data, columnNames);
        upcomingCoursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Set row height to accommodate multiple lines of text
        upcomingCoursesTable.setRowHeight(upcomingCoursesTable.getRowHeight() + 10);
        // Allow word wrapping in cells
        upcomingCoursesTable.getColumnModel().getColumn(1).setCellRenderer(new MultiLineCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(upcomingCoursesTable);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshUpcomingCoursesTable());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        upcomingCoursesPanel.add(titleLabel, BorderLayout.NORTH);
        upcomingCoursesPanel.add(scrollPane, BorderLayout.CENTER);
        upcomingCoursesPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize the table
        refreshUpcomingCoursesTable();
    }
    
    /**
     * Create the ending courses tab showing courses ending within 30 days.
     */
    private void createEndingCoursesTab() {
        endingCoursesPanel = new JPanel(new BorderLayout());
        
        // Create title label
        JLabel titleLabel = new JLabel("Courses Ending Within 30 Days", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Create table model and table
        String[] columnNames = {"Course Name", "Instructor(s)", "Room", "Branch", "Price", "End Date"};
        Object[][] data = new Object[0][6]; // Empty data initially
        
        endingCoursesTable = new JTable(data, columnNames);
        endingCoursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Set row height to accommodate multiple lines of text
        endingCoursesTable.setRowHeight(endingCoursesTable.getRowHeight() + 10);
        // Allow word wrapping in cells
        endingCoursesTable.getColumnModel().getColumn(1).setCellRenderer(new MultiLineCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(endingCoursesTable);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshEndingCoursesTable());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        endingCoursesPanel.add(titleLabel, BorderLayout.NORTH);
        endingCoursesPanel.add(scrollPane, BorderLayout.CENTER);
        endingCoursesPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize the table
        refreshEndingCoursesTable();
    }
    
    /**
     * Refresh the upcoming courses table.
     */
    private void refreshUpcomingCoursesTable() {
        ArrayList<Course> courses = mainFrame.getCourseManager().getCourses();
        List<Course> upcomingCourses = new ArrayList<>();
        
        // Get today's date
        LocalDate today = LocalDate.now();
        
        // Filter courses starting within 30 days
        for (Course course : courses) {
            try {
                LocalDate startDate = LocalDate.parse(course.getStartDate());
                long daysUntilStart = ChronoUnit.DAYS.between(today, startDate);
                
                if (daysUntilStart >= 0 && daysUntilStart <= 30) {
                    upcomingCourses.add(course);
                }
            } catch (Exception e) {
                // Skip courses with invalid dates
                System.err.println("Error parsing date for course: " + course.getName());
            }
        }
        
        // Create data for table
        Object[][] data = new Object[upcomingCourses.size()][6];
        for (int i = 0; i < upcomingCourses.size(); i++) {
            Course course = upcomingCourses.get(i);
            data[i][0] = course.getName();
            data[i][1] = course.getInstructorName();
            data[i][2] = course.getRoom();
            data[i][3] = course.getBranch();
            data[i][4] = String.format("%.2f", course.getPrice());
            data[i][5] = course.getStartDate();
        }
        
        // Update table model
        upcomingCoursesTable.setModel(new DefaultTableModel(data, new String[]{
            "Course Name", "Instructor(s)", "Room", "Branch", "Price", "Start Date"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        });
        
        // Set custom renderer for instructors column
        upcomingCoursesTable.getColumnModel().getColumn(1).setCellRenderer(new MultiLineCellRenderer());
    }
    
    /**
     * Refresh the ending courses table.
     */
    private void refreshEndingCoursesTable() {
        ArrayList<Course> courses = mainFrame.getCourseManager().getCourses();
        List<Course> endingCourses = new ArrayList<>();
        
        // Get today's date
        LocalDate today = LocalDate.now();
        
        // Filter courses ending within 30 days
        for (Course course : courses) {
            try {
                LocalDate endDate = LocalDate.parse(course.getEndDate());
                long daysUntilEnd = ChronoUnit.DAYS.between(today, endDate);
                
                if (daysUntilEnd >= 0 && daysUntilEnd <= 30) {
                    endingCourses.add(course);
                }
            } catch (Exception e) {
                // Skip courses with invalid dates
                System.err.println("Error parsing date for course: " + course.getName());
            }
        }
        
        // Create data for table
        Object[][] data = new Object[endingCourses.size()][6];
        for (int i = 0; i < endingCourses.size(); i++) {
            Course course = endingCourses.get(i);
            data[i][0] = course.getName();
            data[i][1] = course.getInstructorName();
            data[i][2] = course.getRoom();
            data[i][3] = course.getBranch();
            data[i][4] = String.format("%.2f", course.getPrice());
            data[i][5] = course.getEndDate();
        }
        
        // Update table model
        endingCoursesTable.setModel(new DefaultTableModel(data, new String[]{
            "Course Name", "Instructor(s)", "Room", "Branch", "Price", "End Date"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        });
        
        // Set custom renderer for instructors column
        endingCoursesTable.getColumnModel().getColumn(1).setCellRenderer(new MultiLineCellRenderer());
    }
    
    /**
     * A cell renderer that supports multiple lines of text.
     */
    private static class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        
        public MultiLineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            
            setFont(table.getFont());
            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (table.isCellEditable(row, column)) {
                    setForeground(UIManager.getColor("Table.focusCellForeground"));
                    setBackground(UIManager.getColor("Table.focusCellBackground"));
                }
            } else {
                setBorder(new EmptyBorder(1, 2, 1, 2));
            }
            
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
} 