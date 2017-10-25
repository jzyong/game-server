//炮
(function(){
    var Bullet=window.Bullet=Class.extend({
        init:function(type,x,y,rotate){
            //炮弹具体尺寸
            var BULLET_SIZE=[
                null,
                {x: 86, y: 0, w: 24, h: 26,score:1},
                {x: 62, y: 0, w: 25, h: 29,score:2},
                {x: 30, y: 0, w: 31, h: 35,score:3},
                {x: 32, y: 35, w: 27, h: 31,score:4},
                {x: 30, y: 82, w: 29, h: 33,score:5},
                {x: 0, y: 82, w: 30, h: 34,score:6},
                {x: 0, y: 0, w: 30, h: 44,score:7}
            ];
            this.type=type;
            //炮弹切片X Y值
            this.cutX=BULLET_SIZE[this.type].x;
            this.cutY=BULLET_SIZE[this.type].y;
            this.x=x;
            this.y=y;
            this.w=BULLET_SIZE[this.type].w;
            this.h=BULLET_SIZE[this.type].h;
            this.rotate=rotate;
            //炮弹向前运动的增量
            this.speed=this.type*3.5;
            //将炮弹向前运动的增量根据三角函数分解到this.dx和this.dy上
            this.dx=Math.sin(this.rotate*Math.PI/180)*this.speed;
            this.dy=Math.cos(this.rotate*Math.PI/180)*this.speed;
            this.image= g.Robject["bullet"];

            //执行超类构造函数
            //this._super();
        },
        iscrash:function(){
            var writeBullet = this;

        },
        render:function(){
            g.ctx.save();
            g.ctx.translate(this.x,this.y);
            g.ctx.rotate(this.rotate*Math.PI/180);
            g.ctx.drawImage(this.image,this.cutX, this.cutY,this.w,this.h,-this.w/2,-this.h/2,this.w,this.h);
            g.ctx.restore();
        },
        update:function(){
            //炮弹向前移动
                this.x+=this.dx;
                this.y-=this.dy;
            //判断炮弹的位置 判断炮弹是否出屏幕 如果出屏幕 从actors中移除

            if(this.x<-50 ||this.x>(g.canvas.width+50) || this.y<0 || this.y>(g.canvas.height+50)){
                g.arrBullet= _.without(g.arrBullet,this);
            }

        }
    })
})();