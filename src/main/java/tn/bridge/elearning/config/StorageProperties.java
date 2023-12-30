package tn.bridge.elearning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    @Value("${storage.location}")
    private String location;
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}