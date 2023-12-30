package tn.bridge.elearning.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.bridge.elearning.entities.Course;
@Repository
public interface CourseRepository extends JpaRepository<Course,String> {
}
