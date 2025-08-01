package dev.sviri.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class VolleyApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolleyApplication.class, args);
    }

    @Configuration
    public static class MvcConfigurer implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("file:src/main/resources/static/")
                    .addResourceLocations("file:../frontend/build/www/")
                    .addResourceLocations("classpath:/static/");
        }
    }
}
