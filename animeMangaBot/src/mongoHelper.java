import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.HashMap;
import java.util.function.Consumer;
import org.bson.Document;

/**
 * This class helps the animeMangaDB ran by MongoDB update better, and it also
 * helps the code look a little nicer.
 * 
 * @author Anthony Nguyen
 *
 */
public class mongoHelper {

	// Token to connect to server
	private String connectionString;
	// Create a new client and connect to the database server
	private MongoClient mongoClient;
	// Connects to database and collections
	private MongoDatabase animangaDB;
	private MongoCollection<Document> userCollection;

	public mongoHelper() {
		String connectionString = System.getenv("MONGODB_API_TOKEN");
		mongoClient = MongoClients.create(connectionString);
		animangaDB = mongoClient.getDatabase("animeMangaBotDB");
		userCollection = animangaDB.getCollection("userData");
		;
	}

	/**
	 * Takes the database information and puts it back into the database
	 * 
	 * @param database
	 */
	public void importDB(HashMap<String, animangaList> database) {
		userCollection.find().forEach((Consumer<? super Document>) document -> {
			String userID = document.getString("userID");
			database.put(userID, new animangaList((Document) document.get("animangaList")));
		});
	}

	/**
	 * Checks to see if userID is already in the userID collections. If in database,
	 * update it; Otherwise, add it to the database.
	 * 
	 * @param database
	 * @param userID
	 */
	public void exportDB(HashMap<String, animangaList> database, String userID) {
		if (userCollection.find(new Document("userID", userID)).first() != null)
			userCollection.replaceOne(new Document("userID", userID),
					new Document("userID", userID).append("animangaList", database.get(userID).toDocument()));
		else
			userCollection.insertOne(
					new Document("userID", userID).append("animangaList", database.get(userID).toDocument()));
	}

}
