package me.whizvox.skyislands.util;

import org.joml.Vector2i;

public record Box2d(int x1, int z1, int x2, int z2) {

    public Vector2i getCenter() {
        return new Vector2i((x1 + x2) / 2, (z1 + z2) / 2);
    }

}
