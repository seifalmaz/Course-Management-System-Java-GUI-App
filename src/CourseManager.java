import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

public class CourseManager { // Renamed from 'coursemanager' to follow Java naming conventions

    private ArrayList<Course> courses;

    public CourseManager() {
        // Load courses from file when CourseManager is initialized
        this.courses = DataManager.loadCourses();
    }

    public void addCourseFromInput(Scanner input) {
        System.out.print("Course name: ");
        String name = input.nextLine();
        System.out.print("Instructor: ");
        String instructor = input.nextLine();
        System.out.print("Room: ");
        String room = input.nextLine();
        System.out.print("Branch: ");
        String branch = input.nextLine();
        System.out.print("Price: ");
        double price = input.nextDouble();
        input.nextLine();
        System.out.print("Start date (yyyy-MM-dd): ");
        String start = input.nextLine();
        System.out.print("End date (yyyy-MM-dd): ");
        String end = input.nextLine();
        System.out.print("Description: ");
        String description = input.nextLine();

        courses.add(new Course(name, instructor, room, branch, price, start, end, description));
        System.out.println("Course added successfully.");
        
        // Save courses after adding a new one
        saveCourses();
    }

    public void updateCourseFromInput(Scanner input) {
        System.out.print("Enter course name to update: ");
        String oldName = input.nextLine();

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getName().equalsIgnoreCase(oldName)) {
                System.out.println("Enter new details:");
                System.out.print("New name: ");
                String newName = input.nextLine();
                System.out.print("Instructor: ");
                String instructor = input.nextLine();
                System.out.print("Room: ");
                String room = input.nextLine();
                System.out.print("Branch: ");
                String branch = input.nextLine();
                System.out.print("Price: ");
                double price = input.nextDouble();
                input.nextLine();
                System.out.print("Start date (yyyy-MM-dd): ");
                String start = input.nextLine();
                System.out.print("End date (yyyy-MM-dd): ");
                String end = input.nextLine();
                System.out.print("Description: ");
                String description = input.nextLine();

                courses.set(i, new Course(newName, instructor, room, branch, price, start, end, description));
                System.out.println("Course updated.");
                
                // Save courses after updating
                saveCourses();
                return;
            }
        }
        System.out.println("Course not found.");
    }

    public void deleteCourse(String name) {
        boolean removed = courses.removeIf(course -> course.getName().equalsIgnoreCase(name));
        System.out.println(removed ? "Deleted successfully." : "Course not found.");
        
        // Save courses after deletion
        if (removed) {
            saveCourses();
        }
    }

    // Helper method to save courses
    private void saveCourses() {
        if (DataManager.saveCourses(courses)) {
            System.out.println("Courses saved to file.");
        } else {
            System.out.println("Failed to save courses to file.");
        }
    }

    public void showAllCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses available."); 
        }else {
            courses.forEach(Course::displayCourseInfo);
        }
    }

    public void showAllInstructors() {
        if (courses.isEmpty()) {
            System.out.println("No courses."); 
        }else {
            System.out.println("Instructors:");
            for (Course course : courses) {
                System.out.println("- " + course.getInstructorName());
            }
        }
    }

    public void showCoursesNearStartOrEnd() {
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("Courses near to start or end within 3 days:");
        for (Course course : courses) {
            try {
                LocalDate start = LocalDate.parse(course.getStartDate(), formatter);
                LocalDate end = LocalDate.parse(course.getEndDate(), formatter);
                long startDiff = ChronoUnit.DAYS.between(today, start);
                long endDiff = ChronoUnit.DAYS.between(today, end);

                if ((startDiff >= 0 && startDiff <= 3) || (endDiff >= 0 && endDiff <= 3)) {
                    course.displayCourseInfo();
                }
            } catch (Exception e) {
                System.out.println("Invalid date in course: " + course.getName());
            }
        }
    }

    public void showCoursesByNumber() {
        for (int i = 0; i < courses.size(); i++) {
            System.out.println((i + 1) + ". " + courses.get(i).getName());
        }
    }

    public String getCourseNameByIndex(int index) {
        if (index >= 0 && index < courses.size()) {
            return courses.get(index).getName();
        }
        return null;
    }

    // Get all courses
    public ArrayList<Course> getCourses() {
        return courses;
    }

    /**
     * Adds a new course with the parameters from the GUI.
     * 
     * @param name The name of the course.
     * @param instructor The instructor name.
     * @param startDate The start date.
     * @param endDate The end date.
     * @param description The course description.
     * @return true if added successfully.
     */
    public boolean addCourse(String name, String instructor, String startDate, String endDate, String description) {
        // Use default values for room, branch, and price
        String room = "TBD";
        String branch = "Main";
        double price = 0.0;
        
        // Add the course
        courses.add(new Course(name, instructor, room, branch, price, startDate, endDate, description));
        saveCourses(); // Save changes to file
        return true;
    }
    
    /**
     * Updates an existing course with parameters from the GUI.
     * 
     * @param courseName The original course name to find it.
     * @param newName The new course name.
     * @param instructor The new instructor name.
     * @param startDate The new start date.
     * @param endDate The new end date.
     * @param description The new description.
     * @return true if updated successfully.
     */
    public boolean updateCourse(String courseName, String newName, String instructor, String startDate, String endDate, String description) {
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            if (course.getName().equals(courseName)) {
                // Create a new course with the updated values
                // Preserve existing values for room, branch, and price
                String room = course.getRoom();
                String branch = course.getBranch();
                double price = course.getPrice();
                
                // Replace the course at the same index
                courses.set(i, new Course(newName, instructor, room, branch, price, startDate, endDate, description));
                saveCourses(); // Save changes to file
                return true;
            }
        }
        return false; // Course not found
    }

    /**
     * Adds a Course object directly to the course list.
     * 
     * @param course The Course object to add.
     * @return true if added successfully.
     */
    public boolean addCourse(Course course) {
        courses.add(course);
        saveCourses(); // Save changes to file
        return true;
    }
}
