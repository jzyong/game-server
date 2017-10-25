//死鱼
(function(){
    var DieFish=window.DieFish=Actor.extend({
        init:function(type,x,y,rotate){
            //各种不同的鱼的图片的数据
            var FISH_SIZE=[
                null,
                {w: 55, h: 37, collR: 17},
                {w: 78, h: 64, collR: 24},
                {w: 72, h: 56, collR: 20},
                {w: 77, h: 59, collR: 22},
                {w: 107, h: 122, collR: 29}
            ];
            this.x=x;
            this.y=y;
            this.type=type;
            this.w=FISH_SIZE[this.type].w;
            this.h=FISH_SIZE[this.type].h;
            //鱼的半径
            this.collR=FISH_SIZE[this.type].collR;
            this.rotate=rotate;
            this.bgidx=4;
            //死鱼被实例化时的帧数
            this.initFrame= g.f;
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
            //死鱼翻肚皮动态效果 每隔10帧鱼摆动一次（就是改变切片的x/y值）
            if(g.f%8==0){
                //渔网生成一段时间之后消失
                if(g.f-this.initFrame>40){
                    g.actors= _.without(g.actors,this);
                    return;
                }else{
                    this.bgidx++;
                    if(this.bgidx>7){
                        this.bgidx=4;
                    }
                }


            }
        }
    })
})();