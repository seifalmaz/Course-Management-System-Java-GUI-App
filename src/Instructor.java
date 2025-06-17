import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {

    private String instructorName;
    private String assignedCourses;
    List<Course> assignedCourse;

    public Instructor(String username, String password, String instructorName, String assignedCourses) {
        super(username, password);
        this.instructorName = instructorName;
        this.assignedCourses = assignedCourses;
        this.assignedCourse = new ArrayList<>(); // Fixed: Initialized the list
    }

    public Instructor(String username, String password) {
        super(username, password);
        this.instructorName = "Unknown";
        this.assignedCourses = "None";
        this.assignedCourse = new ArrayList<>(); // Fixed: Initialized the list
    }

    public void assignCourse(String course) {
        // Check if the instructor has any courses first
        if (this.assignedCourses.equals("None")) {
            this.assignedCourses = course;
        } else {
            // Check if the course is already assigned to prevent duplicates
            String[] courses = this.assignedCourses.split(",");
            for (String c : courses) {
                if (c.trim().equals(course)) {
                    // Course already assigned, no need to add it again
                    return;
                }
            }
            this.assignedCourses += ", " + course;
        }
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getAssignedCourses() {
        return assignedCourses;
    }

    public void setAssignedCourses(String assignedCourses) {
        this.assignedCourses = assignedCourses;
    }

    public void assignCourse(Course course) {
        // Check if course is already assigned to prevent duplicates
        for (Course c : assignedCourse) {
            if (c.getName().equals(course.getName())) {
                // Course already assigned, no need to add it again
                return;
            }
        }
        assignedCourse.add(course);
    }

    public List<Course> getAssignedCourse() {
        return assignedCourse;
    }
}
