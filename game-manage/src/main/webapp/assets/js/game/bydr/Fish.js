(function(){
    var Fish=window.Fish=Actor.extend({
        init:function(type,x,y,rotate,id){
            //各种不同的鱼的图片的数据
            var FISH_SIZE=[
                null,
                {w: 55, h: 37, collR: 117 , score:5},
                {w: 78, h: 64, collR: 124 ,score:10},
                {w: 72, h: 56, collR: 120 ,score:30},
                {w: 77, h: 59, collR: 127 ,score:50},
                {w: 107, h: 122, collR: 139 ,score:70}
            ];
            this.id=id;
            this.x=x;
            this.y=y;
            this.type=type;
            this.score=FISH_SIZE[this.type].score;
            this.w=FISH_SIZE[this.type].w;
            this.h=FISH_SIZE[this.type].h;
            //鱼的半径
            this.collR=FISH_SIZE[this.type].collR;
            this.rotate=rotate;
            this.bgidx=0;
            //鱼向前运动的增量
            this.speed=1;
            //将鱼向前运动的增量根据三角函数分解到this.dx和this.dy上
            this.dx=Math.cos(this.rotate*Math.PI/180)*this.speed;
            this.dy=Math.sin(this.rotate*Math.PI/180)*this.speed;

            this.image= g.Robject["fish"+this.type];
            //执行超类构造函数
            this._super();
        },
        render:function(){
            g.ctx.save();
            g.ctx.translate(this.x,this.y);
            g.ctx.rotate(this.rotate*Math.PI/180);
            g.ctx.drawImage(this.image,0, this.bgidx*this.h,this.w,this.h,-this.w/2,-this.h/2,this.w,this.h);
            g.ctx.restore();
        },
        update:function(){
            //每隔10帧鱼尾巴摆动一次（就是改变切片的x/y值）
            if(g.f%10==0){
                this.bgidx++;
                if(this.bgidx>3){
                    this.bgidx=0;
                }

            }
            //鱼向前移动
            this.x+=this.dx;
            this.y+=this.dy;
            //判断鱼的位置 判断鱼是否出屏幕 如果出屏幕 从actors中移除

                if(this.x<-50 ||this.x>(g.canvas.width+50) || this.y<0 || this.y>(g.canvas.height+50)){
                    g.actors= _.without(g.actors,this);
                    g.fishArr=_.without(g.fishArr,this);
                }

        },
        isCrash:function(bullet){
                var x=this.x-bullet.x;
                var y=this.y-bullet.y;
                var distance=Math.sqrt(x*x+y*y);
            //判断子弹和鱼是否碰撞
                if(distance<=this.collR){
                    return true;
                }else{
                    return false;
                }
        }
    })
})();