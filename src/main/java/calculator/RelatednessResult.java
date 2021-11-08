package calculator;

import java.util.List;
import java.util.Objects;

public class RelatednessResult {
    private boolean failed;
    private String parseError;
    private String errorMessage;
    private List<String> alternateQueries;
    private double degree;
    private double coefficient;
    private String graph;
    private double graphWidth;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelatednessResult that = (RelatednessResult) o;
        return failed == that.failed && degree == that.degree && Double.compare(that.coefficient, coefficient) == 0 && Double.compare(that.graphWidth, graphWidth) == 0 && Objects.equals(parseError, that.parseError) && Objects.equals(errorMessage, that.errorMessage) && Objects.equals(alternateQueries, that.alternateQueries) && Objects.equals(graph, that.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(failed, parseError, errorMessage, alternateQueries, degree, coefficient, graph, graphWidth);
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public String getParseError() {
        return parseError;
    }

    public void setParseError(String parseError) {
        this.parseError = parseError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getAlternateQueries() {
        return alternateQueries;
    }

    public void setAlternateQueries(List<String> alternateQueries) {
        this.alternateQueries = alternateQueries;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public double getGraphWidth() {
        return graphWidth;
    }

    public void setGraphWidth(double graphWidth) {
        this.graphWidth = graphWidth;
    }
}
