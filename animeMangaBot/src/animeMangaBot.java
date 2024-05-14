
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
			}

			return Mono.empty();
		}).subscribe();

		gateway.onDisconnect().block();

	}
}
