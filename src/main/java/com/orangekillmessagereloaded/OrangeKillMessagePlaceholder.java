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
         
            String playerId = player.getName(); 
            String message = getMessageFromDataJson(playerId); 
            return message;
        }

        return null;
    }

    private String getMessageFromDataJson(String playerId) {
        try {
      
            String dataFolderPath = plugin.getDataFolder().getAbsolutePath();

            String dataFilePath = dataFolderPath + File.separator + "data.json";

            String content = new String(Files.readAllBytes(Paths.get(dataFilePath)), StandardCharsets.UTF_8);

   
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(content);

     
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String name = (String) jsonObject.get("name");
                if (name.equalsIgnoreCase(playerId)) {
                    return (String) jsonObject.get("message");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }

        return ""; 
    }

}
