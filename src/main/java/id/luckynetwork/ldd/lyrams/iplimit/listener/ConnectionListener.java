package id.luckynetwork.ldd.lyrams.iplimit.listener;

import id.luckynetwork.ldd.lyrams.iplimit.IPLimit;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ConnectionListener implements Listener {

    private final IPLimit plugin;
    private final Map<String, List<Player>> inetAddressListMap = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            InetAddress address = event.getAddress();
            if (address == null) {
                return;
            }

            String hostAddress = address.getHostAddress();
            Player player = event.getPlayer();
            if (!inetAddressListMap.containsKey(hostAddress)) {
                List<Player> loggedIn = new ArrayList<>();
                loggedIn.add(player);

                inetAddressListMap.put(hostAddress, loggedIn);
                return;
            }

            List<Player> loggedIn = inetAddressListMap.get(hostAddress);
            int limit = plugin.getConfiguration().getLimit(address);
            if (loggedIn.size() >= limit) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, plugin.getConfiguration().getDeniedMessage());
                return;
            }

            loggedIn.add(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        InetAddress address = player.getAddress().getAddress();
        boolean remove = false;
        if (inetAddressListMap.containsKey(address.getHostAddress())) {
            List<Player> loggedIn = inetAddressListMap.get(address.getHostAddress());
            loggedIn.remove(player);

            if (loggedIn.isEmpty()) {
                remove = true;
            }
        }

        if (remove) {
            inetAddressListMap.remove(address.getHostAddress());
        }
    }
}
