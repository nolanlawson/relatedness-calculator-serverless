package calculator;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.util.HashMap;

public class AppTest {
  // Test broken because of needing to call another lambda
//  @Test
//  public void successfulResponse() {
//    App app = new App();
//    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
//    input.setQueryStringParameters(new HashMap<String, String>(){{
//      put("q", "grandfather");
//    }});
//    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
//    assertEquals(result.getStatusCode().intValue(), 200);
//    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
//    String content = result.getBody();
//    assertNotNull(content);
//    assertEquals(content, "{}");
//  }
}
