package com.app.testingService.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
    @Bean 
    public OpenAPI openAPI(SecurityScheme s) { 
        return new OpenAPI().addSecurityItem(new SecurityRequirement()         
           .addList("Bearer Authentication")) 
            .components(new Components().addSecuritySchemes 
            ("Bearer Authentication", s)) 
           .info(new Info() 
                    .title("Notes API") 
                    .description(""" 
                        Default users: login:admin,password:admin,role:ADMIN;  
                    """) 
            ); 
    } 
     
 
    @Bean
    public SecurityScheme createAPIKeyScheme() { 
        return new SecurityScheme().type(SecurityScheme.Type.HTTP) 
                .bearerFormat("JWT") 
                .scheme("bearer"); 
    }
}
