package cz.charwot.chasify.utils;


import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import io.github.cdimascio.dotenv.Dotenv;


public class HibernateUtil {

    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final Dotenv dotenv = Dotenv.load();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try { 
            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistance.jdbc.user", dotenv.get("APP_USER"));
            properties.put("jakarta.persistance.jdbc.password", dotenv.get("APP_PASS"));


            return Persistence.createEntityManagerFactory("chasify-pu", properties);
        }
        catch(Exception e) {
            logger.error("The hibernate initialization has failed!", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static void shutdown() {
        getEntityManagerFactory().close();
    }
}
