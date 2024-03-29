package com.liserabackend.services;

import com.liserabackend.dto.CreateEmployee;
import com.liserabackend.dto.CreateStudent;
import com.liserabackend.dto.ModifyPasswordDTO;
import com.liserabackend.entity.Employee;
import com.liserabackend.entity.Role;
import com.liserabackend.entity.Student;
import com.liserabackend.entity.User;
import com.liserabackend.entity.repository.EmployeeRepository;
import com.liserabackend.entity.repository.RoleRepositories;
import com.liserabackend.entity.repository.StudentRepository;
import com.liserabackend.entity.repository.UserRepository;
import com.liserabackend.exceptions.UseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.liserabackend.exceptions.UseExceptionType.USER_ALREADY_EXIST;
import static com.liserabackend.exceptions.UseExceptionType.USER_NOT_FOUND;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserService  { //implements UserDetailsService

    private final UserRepository userRepository;
    private final RoleRepositories roleRepositories;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;
    /*
       User service for all the methods that have to do with creating the different users
       login
       adding role (creat student, create employee etc)
       changing password
     */

    /*@SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UseException(USER_NOT_FOUND));
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database : {}", username);
            Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }*/


    //Users
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    //saveRole??

    public Role saveRole(Role role){
        return roleRepositories.save(role);
    }

    public void addRoleToUser(String username, String roleName) {
        User user=userRepository.findByUsername(username).get();
        Role role=roleRepositories.findByName(roleName);
        user.getRoles().add(role);//get role from user and add new role
        userRepository.save(user);
    }

    public Optional<User> getUserByUserName(String username) throws UseException {
        final var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UseException(USER_NOT_FOUND));
        return Optional.ofNullable(user);
    }

    public Optional<User> updatePassword(String userId, String password) throws UseException {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new UseException(USER_NOT_FOUND));

        user.setPassword(password);
        return Optional.of(saveUser(user));
    }

    public Optional<User> modifyPassword(ModifyPasswordDTO modifyPasswordDTO) throws UseException {
        final var user = userRepository.findAll().stream()
               .filter(u -> u.getPassword().equals(modifyPasswordDTO.getCurrentPassword()))
                .findAny().orElseThrow(() -> new UseException(USER_NOT_FOUND));
        user.setPassword(modifyPasswordDTO.getNewPassword());
        userRepository.save(user);
        return Optional.of(saveUser(user));
    }

    public Optional<User> updateEmail(String userId, String email) throws UseException {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new UseException(USER_NOT_FOUND));
        user.setUsername(email);
        return Optional.ofNullable(saveUser(user));
    }
    public Optional<Student> createStudent(CreateStudent createStudent) throws UseException {
        if(userRepository.findByUsername(createStudent.getUsername()).isPresent())
            new UseException(USER_ALREADY_EXIST);
        User user=new User(createStudent.getUsername(), passwordEncoder.encode(createStudent.getPassword()));
        user.getRoles().add(roleRepositories.findByName("ROLE_STUDENT"));
        userRepository.save(user);
        final Student stud = new Student(createStudent.getFirstName(), createStudent.getLastName(), createStudent.getPhone(), user);
        stud.setSchoolName(createStudent.getSchoolName());
        return  Optional.of(studentRepository.save(stud));
    }

    public Optional<Employee> createEmployee(CreateEmployee createEmployee)  {
        if(userRepository.findByUsername(createEmployee.getUsername()).isPresent())
            new UseException(USER_ALREADY_EXIST);
        User user=new User(createEmployee.getUsername(), passwordEncoder.encode(createEmployee.getPassword()));
        user.getRoles().add(roleRepositories.findByName("ROLE_EMPLOYEE"));
        userRepository.save(user);
        Employee employee = new Employee(createEmployee.getFirstName(),createEmployee.getLastName(),createEmployee.getEmail(),user);
        return  Optional.of(employeeRepository.save(employee));
    }

}
