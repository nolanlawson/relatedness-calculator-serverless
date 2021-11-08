package calculator;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaAsyncClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.nolanlawson.relatedness.Relatedness;
import com.nolanlawson.relatedness.autosuggest.RelationSuggester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.nolanlawson.relatedness.parser.ParseError;
import com.nolanlawson.relatedness.parser.RelationParseResult;
import com.nolanlawson.relatedness.parser.RelativeNameParser;
import com.nolanlawson.relatedness.RelatednessCalculator;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final int MAX_LENGTH = 500;
    private static final RelationSuggester relationSuggester = new RelationSuggester();
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private static final Pattern pattern = Pattern.compile("b=\"0,0,([0-9.]+),");

    /**
     * Use a small LRU cache to hold the response data
     */
    private final Map<String, RelatednessResult> cache = new MapMaker()
            .maximumSize(1000)
            .makeComputingMap(
                    new Function<>() {
                        public RelatednessResult apply(String q) {
                            return generateResultWithoutCaching(q);
                        }
                    });

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf-8");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            final String q = input.getQueryStringParameters().get("q");
            if (q.length() > MAX_LENGTH) {
                throw new RuntimeException("Length too large: " + q.length());
            }

            RelatednessResult result = cache.get(q.trim().toLowerCase());
            String output = gson.toJson(result);
            headers.put("Cache-Control", "public, max-age=604800, s-max-age=604800");
            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (Throwable t) {
            t.printStackTrace();
            return response
                    .withBody("{\"error\":true}")
                    .withStatusCode(500);
        }
    }

    RelatednessResult generateResultWithoutCaching(String query) {
        RelationParseResult relationParseResult;
        try {
            relationParseResult = RelativeNameParser.parse(query, true);
        } catch (Exception e) { // relation exception
            RelatednessResult result = new RelatednessResult();
            result.setFailed(true);
            result.setErrorMessage(e.getMessage());
            return result;
        }

        // if there is ambiguity, give the user a chance to recover
        if (relationParseResult.getParseError() == ParseError.Ambiguity) {
            System.out.println("ambiguous!!");
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
        Relatedness relatedness = RelatednessCalculator.calculate(relationParseResult.getRelation());

        RelatednessResult result = new RelatednessResult();
        result.setGraph(graph);
        result.setCoefficient(relatedness.getCoefficient());
        result.setDegree(relatedness.getAverageDegree());
        result.setGraphWidth(graphWidth);
        return result;
    }

    private String convertToXdot(String graph) {
        AWSLambda client = AWSLambdaAsyncClient.builder()
                .withRegion(Regions.US_WEST_2)
                .build();

        Map<String, String> map = new HashMap<>();
        map.put("body", graph);
        String json = gson.toJson(map);
        InvokeRequest request = new InvokeRequest()
                .withFunctionName(System.getenv("xdotService"))
                .withPayload(json)
                .withInvocationType(InvocationType.RequestResponse);
        InvokeResult result = client.invoke(request);
        String resultBodyJson = new String(result.getPayload().array(), StandardCharsets.UTF_8);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultParsed = gson.fromJson(resultBodyJson, Map.class);
        String body = (String)resultParsed.get("body");
        return body;
    }
}
