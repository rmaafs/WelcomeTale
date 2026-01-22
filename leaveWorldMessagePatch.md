# Leave World Message Patch Documentation

## The Problem

By default, the Hytale server prints a message when a player leaves a world:

```
{username} has left {world}
```

This message originates from the server's language files located at:

```
Assets/Server/Languages/{language}/server.lang
```

Specifically, this line:

```properties
general.playerLeftWorld = {username} has left {world}
```

## Source Code Analysis

The broadcast is triggered in the file:

```
com/hypixel/hytale/server/core/modules/entity/player/PlayerSystems.java
```

Inside the `PlayerRemovedSystem` class, the `onEntityRemoved` method executes when a player leaves a world. The problematic broadcast happens on the last line:

```java
@Override
public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
    World world = store.getExternalData().getWorld();
    PlayerRef playerRefComponent = holder.getComponent(PlayerRef.getComponentType());
    // ... (component initialization code)

    LOGGER.at(Level.INFO).log("Removing player '%s%s' from world '%s' (%s)",
        playerRefComponent.getUsername(),
        displayName != null ? " (" + displayName.getAnsiMessage() + ")" : "",
        world.getName(),
        playerRefComponent.getUuid());

    // ... (player data saving code)

    // ⚠️ THIS IS THE LINE WE WANT TO DISABLE:
    PlayerUtil.broadcastMessageToPlayers(
        playerRefComponent.getUuid(),
        Message.translation("server.general.playerLeftWorld")
            .param("username", playerRefComponent.getUsername())
            .param("world", worldConfig.getDisplayName() != null
                ? worldConfig.getDisplayName()
                : WorldConfig.formatDisplayName(world.getName())),
        store
    );
}
```

## Why Can't We Just Disable It Like Join Messages?

Unlike leave messages, Hytale **does provide a native way** to disable join messages through the plugin API:

```java
private void onPlayerJoinWorld(AddPlayerToWorldEvent event) {
    event.setBroadcastJoinMessage(false);
}
```

Unfortunately, there is **no equivalent method** for leave messages. The events don't expose a method to suppress the default broadcast.

## The Solution: Early Plugins

Until Hytale implements a native way to disable leave messages, the only solution is to use Hytale's **earlyplugins** system.

### What are Early Plugins?

Early plugins are loaded **before the server starts**, allowing them to modify server behavior at the bytecode level using class transformers. This is an **official feature** provided by Hytale for advanced server modifications.

### How Our Patch Works

Our transformer surgically removes the `PlayerUtil.broadcastMessageToPlayers` call from the `onEntityRemoved` method without affecting any other functionality:

```java
// Original bytecode:
aload_1           // Load UUID onto stack
aload_2           // Load Message onto stack
aload_3           // Load Store onto stack
invokestatic      // Call PlayerUtil.broadcastMessageToPlayers(...)

// After transformation:
pop               // Remove Store from stack
pop               // Remove Message from stack
pop               // Remove UUID from stack
// (method call removed)
```

**Important:** The transformer only modifies this specific broadcast call. All other server functionality, including the logger message on line 218, remains completely untouched.

## Installation

### Option 1: Automatic Installation (Recommended)

1. Install the WelcomeTale plugin on your server
2. Run the command in-game:
   ```
   /welcometalepatch
   ```
3. Read the warning message carefully
4. Run the command again within 10 minutes to confirm
5. The patch will be automatically installed to `earlyplugins/LeaveMessageTransformer.jar`
6. Restart your server

### Option 2: Manual Installation

1. Extract the `LeaveMessageTransformer.jar` from inside the WelcomeTale plugin
2. Create an `earlyplugins/` directory in your server root (if it doesn't exist)
3. Copy `LeaveMessageTransformer.jar` to `earlyplugins/`
4. Restart your server

## What to Expect After Installation

### On Server Startup

When you restart your server, you'll see this message in the terminal:

```
[EarlyPlugin] Found: LeaveMessageTransformer.jar
[EarlyPlugin] Loading transformer: com.rmaafs.welcometale.transformers.LeaveMessageTransformer (priority=1000)
===============================================================================================
                              Loaded 1 class transformer(s)!!
===============================================================================================
                       This is unsupported and may cause stability issues.
                                     Use at your own risk!!
===============================================================================================
Press ENTER to accept and continue...
```

This is Hytale's standard warning for early plugins. If you agree to use the patch, **press ENTER** to start the server normally.

### Suppressing the Confirmation Prompt

If you want to avoid pressing ENTER every time you start the server, add the `--accept-early-plugins` flag:

```bash
java -jar HytaleServer.jar --accept-early-plugins --assets Assets.zip
```

### During Runtime

Once the server starts, you'll see this confirmation message:

```
[WelcomeTale] Transforming com/hypixel/hytale/server/core/modules/entity/player/PlayerSystems$PlayerRemovedSystem
[WelcomeTale] Successfully transformed PlayerRemovedSystem
```

From this point forward, the default leave messages will no longer appear. Players leaving worlds will only trigger your custom leave messages configured in WelcomeTale.

## Safety and Transparency

### What This Patch Does

✅ **Only disables** the default "player left world" broadcast  
✅ **Does not affect** any other server functionality  
✅ **Does not modify** player data, world state, or logging  
✅ **Uses official** Hytale early plugin system  
✅ **Fully documented** and open source

### What This Patch Does NOT Do

❌ Does not modify player data or world files  
❌ Does not affect join messages or other broadcasts  
❌ Does not introduce new features or commands  
❌ Does not collect any data  
❌ Does not communicate with external servers

### Technical Guarantees

The transformer is **surgically precise**:

- **Target class:** `PlayerSystems$PlayerRemovedSystem` (no other classes)
- **Target method:** `onEntityRemoved` (no other methods)
- **Target call:** `PlayerUtil.broadcastMessageToPlayers` (no other calls)

The complete source code with detailed documentation is available in the [LeaveMessageTransformer.java](src/main/java/com/rmaafs/welcometale/transformers/LeaveMessageTransformer.java) file.

## Uninstalling the Patch

To remove the patch:

1. Stop your server
2. Delete `earlyplugins/LeaveMessageTransformer.jar`
3. Start your server normally

The default leave messages will be restored immediately.

## Disclaimer

This patch uses Hytale's official early plugin system, which is provided for advanced server modifications. However, class transformation is inherently sensitive. While this patch is designed to be safe and minimal:

- ⚠️ Use at your own risk
- ⚠️ Test in a development environment first
- ⚠️ Keep backups of your server
- ⚠️ This is a temporary solution until Hytale adds native support

**Note:** This patch will become obsolete if Hytale adds a native method to disable leave messages (similar to `setBroadcastJoinMessage`). We will update WelcomeTale accordingly when that happens.
