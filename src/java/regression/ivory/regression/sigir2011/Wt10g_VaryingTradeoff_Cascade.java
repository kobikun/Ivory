package ivory.regression.sigir2011;

import ivory.cascade.retrieval.CascadeBatchQueryRunner;
import ivory.eval.GradedQrels;
import ivory.regression.GroundTruth;
import ivory.regression.GroundTruth.Metric;
import ivory.smrf.retrieval.Accumulator;

import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.umd.cloud9.collection.DocnoMapping;

public class Wt10g_VaryingTradeoff_Cascade {
	private static final Logger LOG = Logger.getLogger(Wt10g_VaryingTradeoff_Cascade.class);

	private static String[] p1 = new String[] {
	 "501", "0.3193",  "502", "0.3123",  "503", "0.1036",  "504", "0.7406",  "505", "0.3852", 
	 "506", "0.1186",  "507", "0.5553",  "508", "0.2812",  "509", "0.5533",  "510", "0.8579", 
	 "511", "0.5177",  "512", "0.3084",  "513", "0.0435",  "514", "0.2691",  "515", "0.2837", 
	 "516", "0.0780",  "517", "0.0767",  "518", "0.3953",  "519", "0.3367",  "520", "0.1743", 
	 "521", "0.2970",  "522", "0.4656",  "523", "0.3672",  "524", "0.1572",  "525", "0.1921", 
	 "526", "0.0762",  "527", "0.8431",  "528", "0.8213",  "529", "0.4364",  "530", "0.6278", 
	 "531", "0.1826",  "532", "0.5110",  "533", "0.6656",  "534", "0.0387",  "535", "0.0981", 
	 "536", "0.2841",  "537", "0.2176",  "538", "0.5110",  "539", "0.4368",  "540", "0.1964", 
	 "541", "0.4812",  "542", "0.0381",  "543", "0.1420",  "544", "0.5432",  "545", "0.4074", 
	 "546", "0.2747",  "547", "0.2521",  "548", "0.5912",  "549", "0.5653",  "550", "0.3659"};

	private static String[] p3 = new String[] {
	 "501", "0.3418",  "502", "0.3123",  "503", "0.1036",  "504", "0.7406",  "505", "0.3852", 
	 "506", "0.1186",  "507", "0.5553",  "508", "0.2776",  "509", "0.5533",  "510", "0.8579", 
	 "511", "0.4897",  "512", "0.3084",  "513", "0.0435",  "514", "0.2691",  "515", "0.2847", 
	 "516", "0.0780",  "517", "0.0802",  "518", "0.3713",  "519", "0.3367",  "520", "0.1743", 
	 "521", "0.3007",  "522", "0.4293",  "523", "0.3672",  "524", "0.1572",  "525", "0.1921", 
	 "526", "0.0762",  "527", "0.8431",  "528", "0.8213",  "529", "0.4364",  "530", "0.6281", 
	 "531", "0.1826",  "532", "0.5110",  "533", "0.5420",  "534", "0.0387",  "535", "0.1234", 
	 "536", "0.2867",  "537", "0.2001",  "538", "0.5110",  "539", "0.4368",  "540", "0.1964", 
	 "541", "0.4814",  "542", "0.0381",  "543", "0.1420",  "544", "0.5432",  "545", "0.4074", 
	 "546", "0.2747",  "547", "0.2521",  "548", "0.5803",  "549", "0.5688",  "550", "0.3659"};

	private static String [] p5 = new String [] {
	 "501", "0.2770",  "502", "0.3035",  "503", "0.1036",  "504", "0.7101",  "505", "0.3803", 
	 "506", "0.1186",  "507", "0.5529",  "508", "0.3604",  "509", "0.5241",  "510", "0.8534", 
	 "511", "0.4555",  "512", "0.3531",  "513", "0.0435",  "514", "0.2639",  "515", "0.2815", 
	 "516", "0.0780",  "517", "0.0802",  "518", "0.4082",  "519", "0.3782",  "520", "0.1779", 
	 "521", "0.2918",  "522", "0.4228",  "523", "0.3672",  "524", "0.1566",  "525", "0.2179", 
	 "526", "0.0762",  "527", "0.8431",  "528", "0.8213",  "529", "0.4666",  "530", "0.6278", 
	 "531", "0.0517",  "532", "0.5110",  "533", "0.5278",  "534", "0.0387",  "535", "0.1245", 
	 "536", "0.2595",  "537", "0.2277",  "538", "0.5110",  "539", "0.4368",  "540", "0.1964", 
	 "541", "0.4894",  "542", "0.0885",  "543", "0.0896",  "544", "0.5444",  "545", "0.3740", 
	 "546", "0.1574",  "547", "0.2533",  "548", "0.5912",  "549", "0.6119",  "550", "0.3731"};

	private static String [] p7 = new String[] {
	 "501", "0.2770",  "502", "0.3035",  "503", "0.1036",  "504", "0.7101",  "505", "0.3803", 
	 "506", "0.1186",  "507", "0.5529",  "508", "0.3604",  "509", "0.5241",  "510", "0.8534", 
	 "511", "0.4555",  "512", "0.3531",  "513", "0.0435",  "514", "0.2639",  "515", "0.2815", 
	 "516", "0.0780",  "517", "0.0802",  "518", "0.4082",  "519", "0.3782",  "520", "0.1779", 
	 "521", "0.2918",  "522", "0.4228",  "523", "0.3672",  "524", "0.1566",  "525", "0.2179", 
	 "526", "0.0762",  "527", "0.8431",  "528", "0.8213",  "529", "0.4666",  "530", "0.6278", 
	 "531", "0.0517",  "532", "0.5110",  "533", "0.5278",  "534", "0.0387",  "535", "0.1245", 
	 "536", "0.2595",  "537", "0.2277",  "538", "0.5110",  "539", "0.4368",  "540", "0.1964", 
	 "541", "0.4894",  "542", "0.0885",  "543", "0.0896",  "544", "0.5444",  "545", "0.3416", 
	 "546", "0.1574",  "547", "0.2533",  "548", "0.5912",  "549", "0.6119",  "550", "0.3731"};

	private static String [] p9 = new String[] {
	 "501", "0.3011",  "502", "0.2535",  "503", "0.1479",  "504", "0.7132",  "505", "0.3732", 
	 "506", "0.1186",  "507", "0.5530",  "508", "0.3835",  "509", "0.5171",  "510", "0.8517", 
	 "511", "0.4330",  "512", "0.3341",  "513", "0.0435",  "514", "0.2113",  "515", "0.4027", 
	 "516", "0.0780",  "517", "0.1242",  "518", "0.2950",  "519", "0.3927",  "520", "0.1879", 
	 "521", "0.2466",  "522", "0.4229",  "523", "0.3672",  "524", "0.1659",  "525", "0.1637", 
	 "526", "0.0762",  "527", "0.8291",  "528", "0.7814",  "529", "0.4496",  "530", "0.6789", 
	 "531", "0.0613",  "532", "0.5110",  "533", "0.4432",  "534", "0.0417",  "535", "0.0974", 
	 "536", "0.2402",  "537", "0.2198",  "538", "0.5110",  "539", "0.2710",  "540", "0.1964", 
	 "541", "0.5270",  "542", "0.0885",  "543", "0.0448",  "544", "0.5999",  "545", "0.2713", 
	 "546", "0.1851",  "547", "0.2474",  "548", "0.6053",  "549", "0.5831",  "550", "0.3949"};
	
	@Test
	public void runRegression() throws Exception {
		Map<String, GroundTruth> g = new HashMap<String, GroundTruth>();

		g.put("Wt10g-Cascade-0.1", new GroundTruth("Wt10g-Cascade-0.1", Metric.NDCG20, 50, p1, 0.3560f));
		g.put("Wt10g-Cascade-0.3", new GroundTruth("Wt10g-Cascade-0.3", Metric.NDCG20, 50, p3, 0.3523f));
		g.put("Wt10g-Cascade-0.5", new GroundTruth("Wt10g-Cascade-0.5", Metric.NDCG20, 50, p5, 0.3491f));
		g.put("Wt10g-Cascade-0.7", new GroundTruth("Wt10g-Cascade-0.7", Metric.NDCG20, 50, p7, 0.3484f));
		g.put("Wt10g-Cascade-0.9", new GroundTruth("Wt10g-Cascade-0.9", Metric.NDCG20, 50, p9, 0.3407f));

		GradedQrels qrels = new GradedQrels("data/wt10g/qrels.wt10g.all");

    String[] params = new String[] {
            "data/wt10g/run.wt10g.SIGIR2011.varying.tradeoff.cascade.xml",
            "data/wt10g/queries.wt10g.501-550.xml" };

		FileSystem fs = FileSystem.getLocal(new Configuration());

		CascadeBatchQueryRunner qr = new CascadeBatchQueryRunner(params, fs);

		long start = System.currentTimeMillis();
		qr.runQueries();
		long end = System.currentTimeMillis();

		LOG.info("Total query time: " + (end - start) + "ms");

		DocnoMapping mapping = qr.getDocnoMapping();

		for (String model : qr.getModels()) {
			LOG.info("Verifying results of model \"" + model + "\"");

			Map<String, Accumulator[]> results = qr.getResults(model);
			g.get(model).verify(results, mapping, qrels);

			LOG.info("Done!");
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(Wt10g_VaryingTradeoff_Cascade.class);
	}
}
