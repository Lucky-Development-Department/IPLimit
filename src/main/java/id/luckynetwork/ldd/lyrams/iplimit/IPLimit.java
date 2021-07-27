package id.luckynetwork.ldd.lyrams.iplimit;

import id.luckynetwork.ldd.lyrams.iplimit.commands.IPLimitCommand;
import id.luckynetwork.ldd.lyrams.iplimit.config.Configuration;
import id.luckynetwork.ldd.lyrams.iplimit.listener.ConnectionListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class IPLimit extends JavaPlugin {

    private Configuration configuration;

    @Override
    public void onEnable() {
        this.configuration = new Configuration(this);

        // register the command
        new IPLimitCommand(this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(this), this);
    }
}
