package com.dataquadinc.controller;

import com.dataquadinc.dto.BDM_Dto;
import com.dataquadinc.dto.ResponseBean;
import com.dataquadinc.model.BDM_Client;
import com.dataquadinc.repository.BDM_Repo;
import com.dataquadinc.service.BDM_service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@CrossOrigin(origins = {
        "http://35.188.150.92",
        "http://192.168.0.140:3000",
        "http://192.168.0.139:3000",
        "https://mymulya.com",
        "http://localhost:3000",
        "http://192.168.0.135:8080"
})
@RestController
@RequestMapping("/requirements")
public class BDM_Controller {

    @Autowired
    private BDM_service service;
    @Autowired
    private BDM_Repo repo;

    private final Path UPLOAD_DIR = Paths.get("uploads");

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Reuse ObjectMapper
    @PostMapping("/bdm/addClient")
    public ResponseEntity<ResponseBean> createClient(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "supportingDocuments", required = false) List<MultipartFile> files) {

        try {
            // Convert JSON string to DTO object
            BDM_Dto dto = objectMapper.readValue(dtoJson, BDM_Dto.class);

            // Debugging logs (optional)
            System.out.println("Parsed DTO: " + dto);
            System.out.println("Received Files: " + (files != null ? files.size() : 0));

            // Pass DTO and files to the service
            BDM_Dto createdClient = service.createClient(dto, files);
            return ResponseEntity.ok(ResponseBean.successResponse("Client added successfully", createdClient));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBean.errorResponse("Invalid JSON", "Error parsing client data"));
        }
    }


    @GetMapping("/bdm/getAll")
    public ResponseEntity<ResponseBean> getAllClients() {
        List<BDM_Dto> clients = service.getAllClients();
        return ResponseEntity.ok(ResponseBean.successResponse("Clients fetched successfully", clients));
    }

    @GetMapping("/bdm/{id}")
    public ResponseEntity<ResponseBean> getClientById(@PathVariable String id) {
        return service.getClientById(id)
                .map(client -> ResponseEntity.ok(ResponseBean.successResponse("Client found", client)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseBean.errorResponse("Client not found", "No client exists with ID: " + id)));
    }

    @PutMapping(value = "/bdm/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ResponseBean> updateClient(
            @PathVariable String id,
            @RequestPart("dto") String dtoJson,  // Expect JSON as a String
            @RequestPart(value = "supportingDocuments", required = false) List<MultipartFile> files) {

        try {
            // ✅ Debugging - Print received JSON before parsing
            System.out.println("Received JSON: " + dtoJson);

            // ✅ Convert JSON string to DTO object
            BDM_Dto dto = objectMapper.readValue(dtoJson, BDM_Dto.class);

            // ✅ Call service to update client
            Optional<BDM_Dto> updatedClient = service.updateClient(id, dto, files);

            return updatedClient.map(client -> ResponseEntity.ok(ResponseBean.successResponse("Client updated successfully", client)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseBean.errorResponse("Update failed", "No client exists with ID: " + id)));

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBean.errorResponse("Invalid JSON Format", "Error in JSON structure: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBean.errorResponse("JSON Parsing Error", "Could not parse client data."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBean.errorResponse("Unexpected Error", "Something went wrong: " + e.getMessage()));
        }
    }


    @DeleteMapping("/bdm/delete/{id}")
    public ResponseEntity<ResponseBean> deleteClient(@PathVariable String id) {
        service.deleteClient(id);
        return ResponseEntity.ok(ResponseBean.successResponse("Client deleted successfully", null));
    }


    @GetMapping("/bdm/{id}/downloadAll")
    public ResponseEntity<Resource> downloadAllSupportingDocuments(@PathVariable String id) {
        Optional<BDM_Client> clientOptional = repo.findById(id);

        if (clientOptional.isPresent()) {
            BDM_Client client = clientOptional.get();
            List<String> fileNames = client.getSupportingDocuments();

            if (fileNames == null || fileNames.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

                for (String fileName : fileNames) {
                    Path filePath = Paths.get("uploads", fileName);
                    if (Files.exists(filePath)) {
                        zipOutputStream.putNextEntry(new ZipEntry(fileName));
                        Files.copy(filePath, zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                }
                zipOutputStream.finish();

                ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Client_" + id + "_Documents.zip\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(resource.contentLength())
                        .body(resource);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }







}
