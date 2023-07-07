package com.example.server.menu.command;

import com.fasterxml.jackson.core.JsonProcessingException;

@FunctionalInterface
public interface Command {
  void execute() throws JsonProcessingException;
}
