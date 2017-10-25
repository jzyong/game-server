(function () {
    var Game = window.Game = Class.extend({
        init: function (id, resourseUrl) {
            this.resourseUrl = resourseUrl;
            //获得画布ctx对象
            this.canvas = document.getElementById(id);
            this.ctx = this.canvas.getContext("2d");
            //图片路径对象
            this.RobjectTxt = null;
            //图片对象json
            this.Robject = {};
            //帧编号
            this.f = 0;
            this.actors = [];
            //游戏帧编号
            this.gt = 0;
            //鱼数组
            this.fishArr = [];
            this.senceNumber = 0;
            //子弹数组
            this.arrBullet = [];
            //炮弹余量
            this.bulletCount = 10;
            //炮弹信号量
            this.a = 0;
            //总分数
            this.score = 0;
            //背景音乐
            this.bg_music = new Audio();
            //加载资源
            this.loadResource();
        },
        loadResource: function () {
            var self = this;
            //加载图片累加器
            var count = 0;
            //设置页面初始图片加载文字
            self.ctx.font = "28px 微软雅黑";
            self.ctx.textAlign = "center";
            self.ctx.fillText("图片正在加载中.....", 300, self.canvas.height * (1 - 0.618));
            //原生js Ajax请求
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function () {
                if (xhr.readyState == 4) {
                    if (xhr.status >= 200 && xhr.status < 300 || xhr.status == 304) {
                        //获得图片资源路径txt文件
                        self.RobjectTxt = JSON.parse(xhr.responseText);
                        //图片总数
                        var imageAmount = _.size(self.RobjectTxt);
                        //遍历图片资源路径txt文件，将图片对象设置完src属性并放入图片对象json中
                        for (var k in self.RobjectTxt) {
                            self.Robject[k] = new Image();
                            self.Robject[k].src = ctx+"/assets/"+self.RobjectTxt[k];    //路径拼接
                            //监听图片加载事件
                            self.Robject[k].onload = function () {
                                count++;
                                //在画布上显示图片加载进度
                                self.ctx.clearRect(0, 0, 800, 600);
                                self.ctx.font = "24px 微软雅黑";
                                self.ctx.textAlign = "center";
                                self.ctx.fillStyle = "white";
                                self.ctx.fillText("图片已经加载到" + count + "/" + imageAmount, 300, self.canvas.height * (1 - 0.618));
                                //判断是否加载完毕
                                if (count == imageAmount) {
                                    //开始游戏
                                    self.start();

                                }
                            }
                        }
                    }
                }
            }
            xhr.open("get", this.resourseUrl, true);
            xhr.send(null);
        },
        start: function () {
            var self = this;
            //new出场景实例 场景管理所有实例上
            this.sence = new Sence();
            this.sence.changeSense(self.senceNumber);
            this.timer = setInterval(function () {
                self.f++;
                self.ctx.clearRect(0, 0, 800, 600);
                //场景管理所有实例update和render方法
                self.sence.show();
                self.ctx.font = "26px 微软雅黑";
                self.ctx.textAlign = "left";
                self.ctx.fillText("帧编号: " + self.f, 20, 20);
                if (g.senceNumber == 1) {
                    g.gt += 0.04;
                }
                var h = parseInt(g.gt / 360);
                var m = parseInt((g.gt - parseInt(g.gt / 360) * 360) / 60);
                var mm = parseInt(g.gt - h * 360 - m * 60);
                h = h < 10 ? "0" + h : h;
                m = m < 10 ? "0" + m : m;
                mm = mm < 10 ? "0" + mm : mm;
                g.ctx.fillText("游戏时间: " + h + " : " + m + " : " + mm, 250, 70);
            }, 40);
            var self = this;

        }

    })


})()

//计算随机数
function rnd(m, n) {
    return parseInt(Math.random() * (n - m) + m);
}