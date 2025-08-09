package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public UserDto create(UserDto dto) {
        validateEmailRequired(dto.getEmail());
        validateEmailUnique(dto.getEmail(), null);
        User u = UserMapper.toUser(dto);
        u.setId(seq.getAndIncrement());
        storage.put(u.getId(), u);
        return UserMapper.toUserDto(u);
    }

//    @Override
//    public UserDto update(Long userId, UserDto patch) {
//        User u = getOrThrow(userId);
//        if (patch.getEmail() != null && !patch.getEmail().equals(u.getEmail())) {
//            validateEmailUnique(patch.getEmail(), userId);
//            u.setEmail(patch.getEmail());
//        }
//        if (patch.getName() != null) {
//            u.setName(patch.getName());
//        }
//        return UserMapper.toUserDto(u);
//    }

    @Override
    public UserDto update(Long userId, UserDto patch) {
        User u = getOrThrow(userId);
        if (patch.getEmail() != null) {
            validateEmailRequired(patch.getEmail());
            if (!patch.getEmail().equals(u.getEmail())) {
                validateEmailUnique(patch.getEmail(), userId);
                u.setEmail(patch.getEmail());
            }
        }
        if (patch.getName() != null) {
            u.setName(patch.getName());
        }
        return UserMapper.toUserDto(u);
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(getOrThrow(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return storage.values().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void delete(Long userId) {
        if (storage.remove(userId) == null) {
            throw new NoSuchElementException("User not found: " + userId);
        }
    }

    private User getOrThrow(Long id) {
        User u = storage.get(id);
        if (u == null) {
            throw new NoSuchElementException("User not found: " + id);
        }
        return u;
    }

    //    private void validateEmailUnique(String email, Long selfId) {
//        boolean exists = storage.values().stream()
//                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email) && !Objects.equals(u.getId(), selfId));
//        if (exists) {
//            throw new IllegalArgumentException("Email already in use: " + email);
//        }
//    }
    private void validateEmailRequired(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain '@'");
        }
    }

    //    private void validateEmailUnique(String email, Long selfId) {
//        if (email == null) return;
//        boolean exists = storage.values().stream()
//                .anyMatch(u -> u.getEmail() != null
//                        && u.getEmail().equalsIgnoreCase(email)
//                        && !Objects.equals(u.getId(), selfId));
//        if (exists) {
//            throw new IllegalArgumentException("Email already in use: " + email);
//        }
//    }
    private void validateEmailUnique(String email, Long selfId) {
        if (email == null) return;
        boolean exists = storage.values().stream()
                .anyMatch(u -> u.getEmail() != null
                        && u.getEmail().equalsIgnoreCase(email)
                        && !Objects.equals(u.getId(), selfId));
        if (exists) {
            throw new ConflictException("Email already in use: " + email);
        }
    }

}