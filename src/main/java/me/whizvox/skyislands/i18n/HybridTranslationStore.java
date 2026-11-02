package me.whizvox.skyislands.i18n;

import com.google.gson.stream.JsonReader;
import me.whizvox.skyislands.SkyIslands;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNullByDefault;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import static me.whizvox.skyislands.SkyIslands.*;

@NotNullByDefault
public class HybridTranslationStore {

    private final Pattern LANGUAGE_CODE_PATTERN = Pattern.compile("[a-z]{2,3}(-[a-zA-Z0-9-]+)?");

    private final CustomMiniMessageTranslationStore componentTranslationStore;
    private final CustomMessageFormatTranslationStore messageFormatTranslationStore;
    private boolean hasRegisteredGlobal;

    public HybridTranslationStore(@KeyPattern.Namespace String namespaceName, Locale defaultLocale, MiniMessage miniMessage) {
        componentTranslationStore = new CustomMiniMessageTranslationStore(
            Key.key(namespaceName, "components"),
            defaultLocale,
            miniMessage
        );
        messageFormatTranslationStore = new CustomMessageFormatTranslationStore(
            Key.key(namespaceName, "message_formats"),
            defaultLocale
        );
        hasRegisteredGlobal = false;
    }

    public void registerGlobal() {
        if (!hasRegisteredGlobal) {
            GlobalTranslator.translator().addSource(componentTranslationStore);
            GlobalTranslator.translator().addSource(messageFormatTranslationStore);
            hasRegisteredGlobal = true;
        }
    }

    public void reload(Path messagesDir) {
        componentTranslationStore.unregisterAll();
        messageFormatTranslationStore.unregisterAll();
        try (var files = Files.list(messagesDir)) {
            files
                .filter(path -> path.getFileName().toString().endsWith(".json"))
                .forEach(path -> {
                    String fileName = path.getFileName().toString();
                    String localeName = fileName.substring(0, fileName.length() - 5);
                    if (LANGUAGE_CODE_PATTERN.matcher(localeName).matches()) {
                        Locale locale = Locale.forLanguageTag(localeName);
                        try (InputStream in = Files.newInputStream(path);
                             JsonReader reader = new JsonReader(new InputStreamReader(in))) {
                            Messages messages = SkyIslands.getInstance().getGson().fromJson(reader, Messages.class);
                            messages.miniMessageTranslations().forEach((key, value) ->
                                componentTranslationStore.register(key, locale, value)
                            );
                            messages.messageFormatTranslations().forEach((key, value) ->
                                messageFormatTranslationStore.register(key, locale, new MessageFormat(value, locale))
                            );
                            info(
                                "Loaded {0} messages from `{1}`",
                                messages.miniMessageTranslations().size() + messages.messageFormatTranslations().size(),
                                fileName
                            );
                        } catch (IOException e) {
                            warning("Could not read language file at `{0}`", path);
                        }
                    } else {
                        warning("Language file name at `{0}` does not represent a valid locale", path);
                    }
                });
        } catch (IOException e) {
            severe(e, "Could not read language files at `{0}`", messagesDir);
        }
    }

}
