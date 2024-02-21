package edu.ucsb.cs156.example.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

import edu.ucsb.cs156.example.entities.User;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    // @Bean
    // @Primary
    // public UserDetailsService userdetailsService() {
    //     // User basicUser = new UserImpl();
    // }
    
}
