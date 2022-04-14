<%--
  Created by IntelliJ IDEA.
  User: olaf
  Date: 9/23/21
  Time: 11:11 AM
--%>
<%@ page import="com.coveritas.heracles.ui.CompanyService; groovy.json.JsonOutput; java.util.stream.Collectors" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="main"/>
  <meta charset="utf-8" />
  <g:set var="organization" value="${companyEntityState.company}"/>
  <title>Coveritas Company Tracker ${company.canonicalName}</title>
  <asset:stylesheet src="vis-timeline-graph2d.min.css"/>
  <asset:stylesheet src="vis-network.min.css"/>
  <script src="https://d3js.org/d3.v3.min.js"></script>
  <script src="/assets/d3.layout.cloud.js"></script>
  <script src="/assets/wordcloud.js"></script>
  <script src="/assets/timeConverter.js"></script>
  <script src="/assets/tempcolor.js"></script>

  <style>

  .vis-item .vis-item-content {
    line-height: 14px;
    font-weight: normal;
    font-size: 12px;
    padding: 1px;
  }

  .article {
    font-size: 14pt;
    text-decoration: none;
    color: #4e555b;
  }

  .articletitle {
    font-size: 14pt;
    text-decoration: none;
    color: black;
  }
  </style>

</head>
<body>

<content tag="navTitle">
  Metagraph Insights for ${company.canonicalName} ${company.ticker ? "(${company.ticker})" : ''}
  <br/><span id="now" style="font-size: 12pt"></span>
</content>


<div id="timeLine" role="navigation">
  <div id="time_line"></div>
</div>

<div style="margin: 0 10px 0 10px">
  <table style="width:100%; border-bottom: #817d7d 1px solid">
    <tr>
      <td style="width:40%; border-right: #817d7d 1px solid">
        <h2 style="margin-left:-10px">Company Associations
          <cv:tt>
            A graph structure of a organization and its relationship to other companies as discovered in our data feeds.
          </cv:tt>
        <span style="float:right">
          Link Depth &nbsp;&nbsp;
          <g:select name="depth" from="['1','2','3']" value="1" onchange="window.loadGraphData(this.value)"/>
        </span>
        </h2>
        <div id="graph" style="width: 100%; height: 500px;"></div>
      </td>

      <td style="width:40%; border-right: #817d7d 1px solid">
        <h2>Link Insights
          <cv:tt>
            Shows articles that relate to the two companies when you double click on a link.
          </cv:tt>
            <span id="shadowing"></span>
          &nbsp;&nbsp;<span id="shadow_link"></span>
        </h2>
        <div id="bind_articles" style="width:100%; height:250px; overflow: auto">

        </div>
        <div id="bind_words" style="width:100%; height:250px">
          <h2>
            Shared Thoughts
            <cv:tt>
              A word cloud display of shared themes or terms between two companies.
            </cv:tt>
          </h2>
        </div>
      </td>

      <td  style="width:20%">
        <h2>Company Info</h2>
        <div id="companyInfo"></div>
      </td>
    </tr>
  </table>
    <h2 class="h1-heading">
      Associated Company News
      <%-- <cv:tt>
        Associated Company News
      </cv:tt>       --%>
    </h2>
</div>

<script src="/assets/timeConverter.js"></script>
<script src="/assets/tempcolor.js"></script>

<script type="module">
  import "/assets/vis-timeline-graph2d.min.js";
  import "/assets/vis-network.min.js";

  let from, to;
  let now = ${ts ?: System.currentTimeMillis()};

  from = now - 12*3600*1000; to = now + 12*3600*1000;

  var wordCloud1 = wordCloud('#bind_words', function(d) {
    return '/system/query?query="' + d.text + '"&from=' + from + "&to=" + to;
  });

  // CompanyInfo ------------------------------------------------------------------------------------------------------
  $( document ).ready(function() {
    $.ajax({
      url: "/api/companyEntityState?uuid=${company.uuid}",
      success: function(data) {
        console.log('CompanyData:', data);
        console.log('CompanyData:', data.company.canonicalName);
        let companyName = data.company.canonicalName;
        let companyCountry = data.company.country;
        let companyTicker = data.company.ticker;
        let  websiteURL = data.company.details ? data.company.details.web_site : '';
        let companyWikiData = data.company.wikiDataId;
        let companyTwitter = data.company.details ? data.company.details.twitter : '';

        let html = '<div>';
        html += "<div>";
        html += "<strong>Name: </strong>";
        html += companyName;
        html += "</div>";
        html += "<div>";
        html += "<strong>Country: </strong>";
        html += companyCountry;
        html += "</div>";
        html += "<div>";
        html += "<strong>Ticker: </strong>";
        html += ((companyTicker !== null) ? companyTicker : '');
        html += "</div>";
        html += "<div class='urlWrapper'>";
        html += "<strong>Website: </strong>";
        html += "<a class='websiteURL' href="+websiteURL+">";
        html += "<span>"+ ((websiteURL !== undefined) ? websiteURL : '') +"</span>";
        html += "</a>";
        html += "</div>";
        html += "<div>";
        html += "<strong>Twitter: </strong>";
        html += "<a href=https://twitter.com/"+companyTwitter+">";
        html += ((companyTwitter !== undefined) ? companyTwitter : '') ;
        html += "</a>";
        html += "</div>";
        html += "<div>";
        html += "<strong>WikiData: </strong>";
        html += "<a href=https://www.wikidata.org/wiki/"+companyWikiData+">";
        html += companyWikiData;
        html += "</a>";
        html += "</div>";
        html += "</div>";

        $('#companyInfo').html(html);
      },
      error: function(err, status) {
        alert(err.responseJSON.message);
      }
    });

    setTimeout(function() {
      var urlWrapperValue = $('.urlWrapper span').html();
      if (urlWrapperValue !== undefined) {
        $('.urlWrapper span').clone().appendTo('a.websiteURL');
        $(".urlWrapper span").not('a span').empty();
      }
    }, 500);
  });

  // Timeline ---------------------------------------------------------------------------------------------------------
  const items = new vis.DataSet();

  const container = document.getElementById('time_line');
  const options = {
    editable: false,
    height: 150,
    maxHeight: 300
  };

  const timeline = new vis.Timeline(container, items, options);

  timeline.on('rangechanged', function(e) {
    from = e.start.getTime(); to =  e.end.getTime();
    now = Math.round((from+to)/2);
    loadTimelineData();
    loadArticles();
    window.loadGraphData(1);
  });

  timeline.on('doubleClick', function(props) {
    window.location = "/organization/info/${company.uuid}?ts=" + props.time.getTime();
  });

  function loadTimelineData () {
    $.ajax({
      url: "/api/companytimeline/${company.uuid}?from="+ from + '&to=' + to,
      success: function(data) {
        items.clear();
        items.add(data.tldata);
        timeline.setOptions( {start: from, end: to});
        timeline.setCurrentTime(now);
        $('#now').html('[' + timeConverter(new Date(now).getTime(), 2) + ' <a href="/organization/info/${company.uuid}"> - <span style="color:pink">Now</span></a>' + ']');
      },
      error: function(err, status) {
        alert(err.responseJSON.message);
      }
    })}

  // Graph ------------------------------------------------------------------------------------------------------------
  const graphOptions = {
    nodes: {
      shape: 'dot',
      mass: 1.5,
      scaling: {
        label: {
          enabled: true
        }
      }
    },
    physics: {
      solver: 'repulsion',
      repulsion: {
        springLength: 250,
        nodeDistance: 150,
        springConstant:0.01
      }
    }
  };
  const graphContainer = document.getElementById('graph');

  window.loadGraphData = function(depth) {
    $.ajax({
      url: "/api/companygraph?uuid=${company.uuid}&depth=" + depth + "&from=" + from + "&to=" + to,
      success: function(data) {
        const network = new vis.Network(graphContainer, data, graphOptions);

        function nodeById(nodes, id) {
          for(let i = 0; i < nodes.length; ++i) {
            if (nodes[i].id === id)
              return nodes[i];
          }
        }

        network.on('click', function(properties) {
          if ([] !== properties.items) {
            let index = properties.nodes[0];
            for (let i = 0; i < data.nodes.length; ++i) {
              if (index === data.nodes[i].id) {
                window.location = "/organization/info?uuid=" + data.nodes[i].uuid + "&from=" + from + "&to=" + to;
              }
            }
          }
          if ([] !== properties.edges) {
            let index = properties.edges[0];
            for (let i = 0; i < data.edges.length; ++i) {
              if (index === data.edges[i].id) {
                const fromNode = nodeById(data.nodes, data.edges[i].from).uuid;
                const toNode = nodeById(data.nodes, data.edges[i].to).uuid;
                let url = "/api/bindingarticles/" + fromNode + "/" + toNode + "?from=" + from + "&to=" +to;
                $.ajax({
                  url: url,
                  success: function(data) {
                    let articles = '<table><tr><th>Title</th><th>Published</th><th>Summary</th></tr>';
                    for (const i in data.articles) {
                      const article = data.articles[i];
                      articles += '<tr><td>';
                      articles += '<a href="' + article.source + '">' + article.title + '</a></td><td>';
                      articles += timeConverter(article.ts) + '</td><td>';
                      articles += article.content + '</td></tr>';
                    }

                    articles += '</table>';
                    $('#bind_articles').html(articles);
                    $('#shadowing').html(data.company.canonicalName + ' and ' + data.shadow.canonicalName);
                  }
                });
                $.ajax({
                  url: "/api/edgestate/" + fromNode + "/" + toNode + "?ts=" + now,
                  success: function(data) {
                    let words = [];
                    for (const word in data.commonWords) {
                      words.push({text: word, temp: data.commonWords[word], size:20});
                    }
                    wordCloud1.update(words);
                    $('#shadow_link').html('' + fromNode + '' + toNode + "<a href="/organization/shadow?uuid=&shadow=&ts=">Link History</a>" + now + '');
                  }
                })
              }
            }
          }
        })
      },
      error: function(err, status) {
        alert(err.responseJSON.message);
      }
    })
  }

  function loadArticles () {
    $.ajax({
      url: "/api/companyarticles?uuid=${company.uuid}&from=" + from + '&to=' + to,
      success: function(data) {
        let articles = '<table><tr><th>Title</th><th>Published</th><th>Summary</th></tr>';
        for (const i in data.articles) {
          const article = data.articles[i];
          articles += '<tr><td>';
          articles += '<a href="' + article.source + '">' + article.title + '</a></td><td>';
          articles += timeConverter(article.contentTs) + '</td><td>';
          articles += article.content + '</td></tr>';
        }

        articles += '</table>';
        $('#articles').html(articles);
      },
      error: function(err, status) {
        alert(err.responseJSON.message);
      }
    })
  }

  window.search = function() {
    const range = timeline.getWindow();
    const from = range.start.getTime(), to = range.end.getTime();
    window.location = encodeURI('/system/query?query=' + $('#q').val() + '&from=' + from + '&to=' + to);
  }

  loadTimelineData();
  loadArticles();
  loadGraphData(1);

</script>

<div style="margin: 0 10px 0 10px">
    <h2>Associated Company News</h2>
  <div id="articles"></div>
</div>



</body>
</html>
