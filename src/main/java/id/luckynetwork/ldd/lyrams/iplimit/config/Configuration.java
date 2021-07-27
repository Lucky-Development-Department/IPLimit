package id.luckynetwork.ldd.lyrams.iplimit.config;

import id.luckynetwork.ldd.lyrams.iplimit.IPLimit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class Configuration {

    private final IPLimit plugin;
    private final Map<String, Integer> customLimitMap = new HashMap<>();
    private final Matcher period = Pattern.compile("\\.").matcher("");
    private final Matcher comma = Pattern.compile(",").matcher("");
    private int defaultLimit;
    private String deniedMessage;

    public Configuration(IPLimit plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();

        this.reload();
        this.save();
    }

    public void reload() {
        plugin.reloadConfig();

        this.defaultLimit = plugin.getConfig().getInt("defaultLimit", 1);
        this.deniedMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("deniedMessage", "&cYou have reached your connected account limit!"));
        this.customLimitMap.clear();

        ConfigurationSection limitsSection = plugin.getConfig().getConfigurationSection("limits");
        Set<String> keys = limitsSection.getKeys(false);
        for (String key : keys) {
            String addressString = comma.reset(key).replaceAll(".");
            try {
                InetAddress address = InetAddress.getByName(addressString);
                int limit = limitsSection.getInt(key);
                if (limit > 0) {
                    customLimitMap.put(address.getHostAddress(), limit);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        plugin.getConfig().set("defaultLimit", defaultLimit);
        plugin.getConfig().set("deniedMessage", deniedMessage);

        ConfigurationSection limitsSection = plugin.getConfig().getConfigurationSection("limits");
        customLimitMap.forEach(((address, limit) -> {
            if (limit > 0) {
                limitsSection.set(period.reset(address).replaceAll(","), limit);
            }
        }));

        plugin.saveConfig();
    }

    public void removeCustomLimit(InetAddress address) {
        String key = period.reset(address.getHostAddress()).replaceAll(",");
        plugin.getConfig().set("limits." + key, null);
        plugin.saveConfig();
    }

    public int getLimit(InetAddress address) {
        return customLimitMap.getOrDefault(address.getHostAddress(), defaultLimit);
    }
}
