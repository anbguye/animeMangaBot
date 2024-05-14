
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

		String t = System.getenv("MANGAANIME_API_TOKEN");
		DiscordClient client = DiscordClient.create(t); // Connects to discord API
		GatewayDiscordClient gateway = client.login().block(); // Handles receiving messages, users joining server,
																// executing actions defined in code

		gateway.on(MessageCreateEvent.class).flatMap(event -> {

			String userInput = event.getMessage().getContent();
			if (userInput.equals("!hello"))
				return event.getMessage().getChannel().flatMap(channel -> channel.createMessage("Hello, Discord!"));

			return Mono.empty();
		}).subscribe();

		gateway.onDisconnect().block();

	}
}
