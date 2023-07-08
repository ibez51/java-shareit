package ru.practicum.shareit.user;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Integer, User> usersMap = new HashMap<>();
    private Integer userIdNumberSeq = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public User getUser(Integer userId) {
        return usersMap.get(userId);
    }

    @Override
    public Integer addUser(User user) {
        user.setId(getId());
        usersMap.put(user.getId(), user);

        return user.getId();
    }

    @Override
    public User updateUser(User user) {
        usersMap.put(user.getId(), user);

        return usersMap.get(user.getId());
    }

    @Override
    public void deleteUser(Integer userId) {
        usersMap.remove(userId);
    }

    @Override
    public boolean isEmailInUse(String email, Integer userIdExclude) {
        return usersMap.values().stream()
                .filter(x -> x.getId() != userIdExclude)
                .anyMatch(x -> email.equals(x.getEmail()));
    }

    private Integer getId() {
        return ++userIdNumberSeq;
    }
}

