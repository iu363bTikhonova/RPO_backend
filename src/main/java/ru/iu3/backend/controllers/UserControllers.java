package ru.iu3.backend.controllers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.User;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.UserRepository;
import ru.iu3.backend.tools.DataValidationException;
import ru.iu3.backend.tools.Utils;


@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class UserControllers {

    @Autowired
    UserRepository usersRepository;

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return usersRepository.findAll(
                PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "login")));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") Long userID)
            throws DataValidationException {
        User user = usersRepository.findById(userID).
                orElseThrow(() -> new DataValidationException("User not founding"));

        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUsers(@RequestBody User users) throws DataValidationException {
        try {
            User nc = usersRepository.save(users);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        } catch (Exception exception) {
            if (exception.getMessage().contains("ConstraintViolationException")) {
                throw new DataValidationException("Этот пользователь есть в БД");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }

    @PostMapping("/users/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userID,
                                             @Validated @RequestBody Set<Museum> museums) {

        Optional<User> uu = usersRepository.findById(userID);
        int cnt = 0;

        if (uu.isPresent()) {
            User u = uu.get();


            for(Museum m: museums) {
                Optional<Museum> mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.addMuseum(mm.get());
                    ++cnt;
                }
            }


            usersRepository.save(u);
        }


        Map<String, String> response = new HashMap<>();
        response.put("added", String.valueOf(cnt));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId,
                                                @Validated @RequestBody Set<Museum> museums) {
        Optional<User> uu = usersRepository.findById(userId);
        int cnt = 0;

        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m: museums) {
                u.removeMuseum(m);
                ++cnt;
            }

            usersRepository.save(u);
        }


        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));

        return ResponseEntity.ok(response);
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUsers(@PathVariable(value = "id") Long userId,
                                            @RequestBody User userDetails) throws DataValidationException {
        try {
            User user = usersRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException("Пользователь с таким индексом не найден"));
            user.login = userDetails.login;
            user.email = userDetails.email;

            String np = userDetails.np;
            System.out.println("Новый пароль (если вдруг забудешь): " + np);
            if (np != null && !np.isEmpty()) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                String salt = new String(Hex.encode(b));
                user.password = Utils.ComputeHash(np, salt);
                user.salt = salt;
            }

            usersRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception exception) {
            if (exception.getMessage().contains("ConstraintViolationException")) {
                throw new DataValidationException("Этот пользователь уже есть в базе");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }

    @PostMapping("/deleteusers")
    public ResponseEntity deleteUsers(@Validated @RequestBody List<User> users) {
        usersRepository.deleteAll(users);
        return new ResponseEntity(HttpStatus.OK);
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUsers(@PathVariable(value = "id") Long userId) {
        Optional<User> users = usersRepository.findById(userId);
        Map<String, Boolean> resp = new HashMap<>();


        if (users.isPresent()) {
            usersRepository.delete(users.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }

        return ResponseEntity.ok(resp);
    }
}