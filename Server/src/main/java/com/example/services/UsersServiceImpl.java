package com.example.services;


import com.example.model.User;
import com.example.repositories.userRepositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsersServiceImpl implements UsersService {

  private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

  private final UserRepository usersRepository;
  private final PasswordEncoder encoder;

  @Autowired
  public UsersServiceImpl(
      UserRepository usersRepository,
      PasswordEncoder encoder
  ) {
    this.usersRepository = usersRepository;
    this.encoder = encoder;
  }

  @Override
  public synchronized String signUp(User user) {

    if (user.getName().isEmpty()) {
      throw new IllegalArgumentException("Name isEmpty");
    }

    if (usersRepository.findByName(user.getName()).isPresent()) {
      throw new IllegalArgumentException("Name " + user.getName() +  " is already register");
    }

    user.setPassword(encoder.encode(user.getPassword()));
    logger.info("user = {}", user);
    logger.info(user.getPassword());
    usersRepository.save(user);
    return "Successful!";
  }

}