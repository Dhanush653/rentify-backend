package com.dhanush.rentify_backend.service;

import com.dhanush.rentify_backend.dto.property.PropertyImageResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dhanush.rentify_backend.config.SupabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Autowired
    private SupabaseConfig supabaseConfig;

    public String uploadImage(MultipartFile file) {
        System.out.println("Inside uploadImage()");
        System.out.println(file.getOriginalFilename());
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is required and must not be empty"
            );
        }
        try {
            String originalName = file.getOriginalFilename() != null
                    ? file.getOriginalFilename()
                    : "file";
            String contentType = file.getContentType() != null
                    ? file.getContentType()
                    : "application/octet-stream";

            String fileName = UUID.randomUUID() + "_" + originalName;
            String uploadUrl = supabaseConfig.getUrl()
                            + "/storage/v1/object/"
                            + supabaseConfig.getBucket()
                            + "/"
                            + fileName;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Authorization", "Bearer " + supabaseConfig.getApiKey())
                    .header("apikey", supabaseConfig.getApiKey())
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to upload image: " + response.body()
                );
            }

            return fileName;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteImage(String fileName) {
        try {
            String deleteUrl = supabaseConfig.getUrl()
                    + "/storage/v1/object/"
                    + supabaseConfig.getBucket()
                    + "/"
                    + fileName;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(deleteUrl))
                    .header("Authorization", "Bearer " + supabaseConfig.getApiKey())
                    .header("apikey", supabaseConfig.getApiKey())
                    .DELETE()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Failed to delete image from Supabase: "
                                + response.body()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
