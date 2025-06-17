/**
 * Represents a course grade for a student.
 */
public class CourseGrade {
    private String courseName;
    private float grade;
    private String instructor;
    
    /**
     * Constructor for CourseGrade.
     * 
     * @param courseName The name of the course.
     * @param grade The grade value.
     * @param instructor The instructor name.
     */
    public CourseGrade(String courseName, float grade, String instructor) {
        this.courseName = courseName;
        this.grade = grade;
        this.instructor = instructor;
    }
    
    /**
     * Get the course name.
     * 
     * @return The course name.
     */
    public String getCourseName() {
        return courseName;
    }
    
    /**
     * Get the grade.
     * 
     * @return The grade.
     */
    public float getGrade() {
        return grade;
    }
    
    /**
     * Get the instructor.
     * 
     * @return The instructor name.
     */
    public String getInstructor() {
        return instructor;
    }
    
    /**
     * Set the grade.
     * 
     * @param grade The new grade.
     */
    public void setGrade(float grade) {
        this.grade = grade;
    }
    
    /**
     * Set the instructor.
     * 
     * @param instructor The new instructor name.
     */
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
} 