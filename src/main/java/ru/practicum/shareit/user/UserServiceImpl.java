package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userDao.createUser(user));
    }

    @Override
    public UserDto getUserById(int userId) {
        return UserMapper.userToDto(userDao.getUserById(userId));
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userDao.updateUser(userId, user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> userList = userDao.getAllUsers();
        return userList.stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }
}
