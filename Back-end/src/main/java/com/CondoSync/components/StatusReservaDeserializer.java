package com.CondoSync.components;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.CondoSync.models.StatusReserva;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class StatusReservaDeserializer extends JsonDeserializer<StatusReserva> {
  @Override
  public StatusReserva deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JsonProcessingException {
    String status = jsonParser.getText();
    return StatusReserva.fromString(status);
  }
}
