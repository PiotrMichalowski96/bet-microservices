package com.piter.api.commons.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequence")
public record DatabaseSequence (
  @Id
  String id,
  Long seq
) {

}
