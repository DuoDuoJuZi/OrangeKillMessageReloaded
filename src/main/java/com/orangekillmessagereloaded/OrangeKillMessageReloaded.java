package com.orangekillmessagereloaded;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class OrangeKillMessageReloaded extends JavaPlugin {

    // 声明静态变量
    static OrangeKillMessageReloaded main;

    @Override
    public void onEnable() {

        getLogger().info("OrangeKillMessage已加载");
        getLogger().info("作者: DuoDuoJuZi");

        new OrangeKillMessagePlaceholder(this).register();

        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);

        getCommand("okm").setExecutor(new Commands(this));
        new Commands(this);

        // 加载配置文件
        saveDefaultConfig();
        main = this;

        // 检查配置文件夹是否存在
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // 检查 data.json 文件是否存在
        File dataFile = new File(dataFolder, "data.json");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                getLogger().info("正在创建数据文件夹data.json");
            } catch (IOException e) {
                getLogger().severe("创建 data.json 失败，请检查报错: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("OrangeKillMessage已卸载");
        getLogger().info("有缘再见");
    }
}
