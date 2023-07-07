package com.example.repositories.userRepositories;

import com.example.model.User;
import com.example.repositories.CrudRepository;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User> {
  Optional<User> findByName(String name);
}
