package cz.charwot.chasify.utils;


import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import io.github.cdimascio.dotenv.Dotenv;


public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try { 
            Dotenv env = Dotenv.load();
            Map<String, Object> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.user", env.get("APP_USER").replaceAll("^'+|'+$", ""));
            props.put("jakarta.persistence.jdbc.password", env.get("APP_PASS").replaceAll("^'+|'+$", ""));

            return Persistence.createEntityManagerFactory("chasify-pu", props);
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
