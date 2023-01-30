package com.dijta.common.dijtasequence.transaction


import com.dijta.common.identity.BaseEntityService
import com.google.common.collect.ImmutableList

public interface IUserService extends BaseEntityService<UserDto, UserEntity> {

    ImmutableList<UserEntity> findAllUsers();

    UserEntity save(UserEntity entity);

    UserDto saveDto(UserDto userDto);
}
