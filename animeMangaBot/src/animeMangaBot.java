
/**
 * The purpose of this bot is to keep track of manga & anime read/watched
 */
import com.mongodb.MongoException;
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

/**
 * @author Anthony Nguyen
 *
 */
public class animeMangaBot {

	public static void main(String args[]) {

		String token = System.getenv("MANGAANIME_API_TOKEN");
		String connectionString = System.getenv("MONGODB_API_TOKEN");
		DiscordClient client = DiscordClient.create(token); // Connects to discord API

		// Create a new client and connect to the server
		try (MongoClient mongoClient = MongoClients.create(connectionString)) {

			HashMap<String, animangaList> database = new HashMap<String, animangaList>();
			GatewayDiscordClient gateway = client.login().block(); // Handles receiving messages, users joining
																	// server, executing actions defined in code
			// Send a ping to confirm a successful connection
			MongoDatabase animangaDB = mongoClient.getDatabase("animeMangaBotDB");
			MongoCollection<Document> userCollection = animangaDB.getCollection("userData");

			gateway.on(MessageCreateEvent.class).flatMap(event -> {

				if (event.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false)) {
					String userInput = event.getMessage().getContent();
					String userID = event.getMessage().getAuthor().map(user -> user.getId().asString())
							.orElse("Unknown");
					String[] input = userInput.split("\\s+");
					String title = "";
					int chapt = 0;

					if (!database.containsKey(userID))
						database.put(userID, new animangaList());

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
						if (database.get(userID).validTitle(title.trim() + " " + input[input.length - 1]))
							title = title.trim() + " " + input[input.length - 1];
					}

					final int finalChapt = chapt;
					final String finalTitle = title.trim();

					switch (input[0]) {
					case "!add":
						try {
							if (!database.get(userID).validTitle(finalTitle)) {
								database.get(userID).add(finalTitle, chapt);
								return event.getMessage().getChannel().flatMap(
										channel -> channel.createMessage("Added " + finalTitle + " to your list."));
							}
							return event.getMessage().getChannel()
									.flatMap(channel -> channel.createMessage("Title is already on your list."));
						} catch (NumberFormatException e) { // if adding title and chapter doesn't work
							if (!database.get(userID).validTitle(title)) {
								database.get(userID).add(title);
								return event.getMessage().getChannel().flatMap(
										channel -> channel.createMessage("Added " + finalTitle + " to your list."));
							}
							return event.getMessage().getChannel()
									.flatMap(channel -> channel.createMessage("Title is already on your list."));
						}
					case "!read":
						if (database.get(userID).validTitle(title)) {
							database.get(userID).readChapter(title);
							return event.getMessage().getChannel()
									.flatMap(channel -> channel.createMessage("Now read/watched up to "
											+ database.get(userID).getChapter(finalTitle) + " in " + finalTitle));
						}
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Title is not on your list."));
					case "!remove":
						if (database.get(userID).validTitle(finalTitle)) {
							database.get(userID).remove(finalTitle);
							return event.getMessage().getChannel().flatMap(
									channel -> channel.createMessage("Removed " + finalTitle + " from your list."));
						}
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Title is not on your list."));
					case "!edit":
						if (database.get(userID).validTitle(finalTitle)) {
							database.get(userID).edit(finalTitle, finalChapt);
							return event.getMessage().getChannel().flatMap(channel -> channel
									.createMessage("Now on chapter/episode " + finalChapt + " in " + finalTitle));
						}
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("Title is not on your list."));
					case "!list":
						if (!database.get(userID).isEmpty())
							return event.getMessage().getChannel()
									.flatMap(channel -> channel.createMessage(database.get(userID).list()));
						return event.getMessage().getChannel()
								.flatMap(channel -> channel.createMessage("List is empty."));
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

					Document userData = new Document("userID", userID).append("animangaList", database.get(userID));
					userCollection.insertOne(userData);

				}

				return Mono.empty();

			}).subscribe();

			gateway.onDisconnect().block();

		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
}
