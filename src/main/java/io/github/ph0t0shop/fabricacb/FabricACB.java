package io.github.ph0t0shop.fabricacb;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FabricACB implements ModInitializer {
	public static MinecraftServer serverInstance;
	public static Logger logger;
	public static ArrayList<String> commandBlacklist = new ArrayList<>();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		logger = LogManager.getLogger("FabricACB");

		logger.info("Initializing");

		File configFile = new File("config" + File.separator + "autocomplete-blacklist.txt");

		try {
			boolean created = configFile.createNewFile();
			if (created) {
				logger.info("Created new config file \"config/autocomplete-blacklist.txt\". Edit this file, then restart " +
						"your server for the changes to take effect.");
				FileWriter writer = new FileWriter(configFile);
				writer.write("""
						# Add blacklisted command literals, each on a new line.
						# Lines starting with # are ignored.
						""");
				writer.close();
			} else {
				Scanner sc = new Scanner(configFile);
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.startsWith("#")) {
						commandBlacklist.add(line);
					}
				}
				sc.close();
			}
		} catch (IOException e) {
			logger.error("Something went wrong loading the config file. FabricACB will not work properly.");
		}

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			serverInstance = server;
		});

		logger.info("Initialized");
	}
}
