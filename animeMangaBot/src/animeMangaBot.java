
/**
 * 
 */
import discord4j.core.DiscordClient; // Import the DiscordClient class from the Discord4J library
import discord4j.core.GatewayDiscordClient; // Import the GatewayDiscordClient class from the Discord4J library
import discord4j.core.event.domain.message.MessageCreateEvent; // Import the MessageCreateEvent class from the Discord4J library
import reactor.core.publisher.Mono; // Import the Mono class from the Reactor library
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anthony Baoloc Nguyen
 *
 */
public class animeMangaBot {

	public static void main(String args[]) {

		String token = System.getenv("MANGAANIME_API_TOKEN");
		DiscordClient client = DiscordClient.create(token); // Connects to discord API
		GatewayDiscordClient gateway = client.login().block(); // Handles receiving messages, users joining server,
																// executing actions defined in code
		List<animeManga> myList = new ArrayList<>();

		gateway.on(MessageCreateEvent.class).flatMap(event -> {

			String userInput = event.getMessage().getContent();

			if (userInput.startsWith("!add")) {
				String[] input = userInput.split("\\s+", 3);
				myList.add(new animeManga(Integer.parseInt(input[1]), input[2]));
				return event.getMessage().getChannel()
						.flatMap(channel -> channel.createMessage("Added: " + input[2] + " Chapter " + input[1]));
			} else if (userInput.startsWith("!remove")) {
				String[] input = userInput.split("\\s+", 2);
				boolean removed = myList.removeIf(val -> val.getAnimeManga().equalsIgnoreCase(input[1]));
				if (removed)
					return event.getMessage().getChannel()
							.flatMap(channel -> channel.createMessage("Removed: " + input[1]));

				return event.getMessage().getChannel()
						.flatMap(channel -> channel.createMessage("Unable to find title."));
			} else if (userInput.startsWith("!read")) {
				String[] input = userInput.split("\\s+", 2);

				for (animeManga animanga : myList)
					if (animanga.getAnimeManga().equals(input[1])) {
						animanga.addChapter();
						return event.getMessage().getChannel().flatMap(channel -> channel
								.createMessage("Currently finished with: " + animanga.getChapter() + " chapters."));
					}

				return event.getMessage().getChannel()
						.flatMap(channel -> channel.createMessage("Unable to find title."));

			} else if (userInput.startsWith("!edit")) {
				String[] input = userInput.split("\\s+", 3);

				for (animeManga animanga : myList)
					if (animanga.getAnimeManga().equalsIgnoreCase(input[2])) {
						animanga.setChapter(Integer.parseInt(input[1]));
						return event.getMessage().getChannel().flatMap(channel -> channel
								.createMessage("Now currently read up to chapter: " + animanga.getChapter()));
					}

				return event.getMessage().getChannel()
						.flatMap(channel -> channel.createMessage("Unable to find title."));
			}

			return Mono.empty();
		}).subscribe();

		gateway.onDisconnect().block();

	}
}
