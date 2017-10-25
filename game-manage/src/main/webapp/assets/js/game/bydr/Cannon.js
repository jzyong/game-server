//炮
(function(){
    var Cannon=window.Cannon=Actor.extend({
        init:function(type,x,y){
            //各种不同的大炮的图片的数据
            this.CANNON_SIZE=[
                null,
                {w: 74, h: 74},
                {w: 74, h: 76},
                {w: 74, h: 76},
                {w: 74, h: 83},
                {w: 74, h: 85},
                {w: 74, h: 90},
                {w: 74, h: 94}
            ];
            this.x=x;
            this.y=y;
            this.type=type;
            this.w=this.CANNON_SIZE[this.type].w;
            this.h=this.CANNON_SIZE[this.type].h;
            this.rotate=0;
            this.bgidx=0;
            //判断是否显示子弹后坐力效果
            this.isEmit=false;
            this.image= g.Robject["cannon"+this.type];
            //执行超类构造函数
            this._super();
        },
        render:function(){
            this.w=this.CANNON_SIZE[this.type].w;
            this.h=this.CANNON_SIZE[this.type].h;
            this.image= g.Robject["cannon"+this.type];
            g.ctx.save();
            g.ctx.translate(this.x,this.y);
            g.ctx.rotate(this.rotate*Math.PI/180);
            g.ctx.drawImage(this.image,0, this.bgidx*this.h,this.w,this.h,-this.w/2,-this.h/2,this.w,this.h);
            g.ctx.restore();
        },
        update:function(){

            //炮弹后坐力方法 如果this.isEmit为true就显示炮弹后座力效果
            if(this.isEmit){
                this.bgidx++;
                if(this.bgidx>4){
                    this.bgidx=0;
                    this.isEmit=false;
                }
            }


        }
    })
})();