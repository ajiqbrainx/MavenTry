package com.dijta.common.dijtasequence.transaction

import com.dijta.common.identity.AbstractBaseEntityService
import com.dijta.common.identity.DijtaConverters
import com.google.common.collect.ImmutableList
import org.dozer.DozerBeanMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class UserServiceImpl extends AbstractBaseEntityService<UserDto, UserEntity> implements IUserService {

    @Autowired
    private final UserRepository repository;

    protected UserServiceImpl(UserRepository repository,
                              DozerBeanMapper mapper) {
        //this.repository = repository;
        super(repository, DijtaConverters.converter(mapper, UserDto.class, UserEntity.class))
    }

    @Override
    @Transactional(readOnly = true)
    ImmutableList<UserEntity> findAllUsers() {
        return repository.findAll().stream().collect(ImmutableList.toImmutableList())
    }

    @Override
    //@Transactional
    UserEntity save(UserEntity entity) {
        println("..........UserEntity inside save call:" + entity)
        println("..........addressEntity inside save call:" + entity.address)
        UserEntity userEntity = repository.save(entity);
        println("..........UserEntity inside after save call:" + entity)
        println("..........UserEntity.pkId inside after save call:" + entity.pkId)
        println("..........addressEntity inside after save call:" + entity.address)
        return userEntity;
    }

    @Override
    @Transactional
    UserDto saveDto(UserDto userDto) {
        println("..........userDto before save call:" + userDto)
        println("..........userDto.pkId before save call:" + userDto.pkId)
        println("..........userDto.addressDto before save call:" + userDto.address)
        UserDto userDtoNew = super.save(userDto);
        println("..........userDtoNew after save call:" + userDtoNew)
        println("..........userDtoNew.pkId after save call:" + userDtoNew.pkId)
        println("..........userDtoNew.addressDto after save call:" + userDtoNew.address)
        println("..........userDtoNew.contactList after save call:" + userDtoNew.contactList)
        return userDtoNew;
    }


    @Override
    @Transactional(readOnly = true)
    List<UserDto> getAll() {
        return super.getAll().stream().collect(ImmutableList.toImmutableList())
    }
}
