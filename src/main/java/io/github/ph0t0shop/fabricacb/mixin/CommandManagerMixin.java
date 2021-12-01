package io.github.ph0t0shop.fabricacb.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.ph0t0shop.fabricacb.FabricACB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	@Redirect(method = "makeTreeForSource", at = @At(value="INVOKE", target="Lcom/mojang/brigadier/tree/CommandNode;canUse(Ljava/lang/Object;)Z"))
	private boolean canUseRedirection(CommandNode<ServerCommandSource> commandNode, Object objSource) {
		ServerCommandSource source = (ServerCommandSource) objSource;
		ServerPlayerEntity player;
		try {
			 player = source.getPlayer();
		} catch (CommandSyntaxException e) {
			FabricACB.LOGGER.warn("Trying to send command tree to non-player. Something is wrong!");
			return commandNode.canUse(source);
		}
		
		if (commandNode instanceof LiteralCommandNode<ServerCommandSource> node) {
			String literal = node.getLiteral();
			if (FabricACB.COMMAND_BLACKLIST.contains(literal.toLowerCase()) && !player.server.getPlayerManager().isOperator(player.getGameProfile())) {
				return false;
			}
		}

		return commandNode.canUse(source);
	}
}
