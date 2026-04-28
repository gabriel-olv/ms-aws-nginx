package br.com.gbrlo.learning.user.api.mapper;

import br.com.gbrlo.learning.user.api.dto.UserCreate;
import br.com.gbrlo.learning.user.api.dto.UserCreated;
import br.com.gbrlo.learning.user.api.dto.UserModel;
import br.com.gbrlo.learning.user.core.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreate userCreate);

    UserCreated toUserCreated(User user);

    UserModel toUserModel(User user);
}
