package org.example.apitesting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


@RestController
public class Endpoints {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping("/firstName")
    public ResponseEntity<JsonNode> getFirstName(@RequestBody Person person) throws IOException {
        String firstName = person.getFirstname();
        if(firstName == null) {
            return ResponseEntity.badRequest().body(objectMapper.createObjectNode().put("error", "FirstName is missing"));
        }
        ClassPathResource resource = new ClassPathResource(firstName + ".json");
        if(!resource.exists()) {
            return ResponseEntity.badRequest().body(objectMapper.createObjectNode().put("error", "FirstName not found"));
        }
        StringBuilder jsonContent = new StringBuilder();
        try(InputStream inputStream = resource.getInputStream()){
            Scanner scanner = new Scanner(inputStream);
            while(scanner.hasNextLine()) {
                jsonContent.append(scanner.nextLine());
            }
        }
        OutputPerson outputPerson = objectMapper.readValue(jsonContent.toString(), OutputPerson.class);
        JsonNode responseJson = objectMapper.valueToTree(outputPerson);
        return ResponseEntity.ok(responseJson);
    }
}
