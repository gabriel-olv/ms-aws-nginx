package br.com.gbrlo.learning.user.infra.repository;

import br.com.gbrlo.learning.user.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
