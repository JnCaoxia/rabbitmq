import com.caox.sharding.utils.DateExtendUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nazi on 2018/7/19.
 */
@Slf4j
public class ElasticSearchTest01 {

    private static final String INDEX = "article"; // 索引
    private static final String TYPE = "contact"; // 类型

    public final static String HOST = "127.0.0.1";
    public final static int PORT = 9300;//http请求的端口是9200，客户端是9300
    private TransportClient client = null;

    @SuppressWarnings({ "resource", "unchecked" })
    @Before
    public void getConnect() throws UnknownHostException {
        client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddresses(
                new InetSocketTransportAddress(InetAddress.getByName(HOST),PORT));
        System.out.println("连接信息:" + client.toString());
    }

    @After
    public void closeConnect() {
        if(null != client) {
            System.out.println("执行关闭连接操作...");
            client.close();
        }
    }

    @SuppressWarnings("resource")
     @Test
    public void test1() throws UnknownHostException {
      //创建客户端
      TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddresses(
              new InetSocketTransportAddress(InetAddress.getByName(HOST),PORT));
        System.out.println("Elasticsearch connect info:" + client.toString());
        System.out.println("hello world!");

    //关闭客户端
       client.close();

    }

    @Test
    public void addIndex1() throws IOException {
        IndexResponse response = client.prepareIndex("msg", "tweet", "1").setSource(XContentFactory.jsonBuilder()
                .startObject().field("userName", "张三")
                .field("sendDate", new Date())
                .field("msg", "你好李四")
                .endObject()).get();

        System.out.println("索引名称:" + response.getIndex() + "\n类型:" + response.getType()
                + "\n文档ID:" + response.getId() + "\n当前实例状态:" + response.status());
    }

    @Test
    public void getData1() {
        GetResponse getResponse = client.prepareGet("article", "jdbc", "8").get();
        System.out.println("索引库的数据:" + getResponse.getSourceAsString());
    }

    /**
     * 列表查询
     */
    @Test
    public void queryList() {
        try {
            String key = "8";
            QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(key, "name", "age","id","student_id");

            SearchResponse res = client.prepareSearch().setIndices(INDEX).setTypes(TYPE).setQuery(queryBuilder).get();

            System.out.println("查询到的总记录个数为：" + res.getHits().getTotalHits());
            for (int i = 0; i < res.getHits().getTotalHits(); i++) {
               System.out.println("第" + (i + 1) + "条记录主要内容为：" + res.getHits().getAt(i).getSource().toString());
            }
        } catch (Exception e) {
           log.error("查询列表失败！:{}",e);
        }
    }
    /**
     * 单个精确值查找(termQuery)
     */
    @Test
    public void termQuery() {
        QueryBuilder queryBuilder = QueryBuilders.termQuery("age", 21);
//        queryBuilder = QueryBuilders.termQuery("isDelete", true);
//        queryBuilder = QueryBuilders.termQuery("my_title", "我的标题12323abcd");
        searchFunction(queryBuilder);
    }

    /**
     * 多个值精确查找(termsQuery)
     *
     * 一个查询相匹配的多个value
     */
    @Test
    public void termsQuery() {
        QueryBuilder queryBuilder = QueryBuilders.termsQuery("hehe","");
        searchFunction(queryBuilder);
    }

    /**
     * 查询相匹配的文档在一个范围(rangeQuery) 时间范围
     * 注意 这里的时间范围仅允许是UTC时间格式
     */
    @Test
    public void rangeQuery() {
        QueryBuilder queryBuilder = QueryBuilders
                .rangeQuery("createtime") // 查询code字段
                .from("2018-07-01T00:00:00.000Z")
                .to("2018-07-19T23:59:59.000Z")
                .includeLower(true)     //  包括下界
                .includeUpper(true);   // 不包括上界
        searchFunction(queryBuilder);
    }

    /**
     * 查询相匹配的文档在一个范围(prefixQuery)
     */
    @Test
    public void prefixQuery() {
        QueryBuilder queryBuilder = QueryBuilders.prefixQuery("name", "hehe");
        searchFunction(queryBuilder);
    }

    /**
     * 通配符检索(wildcardQuery)
     *
     * 值使用用通配符，常用于模糊查询
     *
     * 匹配具有匹配通配符表达式（ (not analyzed ）的字段的文档。 支持的通配符：
     *     *，它匹配任何字符序列（包括空字符序列）
     *     ?，它匹配任何单个字符。
     *
     * 请注意，此查询可能很慢，因为它需要遍历多个术语。 为了防止非常慢的通配符查询，通配符不能以任何一个通配符*或？开头。
     */
    @Test
    public void wildcardQuery() {
        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name", "*ehe*");
        queryBuilder = QueryBuilders.wildcardQuery("name", "*ehe");
//        queryBuilder = QueryBuilders.wildcardQuery("name", "?ehe");
        searchFunction(queryBuilder);
    }

    /**
     * 正则表达式检索(regexpQuery) 不需要^、$
     */
    @Test
    public void regexpQuery() {
        QueryBuilder queryBuilder = QueryBuilders.regexpQuery("name", "he(he)?");
        searchFunction(queryBuilder);
    }

    /**
     * 使用模糊查询匹配文档查询(fuzzyQuery)
     */
    @Test
    public void fuzzyQuery() {
        QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("name", "heh");
        searchFunction(queryBuilder);
    }
    /**
     * Ids检索, 返回指定id的全部信息 (idsQuery)
     *
     * 在idsQuery(type)方法中，也可以指定具体的类型
     */
    @Test
    public void idsQuery() {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("8", "9", "10");
        searchFunction(queryBuilder);
    }

    /************************************************************ 全文检索 ************************************************************/
    /**
     * 单个匹配 (matchQuery)
     *
     * 感觉跟termQuery效果一样
     */
    @Test
    public void matchQuery() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "hehe");
        searchFunction(queryBuilder);
    }

    /**
     * 查询匹配所有文件 (matchAllQuery) 查询所有
     */
    @Test
    public void matchAllQuery() {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        searchFunction(queryBuilder);
    }

    /**
     * 匹配多个字段, field可以使用通配符(multiMatchQuery)
     * 注意：搜索的 text 比如 8 要和 后面搜索的字段"id"、"age"、"student_id" 字段的类型对应
     */
    @Test
    public void multiMatchQuery() {
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("8", "id", "age", "student_id");
        queryBuilder = QueryBuilders.multiMatchQuery("hehe", "*name"); //字段使用通配符
        queryBuilder = QueryBuilders.multiMatchQuery("8", "*id"); //字段使用通配符
        searchFunction(queryBuilder);
    }

    /**
     * 字符串检索(queryString)
     *
     * 一个使用查询解析器解析其内容的查询。
     *  query_string查询提供了以简明的简写语法执行多匹配查询 multi_match queries ，布尔查询 bool queries ，提升得分 boosting ，模糊
     *  匹配 fuzzy matching ，通配符 wildcards，正则表达式 regexp 和范围查询 range queries 的方式。
     *
     *  支持参数达10几种
     */
    @Test
    public void queryString() {
//        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("*hehe");   //通配符查询
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("hehe");
        searchFunction(queryBuilder);
    }
    /**
     * 多个条件查询 匹配条件
     * must 相当于and，就是都满足
     * should 相当于or，满足一个或多个
     * must_not 都不满足
     */
    @Test
    public void testQueryBuilder2() {
        // "科技视频"分词的结果是"科技", "视频", "频"

        // 通配符查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        /**
         * and
         */
//        queryBuilder.must(QueryBuilders.matchQuery("name", "hehe"));
//        queryBuilder.must(QueryBuilders.matchQuery("student_id", 8));

        /**
         * or
         */
//        queryBuilder.should(QueryBuilders.matchQuery("name", "hehe"));
//        queryBuilder.should(QueryBuilders.matchQuery("student_id", 8));
//        queryBuilder.minimumShouldMatch(2); // 最少匹配数

        /**
         * not
         */
        queryBuilder.mustNot(QueryBuilders.matchQuery("name", "hehe"));
        queryBuilder.mustNot(QueryBuilders.matchQuery("student_id", 8));
        searchFunction(queryBuilder);
    }


    /**
     * 类型检索(typeQuery)
     *
     * 查询该类型下的所有数据
     */
    @Test
    public void typeQuery() {
        QueryBuilder queryBuilder = QueryBuilders.typeQuery(TYPE);
        searchFunction(queryBuilder);
    }

    /**
     * 查询遍历抽取
     *
     * 查询结果是根据分值排序(从大到小)
     *
     * @param queryBuilder
     */
    private void searchFunction(QueryBuilder queryBuilder) {
        SearchRequestBuilder requestBuilder = client.prepareSearch().setIndices(INDEX).setTypes(TYPE)
                .setScroll(new TimeValue(60000)).setQuery(queryBuilder);
        // 相当于分页参数  setFrom => pageNo; setSize=> pageCount
        SearchResponse response = requestBuilder.setFrom(0).setSize(100).execute().actionGet();
        System.out.println("--------------查询结果：----------------------");
        // 查询出记录的总条数
        System.out.println("总共的条数："+response.getHits().totalHits);
        System.out.println("--------------显示列表：----------------------");
        for (SearchHit hit : response.getHits()) {
            System.out.println("分值：" + hit.getScore()); // 相关度
            Map<String, Object> map = hit.getSource();
            for (String sKey : map.keySet()) {
                System.out.println(sKey + ": " + map.get(sKey));
            }
            System.out.println("--------------");
        }
        System.out.println("-----------------------------------");
    }

    @Test
    public void test2() throws Exception{
        System.out.println(DateExtendUtil.formatDate2UTCDateString("2018-07-19 00:00:00"));
    }

    @Test
    public void test3(){
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchRequestBuilder requestBuilder = client.prepareSearch().setIndices(INDEX).setTypes(TYPE)
                .setScroll(new TimeValue(60000)).setQuery(queryBuilder);
        // 相当于分页参数  setFrom => pageNo; setSize=> pageCount
        SearchResponse response = requestBuilder.setFrom(0).setSize(100).execute().actionGet();
        MinAggregationBuilder aggregation =
                AggregationBuilders
                        .min("agg")
                        .field("age");
        Min agg = response.getAggregations().get("agg");

        double value = agg.getValue();
        System.out.println(value);
    }

    /**
     * Description:指标聚合查询，COUNT(color) ,min ,max,sum等相当于指标
     *
     * @author nazi
     *
     * 数据格式：{ "price" : 10000, "color" : "red", "make" : "honda", "sold" : "2014-10-28" }
     *
     * 1、可能会注意到我们将 size 设置成 0 。我们并不关心搜索结果的具体内容，所以将返回记录数设置为 0 来提高查询速度。 设置 size: 0 与 Elasticsearch 1.x 中使用 count 搜索类型等价。
     *
     * 2、对text 字段上的脚本进行排序，聚合或访问值时，出现Fielddata is disabled on text fields by default. Set fielddata=true on [color] in order to load fielddata in memory by uninverting the inverted index. Note that this can however use significant memory.
     *    Fielddata默认情况下禁用文本字段，因为Fielddata可以消耗大量的堆空间，特别是在加载高基数text字段时。一旦fielddata被加载到堆中，它将在该段的生命周期中保持在那里。此外，加载fielddata是一个昂贵的过程，可以导致用户体验延迟命中。
     *    可以使用使用该my_field.keyword字段进行聚合，排序或脚本或者启用fielddata（不建议使用）
     */
    @Test
    public void metricsAggregation() {
        try {

            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);
//        	MaxAggregationBuilder field = AggregationBuilders.max("max_price").field("price");
            //MinAggregationBuilder 统计最小值
//        	MinAggregationBuilder field = AggregationBuilders.min("min_price").field("price");
            //SumAggregationBuilder 统计合计
//        	SumAggregationBuilder field = AggregationBuilders.sum("sum_price").field("price");
            //StatsAggregationBuilder 统计聚合即一次性获取最小值、最小值、平均值、求和、统计聚合的集合。
            StatsAggregationBuilder field = AggregationBuilders.stats("stats_age").field("age");
            searchRequestBuilder.addAggregation(field);
            searchRequestBuilder.setSize(0);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            System.out.println(searchResponse.toString());
            Aggregations agg = searchResponse.getAggregations();
            if(agg == null) {
                return;
            }

//            Max max = agg.get("max_price");
//            System.out.println(max.getValue());

//            Min min = agg.get("min_price");
//            System.out.println(min.getValue());

//            Sum sum = agg.get("sum_price");
//            System.out.println(sum.getValue());

            Stats stats = agg.get("stats_age");
            System.out.println("最大值："+stats.getMax());
            System.out.println("最小值："+stats.getMin());
            System.out.println("平均值："+stats.getAvg());
            System.out.println("合计："+stats.getSum());
            System.out.println("总条数："+stats.getCount());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:桶聚合查询，GROUP BY相当于桶
     *
     * @author nazi
     *
     */
    @Test
    public void bucketsAggregation() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);

        TermsAggregationBuilder field = AggregationBuilders.terms("popular_ages").field("age");
        searchRequestBuilder.addAggregation(field);
        searchRequestBuilder.setSize(0);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        System.out.println(searchResponse.toString());

        Terms genders = searchResponse.getAggregations().get("popular_ages");
        for (Terms.Bucket entry : genders.getBuckets()) {
            System.out.println("---------------------开始---------------------");
            Object key = entry.getKey();      // Term
            Long count = entry.getDocCount(); // Doc count
            Aggregations agg = entry.getAggregations();

            System.out.println(key);
            System.out.println(count);
        }
    }

    /**
     * Description:桶聚合查询中添加度量指标
     * 例：计算每种student_id对应的平均年龄是多少
     *
     * @author nazi
     */
    @Test
    public void bucketsMetricsAggregation() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);

        TermsAggregationBuilder nameField = AggregationBuilders.terms("popular_studentIds").field("student_id");
        AvgAggregationBuilder avgAgeField = AggregationBuilders.avg("avg_age").field("age");
        StatsAggregationBuilder field = AggregationBuilders.stats("stats_age").field("age");
        nameField.subAggregation(avgAgeField);
        nameField.subAggregation(field);

        searchRequestBuilder.addAggregation(nameField);
        searchRequestBuilder.setSize(0);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        System.out.println(searchResponse.toString());

        Terms genders = searchResponse.getAggregations().get("popular_studentIds");
        for (Terms.Bucket entry : genders.getBuckets()) {
            Object key = entry.getKey();      // Term
            Long count = entry.getDocCount(); // Doc count

            Aggregations agg = entry.getAggregations();
            Avg avg = agg.get("avg_age");
            Double avgAge = avg.getValue();

            Stats stats = agg.get("stats_age");

            System.out.println(key + " studentId有" + count + "个，平均年龄："
                    + avgAge + ",最大值："+stats.getMax() +",最小值："+stats.getMin() + ",合计："+stats.getSum());
        }
    }

    /**
     * Description:桶嵌套
     * 例：计算每个student_id的ID的分布及对应的平均年龄
     */
    @Test
    public void bucketsMetricsAggregation2() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);

        //student_id
        TermsAggregationBuilder colorsField = AggregationBuilders.terms("popular_studentIds").field("student_id");

        //平均年龄
        AvgAggregationBuilder avgAgeField = AggregationBuilders.avg("avg_age").field("age");
        colorsField.subAggregation(avgAgeField);

        //id // 这里不能以string类型字段聚合
        TermsAggregationBuilder makeField = AggregationBuilders.terms("id").field("id");
        colorsField.subAggregation(makeField);

        searchRequestBuilder.addAggregation(colorsField);
        searchRequestBuilder.setSize(0);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        System.out.println(searchResponse.toString());

        Terms genders = searchResponse.getAggregations().get("popular_studentIds");
        for (Terms.Bucket entry : genders.getBuckets()) {
            Object colorName = entry.getKey();      // Term
            Long colorCount = entry.getDocCount(); // Doc count

            Aggregations agg = entry.getAggregations();
            Avg avg = agg.get("avg_age");
            Double avgAge = avg.getValue();

            String info = "其中";
            Terms makes = entry.getAggregations().get("id");
            for (Terms.Bucket makeEntry : makes.getBuckets()) {
                Object makeName = makeEntry.getKey();
                Long makeCount = makeEntry.getDocCount();
                info = info + makeCount + "个ID是" + makeName + "，";
            }

            System.out.println(colorName + " student_id有" + colorCount + "个，平均每个年龄：" + avgAge + "，" + info);
        }
    }

    /**
     * Description:柱状图聚合,用于各种图表数据的聚合
     *
     * @author nazi
     */
    @Test
    public void histogramAggregation() {
        try {
            // 直方图的区间间隔
            Double interval = 5d;
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);

            HistogramAggregationBuilder priceField = AggregationBuilders.histogram("studentId").field("student_id").interval(interval);
            SumAggregationBuilder revenueField = AggregationBuilders.sum("revenue").field("age");

            //平均年龄
            AvgAggregationBuilder avgAgeField = AggregationBuilders.avg("avg_age").field("age");
            priceField.subAggregation(revenueField);
            priceField.subAggregation(avgAgeField);
            /******************** min_doc_count : 它强制返回所有 buckets，即使 buckets 可能为空******************/
            priceField.minDocCount(0);
            priceField.extendedBounds(0d,200d);
            /********************* extended_bounds: 由于Elasticsearch 默认只返回你的数据中最小值和最大值之间的 buckets  extended_bounds 强制返回上下边界的数据 即使数据是空 *****************/
            searchRequestBuilder.addAggregation(priceField);
            searchRequestBuilder.setSize(0);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            System.out.println(searchResponse.toString());

            Histogram histogram = searchResponse.getAggregations().get("studentId");
            for (Histogram.Bucket entry : histogram.getBuckets()) {
                Object key = entry.getKey();      // Term
                Long count = entry.getDocCount(); // Doc count

                Aggregations agg = entry.getAggregations();
                Sum sum = agg.get("revenue");
                Double revenue = sum.getValue();

                Avg avg = agg.get("avg_age");
                Double avgAge = avg.getValue();

                System.out.println(key + "~" + (interval+(Double)key) + "区间的studentId有" + count + "人，" +
                        "此区间总年龄：" + revenue +",此区间平均年龄："+avgAge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:按时间统计聚合,用于各种图表数据的聚合
     * 按时间统计：https://www.elastic.co/guide/cn/elasticsearch/guide/current/_looking_at_time.html
     * 例： 每月销售多少台汽车
     *
     * @author nazi
     *
     * 返回空buckets处理：https://www.elastic.co/guide/cn/elasticsearch/guide/current/_returning_empty_buckets.html
     *
     * extended_bounds 参数需要一点解释。 min_doc_count 参数强制返回空 buckets，但是 Elasticsearch 默认只返回你的数据中最小值和最大值之间的 buckets。
    因此如果你的数据只落在了 4 月和 7 月之间，那么你只能得到这些月份的 buckets（可能为空也可能不为空）。
    因此为了得到全年数据，我们需要告诉 Elasticsearch 我们想要全部 buckets， 即便那些 buckets 可能落在最小日期 之前 或 最大日期 之后 。
     */
    @Test
    public void dataHistogramAggregation() {
        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 更改搜索出结果数据中的的显示 response
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            for (SearchHit hit : response.getHits()) {
                Map<String, Object> map = hit.getSource();
                for (String sKey : map.keySet()) {
                    if(sKey.equals("createtime")){
                        Date date = sdf1.parse(map.get(sKey) +"");//拿到Date对象
                        String str = sdf2.format(date);
                        map.put(sKey,str);
                    }
                    System.out.println(sKey + ": " + map.get(sKey));
                }
            }
            System.out.println("-----------------------------------");

            DateHistogramAggregationBuilder field = AggregationBuilders.dateHistogram("dateAggregation").field("createtime");
            field.dateHistogramInterval(DateHistogramInterval.MONTH);
//		   field.dateHistogramInterval(DateHistogramInterval.days(10));
//            field.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai")));//指定时区
            field.format("yyyy-MM");

            field.minDocCount(0);//强制返回空 buckets,既空的月份也返回
            field.extendedBounds(new ExtendedBounds("2018-06", "2018-10"));// Elasticsearch 默认只返回你的数据中最小值和最大值之间的 buckets

            searchRequestBuilder.addAggregation(field);
            searchRequestBuilder.setSize(0);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            System.out.println(searchResponse.toString());

            Histogram histogram = searchResponse.getAggregations().get("dateAggregation");
            for (Histogram.Bucket entry : histogram.getBuckets()) {
			   DateTime key = (DateTime) entry.getKey();
                String keyAsString = entry.getKeyAsString();
                String dateFormat = key.toString("yyyy-MM");
                Long count = entry.getDocCount(); // Doc count
                System.out.println(dateFormat + "，存在" + count + "条数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:带有搜索条件的聚合查询
     * 例：student_id(8)所对应的在年龄有多少种
     * @author nazi
     */
    @Test
    public void searchBucketsAggregation() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);

        //搜索条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("student_id", "8"));

        //聚合
        TermsAggregationBuilder field = AggregationBuilders.terms("ages").field("age");
        searchRequestBuilder.addAggregation(field);
//	    searchRequestBuilder.setSize(0);
        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        System.out.println(searchResponse.toString());

        Terms genders = searchResponse.getAggregations().get("ages");
        for (Terms.Bucket entry : genders.getBuckets()) {
            Object key = entry.getKey();      // Term
            Long count = entry.getDocCount(); // Doc count

            System.out.println("student_id ：8" +" =>年龄：" + key + "，共"+count+"个");
        }
    }

    /**
     * Description:全局桶
     * 例：想知道student_id(8)学生与 所有 学生平均年龄的比较
     * @author nazi
     */
    @Test
    public void globalBucketsAggregation() {
        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);

            //搜索条件
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.matchQuery("student_id", "8"));
 
            //平均价格  平均年龄
            AvgAggregationBuilder singleAvgPrice = AggregationBuilders.avg("single_avg_age").field("age");

            //聚合 忽略搜索条件进行的全局年龄平均值
            GlobalAggregationBuilder field = AggregationBuilders.global("all");
            AvgAggregationBuilder avgPrice = AggregationBuilders.avg("avg_age").field("age");
            field.subAggregation(avgPrice);

            searchRequestBuilder.addAggregation(singleAvgPrice);
            searchRequestBuilder.addAggregation(field);
            searchRequestBuilder.setSize(0);
            searchRequestBuilder.setQuery(queryBuilder);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            System.out.println(searchResponse.toString());

            Avg avg = searchResponse.getAggregations().get("single_avg_age");
            System.out.println("student_id为8的平均年龄：" + avg.getValue());

            Global global = searchResponse.getAggregations().get("all");
            Avg allAvg = global.getAggregations().get("avg_age");
            System.out.println("所有学生平均年龄：" + allAvg.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 假设有一个商品索引，搜索时希望在相关度排序的基础上，销量（sales）更高的商品能排在靠前的位置，那么这条查询 DSL 可以是这样的
     * field_value_factor：将某个字段的值进行计算得出分数。
     * factor：对字段值进行预处理，乘以指定的数值（默认为 1）
     * modifier : 将字段值进行加工，有以下的几个选项：其中 : log1p：先将字段值 +1，再计算对数
     * boost_mode可以指定计算后的分数与原始的_score如何合并,其中有：sum：将结果加上_score
     * @throws Exception
     *
     * 根据一个字段进行相关性分析
     */
    @Test
    public void getFunctionScoreQuery() throws Exception {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);
        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        //分页
        searchRequestBuilder.setFrom(0).setSize(10);
        //explain为true表示根据数据相关度排序，和关键字匹配最高的排在前面
        searchRequestBuilder.setExplain(true);
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must(QueryBuilders.matchQuery("student_id", "8"));

        ScoreFunctionBuilder<?> scoreFunctionBuilder = ScoreFunctionBuilders.fieldValueFactorFunction("createtime").modifier(FieldValueFactorFunction.Modifier.LN1P).factor(0.1f);
        FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(queryBuilder,scoreFunctionBuilder).boostMode(CombineFunction.SUM);

        searchRequestBuilder.setQuery(query);

        SearchResponse searchResponse = searchRequestBuilder.execute().get();
        System.out.println(searchResponse.toString());

        long totalCount = searchResponse.getHits().getTotalHits();
        System.out.println("总条数 totalCount:" + totalCount);

        //遍历结果数据
        SearchHit[] hitList = searchResponse.getHits().getHits();
        for (SearchHit hit : hitList) {
            System.out.println("SearchHit hit string:" + hit.getSourceAsString());
        }
    }

    /**
     * 衰减函数 （例子有点牵强，因为不想再造数据集了）   相关性分析：数字、位置和时间类型
     * 假设有一个学生年龄索引，年龄（age）更高的学生能排在靠前的位置，期望值是：23  容错：+-2
     */
    @Test
    public void getDecayFunctionDemo() throws Exception{
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);
        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        //分页
        searchRequestBuilder.setFrom(0).setSize(50);
        //explain为每个匹配到的文档产生一大堆额外内容,设为 true就可以得到更详细的信息；
        //输出 explain 结果代价是十分昂贵的，它只能用作调试工具 。千万不要用于生产环境
        searchRequestBuilder.setExplain(false);

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must(QueryBuilders.matchQuery("student_id", "8"));

        //原点（origin）：该字段最理想的值，这个值可以得到满分（1.0）
        double origin = 23;
        //偏移量（offset）：与原点相差在偏移量之内的值也可以得到满分
        double offset = 1;
        //衰减规模（scale）：当值超出了原点到偏移量这段范围，它所得的分数就开始进行衰减了，衰减规模决定了这个分数衰减速度的快慢
        double scale = 40;
        //衰减值（decay）：该字段可以被接受的值（默认为 0.5），相当于一个分界点，具体的效果与衰减的模式有关
        double decay = 0.5;

        //高斯函数
        //GaussDecayFunctionBuilder functionBuilder = ScoreFunctionBuilders.gaussDecayFunction("productID", origin, scale, offset, decay);
        //以 e 为底的指数函数
        ExponentialDecayFunctionBuilder functionBuilder = ScoreFunctionBuilders.exponentialDecayFunction("age", origin, scale, offset, decay);
        //线性函数
        //LinearDecayFunctionBuilder functionBuilder = ScoreFunctionBuilders.linearDecayFunction("productID", origin, scale, offset, decay);
        FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(queryBuilder,functionBuilder).boostMode(CombineFunction.SUM);

        searchRequestBuilder.setQuery(query);

        SearchResponse searchResponse = searchRequestBuilder.execute().get();
        System.out.println(searchResponse.toString());

        long totalCount = searchResponse.getHits().getTotalHits();
        System.out.println("总条数 totalCount:" + totalCount);

        //遍历结果数据
        SearchHit[] hitList = searchResponse.getHits().getHits();
        for (SearchHit hit : hitList) {
            System.out.println("SearchHit hit string:" + hit.getSourceAsString());
        }

    }

    /**
     * 脚本匹配 script\_score
     * 这里用name进行聚合分析会出现如下错误：
     * Fielddata is disabled on text fields by default.
     * Set fielddata=true on [name] in order to load fielddata in memory by uninverting the inverted index.
     * Note that this can however use significant memory. Alternatively use a keyword field instead.
     *
     * 原因：出现该错误是因为5.x之后，
     * Elasticsearch对排序、聚合所依据的字段用单独的数据结构(fielddata)缓存到内存里了，
     * 但是在text字段上默认是禁用的，如果有需要单独开启，这样做的目的是为了节省内存空间。
     * @throws Exception
     */
    @Test
    public void getScriptScoreQuery() throws Exception {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);
        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        //分页
        searchRequestBuilder.setFrom(0).setSize(50);
        //explain为true表示根据数据相关度排序，和关键字匹配最高的排在前面
        searchRequestBuilder.setExplain(false);
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must(QueryBuilders.matchQuery("student_id", "8"));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recommend_category","hehe");
        /**
         * param1:文件类型
         * param2:文件中的语言
         * param3:文件名字 category-score.groovy 前缀
         * param4: params参数
         */
        Script script = new Script(ScriptType.FILE, "groovy","category-score", params);
        ScriptScoreFunctionBuilder scriptScoreFunctionBuilder =  ScoreFunctionBuilders.scriptFunction(script);
        FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(queryBuilder,scriptScoreFunctionBuilder).boostMode(CombineFunction.SUM);
        searchRequestBuilder.setQuery(query);

        SearchResponse searchResponse = searchRequestBuilder.execute().get();
        System.out.println(searchResponse.toString());

        long totalCount = searchResponse.getHits().getTotalHits();
        System.out.println("总条数 totalCount:" + totalCount);

        //遍历结果数据
        SearchHit[] hitList = searchResponse.getHits().getHits();
        for (SearchHit hit : hitList) {
            System.out.println("SearchHit hit string:" + hit.getSourceAsString());
        }
    }
}

