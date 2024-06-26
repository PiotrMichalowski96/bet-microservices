package com.piter.match.api.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.piter.api.commons.domain.DatabaseSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

  public static final long DEFAULT_ID = 1L;

  private final ReactiveMongoOperations mongoOperations;

  public Mono<Long> generateSequenceMatchId(String seqName) {
    return mongoOperations.findAndModify(
            Query.query(where("_id").is(seqName)),
            new Update().inc("seq", 1),
            options().returnNew(true).upsert(true),
            DatabaseSequence.class)
        .map(DatabaseSequence::seq)
        .defaultIfEmpty(DEFAULT_ID);
  }
}
