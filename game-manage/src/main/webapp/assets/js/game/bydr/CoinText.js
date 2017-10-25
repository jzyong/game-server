//钱币
(function(){
    var CoinText=window.CoinText=Actor.extend({
        init:function(coinNumber,x,y){
            // console.log(coinNumber);
            this.x=x;
            this.y=y;
            this.coinNumber=coinNumber;
            this.w=36;
            this.h=49;
            this.count=0;
            this.image= g.Robject["coinText"];
            //执行超类构造函数
            this._super();
        },
        render:function(){
            g.ctx.save();
            g.ctx.drawImage(this.image,360,0,this.w,this.h,this.x+20,this.y,this.w,this.h);
            var score = (this.coinNumber).toString();
            //拆位数
            for(var i = 0 ; i < score.length ; i++){
                var _char = score.charAt(i);
                g.ctx.drawImage(this.image,_char*this.w,0,this.w,this.h,this.x+20+39.6 * (i+1),this.y,this.w,this.h);
            }
            g.ctx.restore();
        },
        update:function(){
                if(this.count>30){
                    g.actors= _.without(g.actors,this);
                    return;
                }
                //鱼向前移动
                this.y-=1;
                this.count++;

        }
    })
})();