package io.github.ph0t0shop.fabricacb;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class FabricACB implements ModInitializer {
	public final static Logger LOGGER = LogManager.getLogger("FabricACB");
	public final static ArrayList<String> COMMAND_BLACKLIST = new ArrayList<>();
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing");
		
		Path config = FabricLoader.getInstance().getConfigDir().resolve("autocomplete-blacklist.txt");

		try {
			boolean exists = Files.exists(config);
			if (exists) {
				try (var reader = Files.newBufferedReader(config)) {
					reader.lines()
							.filter(line -> !line.startsWith("#"))
							.forEach(COMMAND_BLACKLIST::add);
				}
			} else {
				Files.createFile(config);
				LOGGER.info("Created new config file \"config/autocomplete-blacklist.txt\". Edit this file, then restart your server for the changes to take effect.");
				Files.writeString(config, """
							# Add blacklisted command literals, each on a new line.
							# Lines starting with # are ignored.
							""");
			}
		} catch (IOException e) {
			LOGGER.error("Something went wrong while loading the config file. FabricACB will not work properly.", e);
		}

		LOGGER.info("Initialized");
	}
}
