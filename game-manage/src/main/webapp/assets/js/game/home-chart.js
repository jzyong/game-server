/**
 * home主页图表
 * @author JiangZhiYong
 * */


window.setInterval("updateChart()",5000);



$(function () {
   updateChart();
});
//加载服务器信息统计图表
function updateChart() {
    $('.bar-chart').cssCharts({type:"bar"});
    $('.donut-chart').cssCharts({type:"donut"}).trigger('show-donut-chart');
    $('.line-chart').cssCharts({type:"line"});

    $('.pie-thychart').cssCharts({type:"pie"});


    //服务器个数柱状图
    var serverCountctx, serverCountData, serverCountBarChart, serverCountOptionBars;
    Chart.defaults.global.responsive = true;
    serverCountctx = $('#server-count-bar-chart').get(0).getContext('2d');
    serverCountOptionBars = {
        scaleBeginAtZero: true,
        scaleShowGridLines: true,
        scaleGridLineColor: "rgba(0,0,0,.05)",
        scaleGridLineWidth: 1,
        scaleShowHorizontalLines: true,
        scaleShowVerticalLines: false,
        barShowStroke: true,
        barStrokeWidth: 1,
        barValueSpacing: 5,
        barDatasetSpacing: 3,
        legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].fillColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>"
    };

    //服务器人数柱状图
    var serverOnlineCtx, serverOnlineData, serverOnlineBarChart,serverOlineOptionBars;
    serverOnlineCtx = $('#server-online-bar-chart').get(0).getContext('2d');
    serverOlineOptionBars = {
        scaleBeginAtZero: true,
        scaleShowGridLines: true,
        scaleGridLineColor: "rgba(0,0,0,.05)",
        scaleGridLineWidth: 1,
        scaleShowHorizontalLines: true,
        scaleShowVerticalLines: false,
        barShowStroke: true,
        barStrokeWidth: 1,
        barValueSpacing: 5,
        barDatasetSpacing: 3,
        legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].fillColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>"
    };

    //服务器内存线形图
    var serverMemoryCtx, serverMemoryData, serverMemoryLineChart, serverMemoryLineOptions;
    serverMemoryCtx = $('#server-memory-line-chart').get(0).getContext('2d');
    serverMemoryLineOptions = {
        scaleShowGridLines: true,
        scaleGridLineColor: "rgba(0,0,0,.05)",
        scaleGridLineWidth: 1,
        scaleShowHorizontalLines: true,
        scaleShowVerticalLines: false,
        bezierCurve: false,
        bezierCurveTension: 0.4,
        pointDot: true,
        pointDotRadius: 4,
        pointDotStrokeWidth: 1,
        pointHitDetectionRadius: 20,
        datasetStroke: true,
        datasetStrokeWidth: 2,
        datasetFill: true,
        legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].strokeColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>"
    };


    //获取数据
    $.post("/game-manage/server/statistics", {id: 1}, function (d) {
        if (d && d.status == 10000) {
            //服务器个数柱状图
            serverCountData = {
                labels: eval(d.result[0]),
                datasets: [
                    {
                        label: "My First dataset",
                        fillColor: "rgba(26, 188, 156,0.6)",
                        strokeColor: "#1ABC9C",
                        pointColor: "#1ABC9C",
                        pointStrokeColor: "#fff",
                        pointHighlightFill: "#fff",
                        pointHighlightStroke: "#1ABC9C",
                        data: eval(d.result[1])
                    }
                ]
            };
            serverCountBarChart = new Chart(serverCountctx).Bar(serverCountData, serverCountOptionBars);

            //服务器人数柱状图
            serverOnlineData = {
                labels: eval(d.result[2]),
                datasets: [
                    {
                        label: "My First dataset",
                        fillColor: "rgba(26, 188, 156,0.6)",
                        strokeColor: "#0c8ebc",
                        pointColor: "#0c8ebc",
                        pointStrokeColor: "#fff",
                        pointHighlightFill: "#fff",
                        pointHighlightStroke: "#0c8ebc",
                        data: eval(d.result[3])
                    }
                ]
            };
            serverOnlineBarChart = new Chart(serverOnlineCtx).Bar(serverOnlineData, serverOlineOptionBars);

            //服务器内存线形图
            serverMemoryData = {
                labels: eval(d.result[2]),
                datasets: [
                    {
                        label: "空闲内存",
                        fillColor: "rgba(26, 188, 156,0.2)",
                        strokeColor: "#1ABC9C",
                        pointColor: "#1ABC9C",
                        pointStrokeColor: "#fff",
                        pointHighlightFill: "#fff",
                        pointHighlightStroke: "#1ABC9C",
                        data: eval(d.result[4])
                    }, {
                        label: "可获得内存",
                        fillColor: "rgba(34, 167, 240,0.2)",
                        strokeColor: "#22A7F0",
                        pointColor: "#22A7F0",
                        pointStrokeColor: "#fff",
                        pointHighlightFill: "#fff",
                        pointHighlightStroke: "#22A7F0",
                        data: eval(d.result[5])
                    }
                ]
            };
            serverMemoryLineChart = new Chart(serverMemoryCtx).Line(serverMemoryData, serverMemoryLineOptions);

        } else {
            console.warn(d.message)
        }
    });
}