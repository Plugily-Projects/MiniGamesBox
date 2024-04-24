/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.minigamesbox.classic.handlers.reward;

import plugily.projects.minigamesbox.api.handlers.reward.IRewardType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 07.10.2021
 */
public class RewardType implements IRewardType {

  private static final Map<String, RewardType> rewardTypes = new HashMap<>();


  static {
    rewardTypes.put("END_GAME", new RewardType("game-end", true));
    rewardTypes.put("START_GAME", new RewardType("game-start", true));
  }

  private final String path;
  private final ExecutorType executorType;
  private final boolean protectedOption;

  public RewardType(String path, ExecutorType executorType, boolean protectedOption) {
    this.path = path;
    this.executorType = executorType;
    this.protectedOption = protectedOption;
  }

  public RewardType(String path) {
    this.path = path;
    this.executorType = ExecutorType.DEFAULT;
    this.protectedOption = false;
  }

  public RewardType(String path, boolean protectedOption) {
    this.path = path;
    this.executorType = ExecutorType.DEFAULT;
    this.protectedOption = protectedOption;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public ExecutorType getExecutorType() {
    return executorType;
  }

  @Override
  public boolean isProtected() {
    return protectedOption;
  }

  public static Map<String, RewardType> getRewardTypes() {
    return Collections.unmodifiableMap(rewardTypes);
  }
}
