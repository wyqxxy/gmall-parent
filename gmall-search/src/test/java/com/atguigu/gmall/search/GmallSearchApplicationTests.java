package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Account;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.Aggregation;
import io.searchbox.core.search.aggregation.AvgAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchApplicationTests {

	@Autowired
	JestClient jestClient;

	@Test
	public void contextLoads() {
		System.out.println(jestClient);
	}

	/**
	 * 增，删，改
	 */
	@Test
	public void index() throws IOException {
		Account account = new Account(99000L, 21000L, "lei", "fengyang", 32, "F", "mill road", "tong teacher", "lfy@atguigu.com", "BJ", "CP");
		Index index = new Index.Builder(account).index("bank")
				.type("account")
				.id(account.getAccount_number() + "")
				.build();
		DocumentResult execute = jestClient.execute(index);
		System.out.println(execute.toString());
	}
	@Test
	public void testDelete() throws IOException {
		Delete delete = new Delete.Builder("99000").index("bank")
				.type("account").build();
		DocumentResult execute = jestClient.execute(delete);
		System.out.println(execute.getJsonString());

	}
	/**
	 *  GET bank/account/_search
	 */
	@Test
	public void testSearchAll() throws IOException {
		Search search = new Search.Builder("").addIndex("bank").addType("account")
				.build();
		SearchResult execute = jestClient.execute(search);
		System.out.println(execute.getTotal());
	}

	/**
	 * GET bank/account/_search
	 * {
	 *   "query": {
	 *     "match_all": {}
	 *   }
	 * }
	 */
	@Test
	public void searchBySDL() throws IOException {
		MatchAllQueryBuilder query = new MatchAllQueryBuilder();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(query);
		System.out.println(sourceBuilder.toString());

		Search search = new Search.Builder(sourceBuilder.toString()).addIndex("bank").addType("account").build();
		SearchResult execute = jestClient.execute(search);
		System.out.println(execute.getTotal());

	}

	/**
	 *    /**
	 * GET bank/account/_search
	 * {
	 *   "query": {
	 *     "bool": {
	 *       "must": [
	 *         {"match": {"address": "mill"}},
	 *         {"match": {"gender": "M"}}
	 *       ],
	 *       "must_not": [
	 *         {"match": { "age": "28" }}
	 *       ],
	 *       "should": [
	 *         {"match": {
	 *           "firstname": "Parker"
	 *         }}
	 *       ]
	 *     }
	 *   }
	 * }
	 */
	@Test
	public void searchFh() throws IOException {
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		builder.must(QueryBuilders.matchQuery("address", "mill"))
		.must(QueryBuilders.matchQuery("gender","M"));
		builder.mustNot(QueryBuilders.matchQuery("age",28));
		builder.should(QueryBuilders.matchQuery("firstname","Parker"));
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(builder);
		System.out.println(sourceBuilder.toString());

		Search search = new Search.Builder(sourceBuilder.toString()).addIndex("bank").addType("account").build();
		SearchResult execute = jestClient.execute(search);
		System.out.println(execute.getTotal()+"=========="+execute.getAggregations());

	}

	/**
	 * GET bank/account/_search
	 * {
	 *   "query": {
	 *     "terms": {
	 *       "gender.keyword": [
	 *         "M",
	 *         "F"
	 *       ]
	 *     }
	 *   },
	 *   "aggs": {
	 *     "age_agg": {
	 *       "terms": {
	 *         "field": "age",
	 *         "size": 100
	 *       },
	 *       "aggs": {
	 *         "gender_agg": {
	 *           "terms": {
	 *             "field": "gender.keyword",
	 *             "size": 100
	 *           },
	 *           "aggs": {
	 *             "balance_avg": {
	 *               "avg": {
	 *                 "field": "balance"
	 *               }
	 *             }
	 *           }
	 *         },
	 *         "balance_avg":{
	 *           "avg": {
	 *             "field": "balance"
	 *           }
	 *         }
	 *       }
	 *     }
	 *   }
	 *   ,
	 *   "size": 1000
	 * }
	 */

	@Test
	public void testAggs() throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(queryTerms());
		searchSourceBuilder.aggregation(queryAggs());


		String dsl = searchSourceBuilder.toString();
		System.out.println(dsl);
		Search search = new Search.Builder(dsl)
				.addIndex("bank")
				.addType("account")
				.build();
		SearchResult execute = jestClient.execute(search);
		System.out.println(execute.getTotal()+"==>"+execute.getErrorMessage());

		//从结果中获取值
		printResult(execute);

	}

	private AggregationBuilder queryAggs() {
		TermsAggregationBuilder builder = AggregationBuilders.terms("age_agg");
		builder.size(100).field("age");
		builder.subAggregation(genderAggregation());
		builder.subAggregation(blanceAvgAgg());
		return  builder;
	}

	/**
	 * 打印结果
	 * @param execute
	 */
	private void printResult( SearchResult execute){

		//获取返回结果中的aggregations
		MetricAggregation aggregations = execute.getAggregations();

		//获取命中的记录
		SearchResult.Hit<Account, Void> hit = execute.getFirstHit(Account.class);
		//返回的真正查询到的数据
		Account source = hit.source;
		System.out.println(source);


		//聚合结果
		TermsAggregation age_agg = aggregations.getAggregation("age_agg", TermsAggregation.class);

		List<TermsAggregation.Entry> buckets = age_agg.getBuckets();
		buckets.forEach((b)->{
			System.out.println("年龄："+b.getKey()+"；总共有："+b.getCount());
			AvgAggregation balance_avg = b.getAvgAggregation("balance_avg");
			System.out.println("平均薪资"+balance_avg.getAvg());
			TermsAggregation gender_agg = b.getAggregation("gender_agg", TermsAggregation.class);
			gender_agg.getBuckets().forEach((b2)->{
				System.out.println("性别："+b2.getKey()+"；有："+b2.getCount()+"人；平均薪资："+b2.getAvgAggregation("balance_avg").getAvg());

			});
		});

		System.out.println(age_agg);

	}


	private AggregationBuilder blanceAvgAgg() {
		AvgAggregationBuilder balance_avg =
				AggregationBuilders.avg("balance_avg").field("balance");
		return balance_avg;
	}

	private AggregationBuilder genderAggregation() {
		TermsAggregationBuilder builder = AggregationBuilders.terms("gender_agg");
		builder.size(100).field("gender.keyword");
		builder.subAggregation(AggregationBuilders.avg("balance_avg").field("balance"));
		return  builder;
	}

	private QueryBuilder queryTerms() {
		TermsQueryBuilder termQueryBuilder = QueryBuilders.termsQuery("gender.keyword","F","M");
		return  termQueryBuilder;
	}




}
