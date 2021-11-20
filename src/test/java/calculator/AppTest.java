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
    assertEquals(content, "{\"failed\":false,\"degree\":2.0,\"coefficient\":0.25,\"graph\":\"digraph a {\\n\\tgraph [_draw_=\\\"c 9 -#fffffe00 C 7 -#ffffff P 4 0 0 0 180 166.97 180 166.97 0 \\\",\\n\\t\\tbb=\\\"0,0,166.97,180\\\",\\n\\t\\tsize=\\\"10,10\\\",\\n\\t\\txdotversion=1.7\\n\\t];\\n\\tnode [label=\\\"\\\\N\\\"];\\n\\ta\\t [_draw_=\\\"S 15 -setlinewidth(3) c 7 -#788c45 C 7 -#f6f6f6 E 83.49 162 83.47 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 83.49 157.8 0 105.03 16 -Your grandfather \\\",\\n\\t\\tcolor=\\\"#788c45\\\",\\n\\t\\tfillcolor=\\\"#f6f6f6\\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=\\\"Your grandfather\\\",\\n\\t\\tpenwidth=3.0,\\n\\t\\tpos=\\\"83.487,162\\\",\\n\\t\\tstyle=filled,\\n\\t\\twidth=2.3191];\\n\\tc\\t [_draw_=\\\"c 7 -#000000 e 83.49 90 61.47 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 83.49 85.8 0 73.13 11 -Your parent \\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=\\\"Your parent\\\",\\n\\t\\tpos=\\\"83.487,90\\\",\\n\\t\\twidth=1.7079];\\n\\ta -> c\\t [_draw_=\\\"c 7 -#000000 B 4 83.49 143.83 83.49 136.13 83.49 126.97 83.49 118.42 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 86.99 118.41 83.49 108.41 79.99 118.41 \\\",\\n\\t\\tpos=\\\"e,83.487,108.41 83.487,143.83 83.487,136.13 83.487,126.97 83.487,118.42\\\"];\\n\\tb\\t [_draw_=\\\"S 15 -setlinewidth(3) c 7 -#788c45 C 7 -#f6f6f6 E 83.49 18 28.43 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 83.49 13.8 0 24.9 3 -You \\\",\\n\\t\\tcolor=\\\"#788c45\\\",\\n\\t\\tfillcolor=\\\"#f6f6f6\\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=You,\\n\\t\\tpenwidth=3.0,\\n\\t\\tpos=\\\"83.487,18\\\",\\n\\t\\tstyle=filled,\\n\\t\\twidth=0.7837];\\n\\tc -> b\\t [_draw_=\\\"c 7 -#000000 B 4 83.49 71.83 83.49 64.13 83.49 54.97 83.49 46.42 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 86.99 46.41 83.49 36.41 79.99 46.41 \\\",\\n\\t\\tpos=\\\"e,83.487,36.413 83.487,71.831 83.487,64.131 83.487,54.974 83.487,46.417\\\"];\\n}\\n\",\"graphWidth\":166.97,\"cleanedQuery\":\"grandfather\"}");
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

  @Test
  public void anotherSuccessfulResponse() {
    App app = new App();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    input.setQueryStringParameters(new HashMap<String, String>(){{
      put("q", "grandfather's brother");
    }});
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(result.getStatusCode().intValue(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json;charset=utf-8");
    String content = result.getBody();
    assertNotNull(content);
    assertEquals(content, "{\"failed\":false,\"degree\":4.0,\"coefficient\":0.125,\"graph\":\"digraph a {\\n\\tgraph [_draw_=\\\"c 9 -#fffffe00 C 7 -#ffffff P 4 0 0 0 345.18 323.55 345.18 323.55 0 \\\",\\n\\t\\tbb=\\\"0,0,323.55,345.18\\\",\\n\\t\\tsize=\\\"10,10\\\",\\n\\t\\txdotversion=1.7\\n\\t];\\n\\tnode [label=\\\"\\\\N\\\"];\\n\\ta\\t [_draw_=\\\"S 15 -setlinewidth(3) c 7 -#788c45 C 7 -#f6f6f6 E 83.49 185.3 83.47 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 83.49 181.1 0 105.03 16 -Your grandfather \\\",\\n\\t\\tcolor=\\\"#788c45\\\",\\n\\t\\tfillcolor=\\\"#f6f6f6\\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=\\\"Your grandfather\\\",\\n\\t\\tpenwidth=3.0,\\n\\t\\tpos=\\\"83.487,185.3\\\",\\n\\t\\tstyle=filled,\\n\\t\\twidth=2.3191];\\n\\tc\\t [_draw_=\\\"c 7 -#000000 e 83.49 90 61.47 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 83.49 85.8 0 73.13 11 -Your parent \\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=\\\"Your parent\\\",\\n\\t\\tpos=\\\"83.487,90\\\",\\n\\t\\twidth=1.7079];\\n\\ta -> c\\t [_draw_=\\\"c 7 -#000000 B 4 83.49 166.92 83.49 153.21 83.49 134.22 83.49 118.54 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 86.99 118.32 83.49 108.32 79.99 118.32 \\\",\\n\\t\\tpos=\\\"e,83.487,108.32 83.487,166.92 83.487,153.21 83.487,134.22 83.487,118.54\\\"];\\n\\tb\\t [_draw_=\\\"S 15 -setlinewidth(3) c 7 -#788c45 C 7 -#f6f6f6 E 83.49 18 28.43 18 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 83.49 13.8 0 24.9 3 -You \\\",\\n\\t\\tcolor=\\\"#788c45\\\",\\n\\t\\tfillcolor=\\\"#f6f6f6\\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=0.5,\\n\\t\\tlabel=You,\\n\\t\\tpenwidth=3.0,\\n\\t\\tpos=\\\"83.487,18\\\",\\n\\t\\tstyle=filled,\\n\\t\\twidth=0.7837];\\n\\tc -> b\\t [_draw_=\\\"c 7 -#000000 B 4 83.49 71.83 83.49 64.13 83.49 54.97 83.49 46.42 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 86.99 46.41 83.49 36.41 79.99 46.41 \\\",\\n\\t\\tpos=\\\"e,83.487,36.413 83.487,71.831 83.487,64.131 83.487,54.974 83.487,46.417\\\"];\\n\\td\\t [_draw_=\\\"c 7 -#000000 e 90.49 303.89 69.13 41.09 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 90.49 316.49 0 29.56 4 -Your F 14 9 -Helvetica c 7 -#000000 T 90.49 299.69 0 81.67 13 -grandfather'\\\\\\ns F 14 9 -Helvetica c 7 -#000000 T 90.49 282.89 0 39.68 6 -parent \\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=1.1471,\\n\\t\\tlabel=\\\"Your\\\\ngrandfather's\\\\nparent\\\",\\n\\t\\tpos=\\\"90.487,303.89\\\",\\n\\t\\twidth=1.9185];\\n\\td -> a\\t [_draw_=\\\"c 7 -#000000 B 4 88.04 262.41 87.09 246.33 86.03 228.33 85.17 213.77 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 88.64 213.17 84.55 203.39 81.65 213.58 \\\",\\n\\t\\tpos=\\\"e,84.555,203.39 88.039,262.41 87.09,246.33 86.027,228.33 85.168,213.77\\\"];\\n\\te\\t [_draw_=\\\"S 15 -setlinewidth(3) c 7 -#788c45 C 7 -#f6f6f6 E 254.49 185.3 69.13 41.09 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 254.49 197.9 0 29.56 4 -Your F 14 9 -Helvetica c 7 -#000000 T 254.49 181.1 0 81.67 13 -grandfather'\\\\\\ns F 14 9 -Helvetica c 7 -#000000 T 254.49 164.3 0 44.34 7 -brother \\\",\\n\\t\\tcolor=\\\"#788c45\\\",\\n\\t\\tfillcolor=\\\"#f6f6f6\\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=1.1471,\\n\\t\\tlabel=\\\"Your\\\\ngrandfather's\\\\nbrother\\\",\\n\\t\\tpenwidth=3.0,\\n\\t\\tpos=\\\"254.49,185.3\\\",\\n\\t\\tstyle=filled,\\n\\t\\twidth=1.9185];\\n\\td -> e\\t [_draw_=\\\"c 7 -#000000 B 4 134.87 271.79 155.64 256.77 180.53 238.77 202.22 223.09 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 204.3 225.9 210.35 217.21 200.2 220.23 \\\",\\n\\t\\tpos=\\\"e,210.35,217.21 134.87,271.79 155.64,256.77 180.53,238.77 202.22,223.09\\\"];\\n\\tf\\t [_draw_=\\\"c 7 -#000000 e 250.49 303.89 69.13 41.09 \\\",\\n\\t\\t_ldraw_=\\\"F 14 9 -Helvetica c 7 -#000000 T 250.49 316.49 0 29.56 4 -Your F 14 9 -Helvetica c 7 -#000000 T 250.49 299.69 0 81.67 13 -grandfather'\\\\\\ns F 14 9 -Helvetica c 7 -#000000 T 250.49 282.89 0 75.47 12 -other parent \\\",\\n\\t\\tfontname=Helvetica,\\n\\t\\theight=1.1471,\\n\\t\\tlabel=\\\"Your\\\\ngrandfather's\\\\nother parent\\\",\\n\\t\\tpos=\\\"250.49,303.89\\\",\\n\\t\\twidth=1.9185];\\n\\tf -> a\\t [_draw_=\\\"c 7 -#000000 B 4 205.73 272.11 177.61 252.13 141.95 226.81 116.41 208.68 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 118.35 205.76 108.17 202.82 114.29 211.46 \\\",\\n\\t\\tpos=\\\"e,108.17,202.82 205.73,272.11 177.61,252.13 141.95,226.81 116.41,208.68\\\"];\\n\\tf -> e\\t [_draw_=\\\"c 7 -#000000 B 4 251.89 262.41 252.16 254.24 252.45 245.58 252.74 237.12 \\\",\\n\\t\\t_hdraw_=\\\"S 5 -solid c 7 -#000000 C 7 -#000000 P 3 256.24 237.06 253.08 226.95 249.25 236.83 \\\",\\n\\t\\tpos=\\\"e,253.08,226.95 251.89,262.41 252.16,254.24 252.45,245.58 252.74,237.12\\\"];\\n}\\n\",\"graphWidth\":323.55,\"cleanedQuery\":\"grandfather's brother\"}");
  }

  @Test
  public void testCleanQuery () {
    assertEquals(QueryUtils.cleanQuery("double cousins"), "double cousin");
    assertEquals(QueryUtils.cleanQuery("GRANDCHILDREN"), "grandchild");
    assertEquals(QueryUtils.cleanQuery("  father  "), "father");
    assertEquals(QueryUtils.cleanQuery(null), "");
    assertEquals(QueryUtils.cleanQuery("My father"), "father");
  }
}
