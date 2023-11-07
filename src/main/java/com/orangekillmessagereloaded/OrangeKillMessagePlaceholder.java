package com.orangekillmessagereloaded;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class OrangeKillMessagePlaceholder extends PlaceholderExpansion {

    private final OrangeKillMessageReloaded plugin;

    // 这里我还是不理解，从网上扒的代码
    public OrangeKillMessagePlaceholder(OrangeKillMessageReloaded plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "orangekillmessage";
    }

    @Override
    public String getAuthor() {
        return "YourName";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("message")) {
            // 获取对应玩家ID的消息
            String playerId = player.getName(); // 获取玩家ID
            String message = getMessageFromDataJson(playerId); // 从data.json中获取消息
            return message;
        }

        return null;
    }

    private String getMessageFromDataJson(String playerId) {
        try {
            // 获取插件数据文件夹路径
            String dataFolderPath = plugin.getDataFolder().getAbsolutePath();

            // 构建data.json文件路径
            String dataFilePath = dataFolderPath + File.separator + "data.json";

            // 读取data.json文件内容
            String content = new String(Files.readAllBytes(Paths.get(dataFilePath)), StandardCharsets.UTF_8);

            // 解析 JSON 数据
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(content);

            // 遍历 JSON 数组查找对应玩家ID的消息
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String name = (String) jsonObject.get("name");
                if (name.equalsIgnoreCase(playerId)) {
                    return (String) jsonObject.get("message");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 处理异常
        }

        return ""; // 如果未找到对应玩家ID的消息，则返回空字符串
    }

}
