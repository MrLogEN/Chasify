package cz.charwot.chasify.repositories;

import cz.charwot.chasify.utils.HibernateUtil;
import cz.charwot.chasify.models.User;

public interface IUserRepository {
    public void create(User user);
    public User findById(int id);
    public void update(User user);
    public void delete(User user);
    public void deleteById(int id);
}
