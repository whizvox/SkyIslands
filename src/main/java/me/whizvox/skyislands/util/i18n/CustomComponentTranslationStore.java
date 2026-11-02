package me.whizvox.skyislands.util.i18n;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

@NotNullByDefault
public class CustomComponentTranslationStore extends CustomTranslationStore<Component> {

    public CustomComponentTranslationStore(Key name, Locale defaultLocale) {
        super(name, defaultLocale);
    }

    @Override
    public @Nullable MessageFormat translate(String key, Locale locale) {
        return null;
    }

    @Override
    public @Nullable Component translate(TranslatableComponent component, Locale locale) {
        Component translatedComponent = getTranslation(component.key(), locale);
        if (translatedComponent != null) {
            return translatedComponent.append(component.children());
        }
        return null;
    }

}
