package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.stereotype.Repository;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Repository
public class DatabaseManager {

    private final UserRepository userRepository;

    public DatabaseManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean storeUser(User user) {
        if (!checkEmailExists(user.getEmail())) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean updateUser(User user) {
        if (checkEmailExists(user.getEmail())) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByName(String name) {
        return userRepository.findByNameIgnoreCase(name);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


}
