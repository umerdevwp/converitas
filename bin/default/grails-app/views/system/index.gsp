<%--
  Created by IntelliJ IDEA.
  User: olaf
  Date: 11/10/21
  Time: 10:47 PM
--%>
<%@ page import="groovy.json.JsonOutput; java.util.stream.Collectors" contentType="text/html;charset=UTF-8" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
  <meta name="layout" content="main"/>
  <title>Business News Reader - System State</title>

  <script src="https://d3js.org/d3.v3.min.js"></script>
  <script src="/assets/d3.layout.cloud.js"></script>
  <script src="/assets/vis-timeline-graph2d.min.js"></script>
  <script src="/assets/tempcolor.js"></script>
  <script src="/assets/wordcloud.js"></script>
  <script src="/assets/timeConverter.js"></script>
  <asset:stylesheet src="vis-timeline-graph2d.min.css"/>

  <script type="module">

    const refreshInterval = 60000;

    // Top timeline/chart stuff =======================================================================================
    // Timeline

    import "/assets/vis-network.min.js";

    const timelineItems = new vis.DataSet();

    const container = document.getElementById('visualization-bottom-row');
    const options = {
      editable: false,
      maxHeight: 250,
      type: 'point'
    };

    const timeline = new vis.Timeline(container, timelineItems, options);

    timeline.on('doubleClick', function(props) {
      window.location = "/system/index?ts=" + props.time.getTime();
    });

    timeline.on('rangechanged', function(e) {
          loadTimelineData(e.start.getTime(), e.end.getTime());
    });

    function loadTimelineData (from, to) {
      $.ajax({
        url: '/api/globaltimeline?from='+ from + '&to=' + to,
        success: function(data) {
          console.log(data);
          timelineItems.clear();
          timelineItems.add(data.tldata);
          timeline.setOptions({start: from, end: to});
          if (now)
            timeline.setCurrentTime(now);

          let html = "<table class='s-table' style='width:100%;'>";

          for (const i in data.nuggets) {
            const n = data.nuggets[i];

            var tdate = timeConverter(n.ts);

            html += '<tr>';
            //html += '<td>' + ((n.subtype !== 'None') ? n.subtype : '') + '</td>';        
            html += "<td>";
            html += "<span class='classification-title " + ((n.subtype !== 'None') ? 'title' : '') + "'>";
            html += ((n.subtype !== 'None') ? n.subtype : '');
            html += "</span>";
            html += ""+n.coUUID+"<a href='/organization/info?uuid=&to='>" + n.ts +"" + n.s + ' ' + n.r + ' ' + n.o +"</a>";
            html += "| <a href='" + n.articleUUID + "' target='_blank' rel='noopener noreferrer' class='article'>article</a>";            
            html += "</td>";
            html += "<td width='120px'>";
            html += tdate;
            html += "</td>";
            


            html += '</tr>';
          }
          html += "</table>";

          $('#insightsDiv').html(html);
          loadChartData(from, to);
        },
        error: function(err, status) {
          if (err.status===403) {
            location.replace("/auth/login?url="+window.location.href);
          }
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
      dataAxis: {
        visible: false,
        left: {
          range: {
            min: -0.1,
            max: 1
          }
        }
      },
      legend: true
    };

    var chart2d = new vis.Graph2d(chart_container, chartItems, chartGroups, chartOptions);
    var lastTick = new Date().getTime(), lastRefresh = new Date().getTime();

    chart2d.on('currentTimeTick', function(props) {
      const time = new Date().getTime();

      if (time - lastTick > 60000) {  // Can tick @ 1/sec - too fast so a minimum of 15 seconds
        if (! now) {
          const range = timeline.getWindow();
          loadTimelineData(range.start.getTime(), range.end.getTime());
        }
        lastTick = time;
      }
      if (null !== now)
        chart2d.setCurrentTime(now);
      else
        chart2d.setCurrentTime(time); // We've hijacked the line updater - so update
    });

    function loadChartData (from, to) {
      $.ajax({
        url: '/api/trackedCompanyWindowedState?buckets=20&from='+ from + '&to=' + to,
        success: function(data) {
          /**
           * data is map { uuid -> list of maps of data of which we want ts and heat}
           * uuids will correspond to group, ts is x and heat is y
           */
          chartItems.clear();
          chartGroups.clear();
          console.log('new ajax call...');
          let group = 0;

          for (const uuid in data) {

            // var name = data[uuid][0].organization.canonicalName;

            chartGroups.add({
              content: data[uuid][0].company.canonicalName,
              id: '' + group
            });
            for (const item in data[uuid]) {
              chartItems.add({
                group: group,
                x: data[uuid][item].ts,
                y: data[uuid][item].heat
              });
            }
            ++group;
          }
          chart2d.setItems(chartItems);
          chart2d.setGroups(chartGroups);
          chart2d.setOptions( {start: from, end: to} );
        },
        error: function(err, status) {
          if (err.status===403) {
            location.replace("/auth/login?url="+window.location.href);
          }
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

    const graphOptions = {
      nodes: {
        shape: 'dot',
        mass: 1,
        scaling: {
          label: {
            enabled: true
          }
        }
      },
      physics: {
        barnesHut: {
          gravitationalConstant: -10000
        },
        solver: 'repulsion',
        repulsion: {
          springLength: 250,
          nodeDistance: 150,
          springConstant:0.01
        }
      }
    };
    const graphContainer = document.getElementById('graph');

    window.loadGraphData = function(mode) {

            function modeFilter(d) { return d.mode >= mode; }

            function nodeById(nodes, id) {
              for(let i = 0; i < nodes.length; ++i) {
                if (nodes[i].id === id)
                  return nodes[i];
              }
            }

            $.ajax({
                url: "/api/activecompanygraph" + qts,
                beforeSend: function() {
                    $('.spinnerWrapper').removeClass('hide');
                    $('.modeHeading').addClass('hide');
                    $('#graph').addClass('hide');
                },
                success: function (data) {
                    console.log('data:', data);
                    const network = new vis.Network(graphContainer, {
                        nodes: data.nodes.filter(modeFilter),
                        edges: data.edges
                    }, graphOptions);

                    //$('#num_cos').html(data.nodes.length);
                    $('#mode').html( mode === 0 ? 'Watched' : mode === 1 ? 'Surfaced' : 'Tracking')

                    // Indexed by node mode. Note we have  modes 3,4 for children in the graph. It is ignored in counts
                    let cos_html = [{t: '', s: 0}, {t: '', s: 0}, {t: '', s: 0}, {t: '', s: 0}, {t: '', s: 0}];

                    $('#tracked_cos_size').html('')

                    for (let i = 0; i < data.nodes.length; ++i) {
                        const node = data.nodes[i];
                        let co_html = '<a href=' + node.uuid + qts + '"/organization/info/" style="text-decoration: none">';
                            co_html += '<div style="color:' + node.color + '">' + node.label + '</div></a>';
                        cos_html[node.mode].t += co_html;
                        ++cos_html[node.mode].s;
                    }

                    $('#watched_cos').html(cos_html[0].t);
                    $('#pinned_cos').html(cos_html[1].t);
                    $('#tracked_cos').html(cos_html[2].t);
                    $('#num_watched').html(cos_html[0].s);
                    $('#num_pinned').html(cos_html[1].s);
                    $('#num_tracked').html(cos_html[2].s);

                    network.on('click', function (properties) {
                        let haveNode = false; // Manage code continuation after window_location

                        if ([] !== properties.items)
                        {
                            let index = properties.nodes[0];
                            for (let i = 0; i < data.nodes.length; ++i) {
                                if (index === data.nodes[i].id) {
                                    haveNode = true;
                                    window.location = "/organization/info?uuid=" + data.nodes[i].uuid + ts;
                                }
                            }
                        }
                        if ([] !== properties.edges && !haveNode)
                        {
                          let index = properties.edges[0];
                          for (let i = 0; i < data.edges.length; ++i) {
                            if (index === data.edges[i].id) {
                              const fromNode = nodeById(data.nodes, data.edges[i].from).uuid;
                              const toNode = nodeById(data.nodes, data.edges[i].to).uuid;
                              window.location = '/organization/shadow?uuid=' + fromNode + '&shadow=' + toNode + ts ;
                            }
                          }
                        }
                      })
                },
                complete: function() {
                  $('.spinnerWrapper').addClass('hide');
                  $('.modeHeading').removeClass('hide');
                  $('#graph').removeClass('hide');
                },
                error: function(err, status, error){
                  if (err.status===403) {
                    location.replace("/auth/login?url="+window.location.href);
                  }
                  alert(err.responseJSON.message);
                }
            })
        }

        loadGraphData(2);

        setInterval(
          function() {
            if (now === null)
              $('.buttons.selectedTab').click();
          }, refreshInterval);

    var companyWordCloud = wordCloud('#companyDiv', function(d) { return "/organization/info?uuid=" + d.uuid + ts});
    const hotCompanies = ${raw(JsonOutput.toJson(hotCompanies.collect { [text: it.name, size: it.count, temp: it.temperature, uuid: it.uuid]}))};
    console.log('hotCompanies:', hotCompanies);
    companyWordCloud.update(hotCompanies);

    setInterval(
      function() {
        if (now) return;    // Don't update on timer if we have a 'now'
        companyWordCloud.update(hotCompanies);
        console.log('companyWordCloud updated');
      }, refreshInterval);

    setTimeout(function(){ 
      $('.classification-title').each(function() {      
          if ($(this).text().trim() !== '' );
          $(this).addClass('title');
          console.log($(this).text());
      });
     }, 5000);

     $('.buttons').on('click', function() {
       $('.buttons').removeClass('selectedTab');
       $(this).addClass('selectedTab');
     });


    let pageURL = window.location.href; 
    if (pageURL.indexOf('/system/index') > -1 ) {
      $('.navbar-nav a:first-child').addClass('selectedTab');
    };
    
    if (pageURL.indexOf('/organization/index') > -1 ) {
      $('.navbar-nav a:last-child').addClass('selectedTab');
    }

    window.search = function() {
      const range = timeline.getWindow();
      const from = range.start.getTime(), to = range.end.getTime();
      window.location = encodeURI('/system/query?query=' + $('#q').val() + '&from=' + from + '&to=' + to);
    }
  </script>
</head>

<body>

<content tag="navTitle">Business Metagraph Insights
  ${ts ? raw("<br/><span style='font-size:12pt'>[${new Date(ts)} <a href='/system/index'> - <span style='color:pink'>Now</span></a>]</span>") : ''}</content>

<div style="margin: 0 10px">
<div style="margin: 0 0 10px 0; width:100%; float:left">
<div class="timelineTitle">
  <h2>
    Metagraph Timeline
    <cv:tt>
      The Metagraph Timeline has two components overlayed on a common timeline.  
      The top part displays the line chart for each of the Tracking Companies.  
      Each line represent a measure of that organization’s activity aggregated across our various data feeds.
      Factors that influence the activity score include frequency of mention, nature of mention and date of mentions. 
      The bottom part list news ‘nuggets’ from our various data feeds.
    </cv:tt>

  </h2>
</div>
  <div class="outer-top-row">
    <div id="visualization-top-row"></div>
  </div>

  <div class="outer-bottom-row">
    <div id="visualization-bottom-row"></div>
  </div>
</div>
<%-- <div id="companyDiv" style="width:25%; height: 200px; margin-top: 20px; float: left"></div> --%>
<table style="width: 100%; margin-top: 10px; height:100%; border-top: none">
  <tr>
    <td style="width: 20%; padding-left: 5px; border: solid 1px #ccc" rowspan="2">
      <h2>Tracking Companies (<span id="num_tracked"></span>)
        <cv:tt>
          <p>
            We’ve seeded this system with select companies of interest to ‘track’. The system seeks out references to these companies in our various data feeds.
          </p>
        </cv:tt>
      </h2>
      <div id="tracked_cos" style="max-height: 150px; overflow: auto">
      </div>
        <h2>Surfaced Companies (<span id="num_pinned"></span> )
        <cv:tt>
          <p>
            Surfaced companies are Watched companies that have recently experienced a burst of data feed activity or have become more strongly connected to other Surfaced or Tracked companies.  
          </p>  
          <p>
            Typically, companies will move back and forth over time between being Surfaced or Watched.
          </p>
        </cv:tt>
      </h2>
        <div id="pinned_cos" style="max-height: 150px; overflow: auto">
        </div>      
      <h2>Watched  (<span id="num_watched"></span> )
        <cv:tt>
          <p>
            Listing of all companies that are currently watched by the system.
            A organization becomes Watched when it is referenced alongside a Tracked or an existing Watched organization in our data feeds.
          </p>
          <p>
            Appearing in the Watched list can be transitory.  Companies will persist so long as there is regular mentions of them.  As data feed activity cools for a particular organization, it will drop off the Watched list.
          </p>
        </cv:tt>
      </h2>
        <div id="watched_cos" style="max-height: 500px; overflow: auto">
        </div>
    </td>
    <td style="width:50%; padding-left: 5px; max-height:600px; border: solid 1px #ccc">
      <div class="tabs">
        <button class="buttons selectedTab" onclick="loadGraphData(2)">Tracking</button>
        <button class="buttons" onclick="loadGraphData(1)">Surfaced</button>        
        <button class="buttons" onclick="loadGraphData(0)">Watched</button>
      </div>
      <h2 class="modeHeading"><span id="mode"></span> Companies</h2>
      <div class="spinnerWrapper hide">
        <div class="spinner">Loading...</div>
      </div>
      <div id="graph" style="width:100%; margin: 10px; height: 550px">
      </div>
    </td>
    
    <td style="width:25%; padding-left: 5px; border: solid 1px #ccc">
      <h2>Trending
              <cv:tt>
          <p>
          Word cloud of companies that have significant and current activity in our data feed.
          </p>
        </cv:tt>
      
      </h2>      
      <div id="companyDiv" style="width:100%; height: 250px;"></div>
      <h2>
        Metagraph Nuggets
        <cv:tt>
          <p>Nuggets are automated text highlighted insights for quick perusal. Our classification system is over time and where possible we group nuggets into business relevant categories such as mergers, acquisitions and others.</p>
          <p>Clicking on the nugget will take you to the Metagraph Insights page for that company.</p>
        </cv:tt>      
      
      </h2>
      <div id="insightsDiv" style="max-height: 550px; overflow-y: auto"></div>
    </td>
</table>
</div>
</body>
</html>
