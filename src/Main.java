import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for the Course Management System. Handles user interactions through
 * a console-based menu system.
 */
public class Main {

    /**
     * Main method to run the course management system.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Launch the GUI version of the application
        javax.swing.SwingUtilities.invokeLater(() -> {
            new CourseManagementGUI();
        });
        
        // The following code is for the console version and is commented out
        /*
        // Ensure data directory exists
        if (!DataManager.ensureDataDirectoryExists()) {
            System.out.println("Error: Could not create data directory. Exiting...");
            return;
        }
        
        // Use try-with-resources to ensure Scanner is closed
        try (Scanner input = new Scanner(System.in)) {
            // Initialize admin
            Admin admin = new Admin("admin", "123", "Admin Name");
            
            // Load initial admin-created users from file (if any)
            try {
                admin.loadUserData();
                System.out.println("User data loaded successfully.");
            } catch (Exception e) {
                System.out.println("Warning: Could not load user data. Starting with empty user list.");
                System.out.println("Error details: " + e.getMessage());
            }
            
            // Main program loop
            boolean exit = false;
            while (!exit) {
                exit = displayMainMenu(input, admin);
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        */
    }
    
    /**
     * Displays the main menu and handles user choice.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     * @return true if the user wants to exit the program, false otherwise.
     */
    private static boolean displayMainMenu(Scanner input, Admin admin) {
        printHeader("Course Management System");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput(input);
        
        switch (choice) {
            case 1 -> handleRegistration(input, admin);
            case 2 -> handleLogin(input, admin);
            case 3 -> {
                System.out.println("\nThank you for using Course Management System. Goodbye!");
                return true;
            }
            default -> System.out.println("\nInvalid choice. Please try again.");
        }
        
        return false;
    }
    
    /**
     * Handles the registration process.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void handleRegistration(Scanner input, Admin admin) {
        printHeader("Registration");
        System.out.println("Select user type:");
        System.out.println("1. Admin");
        System.out.println("2. Instructor");
        System.out.println("3. Student");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput(input);
        
        switch (choice) {
            case 0 -> { return; }
            case 1 -> registerAdmin(input, admin);
            case 2 -> registerInstructor(input, admin);
            case 3 -> registerStudent(input, admin);
            default -> System.out.println("\nInvalid choice. Returning to main menu.");
        }
    }
    
    /**
     * Registers a new admin (only existing admin can add new admins).
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void registerAdmin(Scanner input, Admin admin) {
        printHeader("Admin Registration");
        
        System.out.print("Enter new admin username: ");
        String newUsername = input.nextLine();
        System.out.print("Enter new admin password: ");
        String newPassword = input.nextLine();
        System.out.print("Enter new admin name: ");
        String newName = input.nextLine();
        
        try {
            Admin newAdmin = new Admin(newUsername, newPassword, newName);
            admin.addUser(newAdmin);
            System.out.println("\nNew admin registered successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nRegistration failed: " + e.getMessage());
        }
        
        waitForEnter(input);
    }
    
    /**
     * Registers a new instructor.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void registerInstructor(Scanner input, Admin admin) {
        printHeader("Instructor Registration");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();
        System.out.print("Enter full name: ");
        String instructorName = input.nextLine();
        
        // Check if username already exists
        for (User user : admin.getUsers()) {
            if (user.getUsername().equals(username)) {
                System.out.println("\nUsername already exists. Please choose a different username.");
                waitForEnter(input);
                return;
            }
        }
        
        // Show available courses or "None" option
        CourseManager courseManager = new CourseManager();
        ArrayList<Course> courses = courseManager.getCourses();
        
        System.out.println("\nSelect specialization:");
        System.out.println("1. None");
        
        if (!courses.isEmpty()) {
            for (int i = 0; i < courses.size(); i++) {
                System.out.println((i + 2) + ". " + courses.get(i).getName());
            }
        }
        
        System.out.print("Choose an option: ");
        int choice = getIntInput(input);
        
        String specialization = "None";
        
        if (choice > 1 && choice <= courses.size() + 1) {
            specialization = courses.get(choice - 2).getName();
        } else if (choice != 1) {
            System.out.println("\nInvalid choice. Setting specialization to 'None'.");
        }
        
        try {
            Instructor newInstructor = new Instructor(username, password, instructorName, specialization);
            admin.addUser(newInstructor);
            System.out.println("\nRegistration successful! You can now login as an instructor.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nRegistration failed: " + e.getMessage());
        }
        
        waitForEnter(input);
    }
    
    /**
     * Registers a new student.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void registerStudent(Scanner input, Admin admin) {
        printHeader("Student Registration");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();
        System.out.print("Enter full name: ");
        String studentName = input.nextLine();
        
        try {
            Student newStudent = new Student(username, password, studentName);
            admin.addUser(newStudent);
            System.out.println("\nRegistration successful! You can now login as a student.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nRegistration failed: " + e.getMessage());
        }
        
        waitForEnter(input);
    }
    
    /**
     * Handles the login process.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void handleLogin(Scanner input, Admin admin) {
        printHeader("Login");
        System.out.println("Select user type:");
        System.out.println("1. Admin");
        System.out.println("2. Instructor");
        System.out.println("3. Student");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput(input);
        
        switch (choice) {
            case 0 -> { return; }
            case 1 -> loginAdmin(input, admin);
            case 2 -> loginInstructor(input, admin);
            case 3 -> loginStudent(input, admin);
            default -> System.out.println("\nInvalid choice. Returning to main menu.");
        }
    }
    
    /**
     * Handles admin login.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void loginAdmin(Scanner input, Admin admin) {
        printHeader("Admin Login");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();
        
        if (admin.authenticate(username, password)) {
            System.out.println("\nLogin successful. Welcome, " + admin.getAdminName());
            displayAdminMenu(input, admin);
        } else {
            // Check other admin users
            boolean found = false;
            for (User user : admin.getUsers()) {
                if (user instanceof Admin && user.authenticate(username, password)) {
                    found = true;
                    System.out.println("\nLogin successful. Welcome, " + ((Admin) user).getAdminName());
                    displayAdminMenu(input, (Admin) user);
                    break;
                }
            }
            
            if (!found) {
                System.out.println("\nInvalid credentials. Login failed.");
                waitForEnter(input);
            }
        }
    }
    
    /**
     * Handles instructor login.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void loginInstructor(Scanner input, Admin admin) {
        printHeader("Instructor Login");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();
        
        boolean found = false;
        for (User user : admin.getUsers()) {
            if (user instanceof Instructor && user.authenticate(username, password)) {
                found = true;
                System.out.println("\nLogin successful. Welcome, " + ((Instructor) user).getInstructorName());
                displayInstructorMenu(input, admin, (Instructor) user);
                break;
            }
        }
        
        if (!found) {
            System.out.println("\nInvalid credentials or instructor not registered. Login failed.");
            waitForEnter(input);
        }
    }
    
    /**
     * Handles student login.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void loginStudent(Scanner input, Admin admin) {
        printHeader("Student Login");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();
        
        boolean found = false;
        for (User user : admin.getUsers()) {
            if (user instanceof Student && user.authenticate(username, password)) {
                found = true;
                System.out.println("\nLogin successful. Welcome, " + ((Student) user).getStudentName());
                displayStudentMenu(input, admin, (Student) user);
                break;
            }
        }
        
        if (!found) {
            System.out.println("\nInvalid credentials or student not registered. Login failed.");
            waitForEnter(input);
        }
    }
    
    /**
     * Displays the admin menu and handles admin operations.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void displayAdminMenu(Scanner input, Admin admin) {
        CourseManager manager = new CourseManager();
        boolean logout = false;
        
        // Add greeting message at the top of Admin Panel
        printHeader("Welcome, " + admin.getAdminName() + "!");
        
        while (!logout) {
            printHeader("Admin Menu");
            System.out.println("1. Manage Courses");
            System.out.println("2. View All Users");
            System.out.println("3. Assign Instructor to Course");
            System.out.println("4. User Management");
            System.out.println("5. View Courses Dashboard");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            
            int choice = getIntInput(input);
            
            switch (choice) {
                case 0 -> {
                    System.out.println("\nLogged out successfully.");
                    logout = true;
                }
                case 1 -> courseMenu(manager, input);
                case 2 -> {
                    admin.displayAllUsers();
                    waitForEnter(input);
                }
                case 3 -> assignInstructorToCourse(input, admin, manager);
                case 4 -> userManagementMenu(input, admin);
                case 5 -> viewCoursesDashboard(manager);
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Displays the user management menu and handles operations.
     *
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void userManagementMenu(Scanner input, Admin admin) {
        boolean back = false;
        
        while (!back) {
            printHeader("User Management");
            System.out.println("1. Add New User");
            System.out.println("2. Edit Existing User");
            System.out.println("3. Delete User");
            System.out.println("4. List All Users");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Choose an option: ");
            
            int choice = getIntInput(input);
            
            switch (choice) {
                case 0 -> back = true;
                case 1 -> addNewUser(input, admin);
                case 2 -> editExistingUser(input, admin);
                case 3 -> deleteUser(input, admin);
                case 4 -> {
                    admin.displayAllUsers();
                    waitForEnter(input);
                }
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Handles adding a new user.
     *
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void addNewUser(Scanner input, Admin admin) {
        printHeader("Add New User");
        System.out.println("Select user type:");
        System.out.println("1. Admin");
        System.out.println("2. Instructor");
        System.out.println("3. Student");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput(input);
        
        switch (choice) {
            case 0 -> { return; }
            case 1 -> addNewAdmin(input, admin);
            case 2 -> addNewInstructor(input, admin);
            case 3 -> addNewStudent(input, admin);
            default -> System.out.println("\nInvalid choice. Returning to previous menu.");
        }
    }
    
    /**
     * Handles adding a new admin.
     *
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void addNewAdmin(Scanner input, Admin admin) {
        printHeader("Add New Admin");
        
        // Check for duplicate username first
        System.out.print("Enter username: ");
        String username = input.nextLine();
        
        for (User user : admin.getUsers()) {
            if (user.getUsername().equals(username)) {
                System.out.println("\nUsername already exists. Please choose a different username.");
                waitForEnter(input);
                return;
            }
        }
        
        System.out.print("Enter password: ");
        String password = input.nextLine();
        
        System.out.print("Enter admin name: ");
        String adminName = input.nextLine();
        
        try {
            Admin newAdmin = new Admin(username, password, adminName);
            admin.addUser(newAdmin);
            System.out.println("\nNew admin added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError adding admin: " + e.getMessage());
        }
        
        waitForEnter(input);
    }
    
    /**
     * Handles adding a new instructor.
     *
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void addNewInstructor(Scanner input, Admin admin) {
        // This leverages the existing registerInstructor method
        registerInstructor(input, admin);
    }
    
    /**
     * Handles adding a new student.
     *
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void addNewStudent(Scanner input, Admin admin) {
        printHeader("Add New Student");
        
        // Check for duplicate username first
        System.out.print("Enter username: ");
        String username = input.nextLine();
        
        for (User user : admin.getUsers()) {
            if (user.getUsername().equals(username)) {
                System.out.println("\nUsername already exists. Please choose a different username.");
                waitForEnter(input);
                return;
            }
        }
        
        System.out.print("Enter password: ");
        String password = input.nextLine();
        
        System.out.print("Enter student name: ");
        String studentName = input.nextLine();
        
        try {
            Student newStudent = new Student(username, password, studentName);
            
            // Ask if the user wants to add courses
            System.out.print("\nDo you want to add courses for this student? (y/n): ");
            String addCourses = input.nextLine().trim().toLowerCase();
            
            if (addCourses.equals("y") || addCourses.equals("yes")) {
                CourseManager manager = new CourseManager();
                ArrayList<Course> courses = manager.getCourses();
                
                if (courses.isEmpty()) {
                    System.out.println("No courses available to add.");
                } else {
                    System.out.println("\nAvailable courses:");
                    for (int i = 0; i < courses.size(); i++) {
                        System.out.println((i + 1) + ". " + courses.get(i).getName());
                    }
                    
                    System.out.print("\nHow many courses would you like to add? ");
                    int numCourses = getIntInput(input);
                    
                    for (int i = 0; i < numCourses; i++) {
                        System.out.print("Enter course number " + (i + 1) + ": ");
                        int courseNum = getIntInput(input);
                        
                        if (courseNum > 0 && courseNum <= courses.size()) {
                            String courseName = courses.get(courseNum - 1).getName();
                            newStudent.addCourse(courseName, 0.0f, "N/A");
                            System.out.println("Added course: " + courseName);
                        } else {
                            System.out.println("Invalid course number. Skipping.");
                        }
                    }
                }
            }
            
            admin.addUser(newStudent);
            System.out.println("\nNew student added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError adding student: " + e.getMessage());
        }
        
        waitForEnter(input);
    }
    
    /**
     * Handles editing an existing user.
     *
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void editExistingUser(Scanner input, Admin admin) {
        printHeader("Edit Existing User");
        
        List<User> users = admin.getUsers();
        if (users.isEmpty()) {
            System.out.println("No users found to edit.");
            waitForEnter(input);
            return;
        }
        
        // Display all users with numbers
        System.out.println("Available users:");
        int count = 1;
        for (User user : users) {
            String userType = user instanceof Admin ? "Admin" : 
                              user instanceof Instructor ? "Instructor" : "Student";
            String name = user instanceof Admin ? ((Admin) user).getAdminName() : 
                          user instanceof Instructor ? ((Instructor) user).getInstructorName() : 
                          ((Student) user).getStudentName();
            System.out.println(count + ". " + user.getUsername() + " (" + userType + ": " + name + ")");
            count++;
        }
        
        System.out.print("\nEnter user number to edit (0 to cancel): ");
        int userNum = getIntInput(input);
        
        if (userNum <= 0 || userNum > users.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            waitForEnter(input);
            return;
        }
        
        User selectedUser = users.get(userNum - 1);
        admin.updateUser(selectedUser.getUsername(), input);
        
        waitForEnter(input);
    }
    
    /**
     * Handles deleting a user.
     *
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     */
    private static void deleteUser(Scanner input, Admin admin) {
        printHeader("Delete User");
        
        List<User> users = admin.getUsers();
        if (users.isEmpty()) {
            System.out.println("No users found to delete.");
            waitForEnter(input);
            return;
        }
        
        // Display all users with numbers
        System.out.println("Available users:");
        int count = 1;
        for (User user : users) {
            String userType = user instanceof Admin ? "Admin" : 
                              user instanceof Instructor ? "Instructor" : "Student";
            String name = user instanceof Admin ? ((Admin) user).getAdminName() : 
                          user instanceof Instructor ? ((Instructor) user).getInstructorName() : 
                          ((Student) user).getStudentName();
            System.out.println(count + ". " + user.getUsername() + " (" + userType + ": " + name + ")");
            count++;
        }
        
        System.out.print("\nEnter user number to delete (0 to cancel): ");
        int userNum = getIntInput(input);
        
        if (userNum <= 0 || userNum > users.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            waitForEnter(input);
            return;
        }
        
        User selectedUser = users.get(userNum - 1);
        
        // Confirm deletion
        System.out.print("Are you sure you want to delete " + selectedUser.getUsername() + "? (y/n): ");
        String confirm = input.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            admin.deleteUser(selectedUser.getUsername());
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
        
        waitForEnter(input);
    }
    
    /**
     * Displays the instructor menu and handles instructor operations.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance for user management.
     * @param instructor The Instructor instance.
     */
    private static void displayInstructorMenu(Scanner input, Admin admin, Instructor instructor) {
        CourseManager manager = new CourseManager();
        boolean logout = false;
        
        while (!logout) {
            printHeader("Instructor Menu");
            System.out.println("1. View Assigned Courses");
            System.out.println("2. Add Grades");
            System.out.println("3. Upload Course Materials");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            
            int choice = getIntInput(input);
            
            switch (choice) {
                case 0 -> {
                    System.out.println("\nLogged out successfully.");
                    logout = true;
                }
                case 1 -> viewAssignedCourses(instructor);
                case 2 -> admin.add_grade();
                case 3 -> uploadCourseMaterials(input, instructor);
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
            
            if (!logout && choice > 0 && choice <= 3) {
                waitForEnter(input);
            }
        }
    }
    
    /**
     * Displays the assigned courses for an instructor.
     * 
     * @param instructor The Instructor instance.
     */
    private static void viewAssignedCourses(Instructor instructor) {
        printHeader("Assigned Courses");
        
        String[] courses = instructor.getAssignedCourses().split(",");
        
        if (courses.length == 0 || (courses.length == 1 && courses[0].trim().equalsIgnoreCase("None"))) {
            System.out.println("You have no assigned courses.");
        } else {
            for (int i = 0; i < courses.length; i++) {
                String course = courses[i].trim();
                if (!course.equalsIgnoreCase("None")) {
                    System.out.println((i + 1) + ". " + course);
                }
            }
        }
    }
    
    /**
     * Simulates uploading course materials (placeholder).
     * 
     * @param input The Scanner for user input.
     * @param instructor The Instructor instance.
     */
    private static void uploadCourseMaterials(Scanner input, Instructor instructor) {
        printHeader("Upload Course Materials");
        
        viewAssignedCourses(instructor);
        
        System.out.print("\nEnter course name to upload materials for: ");
        String courseName = input.nextLine();
        
        System.out.print("Enter material description: ");
        String description = input.nextLine();
        
        System.out.print("Enter material type (pdf, video, etc.): ");
        String type = input.nextLine();
        
        // This is a placeholder; in a real application, you would save the materials
        System.out.println("\nMaterial '" + description + "' (" + type + ") uploaded for course '" + 
                          courseName + "'");
        System.out.println("Note: This is a simulation. Materials are not actually stored.");
    }
    
    /**
     * Displays the student menu and handles student operations.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance for user management.
     * @param student The Student instance.
     */
    private static void displayStudentMenu(Scanner input, Admin admin, Student student) {
        CourseManager manager = new CourseManager();
        boolean logout = false;
        
        // Add greeting message at the top of Student Panel
        printHeader("Welcome, " + student.getStudentName() + "!");
        
        while (!logout) {
            printHeader("Student Menu");
            System.out.println("1. View Available Courses");
            System.out.println("2. Register for a Course");
            System.out.println("3. View My Courses");
            System.out.println("4. View Grades");
            System.out.println("5. Submit Feedback");
            System.out.println("6. Update Personal Information");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            
            int choice = getIntInput(input);
            
            switch (choice) {
                case 0 -> {
                    System.out.println("\nLogged out successfully.");
                    logout = true;
                }
                case 1 -> {
                    manager.showAllCourses();
                    waitForEnter(input);
                }
                case 2 -> registerForCourse(input, admin, manager, student);
                case 3 -> {
                    student.viewCourses();
                    waitForEnter(input);
                }
                case 4 -> {
                    student.viewGrades();
                    waitForEnter(input);
                }
                case 5 -> {
                    submitFeedback(input, student);
                    waitForEnter(input);
                }
                case 6 -> updateStudentInfo(input, admin, student);
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Registers a student for a course.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance for saving changes.
     * @param manager The CourseManager instance.
     * @param student The Student instance.
     */
    private static void registerForCourse(Scanner input, Admin admin, CourseManager manager, Student student) {
        printHeader("Register for Course");
        
        manager.showCoursesByNumber();
        
        if (manager.getCourses().isEmpty()) {
            System.out.println("No courses available for registration.");
            waitForEnter(input);
            return;
        }
        
        System.out.print("\nEnter course number: ");
        int courseNumber = getIntInput(input);
        
        String courseName = manager.getCourseNameByIndex(courseNumber - 1);
        if (courseName == null) {
            System.out.println("\nInvalid course number.");
        } else if (student.getCourses().contains(courseName)) {
            System.out.println("\nYou are already registered for this course.");
        } else {
            try {
                student.addCourse(courseName, 0.0f, "N/A");
                System.out.println("\nSuccessfully registered for " + courseName);
                admin.saveUsers(); // Save the changes
            } catch (IllegalArgumentException e) {
                System.out.println("\nError: " + e.getMessage());
            }
        }
        
        waitForEnter(input);
    }
    
    /**
     * Allows student to submit feedback.
     * 
     * @param input The Scanner for user input.
     * @param student The Student instance.
     */
    private static void submitFeedback(Scanner input, Student student) {
        printHeader("Submit Feedback");
        
        ArrayList<String> enrolledCourses = student.getCourses();
        if (enrolledCourses.isEmpty()) {
            System.out.println("You are not enrolled in any courses.");
            return;
        }
        
        // Display enrolled courses
        System.out.println("Select a course to provide feedback for:");
        for (int i = 0; i < enrolledCourses.size(); i++) {
            System.out.println((i + 1) + ". " + enrolledCourses.get(i));
        }
        
        // Get course selection
        System.out.print("Enter course number: ");
        int courseIndex = getIntInput(input) - 1;
        
        if (courseIndex < 0 || courseIndex >= enrolledCourses.size()) {
            System.out.println("Invalid course selection.");
            return;
        }
        
        String selectedCourse = enrolledCourses.get(courseIndex);
        
        // Find instructor for the selected course
        String instructorName = findInstructorForCourse(selectedCourse);
        
        // Get feedback message
        System.out.println("You are providing feedback for: " + selectedCourse);
        System.out.print("Enter your feedback: ");
        String feedback = input.nextLine();
        
        // Save feedback to file
        if (DataManager.saveFeedback(student.getStudentName(), student.getUsername(), 
                                    selectedCourse, instructorName, feedback)) {
            System.out.println("Feedback submitted successfully.");
        } else {
            System.out.println("Failed to submit feedback. Please try again later.");
        }
    }
    
    /**
     * Finds the instructor(s) assigned to a course.
     * 
     * @param courseName The name of the course.
     * @return The name of the instructor(s) or "Unknown" if not found.
     */
    private static String findInstructorForCourse(String courseName) {
        CourseManager manager = new CourseManager();
        ArrayList<Course> courses = manager.getCourses();
        
        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                return course.getInstructorName();
            }
        }
        
        return "Unknown";
    }
    
    /**
     * Updates student personal information.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance for saving changes.
     * @param student The Student instance.
     */
    private static void updateStudentInfo(Scanner input, Admin admin, Student student) {
        printHeader("Update Personal Information");
        
        System.out.println("Current Username: " + student.getUsername());
        System.out.println("Current Name: " + student.getStudentName());
        
        System.out.print("\nEnter new username (or leave blank to keep current): ");
        String newUsername = input.nextLine();
        
        System.out.print("Enter new password (or leave blank to keep current): ");
        String newPassword = input.nextLine();
        
        System.out.print("Enter new name (or leave blank to keep current): ");
        String newName = input.nextLine();
        
        try {
            if (!newUsername.trim().isEmpty()) {
                student.setUsername(newUsername);
            }
            
            if (!newPassword.trim().isEmpty()) {
                student.setPassword(newPassword);
            }
            
            if (!newName.trim().isEmpty()) {
                student.setStudentName(newName);
            }
            
            System.out.println("\nPersonal information updated successfully.");
            admin.saveUsers(); // Save the changes
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        
        waitForEnter(input);
    }
    
    /**
     * Displays and handles the course management menu.
     *
     * @param manager The CourseManager instance.
     * @param input The Scanner for user input.
     */
    private static void courseMenu(CourseManager manager, Scanner input) {
        int choice;
        do {
            printHeader("Course Management");
            System.out.println("1. Add Course");
            System.out.println("2. Update Course");
            System.out.println("3. Delete Course");
            System.out.println("4. Show All Courses");
            System.out.println("5. Show All Instructors");
            System.out.println("6. Show Courses Near Start/End");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            
            choice = getIntInput(input);
            
            switch (choice) {
                case 1 -> manager.addCourseFromInput(input);
                case 2 -> manager.updateCourseFromInput(input);
                case 3 -> {
                    System.out.print("Enter course name to delete: ");
                    String name = input.nextLine();
                    if (name.trim().isEmpty()) {
                        System.out.println("Course name cannot be empty.");
                    } else {
                        manager.deleteCourse(name);
                    }
                }
                case 4 -> manager.showAllCourses();
                case 5 -> manager.showAllInstructors();
                case 6 -> manager.showCoursesNearStartOrEnd();
                case 0 -> System.out.println("Returning...");
                default -> System.out.println("Invalid choice.");
            }
            
            if (choice != 0) {
                waitForEnter(input);
            }
        } while (choice != 0);
    }
    
    /**
     * Displays a dashboard of courses that are starting or ending soon.
     * 
     * @param manager The CourseManager instance.
     */
    private static void viewCoursesDashboard(CourseManager manager) {
        printHeader("Courses Dashboard");
        
        System.out.println("Courses starting within 30 days:");
        boolean hasUpcomingCourses = false;
        for (Course course : manager.getCourses()) {
            if (isWithin30Days(course.getStartDate(), true)) {
                System.out.println("- " + course.getName() + " (Starting: " + course.getStartDate() + ")");
                hasUpcomingCourses = true;
            }
        }
        
        if (!hasUpcomingCourses) {
            System.out.println("No courses starting within 30 days.");
        }
        
        System.out.println("\nCourses ending within 30 days:");
        boolean hasEndingCourses = false;
        for (Course course : manager.getCourses()) {
            if (isWithin30Days(course.getEndDate(), false)) {
                System.out.println("- " + course.getName() + " (Ending: " + course.getEndDate() + ")");
                hasEndingCourses = true;
            }
        }
        
        if (!hasEndingCourses) {
            System.out.println("No courses ending within 30 days.");
        }
        
        waitForEnter(new Scanner(System.in));
    }
    
    /**
     * Checks if a date is within 30 days from today.
     * 
     * @param dateString The date string in yyyy-MM-dd format.
     * @param isStartDate true if checking start date, false if checking end date.
     * @return true if the date is within 30 days, false otherwise.
     */
    private static boolean isWithin30Days(String dateString, boolean isStartDate) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateString);
            java.time.LocalDate today = java.time.LocalDate.now();
            
            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, date);
            
            // For start dates, we want dates that are coming up (positive days)
            // For end dates, we want dates that are approaching (positive days)
            return daysUntil >= 0 && daysUntil <= 30;
        } catch (Exception e) {
            // If there's a parsing error, just return false
            return false;
        }
    }
    
    /**
     * Prints a formatted header.
     * 
     * @param title The header title.
     */
    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(" " + title);
        System.out.println("=".repeat(50));
    }
    
    /**
     * Gets integer input from user with error handling.
     * 
     * @param input The Scanner for user input.
     * @return The integer entered by the user or -1 if invalid.
     */
    private static int getIntInput(Scanner input) {
        try {
            int value = input.nextInt();
            input.nextLine(); // Clear buffer
            return value;
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            input.nextLine(); // Clear invalid input
            return -1;
        }
    }
    
    /**
     * Waits for the user to press Enter to continue.
     * 
     * @param input The Scanner for user input.
     */
    private static void waitForEnter(Scanner input) {
        System.out.println("\nPress Enter to continue...");
        input.nextLine();
    }
    
    /**
     * Assigns an instructor to a course.
     * 
     * @param input The Scanner for user input.
     * @param admin The Admin instance.
     * @param manager The CourseManager instance.
     */
    private static void assignInstructorToCourse(Scanner input, Admin admin, CourseManager manager) {
        printHeader("Assign Instructor to Course");
        
        // List all instructors
        System.out.println("Available Instructors:");
        int instructorCount = 0;
        for (User user : admin.getUsers()) {
            if (user instanceof Instructor) {
                instructorCount++;
                System.out.println(instructorCount + ". " + ((Instructor) user).getInstructorName() + 
                                  " (" + user.getUsername() + ")");
            }
        }
        
        if (instructorCount == 0) {
            System.out.println("No instructors available. Please register an instructor first.");
            waitForEnter(input);
            return;
        }
        
        // List all courses
        System.out.println("\nAvailable Courses:");
        manager.showCoursesByNumber();
        
        if (manager.getCourses().isEmpty()) {
            System.out.println("No courses available. Please create a course first.");
            waitForEnter(input);
            return;
        }
        
        // Select instructor
        System.out.print("\nEnter instructor username: ");
        String instructorUsername = input.nextLine();
        
        // Select course
        System.out.print("Enter course number: ");
        int courseNumber = getIntInput(input);
        
        String courseName = manager.getCourseNameByIndex(courseNumber - 1);
        if (courseName == null) {
            System.out.println("\nInvalid course number.");
            waitForEnter(input);
            return;
        }
        
        // Find instructor and assign course
        boolean found = false;
        Instructor targetInstructor = null;
        for (User user : admin.getUsers()) {
            if (user instanceof Instructor && user.getUsername().equals(instructorUsername)) {
                found = true;
                targetInstructor = (Instructor) user;
                targetInstructor.assignCourse(courseName);
                System.out.println("\nCourse '" + courseName + "' assigned to instructor " + 
                                  targetInstructor.getInstructorName());
                break;
            }
        }
        
        if (!found) {
            System.out.println("\nInstructor with username '" + instructorUsername + "' not found.");
            waitForEnter(input);
            return;
        }
        
        // Also update the course record with the instructor's name
        ArrayList<Course> courses = manager.getCourses();
        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                course.addInstructor(targetInstructor.getInstructorName());
                break;
            }
        }
        
        // Save changes
        admin.saveUsers(); // Save instructor changes
        DataManager.saveCourses(courses); // Save course changes
        
        System.out.println("Course and instructor records updated successfully.");
        waitForEnter(input);
    }
}
