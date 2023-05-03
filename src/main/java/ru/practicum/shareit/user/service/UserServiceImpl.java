package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = repository.save(UserMapper.dtoToUser(userDto));
        log.info("Создан пользователь с ID: {} - {}", newUser.getId(), newUser);
        return UserMapper.userToDto(newUser);
    }

    @Override
    public UserDto getUserById(int userId) {
        return UserMapper.userToDto(repository.getExistingUser(userId));
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User userDataToUpdate = UserMapper.dtoToUser(userDto);
        User mainUser = repository.getExistingUser(userId);

        if (userDataToUpdate.getName() != null) {
            mainUser.setName(userDataToUpdate.getName());
        }
        if (userDataToUpdate.getEmail() != null) {
            if (!userDataToUpdate.getEmail().equals(mainUser.getEmail())) {
                checkEmail(userDataToUpdate);
            }
            mainUser.setEmail(userDataToUpdate.getEmail());
        }
        repository.save(mainUser);
        log.info("Обновлен пользователь с ID: {}. Новые данные: {}", userId, mainUser);

        return UserMapper.userToDto(mainUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(int userId) {
        repository.deleteById(userId);
    }

    private void checkEmail(User user) {
        List<User> users = repository.findByEmailContainingIgnoreCase(user.getEmail());
        if (!users.isEmpty()) {
            log.warn("Email: " + user.getEmail() + " уже используется другим пользователем");
            throw new ValidationException("Пользователь с таким email уже зарегистрирован!");
        }
    }
}
