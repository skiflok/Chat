package com.example.repositories.messageRepositories;

import com.example.model.message.Message;
import com.example.repositories.CrudRepository;
import java.util.List;

public interface MessageRepository extends CrudRepository<Message> {

  List<Message> findLast30(Long chatId);

}