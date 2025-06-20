package cz.charwot.chasify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cz.charwot.chasify.services.*;
import cz.charwot.chasify.utils.HibernateUtil;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Configuration
@ComponentScan(basePackages = "cz.charwot.chasify") // Adjust the package to your project structure
public class AppConfig {

    @Bean
    public EntityManagerFactory emf() {
        //return Persistence.createEntityManagerFactory("chasify-pu");
        return HibernateUtil.getEntityManagerFactory();
    }
}
