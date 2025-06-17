import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin extends User {

    private String adminName;
    private List<User> users;

    // Constructor
    public Admin(String username, String password, String adminName) {
        super(username, password);
        this.adminName = adminName;
        // Initialize empty users list (will be loaded separately)
        this.users = new ArrayList<>();
    }

    // Initializes user data from storage
    public void loadUserData() {
        this.users = DataManager.loadUsers();
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    // Admin Dashboard: Menu for Admin Operations
    public void adminDashboard(Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1. Manage Courses");
            System.out.println("2. View All Users");
            System.out.println("3. Assign Instructor to Course");
            System.out.println("4. Manage Users");
            System.out.println("5. View Courses Dashboard");
            System.out.println("0. Back to Main Menu");

            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
                choice = -1;
            }

            switch (choice) {
                case 0 -> exit = true;
                case 1 -> manageCoursesMenu(scanner);
                case 2 -> displayAllUsers();
                case 3 -> assignInstructorToCourseMenu(scanner);
                case 4 -> manageUsersMenu(scanner);
                case 5 -> viewCoursesDashboard();
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // Manage Users Menu with Add, Update, Delete options
    private void manageUsersMenu(Scanner scanner) {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Manage Users ---");
            System.out.println("1. Add New User");
            System.out.println("2. Update Existing User");
            System.out.println("3. Delete User");
            System.out.println("0. Back to Admin Dashboard");
            
            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
                choice = -1;
            }
            
            switch (choice) {
                case 0 -> back = true;
                case 1 -> addNewUser(scanner);
                case 2 -> updateExistingUser(scanner);
                case 3 -> deleteUserWithConfirmation(scanner);
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    // Add a new user (any type)
    private void addNewUser(Scanner scanner) {
        System.out.println("\n--- Add New User ---");
        System.out.println("Select user type:");
        System.out.println("1. Student");
        System.out.println("2. Instructor");
        System.out.println("3. Admin");
        System.out.println("0. Back");
        
        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }
        
        switch (choice) {
            case 0 -> { return; }
            case 1 -> addNewStudent(scanner);
            case 2 -> addNewInstructor(scanner);
            case 3 -> addNewAdmin(scanner);
            default -> System.out.println("Invalid choice.");
        }
    }
    
    // Add a new admin
    private void addNewAdmin(Scanner scanner) {
        System.out.println("\n--- Add New Admin ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists. Please choose a different one.");
                return;
            }
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter admin name: ");
        String name = scanner.nextLine();
        
        Admin newAdmin = new Admin(username, password, name);
        addUser(newAdmin);
        System.out.println("New admin added successfully!");
    }
    
    // Add a new instructor
    private void addNewInstructor(Scanner scanner) {
        System.out.println("\n--- Add New Instructor ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists. Please choose a different one.");
                return;
            }
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter instructor name: ");
        String name = scanner.nextLine();
        
        // Show available courses for specialization
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
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (Exception e) {
            System.out.println("Invalid input. Setting specialization to 'None'.");
            scanner.nextLine(); // Clear invalid input
            choice = 1;
        }
        
        String specialization = "None";
        
        if (choice > 1 && courses.size() >= choice - 1) {
            specialization = courses.get(choice - 2).getName();
        }
        
        Instructor newInstructor = new Instructor(username, password, name, specialization);
        addUser(newInstructor);
        System.out.println("New instructor added successfully!");
    }
    
    // Add a new student
    private void addNewStudent(Scanner scanner) {
        System.out.println("\n--- Add New Student ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists. Please choose a different one.");
                return;
            }
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        
        Student newStudent = new Student(username, password, name);
        
        // Ask if admin wants to enroll the student in courses
        System.out.print("Do you want to enroll this student in courses? (y/n): ");
        String enrollResponse = scanner.nextLine().toLowerCase();
        
        if (enrollResponse.equals("y") || enrollResponse.equals("yes")) {
            CourseManager courseManager = new CourseManager();
            ArrayList<Course> availableCourses = courseManager.getCourses();
            
            if (availableCourses.isEmpty()) {
                System.out.println("No courses available for enrollment.");
            } else {
                System.out.println("\nAvailable courses:");
                for (int i = 0; i < availableCourses.size(); i++) {
                    System.out.println((i + 1) + ". " + availableCourses.get(i).getName());
                }
                
                System.out.print("How many courses to enroll? ");
                int numCourses;
                try {
                    numCourses = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                } catch (Exception e) {
                    System.out.println("Invalid input. No courses will be added.");
                    scanner.nextLine(); // Clear invalid input
                    numCourses = 0;
                }
                
                for (int i = 0; i < numCourses; i++) {
                    System.out.print("Enter course number " + (i + 1) + ": ");
                    try {
                        int courseIndex = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        
                        if (courseIndex >= 1 && courseIndex <= availableCourses.size()) {
                            String courseName = availableCourses.get(courseIndex - 1).getName();
                            if (!newStudent.getCourses().contains(courseName)) {
                                newStudent.addCourse(courseName, 0.0f, "N/A");
                                System.out.println("Enrolled in: " + courseName);
                            } else {
                                System.out.println("Already enrolled in: " + courseName);
                                i--; // Try again
                            }
                        } else {
                            System.out.println("Invalid course number.");
                            i--; // Try again
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input. Skipping course.");
                        scanner.nextLine(); // Clear invalid input
                    }
                }
            }
        }
        
        addUser(newStudent);
        System.out.println("New student added successfully!");
    }
    
    // Update an existing user (first select user type, then select user)
    private void updateExistingUser(Scanner scanner) {
        System.out.println("\n--- Update Existing User ---");
        System.out.println("Select user type:");
        System.out.println("1. Student");
        System.out.println("2. Instructor");
        System.out.println("3. Admin");
        System.out.println("0. Back");
        
        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }
        
        if (choice == 0) {
            return;
        }
        
        // Get list of users of the selected type
        List<User> selectedUsers = new ArrayList<>();
        String userType = "";
        
        switch (choice) {
            case 1 -> {
                userType = "Student";
                for (User user : users) {
                    if (user instanceof Student) {
                        selectedUsers.add(user);
                    }
                }
            }
            case 2 -> {
                userType = "Instructor";
                for (User user : users) {
                    if (user instanceof Instructor) {
                        selectedUsers.add(user);
                    }
                }
            }
            case 3 -> {
                userType = "Admin";
                for (User user : users) {
                    if (user instanceof Admin) {
                        selectedUsers.add(user);
                    }
                }
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }
        
        if (selectedUsers.isEmpty()) {
            System.out.println("No " + userType + " users found.");
            return;
        }
        
        // Display users of the selected type
        System.out.println("\nAvailable " + userType + " users:");
        for (int i = 0; i < selectedUsers.size(); i++) {
            User user = selectedUsers.get(i);
            String name = "";
            
            if (user instanceof Student) {
                name = ((Student) user).getStudentName();
            } else if (user instanceof Instructor) {
                name = ((Instructor) user).getInstructorName();
            } else if (user instanceof Admin) {
                name = ((Admin) user).getAdminName();
            }
            
            System.out.println((i + 1) + ". " + user.getUsername() + " (" + name + ")");
        }
        
        // Select user to update
        System.out.print("Enter user number to update (0 to cancel): ");
        int userIndex;
        try {
            userIndex = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            if (userIndex == 0) {
                return;
            }
            
            if (userIndex < 1 || userIndex > selectedUsers.size()) {
                System.out.println("Invalid user number.");
                return;
            }
            
            User selectedUser = selectedUsers.get(userIndex - 1);
            updateUser(selectedUser.getUsername(), scanner);
            
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }
    }
    
    // Delete a user with confirmation (first select user type, then select user)
    private void deleteUserWithConfirmation(Scanner scanner) {
        System.out.println("\n--- Delete User ---");
        System.out.println("Select user type:");
        System.out.println("1. Student");
        System.out.println("2. Instructor");
        System.out.println("3. Admin");
        System.out.println("0. Back");
        
        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }
        
        if (choice == 0) {
            return;
        }
        
        // Get list of users of the selected type
        List<User> selectedUsers = new ArrayList<>();
        String userType = "";
        
        switch (choice) {
            case 1 -> {
                userType = "Student";
                for (User user : users) {
                    if (user instanceof Student) {
                        selectedUsers.add(user);
                    }
                }
            }
            case 2 -> {
                userType = "Instructor";
                for (User user : users) {
                    if (user instanceof Instructor) {
                        selectedUsers.add(user);
                    }
                }
            }
            case 3 -> {
                userType = "Admin";
                for (User user : users) {
                    if (user instanceof Admin) {
                        selectedUsers.add(user);
                    }
                }
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }
        
        if (selectedUsers.isEmpty()) {
            System.out.println("No " + userType + " users found.");
            return;
        }
        
        // Display users of the selected type
        System.out.println("\nAvailable " + userType + " users:");
        for (int i = 0; i < selectedUsers.size(); i++) {
            User user = selectedUsers.get(i);
            String name = "";
            
            if (user instanceof Student) {
                name = ((Student) user).getStudentName();
            } else if (user instanceof Instructor) {
                name = ((Instructor) user).getInstructorName();
            } else if (user instanceof Admin) {
                name = ((Admin) user).getAdminName();
            }
            
            System.out.println((i + 1) + ". " + user.getUsername() + " (" + name + ")");
        }
        
        // Select user to delete
        System.out.print("Enter user number to delete (0 to cancel): ");
        int userIndex;
        try {
            userIndex = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            if (userIndex == 0) {
                return;
            }
            
            if (userIndex < 1 || userIndex > selectedUsers.size()) {
                System.out.println("Invalid user number.");
                return;
            }
            
            User selectedUser = selectedUsers.get(userIndex - 1);
            
            // Confirm deletion
            System.out.print("Are you sure you want to delete user '" + selectedUser.getUsername() + "'? (y/n): ");
            String confirm = scanner.nextLine().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                deleteUser(selectedUser.getUsername());
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Deletion cancelled.");
            }
            
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }
    }
    
    // Placeholder for manage courses menu (this would connect to existing course management)
    private void manageCoursesMenu(Scanner scanner) {
        // This would connect to existing course management functionality
        // For now, just use the existing functions or add a new menu system
        System.out.println("Manage Courses functionality would be here.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    // Placeholder for assign instructor to course menu
    private void assignInstructorToCourseMenu(Scanner scanner) {
        // This would connect to existing functionality
        System.out.println("Assign instructor to course functionality would be here.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    // Placeholder for view courses dashboard
    private void viewCoursesDashboard() {
        // This would connect to existing functionality
        System.out.println("View courses dashboard functionality would be here.");
    }

    // Add user to the system
    public void addUser(User user) {
        users.add(user);
        System.out.println(user.getUsername() + " added successfully.");
        // Save users to file
        saveUsers();
    }

    // Delete user from the system
    public void deleteUser(String username) {
        User userToDelete = null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                userToDelete = user;
                break;
            }
        }
        if (userToDelete != null) {
            users.remove(userToDelete);
            System.out.println(username + " deleted successfully.");
            // Save users to file
            saveUsers();
        } else {
            System.out.println("User not found.");
        }
    }

    // Update user details
    public void updateUser(String username, Scanner scanner) {
        User userToUpdate = null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                userToUpdate = user;
                break;
            }
        }

        if (userToUpdate != null) {
            System.out.println("Updating user: " + username);
            System.out.print("New Username: ");
            String newUsername = scanner.nextLine();
            userToUpdate.setUsername(newUsername);
            System.out.print("New Password: ");
            String newPassword = scanner.nextLine();
            userToUpdate.setPassword(newPassword);

            if (userToUpdate instanceof Student) { // Fixed: Changed 'Students' to 'Student'
                Student student = (Student) userToUpdate;
                System.out.print("New Name: ");
                String newName = scanner.nextLine();
                student.setStudentName(newName);

                System.out.print("Number of courses: ");
                int n = scanner.nextInt();
                scanner.nextLine();

                for (int i = 0; i < n; i++) {
                    System.out.print("Course " + (i + 1) + " Name: ");
                    String course = scanner.nextLine();
                    System.out.print("Numeric Grade: ");
                    float numericGrade = scanner.nextFloat();
                    scanner.nextLine();
                    System.out.print("Letter Grade: ");
                    String letterGrade = scanner.nextLine();
                    student.updateCourse(i, course, numericGrade, letterGrade);
                }
            } else if (userToUpdate instanceof Instructor) {
                Instructor instructor = (Instructor) userToUpdate;
                System.out.print("New Name: ");
                String newName = scanner.nextLine();
                instructor.setInstructorName(newName);

                System.out.print("New Assigned Courses: ");
                String newCourses = scanner.nextLine();
                instructor.setAssignedCourses(newCourses);
            }

            System.out.println("User updated successfully.");
            // Save users to file
            saveUsers();
        } else {
            System.out.println("User not found.");
        }
    }

    // Display all users
    public void displayAllUsers() {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n--- All Users ---");
        // Increased width for better readability
        System.out.printf("%-25s %-30s %-50s %-15s\n", "Username", "Name", "Courses & Grades", "User Type");

        for (User user : users) {
            if (user instanceof Student) { // Fixed: Changed 'Students' to 'Student'
                Student student = (Student) user;
                // Combine courses and grades into a formatted string
                StringBuilder coursesAndGrades = new StringBuilder();
                ArrayList<String> courses = student.getCourses();
                ArrayList<Float> numGrades = student.getNumericGrades();
                ArrayList<String> letterGrades = student.getLetterGrades();

                for (int i = 0; i < courses.size(); i++) {
                    if (i > 0) {
                        coursesAndGrades.append(", ");
                    }
                    coursesAndGrades.append(courses.get(i)).append(": ")
                            .append(letterGrades.get(i)).append(" (")
                            .append(numGrades.get(i)).append(")");
                }

                // Print the student information in a single line with more space
                System.out.printf("%-25s %-30s %-50s %-15s\n",
                        student.getUsername(),
                        student.getStudentName(),
                        coursesAndGrades.toString(),
                        "Student");
            }
        }
    }

    /*----------< Instructor -----------< */
    public void add_grade() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the student's username: ");
        String studentUsername = scanner.nextLine();

        // Find the student
        Student student = null;
        for (User user : users) {
            if (user instanceof Student && user.getUsername().equals(studentUsername)) {
                student = (Student) user;
                break;
            }
        }
        
        if (student == null) {
            System.out.println("Student with that username not found.");
            return;
        }
        
        // Display the student's courses
        ArrayList<String> studentCourses = student.getCourses();
        if (studentCourses.isEmpty()) {
            System.out.println("This student has no registered courses.");
            return;
        }
        
        System.out.println("\nStudent: " + student.getStudentName());
        System.out.println("Registered Courses:");
        for (int i = 0; i < studentCourses.size(); i++) {
            System.out.println((i + 1) + ". " + studentCourses.get(i));
        }
        
        System.out.print("\nSelect course number to add grades: ");
        int courseIndex = -1;
        try {
            courseIndex = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            courseIndex--; // Adjust for 0-based indexing
            
            if (courseIndex < 0 || courseIndex >= studentCourses.size()) {
                System.out.println("Invalid course number.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }
        
        String courseName = studentCourses.get(courseIndex);
        
        // Input and validate numeric grade
        float numericGrade = 0.0f;
        boolean validNumericGrade = false;
        while (!validNumericGrade) {
            System.out.print("Enter numeric grade (0-100) for " + courseName + ": ");
            try {
                numericGrade = scanner.nextFloat();
                scanner.nextLine(); // Clear buffer
                
                if (numericGrade < 0 || numericGrade > 100) {
                    System.out.println("Grade must be between 0 and 100. Please try again.");
                } else {
                    validNumericGrade = true;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
        
        // Input and validate letter grade
        String letterGrade = "";
        boolean validLetterGrade = false;
        while (!validLetterGrade) {
            System.out.println("Valid letter grades: A+, A, A-, B+, B, B-, C+, C, C-, D+, D, D-, F");
            System.out.print("Enter letter grade for " + courseName + ": ");
            letterGrade = scanner.nextLine().trim().toUpperCase();
            
            // Check if it's a valid letter grade format
            if (letterGrade.matches("^[A-D][+-]?$|^F$")) {
                validLetterGrade = true;
            } else {
                System.out.println("Invalid letter grade format. Please try again.");
            }
        }
        
        // Set the grades
        student.setNumericGrade(courseName, numericGrade);
        student.setLetterGrade(courseName, letterGrade);
        
        // Save to grades file for future reference
        saveGradeToFile(student.getStudentName(), studentUsername, courseName, numericGrade, letterGrade);
        
        // Save user updates
        saveUsers();
        
        System.out.println("Grades successfully updated for " + courseName + ".");
    }
    
    /**
     * Saves grade information to the grades file
     * 
     * @param studentName The student's name
     * @param username The student's username
     * @param course The course name
     * @param numericGrade The numeric grade
     * @param letterGrade The letter grade
     */
    private void saveGradeToFile(String studentName, String username, String course, 
                                float numericGrade, String letterGrade) {
        // Use the DataManager's UTF-8 enabled method instead
        DataManager.saveGrade(studentName, username, course, "N/A", numericGrade, letterGrade);
    }

    public void display_Instructor_Schedule(String instructorUsername) {
        for (User user : users) {
            if (user instanceof Instructor && user.getUsername().equals(instructorUsername)) {
                Instructor instructor = (Instructor) user;
                List<Course> courses = instructor.getAssignedCourse();

                if (courses.isEmpty()) {
                    System.out.println("No courses assigned to instructor: " + instructorUsername);
                    return;
                }

                System.out.println("Schedule for Instructor: " + instructor.getInstructorName());
                System.out.printf("%-25s %-15s %-20s\n", "Course Name", "Room", "Days");

                for (Course course : courses) {
                    System.out.printf("%-25s %-15s %-20s\n",
                            course.getName(), // Fixed: Changed 'getCourseName()' to 'getName()'
                            course.getRoom(),
                            "[Days TBD]"); // Fixed: 'getDaysOfCourse()' not defined; placeholder added
                }
                return;
            }
        }

        System.out.println("Instructor with username '" + instructorUsername + "' not found.");
    }

    // Helper method to save users
    public void saveUsers() {
        if (DataManager.saveUsers(users)) {
            System.out.println("Users saved to file.");
        } else {
            System.out.println("Failed to save users to file.");
        }
    }

    // Get users list
    public List<User> getUsers() {
        return users;
    }
}