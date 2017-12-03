package com.jzy.game.bydr.world.manager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import com.jzy.game.bydr.manager.ConfigManager;
import com.jzy.game.bydr.struct.Team;

/**
 * 组队管理
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月3日 上午11:42:22
 */
public class TeamManager {
	private static volatile TeamManager teamManager;
	private Map<Long, Team> teams = new ConcurrentHashMap<>();

	private TeamManager() {

	}

	public static TeamManager getInstance() {
		if (teamManager == null) {
			synchronized (TeamManager.class) {
				if (teamManager == null) {
					teamManager = new TeamManager();
				}
			}
		}
		return teamManager;
	}

	public Team getIdleTeam(int rank) {
		Optional<Team> optional = teams.values().stream().filter(t -> t.getRank() == rank && t.getStatus() == 0
				&& t.getRoleIds().size() < ConfigManager.getInstance().getGameConfig().getRoomSize()).findAny();
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	public Map<Long, Team> getTeams() {
		return teams;
	}

}
