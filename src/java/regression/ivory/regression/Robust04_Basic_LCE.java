package ivory.regression;

import static ivory.regression.RegressionUtils.loadScoresIntoMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ivory.eval.Qrels;
import ivory.eval.RankedListEvaluator;
import ivory.smrf.retrieval.Accumulator;
import ivory.smrf.retrieval.BatchQueryRunner;

import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.umd.cloud9.collection.DocnoMapping;

public class Robust04_Basic_LCE {

	private static final Logger sLogger = Logger.getLogger(Robust04_Basic_LCE.class);

	private static String[] dir_rm3_f_rawAP = new String[] { "601", "0.6151", "602", "0.2200",
			"603", "0.2653", "604", "0.8570", "605", "0.0556", "606", "0.6381", "607", "0.4035",
			"608", "0.1188", "609", "0.3907", "610", "0.0043", "611", "0.3344", "612", "0.6173",
			"613", "0.2020", "614", "0.6522", "615", "0.0519", "616", "0.6531", "617", "0.3190",
			"618", "0.2160", "619", "0.4794", "620", "0.0336", "621", "0.4663", "622", "0.0000",
			"623", "0.2752", "624", "0.3173", "625", "0.1203", "626", "0.0550", "627", "0.0024",
			"628", "0.2716", "629", "0.0810", "630", "0.5602", "631", "0.0713", "632", "0.4824",
			"633", "0.6519", "634", "0.7615", "635", "0.7645", "636", "0.0460", "637", "0.6329",
			"638", "0.0197", "639", "0.1959", "640", "0.4263", "641", "0.4955", "642", "0.1949",
			"643", "0.5359", "644", "0.3056", "645", "0.6961", "646", "0.3109", "647", "0.5106",
			"648", "0.5520", "649", "0.8303", "650", "0.2903", "651", "0.1013", "652", "0.6399",
			"653", "0.5353", "654", "0.8373", "655", "0.0043", "656", "0.5438", "657", "0.5618",
			"658", "0.3494", "659", "0.4567", "660", "0.7548", "661", "0.4112", "662", "0.3459",
			"663", "0.6603", "664", "0.8607", "665", "0.1828", "666", "0.0083", "667", "0.5010",
			"668", "0.1637", "669", "0.0704", "670", "0.2156", "671", "0.3454", "672", "0.0000",
			"673", "0.2054", "674", "0.0369", "675", "0.2354", "676", "0.2881", "677", "0.9722",
			"678", "0.2488", "679", "0.9583", "680", "0.2831", "681", "0.3472", "682", "0.3073",
			"683", "0.1281", "684", "0.0751", "685", "0.2366", "686", "0.3007", "687", "0.4287",
			"688", "0.1434", "689", "0.0101", "690", "0.0032", "691", "0.3047", "692", "0.4333",
			"693", "0.0959", "694", "0.3748", "695", "0.4926", "696", "0.2871", "697", "0.1322",
			"698", "0.3030", "699", "0.4311", "700", "0.5566" };

	private static String[] dir_rm3_f_rawP10 = new String[] { "601", "0.3000", "602", "0.2000",
			"603", "0.2000", "604", "0.7000", "605", "0.1000", "606", "0.5000", "607", "0.5000",
			"608", "0.0000", "609", "0.7000", "610", "0.0000", "611", "0.7000", "612", "0.7000",
			"613", "0.6000", "614", "0.6000", "615", "0.0000", "616", "0.8000", "617", "0.8000",
			"618", "0.1000", "619", "0.6000", "620", "0.1000", "621", "0.7000", "622", "0.0000",
			"623", "0.6000", "624", "0.4000", "625", "0.1000", "626", "0.0000", "627", "0.0000",
			"628", "0.4000", "629", "0.2000", "630", "0.2000", "631", "0.0000", "632", "1.0000",
			"633", "1.0000", "634", "0.7000", "635", "0.9000", "636", "0.1000", "637", "0.9000",
			"638", "0.2000", "639", "0.3000", "640", "0.8000", "641", "0.6000", "642", "0.3000",
			"643", "0.4000", "644", "0.4000", "645", "0.9000", "646", "0.4000", "647", "0.7000",
			"648", "1.0000", "649", "1.0000", "650", "0.5000", "651", "0.3000", "652", "1.0000",
			"653", "0.4000", "654", "0.9000", "655", "0.0000", "656", "0.6000", "657", "0.7000",
			"658", "0.5000", "659", "0.4000", "660", "1.0000", "661", "0.8000", "662", "0.4000",
			"663", "0.5000", "664", "0.7000", "665", "0.4000", "666", "0.0000", "667", "0.9000",
			"668", "0.4000", "669", "0.0000", "670", "0.4000", "671", "0.0000", "672", "0.0000",
			"673", "0.4000", "674", "0.1000", "675", "0.4000", "676", "0.3000", "677", "0.8000",
			"678", "0.4000", "679", "0.6000", "680", "0.3000", "681", "0.5000", "682", "0.7000",
			"683", "0.3000", "684", "0.1000", "685", "0.3000", "686", "0.5000", "687", "0.6000",
			"688", "0.3000", "689", "0.0000", "690", "0.0000", "691", "0.4000", "692", "0.8000",
			"693", "0.2000", "694", "0.5000", "695", "1.0000", "696", "0.7000", "697", "0.4000",
			"698", "0.4000", "699", "0.6000", "700", "0.7000" };

	private static String[] dir_rm3_s_rawAP = new String[] { "601", "0.6151", "602", "0.2200",
			"603", "0.2653", "604", "0.8569", "605", "0.0557", "606", "0.6381", "607", "0.4028",
			"608", "0.1189", "609", "0.3906", "610", "0.0044", "611", "0.3343", "612", "0.6173",
			"613", "0.2019", "614", "0.6522", "615", "0.0520", "616", "0.6533", "617", "0.3191",
			"618", "0.2161", "619", "0.4728", "620", "0.0336", "621", "0.4664", "622", "0.0000",
			"623", "0.2752", "624", "0.3172", "625", "0.1203", "626", "0.0544", "627", "0.0024",
			"628", "0.2716", "629", "0.0810", "630", "0.5602", "631", "0.0713", "632", "0.4831",
			"633", "0.6515", "634", "0.7615", "635", "0.7645", "636", "0.0460", "637", "0.6329",
			"638", "0.0201", "639", "0.1950", "640", "0.4262", "641", "0.4955", "642", "0.1949",
			"643", "0.5334", "644", "0.3059", "645", "0.6962", "646", "0.3107", "647", "0.5105",
			"648", "0.5520", "649", "0.8307", "650", "0.2907", "651", "0.1013", "652", "0.6394",
			"653", "0.5351", "654", "0.8373", "655", "0.0043", "656", "0.5439", "657", "0.5661",
			"658", "0.3495", "659", "0.4560", "660", "0.7548", "661", "0.4107", "662", "0.3459",
			"663", "0.6603", "664", "0.8607", "665", "0.1828", "666", "0.0083", "667", "0.5010",
			"668", "0.1636", "669", "0.0704", "670", "0.2163", "671", "0.3457", "672", "0.0000",
			"673", "0.2054", "674", "0.0368", "675", "0.2354", "676", "0.2881", "677", "0.9722",
			"678", "0.2488", "679", "0.9583", "680", "0.2831", "681", "0.3473", "682", "0.3074",
			"683", "0.1281", "684", "0.0751", "685", "0.2366", "686", "0.3008", "687", "0.4286",
			"688", "0.1428", "689", "0.0101", "690", "0.0032", "691", "0.3042", "692", "0.4333",
			"693", "0.0957", "694", "0.3752", "695", "0.4927", "696", "0.2872", "697", "0.1322",
			"698", "0.3030", "699", "0.4311", "700", "0.5565" };

	private static String[] dir_rm3_s_rawP10 = new String[] { "601", "0.3000", "602", "0.2000",
			"603", "0.2000", "604", "0.7000", "605", "0.1000", "606", "0.5000", "607", "0.5000",
			"608", "0.0000", "609", "0.7000", "610", "0.0000", "611", "0.7000", "612", "0.7000",
			"613", "0.6000", "614", "0.6000", "615", "0.0000", "616", "0.8000", "617", "0.8000",
			"618", "0.1000", "619", "0.6000", "620", "0.1000", "621", "0.7000", "622", "0.0000",
			"623", "0.6000", "624", "0.4000", "625", "0.1000", "626", "0.0000", "627", "0.0000",
			"628", "0.4000", "629", "0.2000", "630", "0.2000", "631", "0.0000", "632", "1.0000",
			"633", "1.0000", "634", "0.7000", "635", "0.9000", "636", "0.1000", "637", "0.9000",
			"638", "0.2000", "639", "0.3000", "640", "0.8000", "641", "0.6000", "642", "0.3000",
			"643", "0.4000", "644", "0.4000", "645", "0.9000", "646", "0.4000", "647", "0.7000",
			"648", "1.0000", "649", "1.0000", "650", "0.5000", "651", "0.3000", "652", "1.0000",
			"653", "0.4000", "654", "0.9000", "655", "0.0000", "656", "0.6000", "657", "0.7000",
			"658", "0.5000", "659", "0.4000", "660", "1.0000", "661", "0.8000", "662", "0.4000",
			"663", "0.5000", "664", "0.7000", "665", "0.4000", "666", "0.0000", "667", "0.9000",
			"668", "0.4000", "669", "0.0000", "670", "0.4000", "671", "0.0000", "672", "0.0000",
			"673", "0.4000", "674", "0.1000", "675", "0.4000", "676", "0.3000", "677", "0.8000",
			"678", "0.4000", "679", "0.6000", "680", "0.3000", "681", "0.5000", "682", "0.7000",
			"683", "0.3000", "684", "0.1000", "685", "0.3000", "686", "0.5000", "687", "0.6000",
			"688", "0.3000", "689", "0.0000", "690", "0.0000", "691", "0.4000", "692", "0.8000",
			"693", "0.2000", "694", "0.5000", "695", "1.0000", "696", "0.7000", "697", "0.4000",
			"698", "0.4000", "699", "0.6000", "700", "0.7000" };

	private static String[] dir_sd_lce_f_rawAP = new String[] { "601", "0.6428", "602", "0.2631",
			"603", "0.2238", "604", "0.8386", "605", "0.0788", "606", "0.7176", "607", "0.5135",
			"608", "0.1249", "609", "0.4073", "610", "0.0074", "611", "0.3047", "612", "0.6157",
			"613", "0.3072", "614", "0.7170", "615", "0.0525", "616", "0.6714", "617", "0.2653",
			"618", "0.1186", "619", "0.5295", "620", "0.0194", "621", "0.4848", "622", "0.0006",
			"623", "0.3333", "624", "0.3286", "625", "0.0983", "626", "0.1339", "627", "0.0091",
			"628", "0.2939", "629", "0.1322", "630", "0.6838", "631", "0.0655", "632", "0.4862",
			"633", "0.6574", "634", "0.8265", "635", "0.7741", "636", "0.1284", "637", "0.6506",
			"638", "0.0697", "639", "0.3524", "640", "0.4482", "641", "0.5388", "642", "0.1847",
			"643", "0.4902", "644", "0.2403", "645", "0.7149", "646", "0.3516", "647", "0.4850",
			"648", "0.5501", "649", "0.8156", "650", "0.2079", "651", "0.1418", "652", "0.6344",
			"653", "0.5874", "654", "0.8100", "655", "0.0074", "656", "0.5484", "657", "0.5750",
			"658", "0.3233", "659", "0.2362", "660", "0.7362", "661", "0.5058", "662", "0.4927",
			"663", "0.6951", "664", "0.9750", "665", "0.2357", "666", "0.0118", "667", "0.5419",
			"668", "0.2001", "669", "0.0679", "670", "0.3239", "671", "0.3525", "672", "0.0000",
			"673", "0.1413", "674", "0.0403", "675", "0.2165", "676", "0.3073", "677", "0.9599",
			"678", "0.2706", "679", "0.8972", "680", "0.3357", "681", "0.4686", "682", "0.4115",
			"683", "0.1912", "684", "0.1042", "685", "0.2746", "686", "0.3543", "687", "0.4559",
			"688", "0.1560", "689", "0.0105", "690", "0.0037", "691", "0.3287", "692", "0.4468",
			"693", "0.3294", "694", "0.4271", "695", "0.4445", "696", "0.2988", "697", "0.1396",
			"698", "0.4136", "699", "0.4892", "700", "0.6390" };

	private static String[] dir_sd_lce_f_RawP10 = new String[] { "601", "0.3000", "602", "0.2000",
			"603", "0.4000", "604", "0.6000", "605", "0.1000", "606", "0.6000", "607", "0.6000",
			"608", "0.0000", "609", "0.6000", "610", "0.0000", "611", "0.5000", "612", "0.7000",
			"613", "0.7000", "614", "0.7000", "615", "0.0000", "616", "0.9000", "617", "0.6000",
			"618", "0.1000", "619", "0.7000", "620", "0.0000", "621", "0.7000", "622", "0.0000",
			"623", "0.9000", "624", "0.4000", "625", "0.1000", "626", "0.1000", "627", "0.1000",
			"628", "0.5000", "629", "0.1000", "630", "0.3000", "631", "0.0000", "632", "1.0000",
			"633", "1.0000", "634", "0.7000", "635", "0.9000", "636", "0.1000", "637", "0.7000",
			"638", "0.2000", "639", "0.7000", "640", "0.8000", "641", "0.7000", "642", "0.3000",
			"643", "0.4000", "644", "0.3000", "645", "0.9000", "646", "0.4000", "647", "0.7000",
			"648", "1.0000", "649", "1.0000", "650", "0.3000", "651", "0.5000", "652", "1.0000",
			"653", "0.6000", "654", "0.9000", "655", "0.0000", "656", "0.6000", "657", "0.7000",
			"658", "0.5000", "659", "0.4000", "660", "1.0000", "661", "0.7000", "662", "0.5000",
			"663", "0.5000", "664", "0.8000", "665", "0.4000", "666", "0.0000", "667", "0.8000",
			"668", "0.4000", "669", "0.0000", "670", "0.5000", "671", "0.0000", "672", "0.0000",
			"673", "0.4000", "674", "0.0000", "675", "0.4000", "676", "0.4000", "677", "0.8000",
			"678", "0.4000", "679", "0.6000", "680", "0.2000", "681", "0.8000", "682", "0.9000",
			"683", "0.4000", "684", "0.1000", "685", "0.2000", "686", "0.7000", "687", "0.7000",
			"688", "0.2000", "689", "0.0000", "690", "0.0000", "691", "0.4000", "692", "0.8000",
			"693", "0.6000", "694", "0.4000", "695", "1.0000", "696", "0.5000", "697", "0.4000",
			"698", "0.3000", "699", "0.7000", "700", "0.9000" };

	private static String[] dir_sd_lce_s_rawAP = new String[] { "601", "0.6382", "602", "0.2718",
			"603", "0.3811", "604", "0.8434", "605", "0.0992", "606", "0.6536", "607", "0.4812",
			"608", "0.1200", "609", "0.4193", "610", "0.0149", "611", "0.3198", "612", "0.6214",
			"613", "0.1721", "614", "0.7193", "615", "0.0576", "616", "0.7454", "617", "0.4183",
			"618", "0.0873", "619", "0.5591", "620", "0.0238", "621", "0.4543", "622", "0.0016",
			"623", "0.4215", "624", "0.3016", "625", "0.0912", "626", "0.4125", "627", "0.0053",
			"628", "0.2315", "629", "0.0820", "630", "0.5068", "631", "0.0758", "632", "0.2711",
			"633", "0.6109", "634", "0.8203", "635", "0.7744", "636", "0.1797", "637", "0.5122",
			"638", "0.0372", "639", "0.2601", "640", "0.4198", "641", "0.5221", "642", "0.2160",
			"643", "0.4665", "644", "0.2550", "645", "0.7098", "646", "0.3490", "647", "0.4696",
			"648", "0.6371", "649", "0.8061", "650", "0.2049", "651", "0.2388", "652", "0.6376",
			"653", "0.6237", "654", "0.8329", "655", "0.0023", "656", "0.5776", "657", "0.5060",
			"658", "0.2766", "659", "0.1201", "660", "0.7310", "661", "0.4972", "662", "0.6971",
			"663", "0.6895", "664", "0.9260", "665", "0.1945", "666", "0.0101", "667", "0.5451",
			"668", "0.2900", "669", "0.0615", "670", "0.2106", "671", "0.3366", "672", "0.0000",
			"673", "0.1624", "674", "0.0738", "675", "0.2181", "676", "0.3187", "677", "0.9722",
			"678", "0.2288", "679", "0.8556", "680", "0.3184", "681", "0.5766", "682", "0.3693",
			"683", "0.1962", "684", "0.0512", "685", "0.1946", "686", "0.4489", "687", "0.1494",
			"688", "0.1723", "689", "0.0161", "690", "0.0040", "691", "0.3421", "692", "0.4424",
			"693", "0.2862", "694", "0.4210", "695", "0.4430", "696", "0.3523", "697", "0.2253",
			"698", "0.4357", "699", "0.4755", "700", "0.6495" };

	private static String[] dir_sd_lce_s_RawP10 = new String[] { "601", "0.3000", "602", "0.2000",
			"603", "0.4000", "604", "0.6000", "605", "0.1000", "606", "0.5000", "607", "0.5000",
			"608", "0.0000", "609", "0.6000", "610", "0.0000", "611", "0.3000", "612", "0.6000",
			"613", "0.4000", "614", "0.7000", "615", "0.0000", "616", "0.9000", "617", "0.8000",
			"618", "0.1000", "619", "0.7000", "620", "0.0000", "621", "0.7000", "622", "0.0000",
			"623", "0.9000", "624", "0.5000", "625", "0.1000", "626", "0.5000", "627", "0.0000",
			"628", "0.4000", "629", "0.0000", "630", "0.3000", "631", "0.0000", "632", "0.6000",
			"633", "1.0000", "634", "0.7000", "635", "0.9000", "636", "0.2000", "637", "0.7000",
			"638", "0.1000", "639", "0.5000", "640", "0.7000", "641", "0.6000", "642", "0.3000",
			"643", "0.4000", "644", "0.4000", "645", "0.9000", "646", "0.4000", "647", "0.6000",
			"648", "1.0000", "649", "1.0000", "650", "0.4000", "651", "0.6000", "652", "0.9000",
			"653", "0.5000", "654", "0.9000", "655", "0.0000", "656", "0.8000", "657", "0.7000",
			"658", "0.4000", "659", "0.2000", "660", "0.9000", "661", "0.7000", "662", "1.0000",
			"663", "0.6000", "664", "0.7000", "665", "0.3000", "666", "0.0000", "667", "0.9000",
			"668", "0.5000", "669", "0.0000", "670", "0.3000", "671", "0.0000", "672", "0.0000",
			"673", "0.4000", "674", "0.1000", "675", "0.3000", "676", "0.4000", "677", "0.8000",
			"678", "0.4000", "679", "0.6000", "680", "0.2000", "681", "0.8000", "682", "0.7000",
			"683", "0.4000", "684", "0.0000", "685", "0.2000", "686", "0.7000", "687", "0.5000",
			"688", "0.2000", "689", "0.0000", "690", "0.0000", "691", "0.4000", "692", "0.7000",
			"693", "0.4000", "694", "0.4000", "695", "1.0000", "696", "0.6000", "697", "0.6000",
			"698", "0.4000", "699", "0.6000", "700", "0.9000" };

	private static String[] dir_sd_lce_bigram_rawAP = new String[] { "601", "0.6434", "602",
			"0.2998", "603", "0.1383", "604", "0.8403", "605", "0.0680", "606", "0.2846", "607",
			"0.4773", "608", "0.0880", "609", "0.3528", "610", "0.0362", "611", "0.3887", "612",
			"0.5066", "613", "0.1756", "614", "0.5666", "615", "0.0949", "616", "0.5633", "617",
			"0.3288", "618", "0.1006", "619", "0.5814", "620", "0.0319", "621", "0.4642", "622",
			"0.0136", "623", "0.3275", "624", "0.3301", "625", "0.0388", "626", "0.2104", "627",
			"0.0179", "628", "0.2248", "629", "0.1309", "630", "0.5473", "631", "0.1944", "632",
			"0.1982", "633", "0.5256", "634", "0.8360", "635", "0.7351", "636", "0.1638", "637",
			"0.4714", "638", "0.0857", "639", "0.3276", "640", "0.4269", "641", "0.4349", "642",
			"0.1568", "643", "0.5157", "644", "0.4712", "645", "0.6456", "646", "0.3234", "647",
			"0.5563", "648", "0.4926", "649", "0.7410", "650", "0.1110", "651", "0.0426", "652",
			"0.4127", "653", "0.6401", "654", "0.7664", "655", "0.0041", "656", "0.6603", "657",
			"0.4705", "658", "0.3228", "659", "0.1981", "660", "0.5664", "661", "0.4599", "662",
			"0.4392", "663", "0.6610", "664", "0.5351", "665", "0.1895", "666", "0.0132", "667",
			"0.4512", "668", "0.1533", "669", "0.1438", "670", "0.2633", "671", "0.3142", "672",
			"0.0000", "673", "0.2253", "674", "0.0954", "675", "0.2242", "676", "0.2929", "677",
			"0.9037", "678", "0.4133", "679", "0.9484", "680", "0.1697", "681", "0.5865", "682",
			"0.3727", "683", "0.2588", "684", "0.1040", "685", "0.2600", "686", "0.3700", "687",
			"0.2203", "688", "0.0491", "689", "0.0202", "690", "0.0037", "691", "0.2519", "692",
			"0.4096", "693", "0.3380", "694", "0.4366", "695", "0.4047", "696", "0.6324", "697",
			"0.1834", "698", "0.4348", "699", "0.5494", "700", "0.5950" };

	private static String[] dir_sd_lce_bigram_rawP10 = new String[] { "601", "0.3000", "602",
			"0.2000", "603", "0.2000", "604", "0.6000", "605", "0.1000", "606", "0.3000", "607",
			"0.5000", "608", "0.0000", "609", "0.7000", "610", "0.0000", "611", "0.8000", "612",
			"0.5000", "613", "0.3000", "614", "0.6000", "615", "0.0000", "616", "0.7000", "617",
			"0.9000", "618", "0.0000", "619", "0.7000", "620", "0.1000", "621", "0.7000", "622",
			"0.0000", "623", "0.8000", "624", "0.4000", "625", "0.1000", "626", "0.2000", "627",
			"0.1000", "628", "0.3000", "629", "0.3000", "630", "0.2000", "631", "0.0000", "632",
			"0.5000", "633", "0.7000", "634", "0.7000", "635", "0.9000", "636", "0.3000", "637",
			"0.6000", "638", "0.1000", "639", "0.8000", "640", "0.7000", "641", "0.5000", "642",
			"0.3000", "643", "0.4000", "644", "0.4000", "645", "0.8000", "646", "0.4000", "647",
			"0.8000", "648", "1.0000", "649", "1.0000", "650", "0.1000", "651", "0.1000", "652",
			"0.9000", "653", "0.8000", "654", "0.9000", "655", "0.0000", "656", "0.8000", "657",
			"0.7000", "658", "0.6000", "659", "0.2000", "660", "0.8000", "661", "0.8000", "662",
			"0.6000", "663", "0.5000", "664", "0.6000", "665", "0.4000", "666", "0.0000", "667",
			"0.8000", "668", "0.5000", "669", "0.2000", "670", "0.5000", "671", "0.0000", "672",
			"0.0000", "673", "0.4000", "674", "0.1000", "675", "0.4000", "676", "0.2000", "677",
			"0.7000", "678", "0.7000", "679", "0.6000", "680", "0.1000", "681", "0.8000", "682",
			"0.7000", "683", "0.5000", "684", "0.1000", "685", "0.3000", "686", "0.7000", "687",
			"0.6000", "688", "0.0000", "689", "0.0000", "690", "0.0000", "691", "0.4000", "692",
			"0.6000", "693", "0.4000", "694", "0.3000", "695", "1.0000", "696", "0.7000", "697",
			"0.5000", "698", "0.3000", "699", "0.7000", "700", "0.8000" };

	private static Qrels sQrels;
	private static DocnoMapping sMapping;

	@Test
	public void runRegression() throws Exception {

		Map<String, Map<String, Float>> AllModelsAPScores = new HashMap<String, Map<String, Float>>();

		AllModelsAPScores.put("robust04-dir-rm3-f", loadScoresIntoMap(dir_rm3_f_rawAP));
		AllModelsAPScores.put("robust04-dir-rm3-s", loadScoresIntoMap(dir_rm3_s_rawAP));
		AllModelsAPScores.put("robust04-dir-sd-lce-f", loadScoresIntoMap(dir_sd_lce_f_rawAP));
		AllModelsAPScores.put("robust04-dir-sd-lce-s", loadScoresIntoMap(dir_sd_lce_s_rawAP));
		AllModelsAPScores.put("robust04-dir-sd-lce-bigram", loadScoresIntoMap(dir_sd_lce_bigram_rawAP));

		Map<String, Map<String, Float>> AllModelsP10Scores = new HashMap<String, Map<String, Float>>();

		AllModelsP10Scores.put("robust04-dir-rm3-f", loadScoresIntoMap(dir_rm3_f_rawP10));
		AllModelsP10Scores.put("robust04-dir-rm3-s", loadScoresIntoMap(dir_rm3_s_rawP10));
		AllModelsP10Scores.put("robust04-dir-sd-lce-f", loadScoresIntoMap(dir_sd_lce_f_RawP10));
		AllModelsP10Scores.put("robust04-dir-sd-lce-s", loadScoresIntoMap(dir_sd_lce_s_RawP10));
		AllModelsP10Scores.put("robust04-dir-sd-lce-bigram", loadScoresIntoMap(dir_sd_lce_bigram_rawP10));

		sQrels = new Qrels("data/trec/qrels.robust04.noCRFR.txt");

		String[] params = new String[] {
		    "data/trec/run.robust04.basic.lce.xml",
				"data/trec/queries.robust04.xml" };

		FileSystem fs = FileSystem.getLocal(new Configuration());

		BatchQueryRunner qr = new BatchQueryRunner(params, fs);

		long start = System.currentTimeMillis();
		qr.runQueries();
		long end = System.currentTimeMillis();

		sLogger.info("Total query time: " + (end - start) + "ms");

		sMapping = qr.getDocnoMapping();

		for (String model : qr.getModels()) {
			sLogger.info("Verifying results of model \"" + model + "\"");

			Map<String, Accumulator[]> results = qr.getResults(model);

			verifyResults(model, results, AllModelsAPScores.get(model), AllModelsP10Scores.get(model));

			sLogger.info("Done!");
		}

	}

	private static void verifyResults(String model, Map<String, Accumulator[]> results,
			Map<String, Float> apScores, Map<String, Float> p10Scores) {
		float apSum = 0, p10Sum = 0;
		for (String qid : results.keySet()) {
			float ap = (float) RankedListEvaluator.computeAP(results.get(qid), sMapping, sQrels
					.getReldocsForQid(qid));

			float p10 = (float) RankedListEvaluator.computePN(10, results.get(qid), sMapping,
					sQrels.getReldocsForQid(qid));

			apSum += ap;
			p10Sum += p10;

			sLogger.info("verifying qid " + qid + " for model " + model);

			// for this topic, the results don't appear to be deterministic, so
			// manually check the two alternatives
			if (qid.equals("684") && model.equals("robust04-dir-sd-lce-bigram")) {
				assertTrue(Math.abs(ap - 0.1040) < 10e-6 || Math.abs(ap - 0.1038) < 10e-6);
				assertEquals(p10Scores.get(qid), p10, 10e-6);
			} else {
				assertEquals(apScores.get(qid), ap, 10e-6);
				assertEquals(p10Scores.get(qid), p10, 10e-6);
			}
		}

		// one topic didn't contain qrels, so trec_eval only picked up 99 topics
		float MAP = (float) RankedListEvaluator.roundTo4SigFigs(apSum / 99f);
		float P10Avg = (float) RankedListEvaluator.roundTo4SigFigs(p10Sum / 99f);

		if (model.equals("robust04-dir-rm3-f")) {
			assertEquals(0.3558, MAP, 10e-5);
			assertEquals(0.4596, P10Avg, 10e-5);
		} else if (model.equals("robust04-dir-rm3-s")) {
			assertEquals(0.3557, MAP, 10e-5);
			assertEquals(0.4596, P10Avg, 10e-5);
		} else if (model.equals("robust04-dir-sd-lce-f")) {
			assertEquals(0.3789, MAP, 10e-5);
			assertEquals(0.4808, P10Avg, 10e-5);
		} else if (model.equals("robust04-dir-sd-lce-s")) {
			assertEquals(0.3753, MAP, 10e-5);
			assertEquals(0.4657, P10Avg, 10e-5);
		} else if (model.equals("robust04-dir-sd-lce-bigram")) {
			assertEquals(0.3510, MAP, 10e-5);
			assertEquals(0.4535, P10Avg, 10e-5);
		}

	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(Robust04_Basic_LCE.class);
	}
}
