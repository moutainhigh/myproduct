package com.zhumeng.uimei.mapper;

import java.util.List;

import com.zhumeng.uimei.model.UserEntity;

public interface  User1Mapper {

	  List<UserEntity> getAll();

	    void update(UserEntity user);
	    
	    void insert(UserEntity user);

		List<UserEntity> findByParams(UserEntity user);
}
