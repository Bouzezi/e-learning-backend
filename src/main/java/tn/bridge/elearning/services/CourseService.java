package tn.bridge.elearning.services;

import tn.bridge.elearning.dtos.CourseDTO;
import tn.bridge.elearning.dtos.CourseResponse;
import tn.bridge.elearning.entities.Course;
import tn.bridge.elearning.exceptions.CourseNotFoundException;
import tn.bridge.elearning.exceptions.StorageException;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    List<CourseResponse> getAllCourses() throws CourseNotFoundException;
    CourseResponse getOneCourse(String id) throws CourseNotFoundException, IOException;
    Course updateCourseById(String id, CourseDTO course) throws CourseNotFoundException;
    Course createCourse(CourseDTO courseDTO) throws CourseNotFoundException, StorageException;

    void deleteCourse(String id) throws CourseNotFoundException;
}
