package com.rmaafs.welcometale.transformers;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import org.objectweb.asm.*;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

/**
 * ClassTransformer that removes the default leave message broadcast.
 * 
 * <p>Targets ONLY:</p>
 * <ul>
 *   <li>Class: {@code PlayerSystems$PlayerRemovedSystem}</li>
 *   <li>Method: {@code onEntityRemoved}</li>
 *   <li>Call: {@code PlayerUtil.broadcastMessageToPlayers(UUID, Message, Store)}</li>
 * </ul>
 * 
 * <p>Uses ASM to replace the INVOKESTATIC instruction with POP instructions
 * to remove the broadcast call without affecting any other functionality.</p>
 * 
 * @author rmaafs
 */
public class LeaveMessageTransformer implements ClassTransformer {

    private static final String TARGET_CLASS = "com/hypixel/hytale/server/core/modules/entity/player/PlayerSystems$PlayerRemovedSystem";
    private static final String PLAYER_UTIL = "com/hypixel/hytale/server/core/universe/world/PlayerUtil";
    private static final String BROADCAST_METHOD = "broadcastMessageToPlayers";
    private static final String TARGET_METHOD = "onEntityRemoved";

    @Override
    public int priority() {
        return 1000;
    }

    @Nullable
    @Override
    public byte[] transform(@Nonnull String pluginName, @Nonnull String className, @Nonnull byte[] classBytes) {
        if (!TARGET_CLASS.equals(className)) {
            return null;
        }

        try {
            System.out.println("[WelcomeTale] Transforming " + className);

            ClassReader reader = new ClassReader(classBytes);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
            ClassVisitor visitor = new MessageRemoverVisitor(writer);

            reader.accept(visitor, 0);

            System.out.println("[WelcomeTale] Successfully transformed PlayerRemovedSystem");
            return writer.toByteArray();

        } catch (Exception e) {
            System.err.println("[WelcomeTale] Failed to transform: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Visits methods in the target class and wraps the target method
     * with our broadcast remover.
     */
    private static class MessageRemoverVisitor extends ClassVisitor {

        public MessageRemoverVisitor(ClassVisitor cv) {
            super(Opcodes.ASM9, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor,
                String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

            if (TARGET_METHOD.equals(name)) {
                return new BroadcastRemoverMethodVisitor(mv);
            }

            return mv;
        }
    }

    /**
     * Intercepts method calls and removes the broadcastMessageToPlayers call
     * by popping its three arguments (UUID, Message, Store) from the stack.
     */
    private static class BroadcastRemoverMethodVisitor extends MethodVisitor {

        public BroadcastRemoverMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM9, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String methodName,
                String descriptor, boolean isInterface) {

            // Detect and remove the broadcastMessageToPlayers call
            if (opcode == Opcodes.INVOKESTATIC &&
                    PLAYER_UTIL.equals(owner) &&
                    BROADCAST_METHOD.equals(methodName)) {

                // Remove the three arguments from stack (LIFO order)
                super.visitInsn(Opcodes.POP); // Store
                super.visitInsn(Opcodes.POP); // Message
                super.visitInsn(Opcodes.POP); // UUID

                return; // Don't call the original method
            }

            // Pass through all other method calls
            super.visitMethodInsn(opcode, owner, methodName, descriptor, isInterface);
        }
    }
}
