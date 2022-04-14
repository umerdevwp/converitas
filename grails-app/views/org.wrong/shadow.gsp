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
  <title>Metagraph Insights between ${company.company.canonicalName} and ${shadow.company.canonicalName}</title>
  <style media="all">

  #visualization-bottom-row, #visualization-top-row {
    height: 100%;
  }
  .outer-top-row {
    height: 125px;
  }
  .outer-bottom-row {
    max-height: 250px;
  }
  #visualization-top-row .vis-panel.vis-bottom {
    display: none;
  }
  #visualization-top-row .vis-timeline{
    border-bottom: none;
  }
  .form-control:focus + .list-group {
    display: block;
  }
  .vis-item .vis-item-content {
    line-height: 14px;
    font-weight: normal;
    font-size: 12px;
    padding: 1px;
  }
  a {
    text-decoration: none;
  }

  </style>
  <script src="/assets/vis-timeline-graph2d.min.js"></script>
  <script src="/assets/timeConverter.js"></script>
  <asset:stylesheet src="vis-timeline-graph2d.min.css"/>
</head>
<body>

<content tag="navTitle">
  Metagraph Insights between ${company.company.canonicalName} and ${shadow.company.canonicalName}<br/>
  ${ts ? raw("<span style='font-size: 10pt'>[${new Date(Long.parseLong(ts as String))} <a href='/organization/shadow?uuid=${company.company.uuid}&shadow=${shadow.company.uuid}'> - <span style='color:pink'>Now</span></a>]</span></a></span>") : ''}

</content>

<div style="margin: 0 5px">
  <div class="timelineTitle">
    <h2>
      Association Over Time
      <cv:tt>
        The Timeline has two components overlayed on a common timeline.
        The top part displays the relative strength of the association at various times and the
        bottom part list news ‘nuggets’ associationg the two comapanies.
      </cv:tt>

    </h2>
  </div>
  <div class="outer-top-row">
    <div id="visualization-top-row"></div>
  </div>

  <div class="outer-bottom-row">
    <div id="visualization-bottom-row"></div>
  </div>
  <h2>Related Articles</h2>
  <div id="bind_articles"></div>
</div>

<script type="module">

  const timelineItems = new vis.DataSet();

  const container = document.getElementById('visualization-bottom-row');
  const options = {
    editable: false,
    maxHeight: 250,
    type: 'point'
  };

  const timeline = new vis.Timeline(container, timelineItems, options);

  timeline.on('doubleClick', function(props) {
    window.location = "/organization/shadow?uuid=${company.company.uuid}&shadow=${shadow.company.uuid}&ts=" + props.time.getTime();
  });

  timeline.on('rangechanged', function(e) {
    loadTimelineData(e.start.getTime(), e.end.getTime());
  });

  function loadTimelineData (from, to) {
    $.ajax({
      url: "/api/companytimeline/${company.company.uuid}?from="+ from + '&to=' + to,
      success: function(data) {
        timelineItems.clear();
        timelineItems.add(data.tldata);
        timeline.setOptions( {start: from, end: to});
        loadChartData(from, to);
      },
      error: function(err, status) {
        alert(err.responseJSON.message);
      }
    })
  }

  // Chart

  var chart_container = document.getElementById('visualization-top-row');
  var chartItems = new vis.DataSet();
  var chartGroups = new vis.DataSet();
  var chartOptions = {
    height: '100%',
    style: 'bar',
    barChart: {
      width: 10
    },
    dataAxis: {
      visible: false,
      left: {
        range: {
          min: -0.1,
          max: 10
        }
      }
    }
  };

  var chart2d = new vis.Graph2d(chart_container, chartItems, chartGroups, chartOptions);
  var lastTick = new Date().getTime(), lastRefresh = new Date().getTime();

  function loadChartData (from, to) {
    $.ajax({
      url: "/api/shadowstrength/${company.company.uuid}/${shadow.company.uuid}?from=" + from + '&to=' + to,
      success: function(data) {
        // data.values has x/y values.  data.maxvalue
        chartItems.clear();
        chartGroups.clear();

        for (const index in data.values) {
          let item = data.values[index];
          item['group'] = 0;
          chartItems.add(item);
        }
        chart2d.setItems(chartItems);
        chart2d.setOptions({
          start: from,
          end: to,
          dataAxis: {
            left: {
              range: {
                max: 1.1 * data.maxvalue
              }
            }
          }
        });
        loadArticles(from, to);
      },
      error: function(err, status) {
        alert(err.responseJSON.message);
      }
    })
  }

  function loadArticles (from, to) {
    $.ajax({
      url: "/api/bindingarticles/${company.company.uuid}/${shadow.company.uuid}?from=" + from + '&to=' + to,
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
      },
      error: function(err, status) {
        alert(err.responseJSON.message);
      }
    })
  }

  function onChangeChart(range) {
    if (!range.byUser) {
      return;
    }

    timeline.setOptions({
      start: range.start,
      end: range.end,
      height: '112',
    });
  }

  function onChangeTimeline(range) {
    if (!range.byUser) {
      return;
    }

    chart2d.setOptions({
      start: range.start,
      end: range.end,
      height: '100%'
    });
  }

  chart2d.on('rangechange', onChangeChart);
  timeline.on('rangechange', onChangeTimeline);


  chart2d.on('_change', function() {
    visLabelSameWidth();
  });

  $(window).resize(function(){
    visLabelSameWidth();
  });

  // Vis same width label.
  function visLabelSameWidth() {
    var w1 = $("#visualization-top-row .vis-content .vis-data-axis").width();
    var w2 = $("#visualization-bottom-row .vis-labelset .vis-label").width();

    $("#visualization-top-row")[0].childNodes[0].childNodes[2].style.display = 'none';

    if (w2 > w1) {
      $("#visualization-top-row .vis-content")[1].style.width = 0;
    }
    else {
      $("#visualization-bottom-row .vis-labelset .vis-label").width(w1+"px");
    }
  }

  window.search = function() {
    const range = timeline.getWindow();
    const from = range.start.getTime(), to = range.end.getTime();
    window.location = encodeURI('/system/query?query=' + $('#q').val() + '&from=' + from + '&to=' + to);
  }

  let now = ${ts ?: 'null'}; // If not null this is 'now' for the page
  let ts = '', qts = '';

  if (null != now) {
    ts = "&ts=" + now;
    qts = "?ts=" + now;
  }

  if (now)
    loadTimelineData(now-11*3600*1000, now+3600*1000);
  else
    loadTimelineData(Date.now()-11*3600*1000, Date.now()+3600*1000);


  // End Timeline ====================================================================================================
</script>

</body>
</html>
