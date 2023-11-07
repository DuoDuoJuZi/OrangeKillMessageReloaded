package com.orangekillmessagereloaded;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Commands implements CommandExecutor {
    private final Path filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Commands(JavaPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.filePath = new File(dataFolder, "data.json").toPath();
        plugin.getCommand("okm").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.main"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Profile"));
            commandSender.sendMessage(" ");
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.usage"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.set"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.view"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.list"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.reload"));
            commandSender.sendMessage(" ");
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.logogram"));
            return true;
        }

        String subCommand = strings[0].toLowerCase();

        if (subCommand.equals("help")) {
            // 处理 "okm help" 命令
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.main"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Profile"));
            commandSender.sendMessage(" ");
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.usage"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.set"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.view"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.list"));
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.reload"));
            commandSender.sendMessage(" ");
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Help.logogram"));
            return true;
        }
        if (subCommand.equals("reload")) {
            // 处理 "okm reload" 命令
            OrangeKillMessageReloaded.main.reloadConfig();
            commandSender.sendMessage(OrangeKillMessageReloaded.main.getConfig().getString("Lang.Reload"));
        }
        if (subCommand.equals("list")) {
            // 处理 "okm list" 命令
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;

                // 加载自定义消息
                JsonArray customMessages = loadCustomMessages();

                // 计算总页数
                int pageSize = 5; // 每页显示的自定义消息数量
                int totalPages = (int) Math.ceil((double) customMessages.size() / pageSize);

                // 获取当前页数
                int currentPage = 1;
                if (strings.length > 1) {
                    try {
                        currentPage = Integer.parseInt(strings[1]);
                        if (currentPage < 1 || currentPage > totalPages) {
                            player.sendMessage(ChatColor.RED + "无效的页数！");
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "无效的页数！");
                        return true;
                    }
                }

                // 计算起始索引和结束索引
                int startIndex = (currentPage - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, customMessages.size());

                // 发送自定义消息列表
                player.sendMessage(ChatColor.GOLD + "§m---------------------------------");
                player.sendMessage(ChatColor.GOLD + "§6自定义击杀消息 第 §e" + currentPage + " §6/ §e" + totalPages + " §6页");
                player.sendMessage(ChatColor.GOLD + " ");
                for (int i = startIndex; i < endIndex; i++) {
                    JsonObject message = customMessages.get(i).getAsJsonObject();
                    String name = message.get("name").getAsString();
                    String msg = message.get("message").getAsString();
                    player.sendMessage(ChatColor.WHITE + "" + (i + 1) + "." + ChatColor.GRAY + name + ChatColor.WHITE + ": " + ChatColor.YELLOW + msg);
                }
                player.sendMessage(ChatColor.GOLD + "§m---------------------------------");
                player.sendMessage(ChatColor.GRAY + "输入 \"/okm list <Pages>\" 查看更多消息。");
            }
            return true;
        }
        if (strings.length > 0 && strings[0].equals("set")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                String playerName = player.getName(); // 获取玩家ID

                // 检查权限
                if (player.hasPermission("orangekillmessage.set")) {
                    if (strings.length > 2) {
                        String targetPlayerName = strings[1]; // 获取目标玩家名称
                        String message = strings[2]; // 获取玩家输入的内容

                        // 检查是否有设置其他玩家消息的权限
                        if (player.hasPermission("orangekillmessage.setother")) {
                            // 加载现有的自定义消息
                            JsonArray customMessages = loadCustomMessages();

                            // 更新现有的目标玩家自定义消息或创建新的消息
                            updateOrCreateMessage(customMessages, targetPlayerName, message);

                            // 保存自定义消息到文件
                            saveCustomMessages(customMessages);

                            player.sendMessage(ChatColor.GREEN + "已保存玩家 " + targetPlayerName + " 的自定义消息为 " + message);
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "你没有设置其他玩家消息的权限！");
                            return true;
                        }
                    } else if (strings.length > 1) {
                        String message = strings[1]; // 获取玩家输入的内容

                        // 读取现有的自定义消息
                        JsonArray customMessages = loadCustomMessages();

                        // 更新现有的玩家自定义消息或创建新的消息
                        updateOrCreateMessage(customMessages, playerName, message);

                        // 保存自定义消息到文件
                        saveCustomMessages(customMessages);

                        player.sendMessage(ChatColor.GREEN + "已保存你的自定义消息为 " + message);
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "你没有设置消息的权限！");
                    return true;
                }
            }
        }

        // view
        if (subCommand.equals("view")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (strings.length == 1) {
                    // 默认查看自己的自定义消息
                    String playerName = player.getName();
                    JsonObject message = findCustomMessage(playerName);
                    if (message != null) {
                        String mainTitle = OrangeKillMessageReloaded.main.getConfig().getString("Lang.View_self_color") + message.get("message").getAsString();
                        String subTitle = "你的自定义击杀消息";
                        sendTitle(player, mainTitle, subTitle);
                    } else {
                        player.sendMessage(ChatColor.RED + "未找到你的自定义消息！");
                    }
                } else if (strings.length == 2) {
                    // 查看指定玩家的自定义消息
                    String targetPlayerName = strings[1];
                    JsonObject message = findCustomMessage(targetPlayerName);
                    if (message != null) {
                        String mainTitle = OrangeKillMessageReloaded.main.getConfig().getString("Lang.View_other_color") + message.get("message").getAsString();
                        String subTitle =  OrangeKillMessageReloaded.main.getConfig().getString("Lang.View_other_color") + targetPlayerName + " 的自定义击杀消息";
                        sendTitle(player, mainTitle, subTitle);
                    } else {
                        player.sendMessage(ChatColor.RED + "未找到玩家 " + targetPlayerName + " 的自定义消息！");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "用法错误！正确用法: /okm view [playername]");
                }
                return true;
            } else {
                commandSender.sendMessage(ChatColor.RED + "该命令只能由玩家执行！");
                return true;
            }
        }
        return false;
    }

    // 加载自定义消息
    private JsonArray loadCustomMessages() {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (jsonElement instanceof JsonArray) {
                return (JsonArray) jsonElement;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonArray();
    }

    // 更新自定义消息
    private void updateOrCreateMessage(JsonArray customMessages, String playerName, String newMessage) {
        for (int i = 0; i < customMessages.size(); i++) {
            JsonObject message = customMessages.get(i).getAsJsonObject();
            String name = message.get("name").getAsString();
            if (name.equals(playerName)) {
                message.addProperty("message", newMessage);
                return; // 找到匹配的name后立即返回，不再继续遍历
            }
        }

        // 如果没有找到匹配的name，则创建新的name,message在data.json
        JsonObject playerMessage = new JsonObject();
        playerMessage.addProperty("name", playerName);
        playerMessage.addProperty("message", newMessage);
        customMessages.add(playerMessage);
    }

    // 保存自定义消息
    private void saveCustomMessages(JsonArray customMessages) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(customMessages));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // view
    private JsonObject findCustomMessage(String playerName) {
        JsonArray customMessages = loadCustomMessages();
        for (JsonElement element : customMessages) {
            JsonObject message = element.getAsJsonObject();
            if (message.get("name").getAsString().equals(playerName)) {
                return message;
            }
        }
        return null;
    }
    private void sendTitle(Player player, String mainTitle, String subTitle) {
        player.sendTitle(mainTitle, subTitle, 10, 70, 20);
    }
}
