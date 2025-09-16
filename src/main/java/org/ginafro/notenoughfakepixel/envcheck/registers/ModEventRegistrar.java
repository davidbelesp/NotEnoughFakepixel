package org.ginafro.notenoughfakepixel.envcheck.registers;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.ginafro.notenoughfakepixel.utils.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.function.Consumer;

public class ModEventRegistrar {

    private static final String BASE_PACKAGE = "org.ginafro.notenoughfakepixel";

    public static void registerModEvents() {
        registerAnnotated(BASE_PACKAGE, RegisterEvents.class, MinecraftForge.EVENT_BUS::register);
    }

    public static void registerCommands() {
        registerAnnotated(BASE_PACKAGE, RegisterCommand.class, instance ->
                ClientCommandHandler.instance.registerCommand((ICommand) instance));
    }

    private static void registerAnnotated(String basePackage, Class<? extends Annotation> annotation, Consumer<Object> registrar) {
        new Reflections(basePackage)
                .getTypesAnnotatedWith(annotation)
                .forEach(clazz -> {
                    try {
                        registrar.accept(clazz.newInstance());
                    } catch (Exception e) {
                        Logger.logErrorConsole("Failed to register: " + clazz.getName());
                        e.printStackTrace();
                    }
                });

        // Register classes that already have instances (e.g. singletons), classes annotated with @RegisterInstance. search for method getInstance()
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackage)
                .addScanners(new FieldAnnotationsScanner()));
        Set<Field> fields = reflections.getFieldsAnnotatedWith(RegisterInstance.class);
        for (Field field : fields) {
            try {
                Object instance = field.get(null); // static field, so null instance
                registrar.accept(instance);
            } catch (Exception e) {
                Logger.logErrorConsole("Failed to register instance from field: " + field.getName());
                e.printStackTrace();
            }
        }

    }

    public static void registerKeybinds() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(BASE_PACKAGE)
                .addScanners(new FieldAnnotationsScanner()));

        reflections.getTypesAnnotatedWith(RegisterKeybind.class);

        Set<Field> fields = reflections.getFieldsAnnotatedWith(RegisterKeybind.class);
        for (Field field : fields) {
            try {
                if (KeyBinding.class.isAssignableFrom(field.getType())) {
                    KeyBinding key = (KeyBinding) field.get(null); // static field, so null instance
                    ClientRegistry.registerKeyBinding(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
