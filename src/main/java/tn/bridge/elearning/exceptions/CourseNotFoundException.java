package tn.bridge.elearning.exceptions;

public class CourseNotFoundException extends Throwable {
    public CourseNotFoundException(String courseNotFound) {
        super(courseNotFound);
    }
}
