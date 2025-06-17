import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * Instructor panel for the Course Management System GUI.
 */
public class InstructorPanel extends JPanel {
    private CourseManagementGUI mainFrame;
    private JTabbedPane tabbedPane;
    private Instructor instructor;
    
    // Header components
    private JLabel greetingLabel;
    
    // Courses tab components
    private JPanel coursesPanel;
    private JList<String> coursesList;
    private DefaultListModel<String> coursesListModel;
    private JTextArea enrolledStudentsArea;
    
    // Grades tab components
    private JPanel gradesPanel;
    private JComboBox<String> studentCombo;
    private Map<String, String> studentDisplayToUsername; // Maps display names to usernames
    private JComboBox<String> courseCombo;
    private JTextField numericGradeField;
    private JTextField letterGradeField;
    private JButton addGradeButton;
    
    // Materials tab components
    private JPanel materialsPanel;
    private JComboBox<String> materialsCourseCombo;
    private JTextField materialDescriptionField;
    private JComboBox<String> materialTypeCombo;
    private JButton uploadButton;
    
    /**
     * Constructor for the InstructorPanel.
     * 
     * @param mainFrame The main GUI frame.
     */
    public InstructorPanel(CourseManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Initialize the student name to username map
        studentDisplayToUsername = new HashMap<>();
        
        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Instructor Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        // Add greeting label (will be populated in setInstructor)
        greetingLabel = new JLabel("", JLabel.CENTER);
        greetingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(greetingLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create tabs
        createCoursesTab();
        createGradesTab();
        createMaterialsTab();
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("My Courses", coursesPanel);
        tabbedPane.addTab("Add Grades", gradesPanel);
        tabbedPane.addTab("Upload Materials", materialsPanel);
        
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
     * Set the instructor for this panel.
     * 
     * @param instructor The instructor.
     */
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
        
        // Update greeting message
        greetingLabel.setText("Hello, " + instructor.getInstructorName() + "!");
        
        // Update tabs with instructor data
        updateCoursesTab();
        updateCourseList();
        updateMaterialsCourseList();
    }
    
    /**
     * Create the courses tab.
     */
    private void createCoursesTab() {
        coursesPanel = new JPanel(new BorderLayout());
        
        // Create split pane for courses and enrolled students
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left panel - Course list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("My Assigned Courses"));
        
        coursesListModel = new DefaultListModel<>();
        coursesList = new JList<>(coursesListModel);
        coursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        coursesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateEnrolledStudents();
            }
        });
        
        JScrollPane coursesScrollPane = new JScrollPane(coursesList);
        leftPanel.add(coursesScrollPane, BorderLayout.CENTER);
        
        // Right panel - Enrolled students
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Enrolled Students"));
        
        enrolledStudentsArea = new JTextArea();
        enrolledStudentsArea.setEditable(false);
        enrolledStudentsArea.setLineWrap(true);
        enrolledStudentsArea.setWrapStyleWord(true);
        
        JScrollPane studentsScrollPane = new JScrollPane(enrolledStudentsArea);
        rightPanel.add(studentsScrollPane, BorderLayout.CENTER);
        
        // Add panels to split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(300);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            updateCoursesTab();
            updateEnrolledStudents();
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        coursesPanel.add(splitPane, BorderLayout.CENTER);
        coursesPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the grades tab.
     */
    private void createGradesTab() {
        gradesPanel = new JPanel(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Course dropdown (first, so instructor selects course before student)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Course:"), gbc);
        
        gbc.gridx = 1;
        courseCombo = new JComboBox<>();
        courseCombo.addActionListener(e -> {
            // Update student list with only enrolled students
            updateStudentListForSelectedCourse();
        });
        formPanel.add(courseCombo, gbc);
        
        // Student dropdown
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Select Student:"), gbc);
        
        gbc.gridx = 1;
        studentCombo = new JComboBox<>();
        formPanel.add(studentCombo, gbc);
        
        // Numeric Grade field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Numeric Grade (0-100):"), gbc);
        
        gbc.gridx = 1;
        numericGradeField = new JTextField(10);
        formPanel.add(numericGradeField, gbc);
        
        // Letter Grade field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Letter Grade:"), gbc);
        
        gbc.gridx = 1;
        letterGradeField = new JTextField(10);
        formPanel.add(letterGradeField, gbc);
        
        // Add button
        addGradeButton = new JButton("Add Grade");
        addGradeButton.addActionListener(e -> addGrade());
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh Lists");
        refreshButton.addActionListener(e -> {
            updateCourseList();
            updateStudentListForSelectedCourse();
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addGradeButton);
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        gradesPanel.add(formPanel, BorderLayout.CENTER);
        gradesPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the materials tab.
     */
    private void createMaterialsTab() {
        materialsPanel = new JPanel(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Course dropdown
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Course:"), gbc);
        
        gbc.gridx = 1;
        materialsCourseCombo = new JComboBox<>();
        formPanel.add(materialsCourseCombo, gbc);
        
        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        materialDescriptionField = new JTextField(20);
        formPanel.add(materialDescriptionField, gbc);
        
        // Material type dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Material Type:"), gbc);
        
        gbc.gridx = 1;
        String[] materialTypes = {"PDF", "Video", "Document", "Presentation", "Other"};
        materialTypeCombo = new JComboBox<>(materialTypes);
        formPanel.add(materialTypeCombo, gbc);
        
        // Upload button
        uploadButton = new JButton("Upload Material");
        uploadButton.addActionListener(e -> uploadMaterial());
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateMaterialsCourseList());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(uploadButton);
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        materialsPanel.add(formPanel, BorderLayout.CENTER);
        materialsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Update the courses tab with current instructor's courses.
     */
    private void updateCoursesTab() {
        if (instructor == null) {
            return;
        }
        
        coursesListModel.clear();
        
        // Get assigned courses and handle different formats
        String assignedCoursesStr = instructor.getAssignedCourses();
        System.out.println("Raw assigned courses: " + assignedCoursesStr);
        
        // Split by comma, and handle potential formats
        String[] courses = assignedCoursesStr.split(",");
        
        for (String course : courses) {
            String trimmed = course.trim();
            if (!trimmed.equalsIgnoreCase("None") && !trimmed.isEmpty()) {
                coursesListModel.addElement(trimmed);
                System.out.println("Added course to list: " + trimmed);
            }
        }
        
        // Debug output
        System.out.println("Total courses added to list: " + coursesListModel.size());
        
        // Clear enrolled students area if no course is selected
        if (coursesList.getSelectedIndex() == -1 && coursesListModel.size() > 0) {
            coursesList.setSelectedIndex(0);
        } else if (coursesListModel.size() == 0) {
            enrolledStudentsArea.setText("No courses assigned.");
        }
    }
    
    /**
     * Update the enrolled students display for the selected course.
     */
    private void updateEnrolledStudents() {
        String selectedCourse = coursesList.getSelectedValue();
        if (selectedCourse == null) {
            enrolledStudentsArea.setText("No course selected.");
            return;
        }
        
        Admin admin = mainFrame.getAdmin();
        java.util.List<User> users = admin.getUsers();
        
        StringBuilder sb = new StringBuilder();
        sb.append("Course: ").append(selectedCourse).append("\n");
        sb.append("Enrolled Students:\n");
        
        boolean foundStudents = false;
        
        for (User user : users) {
            if (user instanceof Student) {
                Student student = (Student) user;
                ArrayList<String> courses = student.getCourses();
                
                if (courses.contains(selectedCourse)) {
                    foundStudents = true;
                    sb.append("- ").append(student.getStudentName()).append("\n");
                }
            }
        }
        
        if (!foundStudents) {
            sb.append("No students enrolled in this course.");
        }
        
        enrolledStudentsArea.setText(sb.toString());
    }
    
    /**
     * Update the student dropdown list for only students enrolled in selected course.
     */
    private void updateStudentListForSelectedCourse() {
        String selectedCourse = (String) courseCombo.getSelectedItem();
        if (selectedCourse == null) {
            return;
        }
        
        Admin admin = mainFrame.getAdmin();
        studentCombo.removeAllItems();
        studentDisplayToUsername.clear();
        
        for (User user : admin.getUsers()) {
            if (user instanceof Student) {
                Student student = (Student) user;
                ArrayList<String> courses = student.getCourses();
                
                if (courses.contains(selectedCourse)) {
                    String displayName = student.getStudentName();
                    studentDisplayToUsername.put(displayName, student.getUsername());
                    studentCombo.addItem(displayName);
                }
            }
        }
    }
    
    /**
     * Update the course dropdown list for grades tab.
     */
    private void updateCourseList() {
        if (instructor == null) {
            return;
        }
        
        courseCombo.removeAllItems();
        
        String[] courses = instructor.getAssignedCourses().split(",");
        for (String course : courses) {
            String trimmed = course.trim();
            if (!trimmed.equalsIgnoreCase("None") && !trimmed.isEmpty()) {
                courseCombo.addItem(trimmed);
            }
        }
        
        // If courses are available, populate student list for first course
        if (courseCombo.getItemCount() > 0) {
            updateStudentListForSelectedCourse();
        }
    }
    
    /**
     * Update the course dropdown list for materials tab.
     */
    private void updateMaterialsCourseList() {
        if (instructor == null) {
            return;
        }
        
        materialsCourseCombo.removeAllItems();
        
        String[] courses = instructor.getAssignedCourses().split(",");
        for (String course : courses) {
            String trimmed = course.trim();
            if (!trimmed.equalsIgnoreCase("None") && !trimmed.isEmpty()) {
                materialsCourseCombo.addItem(trimmed);
            }
        }
    }
    
    /**
     * Add a grade for a student.
     */
    private void addGrade() {
        String selectedStudentDisplay = (String) studentCombo.getSelectedItem();
        if (selectedStudentDisplay == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a student",
                "Add Grade Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String studentUsername = studentDisplayToUsername.get(selectedStudentDisplay);
        String courseName = (String) courseCombo.getSelectedItem();
        String numericGradeText = numericGradeField.getText().trim();
        String letterGrade = letterGradeField.getText().trim();
        
        if (courseName == null || numericGradeText.isEmpty() || letterGrade.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select a course and enter both numeric and letter grades",
                "Add Grade Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            float numericGrade = Float.parseFloat(numericGradeText);
            if (numericGrade < 0 || numericGrade > 100) {
                JOptionPane.showMessageDialog(this, 
                    "Numeric grade must be between 0 and 100",
                    "Add Grade Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Admin admin = mainFrame.getAdmin();
            boolean found = false;
            
            for (User user : admin.getUsers()) {
                if (user instanceof Student && user.getUsername().equals(studentUsername)) {
                    found = true;
                    Student student = (Student) user;
                    
                    // Check if student is registered for this course
                    ArrayList<String> studentCourses = student.getCourses();
                    if (!studentCourses.contains(courseName)) {
                        JOptionPane.showMessageDialog(this, 
                            "Student is not enrolled in this course",
                            "Add Grade Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Set grade for the course
                    int courseIndex = studentCourses.indexOf(courseName);
                    if (courseIndex != -1) {
                        student.getNumericGrades().set(courseIndex, numericGrade);
                        student.getLetterGrades().set(courseIndex, letterGrade);
                        
                        // Save the changes
                        admin.saveUsers();
                        
                        // Also save to grades.txt with proper formatting
                        boolean gradesSaved = DataManager.saveGrade(
                            student.getStudentName(),
                            student.getUsername(),
                            courseName,
                            instructor.getInstructorName(),
                            numericGrade,
                            letterGrade
                        );
                        
                        if (gradesSaved) {
                            JOptionPane.showMessageDialog(this, 
                                "Grade added successfully for " + student.getStudentName(),
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                            
                            // Clear input fields
                            numericGradeField.setText("");
                            letterGradeField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Grade added to student record but could not be saved to grades file.",
                                "Partial Success", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    break;
                }
            }
            
            if (!found) {
                JOptionPane.showMessageDialog(this, 
                    "Student not found with username: " + studentUsername,
                    "Add Grade Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid numeric grade format. Please enter a number.",
                "Add Grade Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Upload course material (simulation).
     */
    private void uploadMaterial() {
        String courseName = (String) materialsCourseCombo.getSelectedItem();
        String description = materialDescriptionField.getText().trim();
        String type = (String) materialTypeCombo.getSelectedItem();
        
        if (courseName == null || description.isEmpty() || type == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a course, enter a description, and select a material type",
                "Upload Material Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, 
            "Material '" + description + "' (" + type + ") uploaded for course '" + courseName + "'\n" +
            "Note: This is a simulation. Materials are not actually stored.",
            "Upload Successful", JOptionPane.INFORMATION_MESSAGE);
            
        materialDescriptionField.setText("");
    }
} 