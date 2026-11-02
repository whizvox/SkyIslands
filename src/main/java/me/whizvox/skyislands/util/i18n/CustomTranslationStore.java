package me.whizvox.skyislands.util.i18n;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationStore;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@NotNullByDefault
public abstract class CustomTranslationStore<T> implements TranslationStore<T> {

    /*
     * AbstractTranslationStore does a couple of things I don't like. I want to be able to reload all translations
     * without a server restart, hence the existence of unregister(), and remove some bloat that I don't care about.
     */

    private final Key name;
    private final Map<String, Translation> translations;
    private Locale defaultLocale;

    public CustomTranslationStore(Key name, Locale defaultLocale) {
        this.name = name;
        this.defaultLocale = defaultLocale;
        translations = new Object2ObjectOpenHashMap<>();
    }

    protected @Nullable T getTranslation(String key, Locale locale) {
        Translation translation = translations.get(key);
        if (translation != null) {
            return translation.translate(locale);
        }
        return null;
    }

    @Override
    public Key name() {
        return name;
    }

    @Override
    public void defaultLocale(Locale locale) {
        this.defaultLocale = locale;
    }

    @Override
    public boolean contains(String key) {
        return translations.containsKey(key);
    }

    @Override
    public boolean contains(String key, Locale locale) {
        return translations.containsKey(key) && locale == this.defaultLocale;
    }

    @Override
    public void register(String key, Locale locale, T translation) {
        translations.computeIfAbsent(key, _ -> new Translation()).register(locale, translation);
    }

    @Override
    public void registerAll(Locale locale, Map<String, T> translations) {
        translations.forEach((key, component) -> register(key, locale, component));
    }

    @Override
    public void registerAll(Locale locale, Set<String> keys, Function<String, T> function) {
        keys.forEach(key -> register(key, locale, function.apply(key)));
    }

    @Override
    public void unregister(String key) {
        translations.remove(key);
    }

    public void unregisterAll() {
        translations.clear();
    }

    private final class Translation {

        private final Map<Locale, T> translations;

        Translation() {
            this.translations = new Object2ObjectArrayMap<>();
        }

        @Nullable T translate(Locale locale) {
            T comp = translations.get(locale);
            if (comp != null) {
                comp = translations.get(Locale.of(locale.getLanguage()));
                if (comp == null) {
                    comp = translations.get(defaultLocale);
                }
            }
            return comp;
        }

        void register(Locale locale, T translation) {
            translations.put(requireNonNull(locale, "locale"), requireNonNull(translation, "translation"));
        }

    }

}
