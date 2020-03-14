package uff.ic.swlab.kws;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InvalidNameException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.expr.aggregate.Accumulator;
import org.apache.jena.sparql.expr.aggregate.AccumulatorFactory;
import org.apache.jena.sparql.expr.aggregate.AggCustom;
import org.apache.jena.sparql.expr.aggregate.AggregateRegistry;
import org.apache.jena.sparql.graph.NodeConst;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uff.ic.swlab.jena.sparql.aggregate.AccTMinMax;
import uff.ic.swlab.jena.sparql.aggregate.KwFreqScore;
import uff.ic.swlab.jena.sparql.aggregate.MinimumCommonString;

public class FusekiServer {

    public String hostname;
    public int httpPort;

    public FusekiServer() {
        hostname = "localhost";
        httpPort = 3030;
    }

    public FusekiServer(String hostname, int httpPort) {
        this.hostname = hostname;
        this.httpPort = httpPort;
    }

    public String baseHttpUrl() {
        return "http://" + hostname + (httpPort == 80 ? "" : ":" + httpPort) + "/";
    }

    public String getBackupURL(String datasetname) {
        return String.format(baseHttpUrl() + "$/backup/%1$s", datasetname);
    }

    public String getQuadsURL(String datasetname) {
        return String.format(baseHttpUrl() + "%1s/", datasetname);
    }

    public String getSparqlURL(String datasetname) {
        return String.format(baseHttpUrl() + "%1s/sparql", datasetname);
    }

    public String getUpdateURL(String datasetname) {
        return String.format(baseHttpUrl() + "%1$s/update", datasetname);
    }

    public String getDataURL(String datasetname) {
        return String.format(baseHttpUrl() + "%1s/data", datasetname);
    }

    public String updateURL(String datasetname) {
        return String.format(baseHttpUrl() + "%1s/update", datasetname);
    }

    public synchronized List<String> listGraphNames(String datasetname) {
        List<String> graphNames = new ArrayList<>();

        String queryString = "select distinct ?g where {graph ?g {[] ?p [].}}";
        try (QueryExecution exec = new QueryEngineHTTP(getSparqlURL(datasetname), queryString, HttpClients.createDefault())) {
            ResultSet rs = exec.execSelect();
            while (rs.hasNext())
                graphNames.add(rs.next().getResource("g").getURI());
        } catch (Exception e) {
        }

        return graphNames;
    }

    public synchronized List<String> listGraphNames(String datasetname, long timeout) {
        List<String> graphNames = new ArrayList<>();

        String queryString = "select distinct ?g where {graph ?g {[] ?p [].}}";
        try (QueryExecution exec = new QueryEngineHTTP(getSparqlURL(datasetname), queryString, HttpClients.createDefault())) {
            ((QueryEngineHTTP) exec).setTimeout(timeout);
            ResultSet rs = exec.execSelect();
            while (rs.hasNext())
                graphNames.add(rs.next().getResource("g").getURI());
        } catch (Exception e) {
        }

        return graphNames;
    }

    public synchronized Model execConstruct(String queryString, String datasetname) {
        Model result = ModelFactory.createDefaultModel();
        try (final QueryExecution exec = new QueryEngineHTTP(getSparqlURL(datasetname), queryString, HttpClients.createDefault())) {
            ((QueryEngineHTTP) exec).setModelContentType(WebContent.contentTypeRDFXML);
            exec.execConstruct(result);
        }
        return result;
    }

    public ResultSet execSelect(String queryString, String datasetname) {
        ResultSet result;
        final QueryEngineHTTP qe = new QueryEngineHTTP(getSparqlURL(datasetname), queryString);
        result = qe.execSelect();
        // try (final QueryExecution exec = new QueryEngineHTTP(getSparqlURL(datasetname), queryString, HttpClients.createDefault())) {
        //     ((QueryEngineHTTP) exec).setModelContentType(WebContent.contentTypeRDFXML);
        //     result = exec.execSelect();
        // }
        return result;
    }

    public synchronized void execUpdate(String queryString, String datasetname) {
        String aggUri1 = "http://uff.ic.swlab.jena.sparql.aggregate/tMinMax";
        String aggUri2 = "http://uff.ic.swlab.jena.sparql.aggregate/kwFreqScore";
        String aggUri3 = "http://uff.ic.swlab.jena.sparql.aggregate/minimumCommonString";
        AggregateRegistry.register(aggUri1, tMinMaxFactory, NodeConst.nodeMinusOne);
        AggregateRegistry.register(aggUri2, kwFreqScoreFactory, NodeConst.nodeMinusOne);
        AggregateRegistry.register(aggUri3, minimumCommonString, NodeConst.nodeMinusOne);

        UpdateRequest request = UpdateFactory.create(queryString);
        UpdateProcessor execution = UpdateExecutionFactory.createRemote(request, getUpdateURL(datasetname));
        execution.execute();
    }

    public void backupDataset(String datasetname) throws Exception, IOException {
        System.out.println(String.format("Requesting backup of the Fuseki dataset %1$s...", datasetname));
        String backupUrl = getBackupURL(datasetname);
        HttpClient httpclient = HttpClients.createDefault();
        try {
            HttpResponse response = httpclient.execute(new HttpPost(backupUrl));
            int statuscode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null && statuscode == 200)
                try (final InputStream instream = entity.getContent()) {
                System.out.println(IOUtils.toString(instream, "utf-8"));
                System.out.println("Done.");
            } else
                System.out.println("Backup request failed.");
        } catch (Throwable e) {
            System.out.println("Backup request failed.");
        }
    }

    public synchronized void putModel(Model sourceModel, String datasetname) throws InvalidNameException {
        DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(getDataURL(datasetname), HttpClients.createDefault());
        accessor.putModel(sourceModel);
        Logger.getLogger("info").log(Level.INFO, String.format("Dataset saved (<%1$s>).", "default graph"));
    }

    public synchronized void putModel(Model sourceModel, String datasetname, String graphUri) throws InvalidNameException {
        if (graphUri != null && !graphUri.equals("")) {
            DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(getDataURL(datasetname), HttpClients.createDefault());
            accessor.putModel(graphUri, sourceModel);
            Logger.getLogger("info").log(Level.INFO, String.format("Dataset saved (<%1s>).", graphUri));
            return;
        }
        throw new InvalidNameException(String.format("Invalid graph URI: %1s.", graphUri));
    }

    public synchronized Model getModel(String datasetname, String graphUri) throws InvalidNameException {
        if (graphUri != null && !graphUri.equals("")) {
            DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(getDataURL(datasetname), HttpClients.createDefault());
            Model model = accessor.getModel(graphUri);
            if (model != null)
                return model;
            else
                return ModelFactory.createDefaultModel();
        }
        throw new InvalidNameException(String.format("Invalid graph URI: %1s.", graphUri));
    }

    public synchronized Model getModel(String datasetname) {
        DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(getDataURL(datasetname), HttpClients.createDefault());
        Model model = accessor.getModel();
        if (model != null)
            return model;
        else
            return ModelFactory.createDefaultModel();
    }

    public Dataset getDataset(String datasetname) {
        Dataset dataset = DatasetFactory.create();
        RDFDataMgr.read(dataset, getQuadsURL(datasetname));
        return dataset;
    }

    private static final AccumulatorFactory tMinMaxFactory = new AccumulatorFactory() {
        @Override
        public Accumulator createAccumulator(AggCustom agg, boolean distinct) {
            return new AccTMinMax(agg);
        }
    };

    private static final AccumulatorFactory kwFreqScoreFactory = new AccumulatorFactory() {
        @Override
        public Accumulator createAccumulator(AggCustom agg, boolean distinct) {
            return new KwFreqScore(agg);
        }
    };

    private static final AccumulatorFactory minimumCommonString = new AccumulatorFactory() {
        @Override
        public Accumulator createAccumulator(AggCustom agg, boolean distinct) {
            return new MinimumCommonString(agg);
        }
    };

}
