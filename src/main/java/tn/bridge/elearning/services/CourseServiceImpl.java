package tn.bridge.elearning.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.bridge.elearning.config.StorageProperties;
import tn.bridge.elearning.dtos.CourseDTO;
import tn.bridge.elearning.dtos.CourseResponse;
import tn.bridge.elearning.entities.Course;
import tn.bridge.elearning.exceptions.CourseNotFoundException;
import tn.bridge.elearning.exceptions.StorageException;
import tn.bridge.elearning.exceptions.StorageFileNotFoundException;
import tn.bridge.elearning.repositories.CourseRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService{
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    FileSystemStorageService fileSystemStorageService;
    @Autowired
    private StorageProperties storageProperties;
    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> allCourses = courseRepository.findAll();

        return allCourses.stream()
                .map(course -> {
                    CourseResponse courseResponse = new CourseResponse();
                    courseResponse.setId(course.getId());
                    courseResponse.setTitle(course.getTitle());
                    courseResponse.setDescription(course.getDescription());
                    courseResponse.setPrice(course.getPrice());

                    try {
                        String imagePath = course.getImage();
                        Path imageFilePath = Paths.get(storageProperties.getLocation(), imagePath);
                        System.out.println(imageFilePath);
                        if (Files.exists(imageFilePath)) {
                            byte[] imageData = Files.readAllBytes(imageFilePath);
                            String base64Image = Base64.encodeBase64String(imageData);
                            courseResponse.setImageBase64(base64Image);
                        } else {
                            throw new IOException("Image file not found for course ID: " + course.getId());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error processing course with ID: " + course.getId(), e);
                    }
                    return courseResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponse getOneCourse(String id) throws CourseNotFoundException, StorageFileNotFoundException {
        Course course = courseRepository.findById(id).orElse(null);

        if (course == null) {
            throw new CourseNotFoundException("Course not found");
        } else {
            CourseResponse res = new CourseResponse();
            res.setId(course.getId());
            res.setTitle(course.getTitle());
            res.setDescription(course.getDescription());
            res.setPrice(course.getPrice());

            try {
                String imagePath = course.getImage();
                Path imageFilePath = Paths.get(storageProperties.getLocation(), imagePath);

                if (Files.exists(imageFilePath)) {
                    byte[] imageData = Files.readAllBytes(imageFilePath);
                    String base64Image = Base64.encodeBase64String(imageData);
                    res.setImageBase64(base64Image);
                } else {
                    throw new StorageFileNotFoundException("Image file not found");
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle the IOException, log it, or rethrow if necessary
                throw new StorageFileNotFoundException("Error reading image file");
            }

            return res;
        }
    }

    @Override
    public Course createCourse(CourseDTO courseDTO) throws CourseNotFoundException, StorageException {
        if(courseDTO == null)
            throw new CourseNotFoundException("cannot save empty course");
        else{
            Course c1=new Course();
            if (courseDTO.getImage().isEmpty()) {
                throw new StorageException("Failed to read empty file ");
            }
            c1.setImage(courseDTO.getImage().getOriginalFilename());
            c1.setTitle(courseDTO.getTitle());
            c1.setDescription(courseDTO.getDescription());
            c1.setPrice(courseDTO.getPrice());
            courseRepository.save(c1);
            fileSystemStorageService.store(courseDTO.getImage(), c1.getImage());
            return c1;
        }
    }
    @Override
    public Course updateCourseById(String id, CourseDTO course) throws CourseNotFoundException {
        Course c1 = courseRepository.findById(id).orElse(null);
        if (c1 == null) {
            throw new CourseNotFoundException("Course not found");
        } else {
            if (course.getTitle() != null) {
                c1.setTitle(course.getTitle());
            }
            if (course.getDescription() != null) {
                c1.setDescription(course.getDescription());
            }
            if (course.getPrice() != null) {
                c1.setPrice(course.getPrice());
            }
            if (course.getImage() != null) {
                c1.setImage(course.getImage().getOriginalFilename());
                fileSystemStorageService.store(course.getImage(), c1.getImage());
            }
            courseRepository.save(c1);
            return c1;
        }
    }

    @Override
    public void deleteCourse(String id) throws CourseNotFoundException {
        Course course = courseRepository.findById(id).orElse(null);

        if (course == null) {
            throw new CourseNotFoundException("Course not found");
        }
        else{
            try {
                fileSystemStorageService.deleteFile(course.getImage());
                courseRepository.delete(course);
            } catch (StorageException e) {
                e.printStackTrace();
                throw new CourseNotFoundException("Failed to delete course");
            }
        }

    }


}
