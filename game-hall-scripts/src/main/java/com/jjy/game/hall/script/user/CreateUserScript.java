package com.jjy.game.hall.script.user;

import java.util.function.Consumer;

import com.jjy.game.hall.script.IUserScript;
import com.jjy.game.model.mongo.hall.dao.HallInfoDao;
import com.jjy.game.model.struct.User;

public class CreateUserScript implements IUserScript {

	@Override
	public User createUser(Consumer<User> userConsumer) {
		User user = new User();
		user.setId(HallInfoDao.getUserId());
		if (userConsumer != null) {
			userConsumer.accept(user);
		}
		return user;
	}

}
