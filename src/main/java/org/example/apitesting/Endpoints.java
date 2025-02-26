package org.example.apitesting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Set;


@RestController

public class Endpoints {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchema jsonSchema;


    public Endpoints() throws IOException {
        try(InputStream schemaStream = new ClassPathResource("JsonValidator.json").getInputStream()) {
            JsonNode schemaNode = objectMapper.readTree(schemaStream);
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            this.jsonSchema = factory.getSchema(schemaNode);
        }
    }

    @PostMapping("/firstName")
    public ResponseEntity<JsonNode> getFirstName(@RequestBody Person person) throws IOException {
        JsonNode requestJson = objectMapper.valueToTree(person);

        // Validate request against JSON schema
        Set<ValidationMessage> errors = jsonSchema.validate(requestJson);
        if (!errors.isEmpty()) {
            JsonNode errorResponse = objectMapper.createObjectNode()
                    .put("error", "Invalid JSON request")
                    .put("details", errors.toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }

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
        try (InputStream responseSchemaStream = new ClassPathResource("OutputJsonValidator.json").getInputStream()) {
            JsonNode responseSchemaNode = objectMapper.readTree(responseSchemaStream);
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            JsonSchema responseSchema = factory.getSchema(responseSchemaNode);

            Set<ValidationMessage> responseErrors = responseSchema.validate(responseJson);
            if (!responseErrors.isEmpty()) {
                JsonNode errorResponse = objectMapper.createObjectNode()
                        .put("error", "Invalid JSON response")
                        .put("details", responseErrors.toString());
                return ResponseEntity.internalServerError().body(errorResponse);
            }
        }
        return ResponseEntity.ok(responseJson);
    }
}
