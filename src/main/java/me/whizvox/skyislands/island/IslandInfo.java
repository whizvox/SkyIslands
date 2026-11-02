package me.whizvox.skyislands.island;

import me.whizvox.skyislands.util.Box2d;

public record IslandInfo(int id,
                         Island island,
                         Box2d plotArea,
                         Box2d buildArea) {
}
