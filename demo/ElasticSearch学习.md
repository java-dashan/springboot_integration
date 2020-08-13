# 						ElasticSearch学习

### 1.索引(index)

#### 建立索引

```json
##(包括自定义分词器)
PUT /my_index
{
    "settings": {
        "analysis": {
            "char_filter": {
                "&_to_and": {
                    "type":       "mapping",
                    "mappings": [ "&=> and "]
            }},
            "filter": {
                "my_stopwords": {
                    "type":       "stop",
                    "stopwords": [ "the", "a" ]
            }},
            "analyzer": {
                "my_analyzer": {
                    "type":         "custom",
                    "char_filter":  [ "html_strip", "&_to_and" ],
                    "tokenizer":    "standard",
                    "filter":       [ "lowercase", "my_stopwords" ]
            }}
		}
    },
    "mappings": {
        "my_type": {
            "dynamic_templates": [
                //自定义动态mapping,如果field匹配如下格式
                { "es": {
                      "match":              "*_es", 
                      "match_mapping_type": "string",
                      "mapping": {
                          "type":           "string",
                          "analyzer":       "spanish"
                      }
                }},
                { "en": {
                      "match":              "*", 
                      "match_mapping_type": "string",
                      "mapping": {
                          "type":           "string",
                          "analyzer":       "english"
                      }
                }}
            ]
	}}
}
```

#### 删除索引

```json
DELETE /my_index
DELETE /my_*
DELETE /my_index1,my_index2
DELETE /all   ##删除所有或者(/*)
```



#### 更改索引

```json
PUT /my_index/_settings
{
     "number_of_replicas": 2,//修改副本个数
     "analysis": {
            "char_filter": {
                "&_to_and": {
                    "type":       "mapping",
                    "mappings": [ "&=> and "]
            	}
            },
            "filter": {
                "my_stopwords": {
                    "type":       "stop",
                    "stopwords": [ "the", "a" ]
            	}
            },
            "analyzer": {
                "my_analyzer": {
                    "type":         "custom",
                    "char_filter":  [ "html_strip", "&_to_and" ],
                    "tokenizer":    "standard",
                    "filter":       [ "lowercase", "my_stopwords" ]
            	}
            }
	}
}
```

#### 查看索引

```json
GET /index
```



### 2.类型(type)(7.0已移除)

### 3.文档（document）

#### 增加文档

```json
#指定id
PUT /index/type/1
{
	"name":'da',
	"age":18
}
#id由es生成
POST /index/type
{
	"name":'da',
	"age":18
}
```

#### 修改文档

```json
#部分修改
POST /index/type/1/_update
{
    "doc":{
        "name":"dashan"
    }
}
#全量替换或者   (要把参数全带上),如果末尾再加/_create即强制创建,如果已存在会报错,用来区分修改和新增
PUT /index/type/1
{
	"name":'dashan',
	"age":18
}
```

#### ==查询文档==

```json
#根据id查询
GET /index/type/1
#  query string search
GET /index/type/_search
GET /index/type/_search?q=name:da&sort=age:desc
# query DSL(domain specified language 特定领域语言)
GET /index/test_index/_search
{
  "version":true,   //设置为true 显示版本
  "query":{
    "match": {
      "name": "dashan"
    }
  },
  "sort":[{
    "age":"desc"
  }],
  "from":0,
  "size":1,
  "_source":["name","age"]
}

//查询多个字段包含关键字
GET /index/type/_search
{
    "query":{
        "multi_match":{
            "query":"this is a ggg",
            "fields":["field1^2","field2"],//^2表示boost权重
            "type":"best_fields",
            "tie_breaker":0.3,
            "minimum_should_match":50%
        }
    }
}
//精确查询(查询不分词的字段)
GET /index/type/_search
{
    "query":{
        "term":{
            "name":"dashan"
        }
    }
}
//某个字段包含多个关键字
GET /index/type/_search
{
    "query":{
        "terms":{
            "tags":["a","b"],
            “miniumu_match”:2   //最少匹配个数
        }
    }
}
//短语查询短语查询，slop定义的关键词之间隔多个未知的单词,查询elasticsearch,distributed 之间间隔2个单词的文档
GET /index/type/_search
{
    "query":{
        "match_phrase":{
            "preview":{
                "query":"Elasticsearch,distributed",
                "slop":2
            }
        }
    }
}
//wildcard 通配符查询
GET /library/books/_search
{
	“query”:{
		"wildcard":{
			"preview":"luc?ne*"
		}
    }
}
//组合查询
GET /index/type/_search
{
    "query":{
        "bool":{
            "should":{},
            "must":{},
            "must_not":{},
            "filter":{}
        }
    }
}
//filter也可以写在bool外面
GET /index/type/_search
{
    "query":{
        "constant_score":{
            "filter":{
                "range":{
                    "field_name":{
                        "gt":30
                    }
                }
            }
        }
    }
}

//定制分数 script_score

GET /article2/_search
{
  "query": {
    "script_score": {
      "query": {
        "match_all": {   //"match": {"name": "Final Fantasy"}
        }
      },
      "script": {
        "source": " (doc['comments'].value*10)"
      }
    }
  }
}
//要使用function_score，用户必须定义一个查询和一个或多个函数，这些函数为查询返回的每个文档计算一个新分数。
GET best_games/_search
{
  "_source": [
    "name",
    "critic_score",
    "user_score"
  ],
  "query": {
    "function_score": {
      "query": {
        "match": {
          "name": "Final Fantasy"
        }
      },
      "script_score": {
        "script": "_score * (doc['user_score'].value*10+doc['critic_score'].value)/2/100"
      }
    }
  }
}


```

#### 删除文档

```json
DELETE /index/type/1
```

### 4._version, ___source, ___cat?v,  _indeies, routing,consistency

```json
//乐观锁
//1._version用来控制并发(这属于乐观锁),如果type设置成external表示version要大于es中version
PUT /index/type/id?version=1&version_type=external
{
	"field":"content"
}
//2.if_seq_no和if_primary_term一起(if_seq_no会变，创建文档时该文档的这个值随当前文档最大+1,if_primary_term不会变)
PUT /test1/test1/3?if_seq_no=2&if_primary_term=1
//3.部分修改原理就是以上乐观锁,在并发情况下,我们可以使用retry_on_conflict重试操作
POST /test1/test1/3?retry_on_conflict=5

#_source
//不返回文档,只返回命中统计
GET /_search
{
    "_source": false,
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}

//返回部分字段

GET /_search
{
    "_source": "obj.*",
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
GET /_search
{
    "_source": [ "obj1.*", "obj2.*" ],
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}

//更精确的方法
GET /_search
{
    "_source": {
        "includes": [ "obj1.*", "obj2.*" ],
        "excludes": [ "*.description" ]
    },
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}



GET /_cluster/health?v      //查看集群健康
GET /_cat/health?v   		//查看集群状态		
GET /_cat/indices?v         //查看所有索引状态

#document routing默认值时文档_id(文档分配到哪个分片依赖)(也可以在这看出为什么主分片不能变)
//公式shard = hash(routing) % num_of_primary_shards
//这样做可以隔离某一类document,用于高并发场景,应用级别的负载均衡,提高批量读取数据的性能等等
PUT /index/type/id?routing=...

#consistency(写一致性)
//参数：one(要求只要有一个主分片是活跃的),all(要求所有分片都是活跃)
//默认值quorum(要求大部分分片活跃w),(p+num_of_r)/2+1,当r>1时才生效,quorum不齐全时(当单双节点不满足公式时会特殊处理),ES会等待默认一分钟,我们可以设置timeout
PUT /index/type/id?consistency=one
PUT /index/type/id?timeout=30
```



### 5.聚合burket

```json
//如果要使用聚合
//方法1.修改mapping,将字段的fileddate设置为true
PUT /index/_mapping/type
{
    "properties":{
        "想要聚合的那个field":{
            "type":"text",
            "fielddate":"true"
        }
    }
}
//方法2.使用.keyword

#例如：根据性别聚合分析
GET /index/type/_search
{
    "aggs":{
        "自定义名称":{
            "terms":{
                "field":"想要聚合的那个field" 
            }
        }
    }
}
GET movies/_search
{
  "aggs": {
    "group_by_director": {
      "terms": {
        "field": "director.keyword",
        "size": 10
      }
    }
  }
}
#例如：根据性别聚合分析平均年龄(两步走,1.聚合性别，2.再内部聚合分析年龄)
GET /index/type_search
{
    "aggs":{
        "group_by_sex":{
            "terms":{
                "field":"sex"
            },
            "aggs":{
                "avg_age":{
                    "avg":{
                        "field":"age"
                    }
                }
            }
        }
    }
}
#根据性别聚合，平均年龄降序排序(添加order)
GET /index/type/_search
{
    "aggs":{
        "group_by_sex":{
            "terms":{
                "field":"sex",
                "order":{
                    "avg_age":"desc"
                }
            },
            "aggs":{
                "avg_age":{
                    "avg":{
                        "field":"age"
                    }
                }
            }
        }
    }
}
#按照指定区间范围进行分组，再按照tag进行分组再按照每组平均价格进行排序
#数据格式
{
    “tag”:[],
    “price":100
}
GET /index/type/_search
{
    "size":0,
    "aggs":{
        "group_by_range":{
            "range":{
                "field":"price",
                "range":[
                {
                    "from":0,
                    "to":20
                },
                {
                    "from":20,
                    "to":40
                },
                {
                    "from":40,
                    "to":60
                }
                ]
            },
            "aggs":{
                "group_by_tags":{
                    "terms":{
                        "field":"tags",
                        "order":{
                            "avg_price":"desc"
                        }
                    },
                    "aggs":{
                        "avg_price":{
                            "avg":{
                                "field":"price"
                            }
                        }
                    }
                }
            }
        }
    }
}
#多重下钻
#根据颜色分组,得到每种颜色商品平均值和每种品牌的平均值
GET /index/type/_search
{
	"size":0,
    "aggs":{
        "group_by_color":{
            "terms":{
                "field":"color"
            },
            "aggs":{
                "color_avg_price":{
                    "avg":{
						"field":"price"
                    }
                },
                "group_by_brand":{
                    "terms":{
                        "field":"brand"
                    },
                    "aggs":{
						"brand_avg_price":{
                            "avg":{
                                "field":"price"
                            }
                        }
                    }
                }
            }
        }
    }
}

#优化(从深度优先到广度优先)原理图如下
GET /index/type/_search
{
    "aggs":{
        "actors":{
            "terms":{
				"field":"actors",
                "size":10,   //聚合数量
                "collect_mode":"breadth_first" //广度优先(默认深度优先)
            },
            "aggs":{
                "costars":{
                    "terms":{
                        "field":"films",
                        "size":5
                    }
                }
            }
        }
    }
}
```

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\bucket优化-深度到广度优先.png)

### 6.脚本

```json
#数据格式
{
    "num":0,
    "tags":[]
}
#1.内置脚本(即，当场写脚本语句)
POST /index/type/id/_update
{
    "script":"ctx._source.num+=1"
}
#2.外置脚本(放在es的config/scripts下面)
#脚本名称：test.groovy
#脚本内容: ctx._source.tags+=new_tag
POST /index/type/id/_update
{
    "script":{
        "lang":"groovy",
        "file":"test",
        "params":{
			"new_tag":"tag1"
        }
    }
}
#使用脚本删除文档
#脚本名称：delete.groovy
#脚本内容: ctx.op = ctx._source.num==count?'delete'?'none'
POST /index/type/id/_update
{
    "script":{
        "lang":"groovy",
        "file":"delete",
        "params":{
            "count":1
        }
    }
}
#upsert操作
#如果文档不存在或者不存在这个值使用POST /index/type/id/_update{}会报错,这个时候可以使用脚本
POST /index/type/id/_update
{
    "script":"ctx._source.num+=1",
    "upsert":{
        "num":0,
        "tags":[]
    }
}

```

### 7.批量

```json
#批量查询mget
//1.不同index
GET /_mget
{
    "docs":[
        {
            "_index":"test1",
            "_type":"test1",
            "_id":1
        },
        {
            "_index":"test1",
            "_type":"test1",
            "_id":2
        }
    ]
}
//2.相同同index
GET /test1/_mget{
     "docs":[
        {
            "_type":"test1",  //好像可以不写
            "_id":1
        },
        {
            "_type":"test1",
            "_id":1
        }
      ]  
}
//3.相同index,type(好像可以不写type)
GET /test1/test1/_mget
{
    "ids":[1,2]
}

#批量增删改_bulk(严格要求json串,不同操作才能换行,除了delete其它三个都是两行)
//1.delete删除
//2.index插入或全量替换
//3.create强制创建
//4.update部分替换
//5.如果index或者type相同,和批量查询一样
//6.最佳大小在6-15M之间(1000-5000条数据之间)
POST /_bulk
{"delete":{"_index":"test1","_id":1}}
{"create":{"_index":"test1","_id":9}}
{"name":"i","age":16,"likes":["pingpangqiu"]}
{"index":{"_index":"test1","_id":10}}
{"name":"j","age":17,"likes":["bangqiu"]}
{"update":{"_index":"test1","_id":2}}
{"doc":{"name":"test b"}}
```

### 8.deep paging原理

```json
//假设查询1000-1009条数据
//1.因为es时分布式,数据会根据一定规则路由到不同节点上的shard,所以当请求查询时,会首先由协调节点接收,再转发请求到相应节点,每个节点返回1009条数据(为保证数据可靠性)给协调节点,再由协调节点进行排序,拿到其中1009条返回
//2.如果每个节点只返回其1000-1009是不对的:
P0节点:3,6,9
P1节点:4,10,13,16
P2节点:11,14,17
//问：返回第二条数据
//按照思路一返回就是4,而思路二返回6错误
#缺点，保存大量数据,再排序,耗费大量带宽,cpu,内存,尽量避免出现
```

### 9.mapping

```json
#决定分词和搜索行为(如果未指定mapping,ES会自动创建)
//查询当前type对应的mapping
GET /index/_mapping/type

//分词器
character filter: 预处理,如(&-->and,过滤html标签)
tokenizer:分词
token filter:时态转换,大小写转换等normalization操作
ES内置分词器:
默认standard analyzer,simple analyzer,whitespace analyzer,language analyzer
//查看搜索被如何分词
GET /index/_analyze
{
    "field":"title",	//你要根据哪个field分词
    "text":"a dog"		//分词的内容
}

//新建mapping(建立了不能修改)
PUT /index
{
    "mappings":{
        "type_name":{
            "dynamic":"strict", //碰到陌生字段报错,true遇到陌生字段自动mapping，false，遇到陌生字段忽略
            "properties":{
                "field_name":{
                    "type":"long"
                },
                "field_name":{
                    "type":"text",
                    "analyzer":"english"  //可以指定自定义分词器
                },
                "field_name":{
					"type":"text",
                    "index":"not_analyzed" //如果设置成no,意思是不分词也不检索
                }
            }
        }
    }
}

#如何对string类型进行排序
//通过将该字段索引两次,一个进行搜索,一个进行排序
PUT /index
{
    "mappings":{
        "type_name":{
            "properties":{
                "title":{         //第一次索引
					"type":"text",
                    "analyzer":"english",
                    "fields":{
						"raw":{   //这个表示第二次索引排序
                            "type":"string",
                            "index":"not_analyzed"
                        }
                    },
                    "fielddate":true   //设置为true,有正排索引,才可以聚合
                }
            }
        }
    }
}
//新增字段的mappping
PUT /index/_mapping/type
{
    "properties":{
        "field_name":{
            "type":"short",
            "index":"not_analyzed"
        }
    }
}
```

### 10.问题及解决方案

- bouncing result

```json
//原因,当请求过来轮询在不同节点上,有时主分片上有的数据还没有同步到副本,这种时候我们可以使用preference参数指定查询
```

- 查询大量数据性能优化 ==scroll== (场景:系统处理大量数据,不像分页查询是给用户看的)

![1565931120852](C:\Users\da\AppData\Roaming\Typora\typora-user-images\scroll原理.png)

- reindex

  java终端应用零停机,

  1. 给旧的index使用别名

  2. 采用scroll查询数据,

  3. bulk api批量写入数据到新索引

  4. 别名更换到新索引

     ```json\
     //创建别名
     PUT /old_index/_alias/alise_index
     //更换别名
     POST /_aliases
     {
     	"actions":[
     		{"remove":{"index":"old_index","alias":"aliase_index"}},
     		{"add":{"index":"new_index","alias":"aliase_index"}}
     	]
     }
     ```


- 查询时间

  ```json
  //最近一个月
  GET /index/type/_search
  {
      "query":{
          "constant_score":{
              "filter":{
  				"range":{
                      "field_name":{
                          "gte":"now-30d"  //now-1m好像不对
                      }
                  }
              }
          }
      }
  }
  //某段时间
  GET /index/type/_search
  {
      "query":{
          "constant_score":{
              "filter":{
  				"range":{
                      "field_name":{
                          "gte":"2017-01-01||-30d"
                      }
                  }
              }
          }
      }
  }
  ```

  

- 精准控制全文检索结果

  ```json
  //1.operator:and 表示所有关键字都匹配
  //2.minimum_should_match:75% 表示匹配百分比
  //3.bool组合查询
  GET /index/type/_search
  {
  	"query":{
  		"match":{
  			"field_name":{
  				"query":"java elasticsearch",
  				"operator":"and"
  			}
  		}
  	}
  }
  
  GET /index/type/_search
  {
  	"query":{
  		"match":{
  			"field_name":{				
                  "query":"java elasticsearch spark hadoop",
                  minimum_should_match:"75%"
  			}
  		}
  	}
  }
  //这个和上面一样的结果(不同写法)
  #如果没有must,should必须满足其中一个,默认情况下是一个都可不满足
  GET /index/type/_search
  {
  	"query":{
  		"bool":{
              "should":[
                  {
                      "match":{
                          "field_name":"java"
                      }
                  },
                  {
                      "match":{
                          "field_name":"elasticsearch"
                      }
                  },
                  {
                      "match":{
                          "field_name":"spark"
                      }
                  },
                  {
                      "match":{
                          "field_name":"hadoop"
                      }
                  }
              ],
              "minimum_should_match":3
          }
  	}
  }
  ```

  

- 权重

  ```json
  //搜索标题中包含java,如果有hadoop或者elasticsearch,hadoop排在前面
  #默认情况下所有document权重都是1
  GET /index/type/_search
  {
      "query":{
          "bool":{
              "must":{
                  "match":{
                      "title":"java"
                  }
              },
              "should":[
                  {
                      "match":{
                          "title":{
                              "query":"hadoop",
                              "boost":3
                          }
                      }
                  },
                  {
                      "match":{
                          "title":{
                              "query":"elasticsearch",
                              "boost":2
                          }
                      }
                  }
              ]
          }
      }
  }
  ```

  

- 多shard下,relevance score不准确问题

  ```json
  //TF/IDF算法(假设搜索title字段包含java)
  1.在一个documen的title中,java出现了几次
  2.在所有document中java出现了几次(次数越多分数越低)
  3.document中title的长度
  #而多shard中，document分配不一样,IDF算法计算时只会根据本地document计算,所以会导致分数不一样
  //解决方案
  1.生产环境下尽可能实现均匀分布
  2.测试环境下,将主分片设置为1
  3.测试环境下，搜索附带search_type=dfs_query_then_fetch参数,会将local IDF取出来计算global IDF,但是会导致性能问题
  ```

  

- best fields策略(某一个field匹配到了尽可能多的关键词)

```json
//比如搜索条件如下
GET /index/type/_search
{
    "query":{
        "bool":{
            "should":[
                {"match":{"title":"java solution"}},
                {"match":{"content":"java solution"}}
            ]
        }
    }
}
//结果为两个字段各包含一个的比一个字段包含两个的排在前面
#解决方案(直接取多个query中分数最高的那个)
GET /index/type/_search
{
    "query":{
        "dis_max":{
            "queries":[
                {"match":{"title":"java solution"}},
                {"match":{"content":"java solution"}}
            ]
        }
    }
}
#优化(如果都一个包含java,一个两个字段各包含一个字段,可能出现第二个排在后面,完全不考虑其他query就有问题)
//tie_breaker,其他query分数乘以这个+最高分数query
GET /index/type/_search
{
    "query":{
        "dis_max":{
            "queries":[
                {"match":{"title":"java solution"}},
                {"match":{"content":"java solution"}}
            ],
            "tie_breaker":0.3
        }
    }
}
//另一种写法
GET /index/type/_search
{
    "query":{
        "multi_match":{
            "query":"learning courses",
            "type":"most_field",  //best_field,cross_field
            "fields":["sub_title","sub_title.std"]
        }
    }
}
```

- most_field ==区别== best_field

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\best_field与most_field.png)

- 使用copy_to解决cross_field弊端

  

```json
PUT /index/_mapping/article
{
    "properties":{
        "last_name":{
            "type":"String",
            "copy_to":"full_name"
        },
        "first_name":{
        	"type":"String",
            "copy_to":"full_name"
        },
        "full_name":{
            "type":"String"
        }
    }
}
```

- 短语匹配match_phrase( ==还是会分词== )

  ```json
  //相当于精确搜索.必须和搜索短语一模一样,如下:
  GET /index/type_search
  {
      "query":{
          "match_phrase":{
  			"content":"java spark"
              //"slop":1  //相当于上面单词中间的插槽位数(如果在这个数以内匹配的也会返回)
          }
      }
  }
  #原理：相当于分成多个term查询,然后在分词时会记录单词的position位置,然后根据搜索条件,如上spark比java的position大了1
  ```

  

- rescore优化近似匹配(就是加上slop的短语匹配)

  ```json
  //原理：用户一般进行分页查询,所以slop不需要对所有的doc进行计算和贡献,如查询到前十条结果，根据结果集再进行重新打分
  GET /index/type/_search
  {
      "query":{
          "match":{
              "title":{
  				"query":"java spark",
                  "minimum_should_match":"50%"
              }
          },
          "rescore":{
              "window_size":50, //查询结果数量
              "query":{
                  "rescore_query":{
                      "match_phrase":{
                      	"title":{
                              "query":"java spark",
                              "slop":50
                          }
                  	}
                  }
              }
          }
      }
  }
  ```

  

- 前缀搜索,通配符搜索,正则搜索

  ```json
  //前缀(搜索词越短性能越差)
  GET /index/type/_search
  {
      "query":{
  		"prefix":{
  			"title":"java"
          }
      }
  }
  //通配符,?：一个字符，*：0个或者多个字符
  {
      "query":{
  		"wildcard":{
  			"title":"j?v*"  
          }
      }
  }
  //正则,  .:一个字符,+:前面的正则表达式可以出现一次或多次
  {
      "query":{
  		"regexp":{
  			"title":"W[0-9].+"  
          }
      }
  }
  ```

  

- search-time，index_time搜索推荐

  ```json
  //search_time相当于前缀搜索+match_phrase,就是把最后一个term作为前缀
  //match hello,再把w作为前缀
  GET /index/type/_search
  {
      "match_phrase_prfix":{
          "title":{
  			"query":"hello w",
              "slop":10,
              "max_expansions":60 //最多匹配多少个(提高性能)
          }
      }
  }
  //index_time:通过ngram分词机制
  PUT /index
  {
      "settings":{
          "ananlysis":{
              "filter":{
                  "autocomplete_filter":{
                      "type":"edge_ngram",
                      "min_gram":1,
                      "max_gram":20
                  }
              },
              "analyzer":{
                  "autocomplete":{
                      "type":"custom",
                      "tokenizer":"standard",
                      "filter":{
                          "lowercase",
                          "autocomplete_filter"
                      }
                  }
              }
          }
      }
  }
  PUT /index/_mapping/type
  {
      "properties":{
          "title":{
              "type":"string",
              "analyzer":"autocomplete",
              "search_analyzer":"standard"
          }
      }
  }
  GET /index/type/_search
  {
  	"query":{
          "match_phrase":{
         		"title":"hello w"
      	}
      }
  }
  ```

  什么是ngram

  ![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\ngram.png)

- negative boost

```json
//包含java,包含spark减分(乘以negative_boost)
GET /index/type/_search
{
	"query":{
		"boosting":{
			"positive":{
                "match":{
					"title":"java"
                }
            },
            "negative":{
                "match":{
                    "title":"spark"
                }
            },
            "negative_boost":0.2
        }
    }
}
```

- 自定义分数增强function_score

  ```json
  //将对梯子搜索得到的分数跟num进行运算,由num在一定程度上增强帖子分数,看帖子的人越多,那么帖子的分数越高
  //底层公式为new_score=old_score * log(1+factor*num) factor如果未指定默认为1
  {
      "query":{
          "function_score":{
              "query":{
  				"multi_match":{
  					"query":"java spark",
                      "fields":["title","content"]
                  }
              },
              "field_value_factor":{
  				"field":"num",
                  "modifier":"log1p",
                  "factor":0.1
              },
              "boost_mode":"sum",//决定公式是相加还是相乘等操作
              "max_boost":1.5//限制计算的分数
          }
      }
  }
  ```

  

### 11.IK分词器

```json
//下载IK分词器 git clone https://github.com/medcl/elasticsearchanalvsis-ik
//解压到es  ../plugins/ik

POST /_analyze
{
  "analyzer": "ik_smart",
  "text": "北京人民欢迎你"
}

PUT /index
{
	"mappings":{
        "type":{
            "properties":{
                "field":{
                    "type":"text",
                    "analyzer":"ik_max_word"  //细粒度分词,ik_smart粗粒度分词
                }
            }
        }
    }
}

#配置文件
//es/plugins/ik/config/
//IKAnalyzer.cfg.xml用来配置自定义词库/main.dic默认主要词库/quantifier.dic单位相关词库/suffix.dic后缀名词库 /surname.dic姓氏词库/stopword.dic停用词
#ES热更新
1.修改ik分词器源码,手动支持从mysql定时加载新词库
2.基于ik分词器原生支持的热更新方案,部署一个web服务器,提供一个http接口,通过modified和tag两个http响应头,来提供词语的热更新(不太稳定)
```



### 12.监控fielddata内存使用

```json
#fielddata内存限制
indices.fielddata.cache.size:20% 朝珠限制,清楚内存已有的fielddata数据,默认无限制,限制内存使用,但是会导致频繁evict和reload,大量IO性能损耗,以及内存碎片和gc
GET /_stats/fielddata?fields=*
GET /_nodes/stats/indices/fielddata?fields=*
GET /_nodes/stats/indices/fielddata?level=indices&fields=*

#circuit breaker 短路器
如果一次query load的fielddata超过总内存,就会oom
circuit breaker 会估算query要加载的fielddata大小,如果超出总内存,就度ANLU,query直接失败
indices.breaker.fielddata.limit:fielddata的内存限制,默认60%
indices.breaker.request.limit:执行聚合的内存限制,默认40%
indices.breaker.total.limit:综合上面两个,限制在70%以内
```



### 13.数据建模实战



### 14.java api

```java
//  方式1.ElasticsearchRepository  https://blog.csdn.net/qq_16436555/article/details/94398049
// NativeSearchQueryBuilder QueryBuilder SortBuilder
//构建器 QueryBuilders SortBuilders
NativeSearchQuery(QueryBuilder query, QueryBuilder filter, List<SortBuilder> sorts, Field[] highlightFields) 
NativeSearchQuery native = NativeSearchQueryBuilder.withQuery(QueryBuilder1).withFilter(QueryBuilder2).withSort(SortBuilder1).withXXXX().build();

@Autowired
	private BookDao bookDao;

	/**
	 * 1、查  id
	 * @param id
	 * @return
	 */
	@GetMapping("/get/{id}")
	public Book getBookById(@PathVariable String id) {
		return bookDao.findOne(id);
	}

	/**
	 * 2、查  ++:全文检索（根据整个实体的所有属性，可能结果为0个）
	 * @param q
	 * @return
	 */
	@GetMapping("/select/{q}")
	public List<Book> testSearch(@PathVariable String q) {
		QueryStringQueryBuilder builder = new QueryStringQueryBuilder(q);
		Iterable<Book> searchResult = bookDao.search(builder);
		Iterator<Book> iterator = searchResult.iterator();
		List<Book> list = new ArrayList<Book>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	/**
	 * 3、查   +++：分页、分数、分域（结果一个也不少）
	 * @param page
	 * @param size
	 * @param q
	 * @return 
	 * @return
	 */
	@GetMapping("/{page}/{size}/{q}")
	public List<Book> searchCity(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String q) {

		// 分页参数
		Pageable pageable = new PageRequest(page, size);

		// 分数，并自动按分排序
		FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
				.add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", q)),
						ScoreFunctionBuilders.weightFactorFunction(1000)) // 权重：name 1000分
				.add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("message", q)),
						ScoreFunctionBuilders.weightFactorFunction(100)); // 权重：message 100分

		// 分数、分页
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
				.withQuery(functionScoreQueryBuilder).build();

		Page<Book> searchPageResults = bookDao.search(searchQuery);
		return searchPageResults.getContent();

	}

	/**
	 * 4、增
	 * @param book
	 * @return
	 */
	@PostMapping("/insert")
	public Book insertBook(Book book) {
		bookDao.save(book);
		return book;
	}

	/**
	 * 5、删 id
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delete/{id}")
	public Book insertBook(@PathVariable String id) {
		Book book = bookDao.findOne(id);
		bookDao.delete(id);
		return book;
	}

	/**
	 * 6、改
	 * @param book
	 * @return
	 */
	@PutMapping("/update")
	public Book updateBook(Book book) {
		bookDao.save(book);
		return book;
	}




//----------------------------------------------------------------






//  方式2.ElasticsearchTemplate
	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	/**
	 * 查询所有
	 * @throws Exception
	 */
	@GetMapping("/all")
	public List<Map<String, Object>> searchAll() throws Exception {
		//这一步是最关键的
		Client client = elasticsearchTemplate.getClient();
		// @Document(indexName = "product", type = "book")
		SearchRequestBuilder srb = client.prepareSearch("product").setTypes("book");
		SearchResponse sr = srb.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet(); // 查询所有
		SearchHits hits = sr.getHits();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (SearchHit hit : hits) {
			Map<String, Object> source = hit.getSource();
			list.add(source);
			System.out.println(hit.getSourceAsString());
		}
		return list;
	}

```

 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20190702095944900.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzE2NDM2NTU1,size_16,color_FFFFFF,t_70)  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20190702095925728.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzE2NDM2NTU1,size_16,color_FFFFFF,t_70) ![img](https://img-blog.csdn.net/20170726163702583?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnlhbGVpeGlhb3d1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)  



















