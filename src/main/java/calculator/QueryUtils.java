package calculator;

/**
 * Utils for fixing user "errors" in their input.  I'm keeping this in the frontend because I think it's out
 * of the scope of the backend app.  The backend app assumes certain formatting in the input
 * (no plurals, no "my" or "your"), etc., whereas the frontend is more open-ended.  
 * 
 * @author nolan
 *
 */
public class QueryUtils {

	public static String cleanQuery(String query) {
		if (query == null) {
			query = "";
		}
		query = query.trim().replace('+', ' ').toLowerCase();

		query = query.replaceAll("\\s+", " ");

		// I assume people will be tempted to enter reciprocal relations as plurals, e.g.
		// "cousins" instead of "cousin"
		query = query.replaceAll("cousins", "cousin")
				.replaceAll("twins", "twin")
				.replaceAll("brothers", "brother")
				.replaceAll("sisters", "sister")
				.replaceAll("children", "child");

		// I assume people will be tempted to add "my", "your", etc.
		return query.replaceAll("^(?:my|your|the)\\s+", "");
	}
}
