package com.dhanush.rentify_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String url;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.api.key}")
    private String apiKey;

}
