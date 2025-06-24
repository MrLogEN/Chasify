package cz.charwot.chasify.repositories;

import java.util.List;

import cz.charwot.chasify.models.User;

public interface IUserRepository {
    public void create(User user);
    public User findById(int id);
    public User findByEmail(String email);
    public void update(User user) throws IllegalArgumentException;
    public void delete(User user);
    public void deleteById(int id);
    public List<User> getAll();
}
