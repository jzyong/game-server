/**
 * 捕鱼数据js
 * @author JiangZhiYong
 * */

/**角色ID*/
var roleId;
/**用户ID*/
var userId;


/**背包列表*/
var packetItems;
/**邮件列表*/
var mailList;

//捕鱼游戏根对象
var g;



/**定时任务*/
var intervalNum= window.setInterval("update()",1000);

/**
 * 定时函数
 */
function update() {
    // console.debug("定时器执行："+intervalNum);

    //请求心跳
    heartRequest();
}

/**
 * canvas 页面加载初始化
 */
function bydrInit() {
    g=new Game("bydrCanvas",ctx+"/assets/js/game/bydr/R.txt");

   /* var stage=new createjs.Stage("bydrCanvas");
    var circle=new createjs.Shape();
    circle.graphics.beginFill("DeepSkyBlue").drawCircle(0, 0, 50);
    circle.x = 100;
    circle.y = 100;
    stage.addChild(circle);
    createjs.Tween.get(circle, {loop: true})
        .to({x: 400}, 1000, createjs.Ease.getPowInOut(4))
        .to({alpha: 0, y: 75}, 500, createjs.Ease.getPowInOut(2))
        .to({alpha: 0, y: 125}, 100)
        .to({alpha: 1, y: 100}, 500, createjs.Ease.getPowInOut(2))
        .to({x: 100}, 800, createjs.Ease.getPowInOut(2));
    createjs.Ticker.setFPS(60);
    createjs.Ticker.addEventListener("tick", stage);*/
}

/**
 * 是否登录
 */
function isLogin() {
    if(roleId==null||roleId<1){
        alert("角色未登陆");
        return false;
    }
    return true;
}