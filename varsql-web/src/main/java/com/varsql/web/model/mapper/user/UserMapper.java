package com.varsql.web.model.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.varsql.web.dto.user.UserResponseDTO;
import com.varsql.web.model.entity.user.UserEntity;
import com.varsql.web.model.mapper.base.GenericMapper;
import com.varsql.web.model.mapper.base.IgnoreUnmappedMapperConfig;

@Mapper(componentModel = "spring", config = IgnoreUnmappedMapperConfig.class)
public interface UserMapper extends GenericMapper<UserResponseDTO, UserEntity> {
	UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );
}