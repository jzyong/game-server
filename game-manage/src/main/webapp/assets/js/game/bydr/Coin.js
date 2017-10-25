//钱币
(function(){
    var Coin=window.Coin=Actor.extend({
        init:function(type,x,y){
            this.x=x;
            this.y=y;
            this.type=type;
            this.w=60;
            this.h=60;
            this.bgidx=0;
            //让金币分十次移动到目标位置 10次正好与金币旋转次数一致
            //金币x、y增量
            this.dx= (this.x-5)/10;
            this.dy=(g.canvas.height+10-this.y)/10;
            //根据鱼的类型 判断钱币是金币还是银币
            if(this.type<3){
                this.image= g.Robject["coinAni1"];
            }else{
                this.image= g.Robject["coinAni2"];
            }
            //执行超类构造函数
            this._super();
        },
        render:function(){
            g.ctx.save();
            g.ctx.translate(this.x,this.y);
            g.ctx.drawImage(this.image,0, this.bgidx*this.h,this.w,this.h,-this.w/2,-this.h/2,this.w,this.h);
            g.ctx.restore();
        },
        update:function(){

            if(this.x<=this.x-5 && this.y>=g.canvas.height+10-this.y){
                console.log("XXX");
                return;
            }
            //金币转动（就是改变切片的x/y值）图片转动十次
                this.bgidx++;
                if(this.bgidx>9){
                    this.bgidx=0;
                }
            if(g.f%4==0){
                //鱼向前移动
                this.x-=this.dx;
                this.y+=this.dy;
            }


        }
    })
})();