package com.jjy.game.hall.tcp.login;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.manager.RoleManager;
import com.jjy.game.hall.manager.UserManager;
import com.jjy.game.hall.script.IRoleScript;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallLoginMessage;
import com.jjy.game.message.hall.HallLoginMessage.LoginRequest;
import com.jjy.game.model.constant.Config;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.mongo.hall.dao.RoleDao;
import com.jjy.game.model.mongo.hall.dao.UserDao;
import com.jjy.game.model.redis.channel.HallChannel;
import com.jjy.game.model.redis.key.HallKey;
import com.jjy.game.model.struct.Role;
import com.jjy.game.model.struct.User;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.jedis.JedisPubSubMessage;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.engine.util.JsonUtil;

/**
 * 登陆
 *
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
@HandlerEntity(mid = MID.LoginReq_VALUE, desc = "登陆", thread = ThreadType.IO, msg = LoginRequest.class)
public class LoginHandler extends TcpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

	@Override
	public void run() {
		LoginRequest request = getMsg();

		switch (request.getLoginType()) {
		case ACCOUNT:
			loginWithAccount(request);
			break;
		}
	}

	private void loginWithAccount(LoginRequest request) {
		LOGGER.info("用户：{}登录", request.getAccount());
		if (request.getAccount() == null || request.getPassword() == null) { // TODO
																				// 验证
			return;
		}

		User user = UserDao.findByAccount(request.getAccount());

		if (user == null) {
			user = UserManager.getInstance().createUser(u -> {
				u.setAccunt(request.getAccount());
				u.setPassword(request.getPassword());
			});
		}
		UserDao.saveUser(user);

		Role role = RoleDao.getRoleByUserId(user.getId());
		if (role == null) {
			role = RoleManager.getInstance().createUser(user.getId(), r -> {
				r.setNick("jzy");
				r.setGem(1000);
			});
		}else {
			//以redis数据为准
			Map<String, String> hgetAll = JedisManager.getJedisCluster().hgetAll(HallKey.Role_Map_Info.getKey(role.getId()));
			JsonUtil.map2Object(hgetAll, role);
		}
		LOGGER.debug("{}_key:{}", role.getNick(), HallKey.Role_Map_Info.getKey(role.getId()));

		RoleManager.getInstance().login(role,Reason.UserLogin);
		
		// 广播给其他服务器
		JedisPubSubMessage message = new JedisPubSubMessage(role.getId(), Config.SERVER_ID);
		JedisManager.getJedisCluster().publish(HallChannel.LoginHall.name(), message.toString());

		HallLoginMessage.LoginResponse.Builder builder = HallLoginMessage.LoginResponse.newBuilder();
		builder.setIsOk(true);
		builder.setUid(user.getId());
		builder.setRid(role.getId());
		builder.setSessionId(request.getSessionId());
		sendIdMsg(builder.build());

	}

}
