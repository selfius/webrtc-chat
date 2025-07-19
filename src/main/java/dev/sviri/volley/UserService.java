package dev.sviri.volley;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final Map<UUID, User> users = new HashMap<>();

    User createUser() {
        User user = new User();
        users.put(user.uid(), user);
        return user;
    }


    @Nullable
    User findUser(UUID uid) {
        return users.get(uid);
    }
}
