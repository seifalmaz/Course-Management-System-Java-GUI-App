import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String name;
    private String instructorName; // This will now store a comma-separated list of instructors
    private final String room;
    private final String branch;
    private final double price;
    private final String startDate;
    private final String endDate;
    private final String description;

    public Course(String name, String instructorName, String room, String branch, double price, String startDate, String endDate, String description) {
        this.name = name;
        this.instructorName = instructorName;
        this.room = room;
        this.branch = branch;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getInstructorName() {
        return instructorName;
    }
    
    // Add a new instructor to the course
    public void addInstructor(String instructor) {
        if (instructor == null || instructor.trim().isEmpty()) {
            return;
        }
        
        // Check if this is the first instructor
        if (this.instructorName == null || this.instructorName.trim().isEmpty() || this.instructorName.equals("None")) {
            this.instructorName = instructor;
            return;
        }
        
        // Check if instructor is already assigned to prevent duplicates
        List<String> instructors = new ArrayList<>(Arrays.asList(this.instructorName.split(",")));
        for (String existingInstructor : instructors) {
            if (existingInstructor.trim().equals(instructor.trim())) {
                return; // Already assigned
            }
        }
        
        // Add the new instructor
        this.instructorName += ", " + instructor;
    }

    public String getInstructor() {
        return instructorName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getRoom() { // Added getter for 'room' since it's accessed in Admin.java
        return room;
    }

    public String getBranch() {
        return branch;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void displayCourseInfo() {
        System.out.println("Course Name: " + name);
        System.out.println("Instructor(s): " + instructorName);
        System.out.println("Room: " + room);
        System.out.println("Branch: " + branch);
        System.out.println("Price: " + price);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Description: " + description);
        System.out.println("------------------------------");
    }
}
