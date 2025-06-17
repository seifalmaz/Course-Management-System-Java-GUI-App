import java.util.ArrayList;
import java.util.Scanner;

/**
 * Student class representing a student user in the course management system.
 * Extends User to inherit username and password functionality. Manages
 * student-specific data like name, courses, and grades.
 */
public class Student extends User {

    private String studentName;
    private ArrayList<String> courses;
    private ArrayList<Float> numericGrades;
    private ArrayList<String> letterGrades;

    /**
     * Constructor to initialize a student with username, password, and name.
     *
     * @param username The student's username.
     * @param password The student's password.
     * @param studentName The student's full name.
     * @throws IllegalArgumentException if username, password, or studentName is
     * null or empty.
     */
    public Student(String username, String password, String studentName) {
        super(username, password);
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be null or empty");
        }
        this.studentName = studentName;
        this.courses = new ArrayList<>();
        this.numericGrades = new ArrayList<>();
        this.letterGrades = new ArrayList<>();
    }

    /**
     * Gets the student's full name.
     *
     * @return The student's name.
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Sets the student's full name.
     *
     * @param studentName The new name.
     * @throws IllegalArgumentException if studentName is null or empty.
     */
    public void setStudentName(String studentName) {
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be null or empty");
        }
        this.studentName = studentName;
    }

    /**
     * Gets the list of registered courses.
     *
     * @return The list of course names.
     */
    public ArrayList<String> getCourses() {
        return courses;
    }

    /**
     * Gets the list of numeric grades.
     *
     * @return The list of numeric grades.
     */
    public ArrayList<Float> getNumericGrades() {
        return numericGrades;
    }

    /**
     * Gets the list of letter grades.
     *
     * @return The list of letter grades.
     */
    public ArrayList<String> getLetterGrades() {
        return letterGrades;
    }

    /**
     * Adds a course with its grade.
     *
     * @param course The course name.
     * @param numericGrade The numeric grade.
     * @param letterGrade The letter grade.
     * @throws IllegalArgumentException if course or letterGrade is null/empty
     * or numericGrade is invalid.
     */
    public void addCourse(String course, float numericGrade, String letterGrade) {
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty");
        }
        if (letterGrade == null || letterGrade.trim().isEmpty()) {
            throw new IllegalArgumentException("Letter grade cannot be null or empty");
        }
        if (numericGrade < 0 || numericGrade > 100) {
            throw new IllegalArgumentException("Numeric grade must be between 0 and 100");
        }
        courses.add(course);
        numericGrades.add(numericGrade);
        letterGrades.add(letterGrade);
    }

    /**
     * Updates a course and its grades at the specified index.
     *
     * @param index The index of the course to update.
     * @param newCourse The new course name.
     * @param newNumericGrade The new numeric grade.
     * @param newLetterGrade The new letter grade.
     * @throws IllegalArgumentException if inputs are invalid.
     * @throws IndexOutOfBoundsException if index is invalid.
     */
    public void updateCourse(int index, String newCourse, float newNumericGrade, String newLetterGrade) {
        if (index < 0 || index >= courses.size()) {
            throw new IndexOutOfBoundsException("Invalid course index");
        }
        if (newCourse == null || newCourse.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty");
        }
        if (newLetterGrade == null || newLetterGrade.trim().isEmpty()) {
            throw new IllegalArgumentException("Letter grade cannot be null or empty");
        }
        if (newNumericGrade < 0 || newNumericGrade > 100) {
            throw new IllegalArgumentException("Numeric grade must be between 0 and 100");
        }
        courses.set(index, newCourse);
        numericGrades.set(index, newNumericGrade);
        letterGrades.set(index, newLetterGrade);
    }

    /**
     * Sets the numeric grade for a specific course.
     *
     * @param course The course name.
     * @param grade The new numeric grade.
     * @throws IllegalArgumentException if course is null/empty or grade is
     * invalid.
     */
    public void setNumericGrade(String course, float grade) {
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty");
        }
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Numeric grade must be between 0 and 100");
        }
        int index = courses.indexOf(course);
        if (index != -1) {
            numericGrades.set(index, grade);
        } else {
            throw new IllegalArgumentException("Course not found");
        }
    }

    /**
     * Sets the letter grade for a specific course.
     *
     * @param course The course name.
     * @param grade The new letter grade.
     * @throws IllegalArgumentException if course or grade is null/empty.
     */
    public void setLetterGrade(String course, String grade) {
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty");
        }
        if (grade == null || grade.trim().isEmpty()) {
            throw new IllegalArgumentException("Letter grade cannot be null or empty");
        }
        int index = courses.indexOf(course);
        if (index != -1) {
            letterGrades.set(index, grade);
        } else {
            throw new IllegalArgumentException("Course not found");
        }
    }

    /**
     * Allows the student to register a course using the CourseManager.
     *
     * @param manager The CourseManager instance.
     */
    public void chooseCourseToAdd(CourseManager manager) {
        Scanner input = new Scanner(System.in);
        manager.showCoursesByNumber();
        System.out.print("Enter course number: ");
        try {
            int choice = input.nextInt();
            input.nextLine(); // Clear buffer
            String course = manager.getCourseNameByIndex(choice - 1);
            if (course != null && !courses.contains(course)) {
                courses.add(course);
                numericGrades.add(0.0f); // Default grade
                letterGrades.add("N/A"); // Default letter grade
                System.out.println("Course registered: " + course);
            } else {
                System.out.println("Invalid course or already registered.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            if (input.hasNext()) {
                input.nextLine(); // Clear invalid input
            }
        }
    }

    /**
     * Displays the student's registered courses.
     */
    public void viewCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses registered.");
        } else {
            System.out.println("Registered Courses:");
            for (int i = 0; i < courses.size(); i++) {
                System.out.println("- " + courses.get(i));
            }
        }
    }

    /**
     * Displays the student's grades for all courses.
     */
    public void viewGrades() {
        if (courses.isEmpty()) {
            System.out.println("No grades available.");
        } else {
            System.out.println("Grades:");
            System.out.printf("%-30s %-15s %-10s\n", "Course", "Numeric Grade", "Letter Grade");
            System.out.println("-".repeat(60));
            
            for (int i = 0; i < courses.size(); i++) {
                System.out.printf("%-30s %-15.2f %-10s\n", 
                    courses.get(i), 
                    numericGrades.get(i), 
                    letterGrades.get(i));
            }
        }
    }

    /**
     * Submits feedback as a survey.
     *
     * @param feedback The feedback text.
     * @throws IllegalArgumentException if feedback is null or empty.
     */
    public void submitSurvey(String feedback) {
        if (feedback == null || feedback.trim().isEmpty()) {
            throw new IllegalArgumentException("Feedback cannot be null or empty");
        }
        System.out.println("Feedback received: " + feedback);
    }

    /**
     * Updates the student's username and password.
     *
     * @param newUsername The new username.
     * @param newPassword The new password.
     * @throws IllegalArgumentException if inputs are invalid.
     */
    public void updateInfo(String newUsername, String newPassword) {
        try {
            if (newUsername == null || newUsername.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }
            
            setUsername(newUsername);
            setPassword(newPassword);
            System.out.println("Student information updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error updating information: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get the student's password.
     * 
     * @return The password.
     */
    public String getPassword() {
        return super.getPassword();
    }
    
    /**
     * Get the student's course grades.
     * 
     * @return ArrayList of CourseGrade objects.
     */
    public ArrayList<CourseGrade> getCourseGrades() {
        ArrayList<CourseGrade> courseGrades = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            String course = courses.get(i);
            float grade = 0.0f;
            if (i < numericGrades.size()) {
                grade = numericGrades.get(i);
            }
            String instructor = "N/A"; // Replace with your instructor data if available
            courseGrades.add(new CourseGrade(course, grade, instructor));
        }
        return courseGrades;
    }
}
