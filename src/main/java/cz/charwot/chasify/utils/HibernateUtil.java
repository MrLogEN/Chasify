package cz.charwot.chasify.utils;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;


public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory buildSessionFactory() {
        try { 
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            Dotenv dotenv = new Dotenv.load();

            configuration.setProperty("hibernate.connection.user", dotenv.get("APP_USER"));
            configuration.setProperty("hibernate.connection.password", dotenv.get("APP_PASS"));

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
            .applySettings(configuration.getProperties())
            .build();

            return configuration.buildSessionFactory(serviceRegistry);
        }
        catch(Exception e) {
            logger.error("The hibernate initialization has failed!", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionfactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionfactory().close();
    }
}
