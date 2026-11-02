package me.whizvox.skyislands.i18n;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@NotNullByDefault
public class CustomMessageFormatTranslationStore extends CustomTranslationStore<MessageFormat> {

    public CustomMessageFormatTranslationStore(Key name, Locale defaultLocale) {
        super(name, defaultLocale);
    }

    public void register(String key, Locale locale, String pattern) {
        super.register(key, locale, new MessageFormat(pattern, locale));
    }

    public void registerAllPatterns(Locale locale, Map<String, String> translations) {
        translations.forEach((key, component) -> register(key, locale, component));
    }

    public void registerAllPatterns(Locale locale, Set<String> keys, Function<String, String> function) {
        keys.forEach(key -> register(key, locale, function.apply(key)));
    }

    @Override
    public @Nullable MessageFormat translate(String key, Locale locale) {
        return getTranslation(key, locale);
    }

}
