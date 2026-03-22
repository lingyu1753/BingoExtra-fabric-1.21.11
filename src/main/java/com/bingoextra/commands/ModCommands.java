package com.bingoextra.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

public class ModCommands {
    public static final String POS = "pos";
    public static final String GET = "get";
    public static final String SHARE = "share";
    public static final String DISPLAY = "display";
    public static final String ON = "on";
    public static final String OFF = "off";
    public static final String REJOIN = "rejoin";
    public static final String BINGO = "bingo";
    public static final String MODE = "mode";
    public static final String EASY = "easy";
    public static final String HARD = "hard";


    public static void registerPosGetCommand() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(Commands.literal(POS)
                                                    .then(Commands.literal(GET)
                                                                  .executes(context -> {
                                                                      Player player = context.getSource().getPlayer();
                                                                      if (player != null) {
                                                                          String playerName = player.getName().getString();
                                                                          BlockPos pos = player.getOnPos();
                                                                          ResourceKey<Level> dimension = context.getSource().getLevel().dimension();
                                                                          String info;
                                                                          info = dimension == Level.OVERWORLD ?
                                                                                 String.format("§f<%s> §f[§a主世界§f] §b%d %d %d §f-> §f[§c下界§f] §b%d %d %d ", playerName, pos.getX(), pos.getY(), pos.getZ(), pos.getX() / 8, pos.getY(), pos.getZ() / 8) :
                                                                                 dimension == Level.NETHER ?
                                                                                 String.format("§f<%s> §f[§c下界§f] §b%d %d %d §f-> §f[§a主世界§f] §b%d %d %d ", playerName, pos.getX(), pos.getY(), pos.getZ(), pos.getX() * 8, pos.getY(), pos.getZ() * 8) :
                                                                                 dimension == Level.END ?
                                                                                 String.format("§f<%s> §f[§e末地§f] §b%d %d %d ", playerName, pos.getX(), pos.getY(), pos.getZ()) :
                                                                                 String.format("§f<%s> §f[§d未知维度§f] §b%d %d %d ", playerName, pos.getX(), pos.getY(), pos.getZ());
                                                                          context.getSource().sendSystemMessage(
                                                                                  Component.literal(info)
                                                                                           .append(Component.literal(" §6[队内分享]")
                                                                                                            .withStyle(style -> style.withClickEvent(
                                                                                                                    new ClickEvent.RunCommand("/pos share")
                                                                                                                                                    ))));
                                                                      }
                                                                      return 1;
                                                                  }))
                                           ));
    }

    public static void registerPosShareCommand() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(Commands.literal(POS)
                                                    .then(Commands.literal(SHARE)
                                                                  .executes(context -> {
                                                                      Player player = context.getSource().getPlayer();
                                                                      if (player != null) {
                                                                          BlockPos pos = player.getOnPos();
                                                                          ServerLevel level = context.getSource().getLevel();
                                                                          ResourceKey<Level> dimension = level.dimension();
                                                                          String info;
                                                                          info = dimension == Level.OVERWORLD ?
                                                                                 String.format("§f[§a主世界§f] §b%d %d %d §f-> §f[§c下界§f] §b%d %d %d ", pos.getX(), pos.getY(), pos.getZ(), pos.getX() / 8, pos.getY(), pos.getZ() / 8) :
                                                                                 dimension == Level.NETHER ?
                                                                                 String.format("§f[§c下界§f] §b%d %d %d §f-> §f[§a主世界§f] §b%d %d %d ", pos.getX(), pos.getY(), pos.getZ(), pos.getX() * 8, pos.getY(), pos.getZ() * 8) :
                                                                                 dimension == Level.END ?
                                                                                 String.format("§f[§e末地§f] §b%d %d %d ", pos.getX(), pos.getY(), pos.getZ()) :
                                                                                 String.format("§f[§d未知维度§f] §b%d %d %d ", pos.getX(), pos.getY(), pos.getZ());
                                                                          context.getSource().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStackForNameResolution(level), String.format("/teammsg %s", info));
                                                                      }
                                                                      return 1;
                                                                  }))
                                           ));
    }

    public static void registerPosDisplayCommand() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(Commands.literal(POS)
                                                    .then(Commands.literal(DISPLAY)
                                                                  .then(Commands.literal(ON)
                                                                                .executes(context -> {
                                                                                    Player player = context.getSource().getPlayer();
                                                                                    if (player != null) {
                                                                                        player.addTag("POS_DISPLAY");
                                                                                        context.getSource().sendSystemMessage(Component.literal("§e坐标显示 §a已开启"));
                                                                                    }
                                                                                    return 1;
                                                                                }))
                                                         )));
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(Commands.literal(POS)
                                                    .then(Commands.literal(DISPLAY)
                                                                  .then(Commands.literal(OFF)
                                                                                .executes(context -> {
                                                                                    Player player = context.getSource().getPlayer();
                                                                                    if (player != null) {
                                                                                        player.removeTag("POS_DISPLAY");
                                                                                        context.getSource().sendSystemMessage(Component.literal("§e坐标显示 §c已关闭"));
                                                                                    }
                                                                                    return 1;
                                                                                }))
                                                         )));
    }

    public static void registerRejoinCommand() {
        List<String> teams = List.of("aqua", "blue", "gray", "green", "orange", "pink", "red", "spectators", "yellow");
        for (String team : teams) {
            CommandRegistrationCallback.EVENT.register(
                    (dispatcher, registryAccess, environment) ->
                            dispatcher.register(Commands.literal(REJOIN)
                                                        .then(Commands.literal(team)
                                                                      .executes(context -> {
                                                                          Player player = context.getSource().getPlayer();
                                                                          if (player != null) {
                                                                              context.getSource().sendSystemMessage(Component.literal("§f进入队伍: " + team));
                                                                              ServerLevel level = (ServerLevel) player.level();
                                                                              context.getSource().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStackForNameResolution(level), String.format("/join %s %s", team, player.getName().getString()));
                                                                          }
                                                                          return 1;
                                                                      }))));
        }
    }

    public static void registerBingoDifficultyCommand() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(Commands.literal(BINGO)
                                                    .then(Commands.literal(MODE)
                                                                  .then(Commands.literal(EASY)
                                                                                .executes(context -> {
                                                                                    Player player = context.getSource().getPlayer();
                                                                                    if (player != null) {
                                                                                        context.getSource().sendSystemMessage(Component.literal("§eBingo模式 §f已设置为 §a简单"));
                                                                                        ServerLevel level = (ServerLevel) player.level();
                                                                                        context.getSource().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStackForNameResolution(level), "/bingo filter +tedious +simplified +advancements +items -unobtainable -uncategorized");
                                                                                    }
                                                                                    return 1;
                                                                                })))));
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(Commands.literal(BINGO)
                                                    .then(Commands.literal(MODE)
                                                                  .then(Commands.literal(HARD)
                                                                                .executes(context -> {
                                                                                    Player player = context.getSource().getPlayer();
                                                                                    if (player != null) {
                                                                                        context.getSource().sendSystemMessage(Component.literal("§eBingo模式 §f已设置为 §c困难"));
                                                                                        ServerLevel level = (ServerLevel) player.level();
                                                                                        context.getSource().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStackForNameResolution(level), "/bingo filter +tedious -unobtainable -uncategorized");
                                                                                    }
                                                                                    return 1;
                                                                                })))));
    }

    public static void init() {
        registerPosGetCommand();
        registerPosShareCommand();
        registerPosDisplayCommand();

        registerRejoinCommand();
        registerBingoDifficultyCommand();

        LOGGER.info("commands.ModCommands init");
    }
}
