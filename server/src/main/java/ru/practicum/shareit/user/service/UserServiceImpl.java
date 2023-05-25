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
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = userRepository.save(UserMapper.dtoToUser(userDto));
        log.info("Создан пользователь с ID: {} - {}", newUser.getId(), newUser);
        return UserMapper.userToDto(newUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.userToDto(userRepository.getExistingUser(userId));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User userDataToUpdate = UserMapper.dtoToUser(userDto);
        User mainUser = userRepository.getExistingUser(userId);

        if (userDataToUpdate.getName() != null) {
            mainUser.setName(userDataToUpdate.getName());
        }
        if (userDataToUpdate.getEmail() != null) {
            if (!userDataToUpdate.getEmail().equals(mainUser.getEmail())) {
                checkEmail(userDataToUpdate);
            }
            mainUser.setEmail(userDataToUpdate.getEmail());
        }
        userRepository.save(mainUser);
        log.info("Обновлен пользователь с ID: {}. Новые данные: {}", userId, mainUser);

        return UserMapper.userToDto(mainUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private void checkEmail(User user) {
        List<User> users = userRepository.findByEmailContainingIgnoreCase(user.getEmail());
        if (!users.isEmpty()) {
            log.warn("Email: " + user.getEmail() + " уже используется другим пользователем");
            throw new ValidationException("Пользователь с таким email уже зарегистрирован!");
        }
    }
}
