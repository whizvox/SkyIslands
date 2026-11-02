package me.whizvox.skyislands.util.i18n;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public record Messages(Map<String, String> miniMessageTranslations, Map<String, String> messageFormatTranslations) {

    public Messages() {
        this(new Object2ObjectArrayMap<>(), new Object2ObjectArrayMap<>());
    }

}
