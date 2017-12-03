package com.jzy.game.hall.script.user;

import java.util.function.Consumer;
import com.jzy.game.model.mongo.hall.dao.HallInfoDao;
import com.jzy.game.model.struct.User;
import com.jzy.game.hall.script.IUserScript;

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
