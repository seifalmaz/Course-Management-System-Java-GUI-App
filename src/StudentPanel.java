import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Student panel for the Course Management System GUI.
 */
public class StudentPanel extends JPanel {
    private CourseManagementGUI mainFrame;
    private JTabbedPane tabbedPane;
    private Student student;
    
    // Header component for greeting
    private JLabel greetingLabel;
    
    // Available courses tab components
    private JPanel availableCoursesPanel;
    private JTable availableCoursesTable;
    private JButton registerButton;
    
    // My courses tab components
    private JPanel myCoursesPanel;
    private JTable myCoursesTable;
    private JTextArea courseDetailsArea;
    
    // Feedback tab components
    private JPanel feedbackPanel;
    private JTextArea feedbackArea;
    private JButton submitFeedbackButton;
    
    // Profile tab components
    private JPanel profilePanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JButton updateProfileButton;
    
    /**
     * Constructor for the StudentPanel.
     * 
     * @param mainFrame The main GUI frame.
     */
    public StudentPanel(CourseManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create header panel with title and greeting
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Student Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        // Add greeting label (will be populated when student logs in)
        greetingLabel = new JLabel("", JLabel.CENTER);
        greetingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(greetingLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create tabs
        createAvailableCoursesTab();
        createMyCoursesTab();
        createFeedbackTab();
        createProfileTab();
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("Available Courses", availableCoursesPanel);
        tabbedPane.addTab("My Courses", myCoursesPanel);
        tabbedPane.addTab("Submit Feedback", feedbackPanel);
        tabbedPane.addTab("Profile", profilePanel);
        
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
     * Set the student for this panel.
     * 
     * @param student The student.
     */
    public void setStudent(Student student) {
        this.student = student;
        
        // Update greeting message
        greetingLabel.setText("Hello, " + student.getStudentName() + "!");
        
        updateAvailableCoursesTable();
        updateMyCoursesTable();
        updateProfileFields();
        updateCourseDropdown();
    }
    
    /**
     * Create the available courses tab.
     */
    private void createAvailableCoursesTab() {
        availableCoursesPanel = new JPanel(new BorderLayout());
        
        // Create table
        String[] columnNames = {"Course Name", "Instructor(s)", "Start Date", "End Date", "Description"};
        Object[][] data = new Object[0][5]; // Empty data initially
        
        availableCoursesTable = new JTable(data, columnNames);
        availableCoursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Enable word wrap in table cells
        availableCoursesTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        availableCoursesTable.setRowHeight(60); // Taller rows for wrapped content
        
        JScrollPane scrollPane = new JScrollPane(availableCoursesTable);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        registerButton = new JButton("Register for Selected Course");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(registerButton);
        buttonPanel.add(refreshButton);
        
        // Add action listeners
        registerButton.addActionListener(e -> registerForCourse());
        refreshButton.addActionListener(e -> updateAvailableCoursesTable());
        
        // Add components to panel
        availableCoursesPanel.add(scrollPane, BorderLayout.CENTER);
        availableCoursesPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the my courses tab.
     */
    private void createMyCoursesTab() {
        myCoursesPanel = new JPanel(new BorderLayout());
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Create table panel (top)
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Course Name", "Instructor(s)"};
        Object[][] data = new Object[0][2]; // Empty data initially
        
        myCoursesTable = new JTable(data, columnNames);
        myCoursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myCoursesTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        myCoursesTable.setRowHeight(40);
        
        // Add selection listener to update the details area
        myCoursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCourseDetails();
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(myCoursesTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Create details panel (bottom)
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));
        
        courseDetailsArea = new JTextArea(8, 30);
        courseDetailsArea.setEditable(false);
        courseDetailsArea.setLineWrap(true);
        courseDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(courseDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Add panels to split pane
        splitPane.setTopComponent(tablePanel);
        splitPane.setBottomComponent(detailsPanel);
        splitPane.setResizeWeight(0.6); // 60% to top, 40% to bottom
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            updateMyCoursesTable();
            updateCourseDetails();
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        myCoursesPanel.add(splitPane, BorderLayout.CENTER);
        myCoursesPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the feedback tab.
     */
    private void createFeedbackTab() {
        feedbackPanel = new JPanel(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Course selection dropdown
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel courseLabel = new JLabel("Select Course:");
        formPanel.add(courseLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<String> courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(300, 25));
        formPanel.add(courseComboBox, gbc);
        
        // Feedback area
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel feedbackLabel = new JLabel("Enter your feedback:");
        formPanel.add(feedbackLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        
        feedbackArea = new JTextArea(10, 30);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        formPanel.add(scrollPane, gbc);
        
        // Submit button
        submitFeedbackButton = new JButton("Submit Feedback");
        submitFeedbackButton.addActionListener(e -> submitFeedback(courseComboBox));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitFeedbackButton);
        
        // Add components to panel
        feedbackPanel.add(formPanel, BorderLayout.CENTER);
        feedbackPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the profile tab.
     */
    private void createProfileTab() {
        profilePanel = new JPanel(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Update button
        updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(e -> updateProfile());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateProfileButton);
        
        // Add components to panel
        profilePanel.add(formPanel, BorderLayout.CENTER);
        profilePanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Update the available courses table.
     */
    private void updateAvailableCoursesTable() {
        if (student == null) {
            return;
        }
        
        CourseManager manager = mainFrame.getCourseManager();
        ArrayList<Course> courses = manager.getCourses();
        
        // Filter out courses the student is already registered for
        ArrayList<String> enrolledCourses = student.getCourses();
        
        ArrayList<Course> availableCourses = new ArrayList<>();
        for (Course course : courses) {
            if (!enrolledCourses.contains(course.getName())) {
                availableCourses.add(course);
            }
        }
        
        Object[][] data = new Object[availableCourses.size()][5];
        for (int i = 0; i < availableCourses.size(); i++) {
            Course course = availableCourses.get(i);
            data[i][0] = course.getName();
            data[i][1] = course.getInstructorName();
            data[i][2] = course.getStartDate();
            data[i][3] = course.getEndDate();
            data[i][4] = course.getDescription();
        }
        
        availableCoursesTable.setModel(new javax.swing.table.DefaultTableModel(data, 
            new String[]{"Course Name", "Instructor(s)", "Start Date", "End Date", "Description"}));
    }
    
    /**
     * Update the my courses table.
     */
    private void updateMyCoursesTable() {
        if (student == null) {
            return;
        }
        
        ArrayList<String> courseNames = student.getCourses();
        CourseManager manager = mainFrame.getCourseManager();
        
        Object[][] data = new Object[courseNames.size()][2];
        for (int i = 0; i < courseNames.size(); i++) {
            String courseName = courseNames.get(i);
            
            // Find instructor name from the course manager
            String instructorName = "N/A";
            for (Course course : manager.getCourses()) {
                if (course.getName().equals(courseName)) {
                    instructorName = course.getInstructorName();
                    break;
                }
            }
            
            data[i][0] = courseName;
            data[i][1] = instructorName;
        }
        
        myCoursesTable.setModel(new javax.swing.table.DefaultTableModel(data, 
            new String[]{"Course Name", "Instructor(s)"}));
        
        // Select the first row if available
        if (myCoursesTable.getRowCount() > 0) {
            myCoursesTable.setRowSelectionInterval(0, 0);
            updateCourseDetails();
        } else {
            courseDetailsArea.setText("No courses enrolled.");
        }
    }
    
    /**
     * Update the course details area with information about the selected course.
     */
    private void updateCourseDetails() {
        int selectedRow = myCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            courseDetailsArea.setText("No course selected.");
            return;
        }
        
        String courseName = (String) myCoursesTable.getValueAt(selectedRow, 0);
        String instructorName = (String) myCoursesTable.getValueAt(selectedRow, 1);
        
        // Get grade information for the course
        float numericGrade = 0.0f;
        String letterGrade = "N/A";
        int courseIndex = student.getCourses().indexOf(courseName);
        if (courseIndex != -1) {
            numericGrade = student.getNumericGrades().get(courseIndex);
            letterGrade = student.getLetterGrades().get(courseIndex);
        }
        
        // Get additional course details
        CourseManager manager = mainFrame.getCourseManager();
        Course fullCourse = null;
        for (Course c : manager.getCourses()) {
            if (c.getName().equals(courseName)) {
                fullCourse = c;
                break;
            }
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Course: ").append(courseName).append("\n\n");
        details.append("Instructor(s): ").append(instructorName).append("\n\n");
        
        // Format grade display
        details.append("Grade: ");
        if (numericGrade > 0) {
            details.append(numericGrade).append(" (").append(letterGrade).append(")");
        } else {
            details.append("Not graded yet");
        }
        details.append("\n\n");
        
        // Add course description and other details if available
        if (fullCourse != null) {
            details.append("Start Date: ").append(fullCourse.getStartDate()).append("\n");
            details.append("End Date: ").append(fullCourse.getEndDate()).append("\n");
            details.append("Room: ").append(fullCourse.getRoom()).append("\n");
            details.append("Branch: ").append(fullCourse.getBranch()).append("\n\n");
            details.append("Description:\n").append(fullCourse.getDescription());
        }
        
        courseDetailsArea.setText(details.toString());
    }
    
    /**
     * Helper method to convert numeric grade to letter grade.
     * 
     * @param grade The numeric grade.
     * @return The letter grade.
     */
    private String convertToLetterGrade(float grade) {
        if (grade >= 90) return "A";
        else if (grade >= 80) return "B";
        else if (grade >= 70) return "C";
        else if (grade >= 60) return "D";
        else if (grade > 0) return "F";
        else return "N/A";
    }
    
    /**
     * Submit feedback for a specific course.
     * 
     * @param courseComboBox The combo box containing the selected course.
     */
    private void submitFeedback(JComboBox<String> courseComboBox) {
        if (student == null) {
            return;
        }
        
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        String feedback = feedbackArea.getText().trim();
        
        if (selectedCourse == null || selectedCourse.isEmpty() || selectedCourse.equals("No courses available")) {
            JOptionPane.showMessageDialog(this, 
                "Please select a valid course.",
                "Submission Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (feedback.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Feedback cannot be empty",
                "Submission Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Find instructor for the selected course
        String instructorName = findInstructorForCourse(selectedCourse);
        
        // Save the feedback
        boolean success = DataManager.saveFeedback(
            student.getStudentName(), 
            student.getUsername(), 
            selectedCourse, 
            instructorName, 
            feedback
        );
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
            feedbackArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Submission failed. Please try again later.",
                "Submission Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Find the instructor for a course.
     * 
     * @param courseName The name of the course.
     * @return The name of the instructor.
     */
    private String findInstructorForCourse(String courseName) {
        CourseManager manager = mainFrame.getCourseManager();
        ArrayList<Course> courses = manager.getCourses();
        
        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                return course.getInstructorName();
            }
        }
        
        return "Unknown";
    }
    
    /**
     * Update the course dropdown in the feedback tab.
     */
    private void updateCourseDropdown() {
        if (student == null) {
            return;
        }
        
        // Find the JComboBox in the feedback panel
        for (Component comp : feedbackPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JComboBox) {
                        JComboBox<String> courseComboBox = (JComboBox<String>) innerComp;
                        courseComboBox.removeAllItems();
                        
                        ArrayList<String> courses = student.getCourses();
                        
                        if (courses.isEmpty()) {
                            courseComboBox.addItem("No courses available");
                            courseComboBox.setEnabled(false);
                        } else {
                            courseComboBox.setEnabled(true);
                            for (String courseName : courses) {
                                courseComboBox.addItem(courseName);
                            }
                        }
                        
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Update the profile fields.
     */
    private void updateProfileFields() {
        if (student == null) {
            return;
        }
        
        usernameField.setText(student.getUsername());
        passwordField.setText(student.getPassword());
        nameField.setText(student.getStudentName());
    }
    
    /**
     * Register for a course.
     */
    private void registerForCourse() {
        if (student == null) {
            return;
        }
        
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a course to register for",
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String courseName = (String) availableCoursesTable.getValueAt(selectedRow, 0);
        
        try {
            student.addCourse(courseName, 0.0f, "N/A");
            mainFrame.getAdmin().saveUsers(); // Save the changes
            JOptionPane.showMessageDialog(this, "Successfully registered for " + courseName);
            updateAvailableCoursesTable();
            updateMyCoursesTable();
            updateCourseDropdown();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                "Registration failed: " + e.getMessage(),
                "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update profile information.
     */
    private void updateProfile() {
        if (student == null) {
            return;
        }
        
        String newUsername = usernameField.getText().trim();
        String newPassword = new String(passwordField.getPassword()).trim();
        String newName = nameField.getText().trim();
        
        if (newUsername.isEmpty() || newPassword.isEmpty() || newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "All fields are required",
                "Update Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            student.setUsername(newUsername);
            student.setPassword(newPassword);
            student.setStudentName(newName);
            
            mainFrame.getAdmin().saveUsers(); // Save the changes
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                "Update failed: " + e.getMessage(),
                "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cell renderer for multi-line text in table cells
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
            setText((value == null) ? "" : value.toString());
            
            return this;
        }
    }
} 