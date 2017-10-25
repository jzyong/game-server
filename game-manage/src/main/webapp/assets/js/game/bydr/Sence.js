(function () {
    //场景类
    var Sence = window.Sence = Class.extend({
        init: function () {
            //被子弹碰撞到的鱼数组
            this.emitFishArr = []
            this.bindEvent();
        },
        changeSense: function (number) {
            g.senceNumber = number;
            if (g.senceNumber == 0) {
                //暂停背景音乐
                g.bg_music.pause();
                //new出background实例
                g.bg = new Background();
                //设置开始游戏logo资源 初始位置
                this.startLogo = g.Robject["scence1_bg"];
                //startLogo位置
                this.startLogoX = 0;
                this.startLogoY = 100;


            } else if (g.senceNumber == 1) {
                //重置游戏时间
                g.gt = 0;
                //清空子弹数组
                g.arrBullet = [];
                //清空鱼数组
                g.fishArr = [];
                //添加背景音乐
                g.bg_music.src = ctx + "/assets/music/bydr/bg.ogg";
                g.bg_music.loop = true;
                g.bg_music.play();
                //new出background实例
                g.bg = new Background();
                //new出炮台
                g.cannonBottom = new CannonBottom();
                //new出炮
                g.cannon = new Cannon(_.random(1, 7), 431, 570);
                //new出分数
                g.scoreNumber = new Score();
                //备份炮弹个数
                this._bulletCount = g.bulletCount;
                //能量条
                this.energy = g.Robject["energy_bar"];
                this.energyX = 537;
                this.energyY = 574;
                //炮弹数量
                this.targetScore = g.Robject["target_score"];
                this.targetScoreX = 600;
                this.targetScoreY = 16;
                //加炮弹
                this.cannonPlus = g.Robject["cannon_plus"];
                this.cannonPlusX = 483;
                this.cannonPlusY = 565;
                //减炮弹
                this.cannonMinus = g.Robject["cannon_minus"];
                this.cannonMinusX = 340;
                this.cannonMinusY = 565;

            } else if (g.senceNumber == 2) {
                //暂停背景音乐
                g.bg_music.pause();
                //new出background实例
                g.bg = new Background();
                //设置游戏分数结算图片资源 初始位置
                this.totalGain = g.Robject["totalScore"];
                //游戏分数结算图片位置 400 300为宽高
                this.totalGainX = (800 - 400) / 2;
                this.totalGainY = (600 - 300) / 2;
                //打印分数

                //再玩一次游戏
                this.tryAgain = g.Robject["try_Again"];
                this.tryAgainX = 570;
                this.tryAgainY = 460;
                //回到主页面
                this.BackTo = g.Robject["back_To"];
                this.BackToX = 100;
                this.BackToY = 460;


            }
        },
        show: function () {
            if (g.senceNumber == 0) {
                //只渲染背景 不更新
                g.bg.render();
                g.ctx.drawImage(this.startLogo, this.startLogoX, this.startLogoY, 800, 390);

            } else if (g.senceNumber == 1) {
                _.each(g.actors, function (actor) {
                    actor.update();
                    actor.render();
                });
                g.cannonBottom.render();
                g.cannon.render();
                g.scoreNumber.render();
                _.each(g.arrBullet, function (bullet) {
                    bullet.update();
                    bullet.render();
                });
                //能量条
                g.ctx.drawImage(this.energy, this.energyX, this.energyY, 213, 19);
                //弹药
                //原·目标分数
                g.ctx.drawImage(this.targetScore, this.targetScoreX, this.targetScoreY, 180, 100);
                //打印弹药
                g.ctx.font = "16px 微软雅黑";
                g.ctx.fillStyle = "yellow";
                g.ctx.fillText("弹药库：" + g.bulletCount + "发", 638, 67);
                //炮弹出现
                g.ctx.drawImage(this.cannonPlus, this.cannonPlusX, this.cannonPlusY, 44, 31);
                //炮弹消失
                g.ctx.drawImage(this.cannonMinus, this.cannonMinusX, this.cannonMinusY, 44, 31);
                //遍历鱼数组 判断子弹与鱼是否相撞
                this.fishAndBulletIsEmit();
                //增加炮弹数量
                if ((parseInt((g.score - 0) / 20)) > 0) {
                    g.bulletCount += (parseInt((g.score - 0) / 20) - g.a);
                    g.a = parseInt((g.score - 0) / 20);
                }

                //判断炮弹次数是否用完
                if (g.bulletCount <= 0) {
                    console.log("炮弹次数用尽了");
                    this.changeSense(2);
                }
            } else if (g.senceNumber == 2) {
                //只渲染背景 不更新
                g.bg.render();
                g.ctx.drawImage(this.totalGain, this.totalGainX, this.totalGainY, 400, 300);
                g.ctx.drawImage(this.tryAgain, this.tryAgainX, this.tryAgainY, 180, 65);
                g.ctx.drawImage(this.BackTo, this.BackToX, this.BackToY, 180, 65);
                g.ctx.font = "24px 微软雅黑";
                g.ctx.fillStyle = "yellow";
                g.ctx.fillText(g.score + "分", 380, 292);
                var h = parseInt(g.gt / 360);
                var m = parseInt((g.gt - parseInt(g.gt / 360) * 360) / 60);
                var mm = parseInt(g.gt - h * 360 - m * 60);
                h = h < 10 ? "0" + h : h;
                m = m < 10 ? "0" + m : m;
                mm = mm < 10 ? "0" + mm : mm;
                g.ctx.fillText(h + " : " + m + " : " + mm, 380, 346);
            }
        },
        bindEvent: function () {
            var self = this;
            g.canvas.onmousedown = function (event) {
                event.preventDefault();
                event.stopPropagation();
                var x = event.offsetX;
                var y = event.offsetY;
                switch (g.senceNumber) {
                    case 0:
                        //为开始游戏图标位置
                        var startGameX = 290;
                        var startGameY = 335;
                        //范围
                        if (x > startGameX && x < startGameX + 204 && y > startGameY && y < startGameY + 55) {
                            bydrEnterRoomRequest();	//进入房间
                            // self.changeSense(1);
                        }
                        break;
                    case 1:
                        //监听加减炮弹按钮
                        //为游戏图标位置 加减炮弹位置
                        var cannonPlusX = 491;
                        var cannonPlusY = 573;
                        var cannonMinusX = 347;
                        var cannonMinusY = 572;
                        //范围
                        if (x > cannonPlusX && x < cannonPlusX + 23 && y > cannonPlusY && y < cannonPlusY + 18) {
                            g.cannon.type++;
                            console.log(g.cannon.type);
                            //如果炮弹类型达到最大 则return
                            if (g.cannon.type > 7) {
                                g.cannon.type = 7;
                                return;
                            }
                        } else if (x > cannonMinusX && x < cannonMinusX + 23 && y > cannonMinusY && y < cannonMinusY + 18) {
                            g.cannon.type--;
                            console.log(g.cannon.type);
                            //如果炮弹类型达到最小 则return
                            if (g.cannon.type < 1) {
                                g.cannon.type = 1;
                                return;
                            }
                        } else {
                            //TODO:置入炮弹发射声音
                            //添加炮弹发射的声音文件
                            var oM = new Audio();
                            oM.src = ctx + "/assets/music/bydr/bullet.ogg";
                            oM.play();
                            //记录鼠标点击的位置
                            var cx = event.clientX - g.canvas.offsetLeft - g.cannon.x - 290;	//左边导航宽度为292
                            var cy = g.cannon.y - (event.clientY + g.canvas.offsetTop + 80);		//上导航为79

                            //计算角度，tan为临边比对边
                            var d = 90 - Math.atan2(cy, cx) * 180 / Math.PI;
                            //设置炮的角度
                            g.cannon.rotate = d;
                            //改变 isEmit的值，显示炮弹后座力效果
                            g.cannon.isEmit = true;
                            //new出炮弹 设置炮弹上屏的位置 角度（与大炮角度相同）
                            var bullet = new Bullet(g.cannon.type, g.cannon.x, g.cannon.y, g.cannon.rotate);
                            g.bulletCount--;
                            g.arrBullet.push(bullet);
                            bydrFireRequest(g.cannon.type);
                        }
                        break;
                    case 2:
                        //为游戏图标位置 回到主页面 再玩一次
                        var AgainGameX = 577;
                        var AgainGameY = 470;
                        var BackToGameX = 105;
                        var BackToGameY = 469;
                        //范围
                        if (x > AgainGameX && x < AgainGameX + 164 && y > AgainGameY && y < AgainGameY + 45) {
                            g.bulletCount = self._bulletCount;
                            g.score = 0;
                            self.changeSense(1);
                        } else if (x > BackToGameX && x < BackToGameX + 160 && y > BackToGameY && y < BackToGameY + 45) {
                            g.bulletCount = self._bulletCount;
                            g.score = 0;
                            self.changeSense(0);
                        }
                        break;
                }
            }
        },
        fishAndBulletIsEmit: function () {
            var self = this;
            var writeBullet;
            writeBullet = false;
            var dieFishId =[];
            //遍历鱼数组 判断子弹与鱼是否相撞
            _.each(g.fishArr, function (fish) {
                _.each(g.arrBullet, function (bullet) {
                    if (!writeBullet) {
                        writeBullet = bullet;
                    }
                    if (fish.isCrash(writeBullet)) {
                        self.emitFishArr.push({
                            "x": fish.x,
                            "y": fish.y,
                            "rotate": fish.rotate,
                            "type": fish.type,
                            "score": fish.score
                        });
                        dieFishId.push(fish.id);
                        g.fishArr = _.without(g.fishArr, fish);
                        g.actors = _.without(g.actors, fish);

                        //将子弹从数组中移除
                        g.arrBullet = _.without(g.arrBullet, bullet);
                        // console.log(writeBullet);
                        // //console.log("撞上啦！！！");
                    }
                    ;
                });
            });
            //向服务器发送消息
            bydrFireResultRequest(dieFishId,g.cannon.type)
            _.each(self.emitFishArr, function (emitFish) {
                //TODO:置入金币声音
                //碰撞的时候添加金币的声音文件
                var oM = new Audio();
                oM.src = ctx + "/assets/music/bydr/coin.ogg";
                oM.play();
                //鱼被炮弹打中的位置 就是金币、渔网、死鱼new的初始位置
                var x = emitFish.x;
                var y = emitFish.y;
                //记录鱼的角度 保证死鱼的角度与活鱼相同
                var rotate = emitFish.rotate;
                //记录鱼的类型，出现的金币类型与鱼的类型有关
                var type = emitFish.type;
                //记录鱼的分数
                var score = emitFish.score;
                // console.log("当前鱼的分数："+score);
                // //改变总分数
                // g.score += score;
                // console.log("总分数："+g.score);
                //将鱼从数actors数组中移除
                // g.actors= _.without(g.actors,emitFish);
                // g.fishArr= _.without(g.fishArr,emitFish);
                self.emitFishArr = _.without(self.emitFishArr, emitFish);
                //碰撞之后 new出渔网
                var web = new Web(type, x, y);
                //碰撞之后 new出死鱼
                var dieFish = new DieFish(type, x, y, rotate);
                //碰撞之后 new出钱币
                var coin = new Coin(type, x, y);
                var coinNumber = new CoinText(score, x, y);

            })
        }
    });
})()