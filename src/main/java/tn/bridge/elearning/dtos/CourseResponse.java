package tn.bridge.elearning.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private String id;
    private String title;
    private String description;
    private double price;
    private String imageBase64;
}
