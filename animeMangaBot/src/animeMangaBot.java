
/**
 * The purpose of this bot is to keep track of manga & anime read/watched
 */

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import discord4j.core.DiscordClient; // Import the DiscordClient class from the Discord4J library
import discord4j.core.GatewayDiscordClient; // Import the GatewayDiscordClient class from the Discord4J library
import discord4j.core.event.domain.message.MessageCreateEvent; // Import the MessageCreateEvent class from the Discord4J library
import reactor.core.publisher.Mono; // Import the Mono class from the Reactor library
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @author Anthony Nguyen
 *
 */
public class animeMangaBot {

	public static void main(String args[]) {

		// Sets up mongoDB server connection and animeMangaBotDB
		mongoHelper mongoHelper = new mongoHelper();
		// Token to connect to server
		String token = System.getenv("MANGAANIME_API_TOKEN");

		// HashMap holding userID and respective animanga lists.
		HashMap<String, animangaList> database = new HashMap<String, animangaList>();

		// Actually connects to discord
		DiscordClient client = DiscordClient.create(token);
		GatewayDiscordClient gateway = client.login().block();

		// Imports animeMangaBotDB collections to HashMap of userID
		mongoHelper.importDB(database);

		gateway.on(MessageCreateEvent.class).flatMap(event -> {

			// Checks to see if the bot is reading its own message
			if (event.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false)) {
				String userInput = event.getMessage().getContent();
				String userID = event.getMessage().getAuthor().map(user -> user.getId().asString()).orElse("Unknown");
				String[] input = userInput.split("\\s+");
				String title = "";
				int chapt = 0;

				// Checks if user has used bot before, creates new list for user if not.
				if (!database.containsKey(userID))
					database.put(userID, new animangaList());

				// Basically checks to see if the input has a chapter or not to know which add()
				// to use, and goes to the catch section if it does have a chapter number
				// associated with it.
				if (input.length >= 2) {
					try {
						Integer.parseInt(input[input.length - 1]);
						for (int i = 1; i < input.length - 1; i++) {
							title += input[i] + " ";
						}
						chapt = Integer.parseInt(input[input.length - 1]);
					} catch (NumberFormatException e) {
						for (int i = 1; i < input.length; i++) {
							title += input[i] + " ";
						}
					}

					// Checks to see if titles such as "Mob Psycho 100" which has numbers at the end
					// are in the list, and makes sure its a title if so.
					if (database.get(userID).validTitle(title.trim() + " " + input[input.length - 1]))
						title = title.trim() + " " + input[input.length - 1];
				}

				// Makes sure the title and chapter is correctly listed for accurate switch
				// casing since .split isnt perfect.
				final int finalChapt = chapt;
				final String finalTitle = title.trim();

				// Checks to see which command user inputs, sends it to switch case accordingly.
				switch (input[0]) {
				case "!add":
					try {
						// If it has a chapter, intentionally makes NumberFormatException to just add
						// title, otherwise add both title and chapter #.
						if (!database.get(userID).validTitle(finalTitle)) {
							database.get(userID).add(finalTitle, chapt);
							mongoHelper.exportDB(database, userID);
							return event.getMessage().getChannel().flatMap(
									channel -> channel.createMessage("Added " + finalTitle + " to your list."));
						}
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Title is already on your list."));
					} catch (NumberFormatException e) { // if adding title and chapter doesn't work
						if (!database.get(userID).validTitle(title)) {
							database.get(userID).add(title);
							mongoHelper.exportDB(database, userID);
							return event.getMessage().getChannel().flatMap(
									channel -> channel.createMessage("Added " + finalTitle + " to your list."));
						}
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Title is already on your list."));
					}
				case "!read":
					// Basically adds +1 to the chapter counter of given title.
					if (database.get(userID).validTitle(finalTitle)) {
						database.get(userID).readChapter(finalTitle);
						mongoHelper.exportDB(database, userID);
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Now read/watched up to "
										+ database.get(userID).getChapter(finalTitle) + " in " + finalTitle));
					}
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("Title is not on your list."));
				case "!remove":
					// Removes title from given user's list based off their input.
					if (database.get(userID).validTitle(finalTitle)) {
						database.get(userID).remove(finalTitle);
						mongoHelper.exportDB(database, userID);
						return event.getMessage().getChannel().flatMap(
								channel -> channel.createMessage("Removed " + finalTitle + " from your list."));
					}
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("Title is not on your list."));
				case "!edit":
					// Edit title's chapter based off user input.
					if (database.get(userID).validTitle(finalTitle)) {
						database.get(userID).edit(finalTitle, finalChapt);
						mongoHelper.exportDB(database, userID);
						return event.getMessage().getChannel().flatMap(channel -> channel
								.createMessage("Now on chapter/episode " + finalChapt + " in " + finalTitle));
					}
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("Title is not on your list."));
				case "!list":
					// Lists user title list along with chapter #.
					if (!database.get(userID).isEmpty())
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage(database.get(userID).list()));
					return event.getMessage().getChannel().flatMap(channel -> channel.createMessage("List is empty."));
				case "!help":
					// Lists commands currently available.
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("!add - adds animanga to your list\n"
									+ "!read - adds +1 to your animanga chepisode counter\n"
									+ "!remove - removes animanga from your list\n"
									+ "!edit - episodes currnet chepisode read/watched\n" + ""
									+ "!list - lists your list"));
				default:
					// If user input fails to match anything above, defaults to telling them to use
					// !help.
					if (userInput.startsWith("!"))
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Invalid command, please use !help"));
				}

			}

			// Move on to next step.
			return Mono.empty();

			// Waits for next message.
		}).subscribe();

		// Disconnects from discord server.
		gateway.onDisconnect().block();
	}
}
