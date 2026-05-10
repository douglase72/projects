package com.erdouglass.emdb.user.repository;

import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.user.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

}
