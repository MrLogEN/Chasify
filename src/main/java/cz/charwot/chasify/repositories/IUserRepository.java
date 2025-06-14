package cz.charwot.chasify.repositories;

import cz.charwot.chasify.utils.HibernateUtil;
import cz.charwot.chasify.models.User;

public interface IUserRepository {
    public void create(User user);
    public User findById(long id);
    public User findByEmail(String email);
    public void update(User user) throws IllegalArgumentException;
    public void delete(User user);
    public void deleteById(long id);
}
