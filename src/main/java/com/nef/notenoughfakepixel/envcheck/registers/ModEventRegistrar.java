package com.nef.notenoughfakepixel.envcheck.registers;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Consumer;

public class ModEventRegistrar {

    private static final String BASE_PACKAGE = "com.nef.notenoughfakepixel";

    private static final Reflections REFS = new Reflections(new ConfigurationBuilder()
            .forPackages(BASE_PACKAGE)
            .addScanners(new TypeAnnotationsScanner(), new FieldAnnotationsScanner()));

    public static void registerModEvents() {
        registerAnnotatedTypes(REFS, RegisterEvents.class, Object.class,
                ModEventRegistrar::registerForgeEventListener);

        registerAnnotatedInstances(REFS, RegisterInstance.class, ModEventRegistrar::routeInstance);
    }

    public static void registerCommands() {
        registerAnnotatedTypes(REFS, RegisterCommand.class, ICommand.class,
                ModEventRegistrar::registerCommand);

        registerAnnotatedInstances(REFS, RegisterInstance.class, ModEventRegistrar::routeInstance);
    }

    public static void registerKeybinds() {
        Set<Field> fields = REFS.getFieldsAnnotatedWith(RegisterKeybind.class);
        for (Field field : fields) {
            try {
                if (!Modifier.isStatic(field.getModifiers())) {
                    System.out.println("[NEF Keybind] " + field.getDeclaringClass().getName() + "." + field.getName()
                            + " should be static to register as @RegisterKeybind.");
                    continue;
                }
                if (!KeyBinding.class.isAssignableFrom(field.getType())) {
                    System.out.println("[NEF Keybind] " + field.getName() + " is not a KeyBind.");
                    continue;
                }
                if (!field.isAccessible()) field.setAccessible(true);
                KeyBinding key = (KeyBinding) field.get(null);
                if (key == null) {
                    System.out.println("[NEF Keybind] " + field.getName() + " is null (not initialized).");
                    continue;
                }
                ClientRegistry.registerKeyBinding(key);
            } catch (Throwable t) {
                System.out.println("[NEF Keybind] Failed field register: " + field.getName());
                t.printStackTrace();
            }
        }
    }

    // Generic Core

    private static <A extends Annotation, T> void registerAnnotatedTypes(
            Reflections refs,
            Class<A> annotation,
            Class<T> expectedType,
            Consumer<T> registrar
    ) {
        refs.getTypesAnnotatedWith(annotation).forEach(clazz -> {
            try {
                if (!expectedType.isAssignableFrom(clazz)) {
                    System.out.println("[NEF AutoRegister] " + clazz.getName() + " does not implements " + expectedType.getName()
                            + " for @" + annotation.getSimpleName());
                    return;
                }
                Object instance = newInstance(clazz);
                registrar.accept(expectedType.cast(instance));
            } catch (Throwable t) {
                System.out.println("[NEF AutoRegister] Failed class register: " + clazz.getName());
                t.printStackTrace();
            }
        });
    }

    private static void registerAnnotatedInstances(
            Reflections refs,
            Class<? extends Annotation> instanceAnnotation,
            Consumer<Object> consumer
    ) {
        Set<Field> fields = refs.getFieldsAnnotatedWith(instanceAnnotation);
        for (Field field : fields) {
            try {
                if (!Modifier.isStatic(field.getModifiers())) {
                    System.out.println("[NEF AutoRegister] " + field.getDeclaringClass().getName() + "." + field.getName()
                            + " should be static for @" + instanceAnnotation.getSimpleName());
                    continue;
                }
                if (!field.isAccessible()) field.setAccessible(true);
                Object instance = field.get(null);
                if (instance == null) {
                    System.out.println("[NEF AutoRegister] " + field.getName() + " is null (not initialized singleton).");
                    continue;
                }
                consumer.accept(instance);
            } catch (Throwable t) {
                System.out.println("[NEF AutoRegister] Failed field register: " + field.getName());
                t.printStackTrace();
            }
        }
    }

    // Registers

    private static Object newInstance(Class<?> clazz) throws Exception {
        Constructor<?> c = clazz.getDeclaredConstructor();
        if (!c.isAccessible()) c.setAccessible(true);
        return c.newInstance();
    }

    private static void registerForgeEventListener(Object listener) {
        MinecraftForge.EVENT_BUS.register(listener);
    }

    private static void registerCommand(ICommand command) {
        ClientCommandHandler.instance.registerCommand(command);
    }

    private static void routeInstance(Object instance) {
        if (instance instanceof ICommand) {
            registerCommand((ICommand) instance);
        } else {
            registerForgeEventListener(instance);
        }
    }

}
