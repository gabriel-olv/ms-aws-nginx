package br.com.gbrlo.learning.user.core.service;

import br.com.gbrlo.learning.user.core.model.User;
import br.com.gbrlo.learning.user.infra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> list(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
