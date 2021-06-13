package com.piter.bets.league.eurobets.repository;

import com.piter.bets.league.eurobets.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
