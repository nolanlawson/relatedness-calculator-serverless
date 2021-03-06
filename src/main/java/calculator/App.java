package calculator;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nolanlawson.relatedness.Relatedness;
import com.nolanlawson.relatedness.RelatednessCalculator;
import com.nolanlawson.relatedness.UnknownRelationException;
import com.nolanlawson.relatedness.parser.ParseError;
import com.nolanlawson.relatedness.parser.RelationParseResult;
import com.nolanlawson.relatedness.parser.RelativeNameParser;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final int MAX_LENGTH = 500;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static final Pattern pattern = Pattern.compile("b=\"0,0,([0-9.]+),");
    private static final String dotStaticPath;

    static {
        try {
            final String uuid = UUID.randomUUID().toString();
            final String tmpDir = Files.createTempDirectory("relatedness-" + uuid).toFile().getAbsolutePath();
            dotStaticPath = new File(tmpDir, "dot_static").toString();
            ensureDotStaticExists(); // run statically to benefit from burst speeds during function startup
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Do one throwaway parse to initialize to benefit from burst speeds during function startup
        long start = System.currentTimeMillis();
        RelativeNameParser.parse("grandma", true);
        System.out.println("Took " + (System.currentTimeMillis() - start) + "ms to run RelativeNameParser.parse() (warmup)");
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=utf-8");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            final String q = input.getQueryStringParameters().get("q");
            if (q.length() > MAX_LENGTH) {
                throw new RuntimeException("Length too large: " + q.length());
            }

            RelatednessResult result = generateResult(q);
            String output = gson.toJson(result);
            headers.put("Cache-Control", "public, max-age=0, s-maxage=604800");
            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (Throwable t) {
            t.printStackTrace();
            return response
                    .withBody("{\"failed\":true,\"errorMessage\":\"Internal server error\"}")
                    .withStatusCode(500);
        }
    }

    private RelatednessResult generateResult(String query) {
        query = QueryUtils.cleanQuery(query);
        RelationParseResult relationParseResult;
        try {
            long start = System.currentTimeMillis();
            relationParseResult = RelativeNameParser.parse(query, true);
            System.out.println("Took " + (System.currentTimeMillis() - start) + "ms to run RelativeNameParser.parse()");
        } catch (UnknownRelationException e) { // relation exception
            RelatednessResult result = new RelatednessResult();
            result.setFailed(true);
            result.setErrorMessage(e.getMessage());
            return result;
        }

        // if there is ambiguity, give the user a chance to recover
        if (relationParseResult.getParseError() == ParseError.Ambiguity) {
            RelatednessResult result = new RelatednessResult();
            result.setFailed(true);
            result.setParseError(relationParseResult.getParseError().toString());
            result.setAlternateQueries(relationParseResult.getAmbiguityResolutions());
            return result;
        } else if (relationParseResult.getParseError() == ParseError.StepRelation) {
            RelatednessResult result = new RelatednessResult();
            result.setFailed(true);
            result.setParseError(relationParseResult.getParseError().toString());
            return result;
        }

        // create xdot graph
        String rawGraph = relationParseResult.getGraph().drawGraph();
        String graph = convertToXdot(rawGraph);

        // find the pixel width
        Matcher matcher = pattern.matcher(graph);
        matcher.find();

        double graphWidth = Double.parseDouble(matcher.group(1));

        // calculate the Relatedness from the Relation
        long start = System.currentTimeMillis();
        Relatedness relatedness = RelatednessCalculator.calculate(relationParseResult.getRelation());
        System.out.println("Took " + (System.currentTimeMillis() - start) + "ms to run RelatednessCalculator.calculate()");

        RelatednessResult result = new RelatednessResult();
        result.setGraph(graph);
        result.setCoefficient(relatedness.getCoefficient());
        result.setDegree(relatedness.getAverageDegree());
        result.setGraphWidth(graphWidth);
        result.setCleanedQuery(query);
        return result;
    }

    private String convertToXdot(String graph) {
        long start = System.currentTimeMillis();
        String res = convertToXdotViaCommandLine(graph);
        System.out.println("Took " + (System.currentTimeMillis() - start) + "ms to run convertToXdot()");
        return res;
    }

    private static void ensureDotStaticExists() throws IOException {
        long start = System.currentTimeMillis();
        // using static dot binary via https://lifeinplaintextblog.wordpress.com/deploying-graphviz-on-aws-lambda/
        // with this addendum: https://github.com/restruct/dot-static#additional-notescredits
        File file = new File(dotStaticPath);
        if(!file.exists()) {
            InputStream dotStaticInputStream = App.class.getClassLoader().getResourceAsStream("dot_static");
            OutputStream outputStream = new FileOutputStream(new File(dotStaticPath));
            int length;
            byte[] bytes = new byte[1024];
            while ((length = dotStaticInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
            }
            outputStream.flush();
            outputStream.close();
        }
        file = new File(dotStaticPath);
        file.setExecutable(true);
        System.out.println("Took " + (System.currentTimeMillis() - start) + "ms to run ensureDotStaticExists()");
    }

    private String convertToXdotViaCommandLine(String graph) {
        try {
            Process process = Runtime.getRuntime().exec(dotStaticPath + " -Txdot");
            inheritIO(process.getErrorStream(), System.err);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            output.write(graph);
            output.flush();
            output.close();
            process.waitFor();
            StringBuilder result = new StringBuilder();
            while (true) {
                String s = input.readLine();
                if (s == null) {
                    break;
                }
                result.append(s).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void inheritIO(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                Scanner sc = new Scanner(src);
                while (sc.hasNextLine()) {
                    dest.println(sc.nextLine());
                }
            }
        }).start();
    }
}
