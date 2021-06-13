package com.piter.bets.league.eurobets.mapper;

import com.piter.bets.league.eurobets.dto.UserDTO;
import com.piter.bets.league.eurobets.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

//  @Mapping(target = "id", source = "id")
//  @Mapping(target = "username", source = "username")
//  @Mapping(target = "points", source = "points")
  UserDTO toUserDTO(User user);
}
