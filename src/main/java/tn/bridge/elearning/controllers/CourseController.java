package tn.bridge.elearning.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.bridge.elearning.dtos.CourseDTO;
import tn.bridge.elearning.dtos.CourseResponse;
import tn.bridge.elearning.entities.Course;
import tn.bridge.elearning.exceptions.CourseNotFoundException;
import tn.bridge.elearning.exceptions.StorageException;
import tn.bridge.elearning.exceptions.StorageFileNotFoundException;
import tn.bridge.elearning.services.CourseService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class CourseController {
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponse> retrieveAllCourses() {
        try {
            return courseService.getAllCourses();
        } catch (CourseNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> retrieveOneCourse(@PathVariable String id) {
        try {
            CourseResponse courseResponse = courseService.getOneCourse(id);
            return ResponseEntity.ok(courseResponse);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        } catch (StorageFileNotFoundException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("image file not found");
        }
    }

    @PostMapping
    public ResponseEntity<?> createCourse(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam MultipartFile image) {
        try {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setDescription(description);
            courseDTO.setTitle(title);
            courseDTO.setPrice(price);
            courseDTO.setImage(image);
            Course course = courseService.createCourse(courseDTO);
            return ResponseEntity.ok(course);
        } catch (StorageException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store image: " + e.getMessage());
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cannot save empty course: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable String id,
                                          @RequestParam(required = false) String title,
                                          @RequestParam(required = false) String description,
                                          @RequestParam(required = false) Double price,
                                          @RequestParam(required = false) MultipartFile image) {
        try {
            CourseDTO updateCourse=new CourseDTO();
            updateCourse.setDescription(description);
            updateCourse.setTitle(title);
            updateCourse.setPrice(price);
            updateCourse.setImage(image);
            Course course= courseService.updateCourseById(id, updateCourse);
            return ResponseEntity.ok(course);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found with id: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Course deleted successfully"));
        } catch (CourseNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Course not found"));
        }
    }

}
