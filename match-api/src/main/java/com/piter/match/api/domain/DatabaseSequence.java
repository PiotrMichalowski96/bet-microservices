package com.piter.match.api.domain;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Document(collection = "database_sequence")
public class DatabaseSequence {
  @Id
  String id;
  Long seq;
}
