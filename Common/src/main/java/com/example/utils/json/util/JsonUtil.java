package com.example.utils.json.util;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JsonUtil <T>  {

  String objectToString (T object) throws JsonProcessingException;
  T StringToObject (String string) throws JsonProcessingException;

}
