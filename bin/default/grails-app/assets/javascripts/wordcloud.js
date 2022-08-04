
/**
 *   Encapsulate the word cloud functionality - link is a function which takes a word and generates the
 *   appropriate href
 */
function wordCloud(selector, link) {
    // var color = d3.scaleOrdinal(d3.schemeCategory10);
    //var color = d3.scale.category20();
    const width = $(selector).width();
    const height = $(selector).height();

    //Construct the word cloud's SVG element
    var svg = d3.select(selector).append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        // .attr("transform", "translate(250,250)");
        .attr("transform", "translate("+width/2+","+height/2+")");

    /**
     * Draw the word cloud
     */
    function draw(words) {
        var cloud = svg.selectAll("g text").data(words, (d) => d.text )

        //Entering words
        cloud.enter()
            .append("text")
            .style("font-family", "sans-serif")
            .style("fill", function(d, i) { return tempColor(d.temp/*, d.text*/); })
            .attr("text-anchor", "middle")
            .attr('font-size', 1)
            .html((d) => "<a href='" + encodeURI(link(d)) + "'>" + d.text + "</a>");

        //Entering and existing words
        cloud
            .transition()
            .duration(600)
            .style("font-size", function(d) { return d.size + "px"; })
            // .attr("transform", function(d) {return "translate(" + [d.x+width/3.75, d.y+height/10] + ")rotate(" + d.rotate + ")";})
            // .attr("transform", (d) => "translate(" + [d.x+width*0.2, d.y+height*0.025] + ")rotate(" + d.rotate + ")")
            .attr("transform", (d) => "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")")
            .style("fill-opacity", 1);

        //Exiting words
        cloud.exit()
            .transition()
            .duration(200)
            .style('fill-opacity', 1e-6)
            .attr('font-size', 1)
            .remove();
    }


    //Use the module pattern to encapsulate the visualisation code. We'll
    // expose only the parts that need to be public.
    return {

        //Recompute the word cloud for a new set of words. This method will
        // asycnhronously call draw when the layout has been computed.
        //The outside world will need to call this function, so make it part
        // of the wordCloud return value.
        update: function(words) {
            // min/max word size
            var minSize = d3.min(words, (d) => d.size );
            var maxSize = d3.max(words, (d) => d.size );

            var textScale = d3.scale.linear()
                .domain([minSize,maxSize])
                .range([15,50]);

            d3.layout.cloud().size([width, height])
                .words(words)
                // .words(words.map(function(d) {return {text: d.text, size: textScale(d.size) };}))
                .padding(1)
                .rotate(() => 0 /* ~~(Math.random() * 2) * 90*/ /*~~(Math.random() * 6) - 3) * 30*/ )
                .font("sans-serif")
                .fontSize((d) => textScale(d.size))
                // .fontSize((d) => d.size)
                .on("end", draw)
                .start();
        }
    }

}