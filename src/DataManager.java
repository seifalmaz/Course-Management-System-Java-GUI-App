import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages data persistence for the Course Management System.
 * Handles saving and loading objects to/from text files.
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String COURSES_FILE = DATA_DIR + "/courses.txt";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String FEEDBACK_FILE = DATA_DIR + "/feedbacks.txt";
    private static final String GRADES_FILE = DATA_DIR + "/grades.txt";
    
    /**
     * Saves a list of courses to a text file.
     * 
     * @param courses The list of courses to save.
     * @return true if successful, false otherwise.
     */
    public static boolean saveCourses(ArrayList<Course> courses) {
        ensureDataDirectoryExists();
        try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_FILE))) {
            // Write header
            writer.println("# Courses Data - name|instructorName|room|branch|price|startDate|endDate|description");
            
            // Write each course as a pipe-delimited line
            for (Course course : courses) {
                writer.println(
                    course.getName() + "|" +
                    course.getInstructorName() + "|" +
                    course.getRoom() + "|" +
                    course.getBranch() + "|" + 
                    course.getPrice() + "|" +
                    course.getStartDate() + "|" +
                    course.getEndDate() + "|" +
                    course.getDescription()
                );
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving courses: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads courses from a text file.
     * 
     * @return The list of courses or an empty list if the file doesn't exist.
     */
    public static ArrayList<Course> loadCourses() {
        File file = new File(COURSES_FILE);
        ArrayList<Course> courses = new ArrayList<>();
        
        if (!file.exists()) {
            return courses;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            // Skip header line
            reader.readLine(); 
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    String name = parts[0];
                    String instructorName = parts[1];
                    String room = parts[2];
                    String branch = parts[3];
                    double price = 0.0;
                    try {
                        price = Double.parseDouble(parts[4]);
                    } catch (NumberFormatException e) {
                        // Use default price if parsing fails
                    }
                    String startDate = parts[5];
                    String endDate = parts[6];
                    
                    // Add a default empty description
                    String description = parts.length > 7 ? parts[7] : "";
                    
                    courses.add(new Course(name, instructorName, room, branch, price, startDate, endDate, description));
                }
            }
            return courses;
        } catch (IOException e) {
            System.out.println("Error loading courses: " + e.getMessage());
            return courses;
        }
    }
    
    /**
     * Saves a list of users to a text file.
     * 
     * @param users The list of users to save.
     * @return true if successful, false otherwise.
     */
    public static boolean saveUsers(List<User> users) {
        ensureDataDirectoryExists();
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            // Write header
            writer.println("# Users Data - type|username|password|name|additionalData");
            
            // Write each user as a pipe-delimited line
            for (User user : users) {
                if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    writer.println("ADMIN|" + 
                                  admin.getUsername() + "|" + 
                                  admin.getPassword() + "|" + // Store actual password
                                  admin.getAdminName() + "|");
                } 
                else if (user instanceof Instructor) {
                    Instructor instructor = (Instructor) user;
                    writer.println("INSTRUCTOR|" + 
                                  instructor.getUsername() + "|" + 
                                  instructor.getPassword() + "|" + // Store actual password
                                  instructor.getInstructorName() + "|" + 
                                  instructor.getAssignedCourses());
                } 
                else if (user instanceof Student) {
                    Student student = (Student) user;
                    StringBuilder courseData = new StringBuilder();
                    // Format: course1:grade1:letterGrade1,course2:grade2:letterGrade2
                    ArrayList<String> courses = student.getCourses();
                    ArrayList<Float> grades = student.getNumericGrades();
                    ArrayList<String> letterGrades = student.getLetterGrades();
                    
                    for (int i = 0; i < courses.size(); i++) {
                        if (i > 0) {
                            courseData.append(",");
                        }
                        courseData.append(courses.get(i))
                                 .append(":")
                                 .append(grades.get(i))
                                 .append(":")
                                 .append(letterGrades.get(i));
                    }
                    
                    writer.println("STUDENT|" + 
                                  student.getUsername() + "|" + 
                                  student.getPassword() + "|" + // Store actual password
                                  student.getStudentName() + "|" + 
                                  courseData.toString());
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads users from a text file.
     * 
     * @return The list of users or an empty list if the file doesn't exist.
     */
    public static List<User> loadUsers() {
        File file = new File(USERS_FILE);
        List<User> users = new ArrayList<>();
        
        if (!file.exists()) {
            return users;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            // Skip header line
            reader.readLine(); 
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String type = parts[0];
                    String username = parts[1];
                    String password = parts[2]; 
                    String name = parts[3];
                    String additionalData = parts.length > 4 ? parts[4] : "";
                    
                    // Create appropriate user object based on type
                    switch (type) {
                        case "ADMIN":
                            users.add(new Admin(username, password, name));
                            break;
                            
                        case "INSTRUCTOR":
                            users.add(new Instructor(username, password, name, additionalData));
                            break;
                            
                        case "STUDENT":
                            Student student = new Student(username, password, name);
                            // Parse course data if exists
                            if (!additionalData.isEmpty()) {
                                String[] courseEntries = additionalData.split(",");
                                for (String courseEntry : courseEntries) {
                                    String[] courseInfo = courseEntry.split(":");
                                    if (courseInfo.length >= 3) {
                                        String courseName = courseInfo[0];
                                        float grade = 0.0f;
                                        try {
                                            grade = Float.parseFloat(courseInfo[1]);
                                        } catch (NumberFormatException e) {
                                            // Use default grade if parsing fails
                                        }
                                        String letterGrade = courseInfo[2];
                                        student.addCourse(courseName, grade, letterGrade);
                                    }
                                }
                            }
                            users.add(student);
                            break;
                    }
                }
            }
            return users;
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
            return users;
        }
    }
    
    /**
     * Ensures the data directory exists.
     * 
     * @return true if the directory exists or was created successfully.
     */
    public static boolean ensureDataDirectoryExists() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            return directory.mkdir();
        }
        return true;
    }
    
    /**
     * Ensures the feedback file exists with proper UTF-8 encoding and header.
     * 
     * @return true if successful, false otherwise.
     */
    private static boolean ensureFeedbackFileExists() {
        ensureDataDirectoryExists();
        
        File file = new File(FEEDBACK_FILE);
        if (!file.exists()) {
            try (OutputStreamWriter writer = new OutputStreamWriter(
                     new FileOutputStream(file), "UTF-8");
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                
                // Write a header line to ensure the file has the proper encoding from the start
                bufferedWriter.write("# Feedback Data - timestamp|studentName|studentUsername|courseName|instructorName|feedbackMessage");
                bufferedWriter.newLine();
                return true;
            } catch (IOException e) {
                System.out.println("Error creating feedback file: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Saves feedback to a text file.
     * 
     * @param studentName The name of the student providing feedback.
     * @param studentUsername The username of the student.
     * @param courseName The name of the course.
     * @param instructorName The name of the instructor.
     * @param feedbackMessage The feedback message.
     * @return true if successful, false otherwise.
     */
    public static boolean saveFeedback(String studentName, String studentUsername, 
                                       String courseName, String instructorName, 
                                       String feedbackMessage) {
        // Ensure the file exists with proper UTF-8 encoding
        ensureFeedbackFileExists();
        
        // Create a FileOutputStream with UTF-8 encoding to properly handle special characters
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(FEEDBACK_FILE, true), "UTF-8");
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            
            // Format: timestamp|studentName|studentUsername|courseName|instructorName|feedback
            String timestamp = java.time.LocalDateTime.now().toString();
            String feedbackLine = 
                timestamp + "|" +
                studentName + "|" +
                studentUsername + "|" +
                courseName + "|" +
                instructorName + "|" +
                feedbackMessage;
                
            // Write the line and add a newline
            bufferedWriter.write(feedbackLine);
            bufferedWriter.newLine();
            
            return true;
        } catch (IOException e) {
            System.out.println("Error saving feedback: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Ensures the grades file exists with proper UTF-8 encoding and header.
     * 
     * @return true if successful, false otherwise.
     */
    private static boolean ensureGradeFileExists() {
        ensureDataDirectoryExists();
        
        File file = new File(GRADES_FILE);
        if (!file.exists()) {
            try (OutputStreamWriter writer = new OutputStreamWriter(
                     new FileOutputStream(file), "UTF-8");
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                
                // Write a header line to ensure the file has the proper encoding from the start
                bufferedWriter.write("# Grades Data - timestamp|studentName|studentUsername|courseName|instructorName|numericGrade|letterGrade");
                bufferedWriter.newLine();
                return true;
            } catch (IOException e) {
                System.out.println("Error creating grades file: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Saves a grade to the grades file.
     * 
     * @param studentName The name of the student.
     * @param studentUsername The username of the student.
     * @param courseName The name of the course.
     * @param instructorName The name of the instructor.
     * @param numericGrade The numeric grade.
     * @param letterGrade The letter grade.
     * @return true if successful, false otherwise.
     */
    public static boolean saveGrade(String studentName, String studentUsername, 
                                  String courseName, String instructorName, 
                                  float numericGrade, String letterGrade) {
        // Ensure the file exists with proper UTF-8 encoding
        ensureGradeFileExists();
        
        // Create a FileOutputStream with UTF-8 encoding to properly handle special characters
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(GRADES_FILE, true), "UTF-8");
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            
            // Format: timestamp|studentName|studentUsername|courseName|instructorName|numericGrade|letterGrade
            String timestamp = java.time.LocalDateTime.now().toString();
            String gradeLine = 
                timestamp + "|" +
                studentName + "|" +
                studentUsername + "|" +
                courseName + "|" +
                instructorName + "|" +
                numericGrade + "|" +
                letterGrade;
                
            // Write the line and add a newline
            bufferedWriter.write(gradeLine);
            bufferedWriter.newLine();
            
            return true;
        } catch (IOException e) {
            System.out.println("Error saving grade: " + e.getMessage());
            return false;
        }
    }

    /**
     * Utility method to reset the feedback file if it's corrupted.
     * This should be called by the admin if the feedback file contains unreadable characters.
     * 
     * @return true if the file was successfully reset, false otherwise.
     */
    public static boolean resetFeedbackFile() {
        ensureDataDirectoryExists();
        
        File file = new File(FEEDBACK_FILE);
        if (file.exists()) {
            if (!file.delete()) {
                System.out.println("Error deleting corrupted feedback file");
                return false;
            }
        }
        
        return ensureFeedbackFileExists();
    }
    
    /**
     * Utility method to convert an existing feedback file to UTF-8 format.
     * This should be called if the feedback file is corrupted but contains valuable data.
     * 
     * @return true if the file was successfully converted, false otherwise.
     */
    public static boolean convertFeedbackFileToUTF8() {
        ensureDataDirectoryExists();
        
        File originalFile = new File(FEEDBACK_FILE);
        if (!originalFile.exists()) {
            return ensureFeedbackFileExists();
        }
        
        File tempFile = new File(FEEDBACK_FILE + ".temp");
        
        try {
            // Try different encodings to read the file
            String[] encodingsToTry = {"UTF-8", "ISO-8859-1", "windows-1252", "US-ASCII"};
            boolean conversionSuccessful = false;
            
            for (String encoding : encodingsToTry) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(originalFile), encoding));
                     OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(tempFile), "UTF-8");
                     BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                    
                    // Write the UTF-8 header
                    bufferedWriter.write("# Feedback Data - timestamp|studentName|studentUsername|courseName|instructorName|feedbackMessage");
                    bufferedWriter.newLine();
                    
                    // Copy each line
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Skip the original header or empty lines
                        if (line.trim().isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        
                        // Check if the line is valid feedback by verifying it has the expected format
                        // Must contain at least 5 pipe characters for 6 fields
                        int pipeCount = line.length() - line.replace("|", "").length();
                        if (pipeCount >= 5) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                        }
                    }
                    
                    conversionSuccessful = true;
                    break; // Exit the loop if successful
                } catch (Exception e) {
                    System.out.println("Failed to convert with encoding " + encoding + ": " + e.getMessage());
                    // Continue trying other encodings
                }
            }
            
            if (!conversionSuccessful) {
                System.out.println("Failed to convert feedback file with any encoding");
                tempFile.delete();
                return false;
            }
            
            // Replace the original file with the converted file
            if (originalFile.delete() && tempFile.renameTo(originalFile)) {
                return true;
            } else {
                System.out.println("Error replacing old feedback file with converted file");
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("Error converting feedback file: " + e.getMessage());
            e.printStackTrace();
            
            // Clean up temp file if exists
            if (tempFile.exists()) {
                tempFile.delete();
            }
            
            return false;
        }
    }

    /**
     * Utility method to reset the grades file if it's corrupted.
     * This should be called by the admin if the grades file contains unreadable characters.
     * 
     * @return true if the file was successfully reset, false otherwise.
     */
    public static boolean resetGradeFile() {
        ensureDataDirectoryExists();
        
        File file = new File(GRADES_FILE);
        if (file.exists()) {
            if (!file.delete()) {
                System.out.println("Error deleting corrupted grades file");
                return false;
            }
        }
        
        return ensureGradeFileExists();
    }
    
    /**
     * Utility method to convert an existing grades file to UTF-8 format.
     * This should be called if the grades file is corrupted but contains valuable data.
     * 
     * @return true if the file was successfully converted, false otherwise.
     */
    public static boolean convertGradeFileToUTF8() {
        ensureDataDirectoryExists();
        
        File originalFile = new File(GRADES_FILE);
        if (!originalFile.exists()) {
            return ensureGradeFileExists();
        }
        
        File tempFile = new File(GRADES_FILE + ".temp");
        
        try {
            // Try different encodings to read the file
            String[] encodingsToTry = {"UTF-8", "ISO-8859-1", "windows-1252", "US-ASCII"};
            boolean conversionSuccessful = false;
            
            for (String encoding : encodingsToTry) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(originalFile), encoding));
                     OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(tempFile), "UTF-8");
                     BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                    
                    // Write the UTF-8 header
                    bufferedWriter.write("# Grades Data - timestamp|studentName|studentUsername|courseName|instructorName|numericGrade|letterGrade");
                    bufferedWriter.newLine();
                    
                    // Copy each line
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Skip the original header or empty lines
                        if (line.trim().isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        
                        // Check if the line is valid grade data by verifying it has the expected format
                        // Must contain at least 6 pipe characters for 7 fields
                        int pipeCount = line.length() - line.replace("|", "").length();
                        if (pipeCount >= 6) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                        }
                    }
                    
                    conversionSuccessful = true;
                    break; // Exit the loop if successful
                } catch (Exception e) {
                    System.out.println("Failed to convert with encoding " + encoding + ": " + e.getMessage());
                    // Continue trying other encodings
                }
            }
            
            if (!conversionSuccessful) {
                System.out.println("Failed to convert grades file with any encoding");
                tempFile.delete();
                return false;
            }
            
            // Replace the original file with the converted file
            if (originalFile.delete() && tempFile.renameTo(originalFile)) {
                return true;
            } else {
                System.out.println("Error replacing old grades file with converted file");
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("Error converting grades file: " + e.getMessage());
            e.printStackTrace();
            
            // Clean up temp file if exists
            if (tempFile.exists()) {
                tempFile.delete();
            }
            
            return false;
        }
    }
} 