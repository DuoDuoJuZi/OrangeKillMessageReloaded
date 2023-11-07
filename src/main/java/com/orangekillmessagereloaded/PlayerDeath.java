package com.orangekillmessagereloaded;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.ChatColor;
import java.io.BufferedReader;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlayerDeath implements Listener {

    private String lastKillerPlayer = null;
    private String lastKilledPlayerName = null;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity(); // 获取死亡的玩家对象
        if (player.getKiller() != null) {
            Entity killerEntity = player.getKiller(); // 获取击杀者的实体对象
            if (killerEntity instanceof Player) {
                lastKillerPlayer = ((Player) killerEntity).getName(); // 如果击杀者是玩家，获取玩家的ID作为变量
            } else {
                lastKillerPlayer = null; // 如果击杀者不是玩家，设置为null
            }
        } else {
            lastKillerPlayer = null; // 没有击杀者时设置为null
        }

        lastKilledPlayerName = player.getName();
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (lastKillerPlayer != null) {
            String customMessage = getCustomMessageFromJson(lastKillerPlayer); // 从 JSON 文件中获取自定义消息
            if (customMessage != null) {
                // 替换标题中的默认消息为自定义消息
                String mainTitle = ChatColor.RED + ChatColor.translateAlternateColorCodes('&', customMessage);
                String subTitle = ChatColor.GOLD + lastKillerPlayer + " 的自定义击杀信息！";

                // 发送标题给重生的玩家
                player.sendTitle(mainTitle, subTitle, 10, 70, 20);
            }
        } else {
            // 如果lastKillerPlayer为null，不执行任何操作
        }
        lastKillerPlayer = null; // 在处理完击杀消息后，将lastKillerPlayer设置为null
    }

    private String getCustomMessageFromJson(String playerName) {
        JSONParser parser = new JSONParser();
        File dataFile = new File(OrangeKillMessageReloaded.main.getDataFolder(), "data.json");
        try (BufferedReader reader = Files.newBufferedReader(dataFile.toPath(), StandardCharsets.UTF_8)) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            Object obj = parser.parse(jsonContent.toString());
            JSONArray jsonArray = (JSONArray) obj;
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String name = (String) jsonObject.get("name");
                String message = (String) jsonObject.get("message");
                if (name != null && name.equals(playerName)) {
                    return message; // 返回自定义消息
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
