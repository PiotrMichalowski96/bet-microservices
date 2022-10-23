package com.piter.bet.api.repository;

import com.piter.api.commons.domain.Bet;
import com.piter.bet.api.model.UserResultProjection;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BetRepository extends ReactiveMongoRepository<Bet, Long> {

  Flux<Bet> findAllByMatchId(Long matchId);

  Flux<Bet> findAllByUserNickname(String nickname);

  @Aggregation(pipeline = {
      "{$group: {_id: {user: '$user'}, points: {$sum: '$betResult.points'}}}",
      "{$project: {user: '$_id.user', points: 1, _id: 0}}",
      "{$sort: {points: -1}}"
  })
  Flux<UserResultProjection> findAllUsersResults();

  @Aggregation(pipeline = {
      "{$match: {\"user.nickname\": {$eq: ?0}}}",
      "{$group: {_id: {user: '$user'}, points: {$sum: '$betResult.points'}}}",
      "{$project: {user: '$_id.user', points: 1, _id: 0}}"
  })
  Mono<UserResultProjection> findUserResultByUsersNickname(String nickname);
}
