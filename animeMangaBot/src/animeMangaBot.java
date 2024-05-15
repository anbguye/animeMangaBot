
/**
 * The purpose of this bot is to keep track of manga & anime read/watched
 */
import discord4j.core.DiscordClient; // Import the DiscordClient class from the Discord4J library
import discord4j.core.GatewayDiscordClient; // Import the GatewayDiscordClient class from the Discord4J library
import discord4j.core.event.domain.message.MessageCreateEvent; // Import the MessageCreateEvent class from the Discord4J library
import reactor.core.publisher.Mono; // Import the Mono class from the Reactor library

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Anthony Nguyen
 *
 */
public class animeMangaBot {

	public static void main(String args[]) {

		String token = System.getenv("MANGAANIME_API_TOKEN");
		DiscordClient client = DiscordClient.create(token); // Connects to discord API
		GatewayDiscordClient gateway = client.login().block(); // Handles receiving messages, users joining server,
																// executing actions defined in code
		HashMap<String, animangaList> database = new HashMap<String, animangaList>();

		gateway.on(MessageCreateEvent.class).flatMap(event -> {

			if (event.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false)) {
				String userInput = event.getMessage().getContent();
				String userID = event.getMessage().getAuthor().map(user -> user.getId().asString()).orElse("Unknown");
				String[] input = userInput.split("\\s+", 2);
				String[] chaptInput = userInput.split("\\s+", 3);

				if (!database.containsKey(userID))
					database.put(userID, new animangaList());

				switch (chaptInput[0]) {
				case "!add":
					try {
						if (chaptInput.length == 2 && !database.get(userID).hasTitle(input[1])) { // if input has NO
																									// EPISODES
							database.get(userID).add(input[1]);
							return event.getMessage().getChannel()
									.flatMap(channel -> channel.createMessage("Added " + input[1] + " to your list."));
						} else if (chaptInput.length == 3 && !database.get(userID).hasTitle(chaptInput[1])) { // if
																												// input
																												// has
																												// EPISODES
							database.get(userID).add(chaptInput[1], Integer.parseInt(chaptInput[2]));
							return event.getMessage().getChannel()
									.flatMap(channel -> channel.createMessage("Added " + input[1] + " to your list."));
						}
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Title is already on the list."));
					} catch (NumberFormatException e) {
						if (!database.get(userID).hasTitle(input[1])) { // if INPUT HAS A LOT OF WORDS IN TITLE
							database.get(userID).add(input[1]);
							return event.getMessage().getChannel()
									.flatMap(channel -> channel.createMessage("Added " + input[1] + " to your list."));
						}
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Title is already on the list."));
					}
				case "!read":
					if (database.get(userID).hasTitle(input[1])) {
						database.get(userID).readChapter(input[1]);
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Now read/watched up to "
										+ database.get(userID).getChapter(input[1]) + " in " + input[1]));
					}
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("Title is not on the list."));
				case "!remove":
					if (database.get(userID).hasTitle(input[1])) {
						database.get(userID).remove(input[1]);
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Removed " + input[1] + " from your list."));
					}
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("Title is not on the list."));
				case "!edit":
					if (database.get(userID).hasTitle(chaptInput[1])) {
						database.get(userID).edit(chaptInput[1], Integer.parseInt(chaptInput[2]));
						return event.getMessage().getChannel().flatMap(channel -> channel
								.createMessage("Now on chepisode " + chaptInput[2] + " in " + chaptInput[1]));
					}
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("Title is not on the list."));
				case "!list":
					if (!database.get(userID).isEmpty())
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage(database.get(userID).list()));
					return event.getMessage().getChannel().flatMap(channel -> channel.createMessage("List is empty."));
				case "!help":
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("!add - adds animanga to your list\n"
									+ "!read - adds +1 to your animanga chepisode counter\n"
									+ "!remove - removes animanga from your list\n"
									+ "!edit - episodes currnet chepisode read/watched\n" + ""
									+ "!list - lists your list"));
				default:
					if (userInput.startsWith("!"))
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Invalid command, please use !help"));
				}
			}

			return Mono.empty();

		}).subscribe();

		gateway.onDisconnect().block();

	}
}
