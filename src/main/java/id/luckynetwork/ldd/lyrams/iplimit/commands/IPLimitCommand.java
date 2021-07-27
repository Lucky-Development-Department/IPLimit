package id.luckynetwork.ldd.lyrams.iplimit.commands;

import id.luckynetwork.ldd.lyrams.iplimit.IPLimit;
import id.luckynetwork.ldd.lyrams.iplimit.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

public class IPLimitCommand extends Command {

    private final IPLimit plugin;

    public IPLimitCommand(IPLimit plugin) {
        super("iplimit");
        this.plugin = plugin;
        this.setAliases(Collections.singletonList("ipl"));
        this.registerCommand(this);
    }

    private void registerCommand(Command command) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(plugin.getDescription().getName(), command);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("iplimit.admin")) {
            sender.sendMessage("§cYou don't have the permission to execute this command.");
            return false;
        }

        if (args.length < 1) {
            sendHelpMessage(sender);
            return false;
        }

        Configuration configuration = plugin.getConfiguration();
        switch (args[0].toUpperCase()) {
            case "SET": {
                if (args.length < 2) {
                    sendHelpMessage(sender);
                    return false;
                }

                try {
                    int limit = Integer.parseInt(args[1]);
                    configuration.setDefaultLimit(limit);

                    configuration.save();
                    sender.sendMessage("§aLimit set to " + limit + "!");
                } catch (NumberFormatException ignored) {
                    sender.sendMessage("§cInvalid number!");
                }
                return false;
            }
            case "CHECK": {
                if (args.length < 2) {
                    sender.sendMessage("§aLimit is set to " + configuration.getDefaultLimit() + "!");
                    return false;
                }

                String addressString = args[1];
                try {
                    InetAddress address = InetAddress.getByName(addressString);
                    sender.sendMessage("§aLimit for address " + addressString + " is set to " + configuration.getLimit(address) + "!");
                } catch (UnknownHostException ignored) {
                    sender.sendMessage("§cInvalid IP address!");
                }
                return false;
            }

            case "ADD": {
                if (args.length < 3) {
                    sendHelpMessage(sender);
                    return false;
                }

                String addressString = args[1];
                try {
                    InetAddress address = InetAddress.getByName(addressString);
                    try {
                        int limit = Integer.parseInt(args[2]);
                        configuration.getCustomLimitMap().put(address.getHostAddress(), limit);

                        configuration.save();
                        sender.sendMessage("§aLimit for address " + addressString + " set to " + limit + "!");
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage("§cInvalid number!");
                    }
                } catch (UnknownHostException ignored) {
                    sender.sendMessage("§cInvalid IP address!");
                }
                return false;
            }
            case "REMOVE": {
                if (args.length < 2) {
                    sendHelpMessage(sender);
                    return false;
                }

                String addressString = args[1];
                try {
                    InetAddress address = InetAddress.getByName(addressString);
                    configuration.getCustomLimitMap().remove(address.getHostAddress());
                    configuration.removeCustomLimit(address);

                    configuration.save();
                    sender.sendMessage("§aLimit for address " + addressString + " has been removed!");
                } catch (UnknownHostException ignored) {
                    sender.sendMessage("§cInvalid IP address!");
                }
                return false;
            }

            case "RELOAD": {
                configuration.reload();
                sender.sendMessage("§aConfig reloaded!");
                return false;
            }
        }

        sendHelpMessage(sender);
        return false;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§e/iplimit set <limit> §7- Sets the default ip address limit");
        sender.sendMessage("§e/iplimit check §7- Checks the default ip address limit");
        sender.sendMessage("§e/iplimit add <address> <limit> §7- Adds a custom limit to an ip address");
        sender.sendMessage("§e/iplimit remove <address> §7- Remove a custom limit from an ip address if present");
        sender.sendMessage("§e/iplimit check <address> §7- Remove a custom limit from an ip address if present");
    }
}
