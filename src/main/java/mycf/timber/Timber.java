package mycf.timber;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.AxeItem;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Timber implements ModInitializer {
    public static final Identifier TOGGLE_PLAYER = new Identifier("timber", "toggle_player");
    public static final Identifier TOGGLE_STACK = new Identifier("timber", "toggle_stack");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_PLAYER, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player instanceof Toggleable tplayer) {
                    var newNbt = tplayer.toggleMode$mycftimber();

                    if (newNbt)
                        player.sendMessage(Text.translatable("item.timber.axe.nevermodeon"), true);
                    else
                        player.sendMessage(Text.translatable("item.timber.axe.nevermodeoff"), true);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_STACK, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                var itemStack = player.getMainHandStack();
                if (itemStack.getItem() instanceof AxeItem && player instanceof Toggleable tplayer && tplayer.getToggleMode$mycftimber()) {
                    var newNbt = ((Toggleable) (Object) itemStack).toggleMode$mycftimber();

                    if (newNbt)
                        player.sendMessage(Text.translatable("item.timber.axe.chopall"), true);
                    else
                        player.sendMessage(Text.translatable("item.timber.axe.chopone"), true);
                }
            });
        });
    }
}
