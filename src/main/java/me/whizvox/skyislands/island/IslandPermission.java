package me.whizvox.skyislands.island;

import me.whizvox.skyislands.util.StringHelper;

public enum IslandPermission {

  BUILD,
  INTERACT_BLOCKS,
  INTERACT_CONTAINERS,
  INTERACT_ENTITIES,
  HURT_FRIENDLY_ENTITIES,
  HURT_HOSTILE_ENTITIES,
  HURT_NEUTRAL_ENTITIES,
  HURT_OTHER_ENTITIES;

  public final String str;

  IslandPermission() {
    str = StringHelper.snakeCaseToLowerCamelCase(name());
  }

  @Override
  public String toString() {
    return str;
  }

}
