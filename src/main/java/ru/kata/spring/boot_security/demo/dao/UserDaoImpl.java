package ru.kata.spring.boot_security.demo.dao;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;


@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    private EntityManager entityManager;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserDaoImpl(EntityManager entityManager, @Lazy PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> showAll() {
        List<User> userList = entityManager.createQuery("select p from User p", User.class).getResultList();
        return userList;
    }

    @Override
    public User showById(int id) {
        return entityManager.find(User.class, id);

    }

    @Override
    public void create(User user) {
        Role role = new Role();
        role.setRole("ROLE_USER");
        user.addRoles(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        entityManager.persist(user);
    }

    @Override
    public void update(int id, User newUser) {
        User toUpdateUser = entityManager.find(User.class, id);
        toUpdateUser.setUsername(newUser.getUsername());
        toUpdateUser.setAge(newUser.getAge());

    }

    @Override
    public User findByName(String username) {

        Query query = entityManager.createQuery("Select u from User u left join fetch u.roles where u.username=:username");
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        if (!users.isEmpty()) {
            return users.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void delete(int id) {
        entityManager.remove(entityManager.find(User.class, id));
    }
}