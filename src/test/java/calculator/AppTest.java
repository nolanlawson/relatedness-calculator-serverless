package calculator;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.util.HashMap;

public class AppTest {
  @Test
  public void successfulResponse() {
    App app = new App();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    input.setQueryStringParameters(new HashMap<String, String>(){{
      put("q", "grandfather");
    }});
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(result.getStatusCode().intValue(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"failed\":false,\"degree\":2.0,\"coefficient\":0.25,\"graph\":\"digraph a {\\n\\tgraph [_draw_=\\\"c 9 -#fffffe00 C 7 -#ffffff P 4 0 0 0 180 174.18 180 174.18 0 \\\",\\n\\t\\tbb=\\\"0,0,174.18,180\\\",\\n\\t\\tsize=\\\"10,10\\\",\\n\\t\\txdotversion=1.7\\n\\t];\\n\\tnode [label=\\\"\\\\N\\\"];\\n\\ta\\t[_draw_=\\\"S 15 -setlinewidth(3) c 7 -#788c45 C 7 -#f6f6f6 E 87.09 162 87.18 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 87.09 158.3 0 118 16 -Your grandfather \\\",\\n\\t\\tcolor=\\\"#788c45\\\",\\n\\t\\tfillcolor=\\\"#f6f6f6\\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=\\\"Your grandfather\\\",\\n\\t\\tpenwidth=3.0,\\n\\t\\tpos=\\\"87.092,162\\\",\\n\\t\\tstyle=filled,\\n\\t\\twidth=2.4192];\\n\\tc\\t[_draw_=\\\"c 7 -#000000 e 87.09 90 62.29 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 87.09 86.3 0 80 11 -Your parent \\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=\\\"Your parent\\\",\\n\\t\\tpos=\\\"87.092,90\\\",\\n\\t\\twidth=1.7332];\\n\\ta -> c\\t[_draw_=\\\"c 7 -#000000 B 4 87.09 143.7 87.09 135.98 87.09 126.71 87.09 118.11 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 90.59 118.1 87.09 108.1 83.59 118.1 \\\",\\n\\t\\tpos=\\\"e,87.092,108.1 87.092,143.7 87.092,135.98 87.092,126.71 87.092,118.11\\\"];\\n\\tb\\t[_draw_=\\\"S 15 -setlinewidth(3) c 7 -#788c45 C 7 -#f6f6f6 E 87.09 18 27 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 87.09 14.3 0 25 3 -You \\\",\\n\\t\\tcolor=\\\"#788c45\\\",\\n\\t\\tfillcolor=\\\"#f6f6f6\\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=You,\\n\\t\\tpenwidth=3.0,\\n\\t\\tpos=\\\"87.092,18\\\",\\n\\t\\tstyle=filled,\\n\\t\\twidth=0.75];\\n\\tc -> b\\t[_draw_=\\\"c 7 -#000000 B 4 87.09 71.7 87.09 63.98 87.09 54.71 87.09 46.11 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 90.59 46.1 87.09 36.1 83.59 46.1 \\\",\\n\\t\\tpos=\\\"e,87.092,36.104 87.092,71.697 87.092,63.983 87.092,54.712 87.092,46.112\\\"];\\n}\\n\",\"graphWidth\":174.18}");
  }

  @Test
  public void emptyRequest() {
    App app = new App();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(result.getStatusCode().intValue(), 500);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"failed\":true,\"errorMessage\":\"Internal server error\"}");
  }

  @Test
  public void nullRequest() {
    App app = new App();
    APIGatewayProxyResponseEvent result = app.handleRequest(null, null);
    assertEquals(result.getStatusCode().intValue(), 500);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"failed\":true,\"errorMessage\":\"Internal server error\"}");
  }

  @Test
  public void requestTooLong() {
    App app = new App();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    input.setQueryStringParameters(new HashMap<String, String>(){{
      String q = "";
      for (int i = 0; i < 1000; i++) {
        q += "grandfather";
      }
      put("q", q);
    }});
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(result.getStatusCode().intValue(), 500);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"failed\":true,\"errorMessage\":\"Internal server error\"}");
  }

  @Test
  public void stepRelation() {
    App app = new App();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    input.setQueryStringParameters(new HashMap<String, String>(){{
      put("q", "stepfather");
    }});
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(result.getStatusCode().intValue(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"failed\":true,\"parseError\":\"StepRelation\",\"degree\":0.0,\"coefficient\":0.0,\"graphWidth\":0.0}");
  }

  @Test
  public void ambiguousRelation() {
    App app = new App();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    input.setQueryStringParameters(new HashMap<String, String>(){{
      put("q", "cousin once removed");
    }});
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(result.getStatusCode().intValue(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"failed\":true,\"parseError\":\"Ambiguity\",\"alternateQueries\":[\"parent's cousin\",\"cousin's child\"],\"degree\":0.0,\"coefficient\":0.0,\"graphWidth\":0.0}");
  }

  @Test
  public void unknownRelation() {
    App app = new App();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    input.setQueryStringParameters(new HashMap<String, String>(){{
      put("q", "father's brother's godzilla");
    }});
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(result.getStatusCode().intValue(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"failed\":true,\"errorMessage\":\"Cannot parse 'father's brother's godzilla': unknown string ''s godzilla'\",\"degree\":0.0,\"coefficient\":0.0,\"graphWidth\":0.0}");
  }
}
