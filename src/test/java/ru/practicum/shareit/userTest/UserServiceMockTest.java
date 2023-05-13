package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceMockTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(1L, "name", "email@mail.ru");
        user = new User(1L, "name", "email@mail.ru");
    }

    @Test
    void createUser() {
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUser = userService.createUser(userDto);

        assertEquals(UserMapper.userToDto(user), actualUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        when(userRepository.getExistingUser(userDto.getId())).thenReturn(user);

        UserDto actualUser = userService.getUserById(userDto.getId());

        assertEquals(UserMapper.userToDto(user), actualUser);
    }

    @Test
    void getUserById_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        when(userRepository.getExistingUser(userDto.getId())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userDto.getId()));
    }

    @Test
    void updateUser_whenUserFound_thenUpdateUser() {
        User mainUser = new User(1L, "name", "mail@mail.ru");
        UserDto userDataToUpdate = new UserDto(1L, "updatedName", "updatedMail@mail.ru");
        when(userRepository.getExistingUser(mainUser.getId())).thenReturn(mainUser);

        userService.updateUser(mainUser.getId(), userDataToUpdate);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("updatedName", savedUser.getName());
        assertEquals("updatedMail@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFoundAndUpdateEmail_thenUpdateOnlyEmail() {
        User mainUser = new User(1L, "name", "mail@mail.ru");
        UserDto userDataToUpdate = new UserDto(1L, "name", "updatedMail@mail.ru");
        when(userRepository.getExistingUser(mainUser.getId())).thenReturn(mainUser);

        userService.updateUser(mainUser.getId(), userDataToUpdate);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(mainUser.getName(), savedUser.getName());
        assertEquals("updatedMail@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFoundAndUpdateEmailExists_thenValidationExceptionThrown() {
        User mainUser = new User(1L, "name", "mail@mail.ru");
        UserDto userDataToUpdate = new UserDto(1L, "name", "updatedMail@mail.ru");
        when(userRepository.getExistingUser(mainUser.getId())).thenReturn(mainUser);
        when(userRepository.findByEmailContainingIgnoreCase(userDataToUpdate.getEmail())).thenReturn(List.of(new User()));

        assertThrows(ValidationException.class, () -> userService.updateUser(mainUser.getId(), userDataToUpdate));
        verify(userRepository, never()).save(UserMapper.dtoToUser(userDataToUpdate));
    }

    @Test
    void updateUser_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        User mainUser = new User(1L, "name", "mail@mail.ru");
        UserDto userDataToUpdate = new UserDto(1L, "updatedName", "mail@mail.ru");
        when(userRepository.getExistingUser(mainUser.getId())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(mainUser.getId(), userDataToUpdate));
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>(List.of(user));
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> usersDto = userService.getAllUsers();

        assertNotNull(usersDto);
        assertEquals(1, usersDto.size());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }
}