package cz.charwot.chasify.session;

import org.hibernate.grammars.hql.HqlParser.IsDistinctFromPredicateContext;

import cz.charwot.chasify.models.User;

public class UserSession {

    private static UserSession instance;

    private User loggedInUser; 

    private UserSession(User user) {
        this.loggedInUser = user;
    }

    /**
     * This method starts a new session.
     *
     * Be careful when using this method as it may throw an exception.
     * You should always check if an instace is running {@see #isActive()}.
     *
     * @param user the user to start session with.
     * @throws IllegalStateException when a session already exists.
     */
    public static void start(User user) {
        if(instance == null) {
            instance = new UserSession(user);
        }
        else {
            throw new IllegalStateException("Session is already active.");
        }
    }

    /**
     * This method returns the current session.
     *
     * Be careful when using this method as it may throw an exception.
     * You should always check if an instace is running {@see #isActive()}.
     *
     * @return the current {@link UserSession} instace.
     * @throws IllegalStateException when a session already exists.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("No active session found.");
        }
        return instance;
    }

    /**
     * Stops the current {@link UserSession}.
     */
    public static void end() {
        instance = null;
    }

    /**
     * The current user in this session.
     *
     * @return the session's {@link User}.
     */
    public User getUser() {
        return loggedInUser;
    }

    /**
     * Indicates if a {@UserSession} is active or not.
     *
     * @return a {@link Boolean} value if a {@link UserSession} is active or not. 
     */
    public static boolean isActive() {
        return instance != null;
    }
}
