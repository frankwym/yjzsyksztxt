var chartData = {
  "result": {
    "items": [
      { "耕地": 10, "林地": 6, "编码": 120101000000, "水田": 97, "荒漠": 1, "区域名": "和平区" },
      { "耕地": 10, "林地": 8, "编码": 120102000000, "水田": 31, "荒漠": 0, "区域名": "河东区" },
      { "耕地": 1, "林地": 8, "编码": 120103000000, "水田": 31, "荒漠": 0, "区域名": "河西区" },
      { "耕地": 17, "编码": 120103000000, "水田": 31, "林地": 0, "荒漠": 0, "区域名": "蓟县" },
      { "耕地": 14, "林地": 13, "编码": 120103000000, "水田": 16, "荒漠": 20, "区域名": "武清区" },
      { "耕地": 14, "林地": 52, "编码": 120103000000, "水田": 26, "荒漠": 0, "区域名": "南开区" },
      { "耕地": 14, "林地": 24, "编码": 120103000000, "水田": 0, "荒漠": 0, "区域名": "河北区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 0, "区域名": "红桥区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 10, "区域名": "东丽区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 9, "区域名": "津南区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 0, "区域名": "西青区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 0, "区域名": "滨海新区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 0, "区域名": "宝坻区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 0, "区域名": "宁河区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 0, "区域名": "静海区" },
      { "耕地": 14, "林地": 8, "编码": 120103000000, "水田": 0, "荒漠": 0, "区域名": "北辰区" }
    ],
    "totalCount": 2
  }
};
//声明全局变量图表对象
var chartObj = {
  items: chartData.result.items, //数据所有的items
  areaArray: [], //区域数组
  dataTypes: [], //数据类型数组
  selectedAreaNames: [], //区域选择中选中项的名字
  selectedDataNames: [], //数据选择中选中项的名字
  selectedAreaItems: [], //选中区域的items
  //获取选中区域及其items
  getSelectedArea: function(options) {
    //清空数组
    this.selectedAreaNames = [];
    this.selectedAreaItems = [];
    var selectedAreaNames = this.selectedAreaNames;
    //获取选中区域名字数组
    options.each(function() {
      selectedAreaNames.push(this.text);
    });
    //获取数据中的items
    this.items.forEach(function(element) {
      if (selectedAreaNames.indexOf(element["区域名"]) > -1) {
        this.selectedAreaItems.push(element);
      }
    }, this);
  },
  //获取选中区域及其items
  getSelectedData: function(options) {
    //清空数组
    this.selectedDataNames = [];
    //获取选中区域名字数组
    options.each(function() {
      chartObj.selectedDataNames.push(this.text);
    });
  },
  //创建选项函数
  createOption: function(array, parent) {
    var child;
    array.forEach(function(element) {
      child = "<option>" + element + "</option>";
      parent.append(child);
    });
  },
  //创建警告框函数
  createAlertDialog: function(message) {
    //清空定时器
    clearTimeout(window.timeout);
    //创建警告框
    var alertDialog = '<div class="alert alert-danger fade in"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>警告！</strong>' + message + '</div>';
    $(".alert-container").append(alertDialog);
    //设置定时器，使警告框自动消失
    window.timeout=setTimeout('$(".alert-container").empty()',2000);
  },
  //创建series数组中的子对象函数
  createSeriesItem: function(name, type, data) {
    var obj = {};
    data = [];
    obj.name = name;
    obj.type = type;
    obj.data = data;
    return obj;
  },
  //创建柱状图函数
  createBarChart: function(areaNames, areaItems, dataNames) {
    //声明类目以及series数组
    var chartSeries = [],
      createSeriesItem = chartObj.createSeriesItem;
    //对selectedAreaItems进行遍历，构建legend以及chartSeries数组数据
    dataNames.forEach(function(element) {
      chartSeries.push(createSeriesItem(element, "bar"));
    });
    areaItems.forEach(function(element) {
      for (key in element) {
        if (dataNames.indexOf(key) > -1) {
          chartSeries[dataNames.indexOf(key)].data.push(element[key]);
        };
      }
    });

    // 初始化echarts实例
    var myChart = echarts.init(document.getElementById('echarts-container'));
    // 指定图表的配置项和数据
    var option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      toolbox: {
        show: true,
        feature: {
          mark: { show: true },
          dataView: { show: false, readOnly: false },
          magicType: { show: true, type: ['line', 'bar', 'stack', 'tiled'] },
          restore: { show: true },
          saveAsImage: { show: true }
        }
      },
      legend: {
        data: dataNames
      },
      xAxis: {
        data: areaNames
      },
      yAxis: {},
      series: chartSeries
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
  },
  //创建雷达图函数
  createRadarChart: function(areaNames, areaItems, dataNames) {
    var chartSeries = [], //series数组
      indicatorArray = []; //rader的indicator数组

    //按数据类型组织所有区域值的对象
    var dataMax = function() {
      var obj = {};
      dataNames.forEach(function(element) {
        obj[element] = [];
      });
      return obj;
    }();

    //获取series对象的data属性,data由每个区域的数据对象构成
    var seriesData = function() {
      var createSeriesItem = chartObj.createSeriesItem;
      chartSeries.push(createSeriesItem("雷达图", "radar")); //创建series对象

      var tempData = chartSeries[0].data; //获取series对象的data属性
      //遍历每个区域的item，将数据值存入value，名字存入name
      areaItems.forEach(function(element) {
        //为每个区域创建一个对象
        var obj = {
          value: [],
          name: ""
        };
        //为每个对象添加name
        obj.name = element["区域名"];
        //如果item中存在被选中的数据项，将其值添加到value数组中
        for (key in element) {
          if (dataNames.indexOf(key) > -1) {
            obj.value.push(element[key]);
            dataMax[key].push(element[key]);
          };
        }
        tempData.push(obj);
      });
      return tempData;
    }();
    //将每个数据类型数组中的最大值重新赋值给该数据类型
    for (key in dataMax) {
      dataMax[key] = Math.max.apply(null, dataMax[key]);
    }
    //创建radar的indicator
    dataNames.forEach(function(element) {
      var maxNum = (dataMax[element]) * 1.1;
      indicatorArray.push({ name: element, max: Math.ceil(maxNum) });
    });

    // 初始化echarts实例
    var myChart = echarts.init(document.getElementById('echarts-container'));
    // 指定图表的配置项和数据
    var option = {
      tooltip: {},
      toolbox: {
        show: true,
        feature: {
          mark: { show: true },
          dataView: { show: false, readOnly: false },
          restore: { show: true },
          saveAsImage: { show: true }
        }
      },
      legend: {
        data: areaNames,
        width: 760
      },
      radar: {
        indicator: indicatorArray
      },
      series: chartSeries
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
  }
};

//构建区域选择选项
chartObj.areaArray = (function() {
  var items = chartObj.items,
    array = [];
  //获取区域名称数组
  items.forEach(function(element, index) {
    array.push(element["区域名"]);
  });
  //构建区域选择项
  chartObj.createOption(array, $("#areaIndex"));
  return array;
}());

//构建数据选择选项
chartObj.dataTypes = (function() {
  var items = chartObj.items,
    array = [];
  //获取数据类型名称数组
  items.forEach(function(element) {
    for (key in element) {
      if ((key != "编码") && (key != "区域名") && (array.indexOf(key) == -1)) {
        array.push(key);
      }
    }
  });
  //构建数据类型选择项
  chartObj.createOption(array, $("#dataSelect"));
  return array;
}());

/******生成柱状图******/
$("#barChart").click(function() {
  //获取选中区域选项以及数据选项
  var areaOptions = $("#areaIndex option:selected"),
    dataOptions = $("#dataSelect option:selected");
  chartObj.getSelectedArea(areaOptions);
  chartObj.getSelectedData(dataOptions);

  var selectedAreaNames = chartObj.selectedAreaNames,
    selectedAreaItems = chartObj.selectedAreaItems,
    selectedDataNames = chartObj.selectedDataNames,
    createAlertDialog = chartObj.createAlertDialog;

  //提示用户至少选择一个区域
  if (areaOptions.length == 0) {
    createAlertDialog("请至少选择一个区域");
  } else if (dataOptions.length == 0) {
    createAlertDialog("请至少选择一种数据类型");
  } else {
    chartObj.createBarChart(selectedAreaNames, selectedAreaItems, selectedDataNames);
  }
});

/******生成雷达图******/
$("#radarChart").click(function() {
  //提示用户至少选择一个区域
  //获取选中区域选项以及数据选项
  var areaOptions = $("#areaIndex option:selected"),
    dataOptions = $("#dataSelect option:selected");
  chartObj.getSelectedArea(areaOptions);
  chartObj.getSelectedData(dataOptions);

  var selectedAreaNames = chartObj.selectedAreaNames,
    selectedAreaItems = chartObj.selectedAreaItems,
    selectedDataNames = chartObj.selectedDataNames,
    createAlertDialog = chartObj.createAlertDialog;

  //提示用户至少选择一个区域
  if (areaOptions.length == 0) {
    createAlertDialog("请至少选择一个区域");
  } else if (dataOptions.length < 3) {
    createAlertDialog("请至少选择三种数据类型");
  } else {
    chartObj.createRadarChart(selectedAreaNames, selectedAreaItems, selectedDataNames);
  }
});
