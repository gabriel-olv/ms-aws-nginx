package br.com.gbrlo.learning.user.api.controller;

import br.com.gbrlo.learning.user.api.dto.UserCreate;
import br.com.gbrlo.learning.user.api.dto.UserCreated;
import br.com.gbrlo.learning.user.api.dto.UserModel;
import br.com.gbrlo.learning.user.api.mapper.UserMapper;
import br.com.gbrlo.learning.user.core.model.User;
import br.com.gbrlo.learning.user.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserCreated create(@RequestBody UserCreate input) {
        logHostFromRequest();
        User created = userService.create(userMapper.toUser(input));
        return userMapper.toUserCreated(created);
    }

    private static void logHostFromRequest() {
        String host = ServletUriComponentsBuilder.fromCurrentRequest().build().getHost();
        logger.info("Request from Host = {}", host);
    }

    @GetMapping
    PagedModel<UserModel> list(@PageableDefault(
            size = 7,
            sort = "id",
            direction = Sort.Direction.ASC) Pageable pageable) {
        logHostFromRequest();
        Page<User> page = userService.list(pageable);
        return new PagedModel<>(page.map(userMapper::toUserModel));
    }
}
