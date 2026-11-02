package me.whizvox.skyislands.i18n;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

@NotNullByDefault
public class CustomMiniMessageTranslationStore extends CustomTranslationStore<String> {

    private final MiniMessageTranslator translator;

    public CustomMiniMessageTranslationStore(Key name, Locale defaultLocale, MiniMessage miniMessage) {
        super(name, defaultLocale);
        translator = new MiniMessageTranslator(miniMessage) {
            @Override
            protected @Nullable String getMiniMessageString(String key, Locale locale) {
                return getTranslation(key, locale);
            }
            @Override
            public Key name() {
                return name;
            }
        };
    }

    @Override
    public @Nullable MessageFormat translate(String key, Locale locale) {
        return null;
    }

    @Override
    public @Nullable Component translate(TranslatableComponent component, Locale locale) {
        return translator.translate(component, locale);
    }

}
