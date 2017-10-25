//渔网
(function(){
    var Web=window.Web=Actor.extend({
        init:function(type,x,y){
            //网的尺寸
            var WEB_SIZE=[
                null,
                {x:332,y:373,w:87,h:86},
                {x:13,y:413,w:108,h:106},
                {x:177,y:369,w:125,h:124},
                {x:252,y:179,w:149,h:149},
                {x:1,y:244,w:160,h:154},
                {x:21,y:22,w:198,h:199},
                {x:241,y:0,w:180,h:179}
            ];
            this.type=type;
            this.scale=1;
            //渔网切片X Y值
            this.cutX=WEB_SIZE[this.type].x;
            this.cutY=WEB_SIZE[this.type].y;
            this.x=x;
            this.y=y;
            this.w=WEB_SIZE[this.type].w;
            this.h=WEB_SIZE[this.type].h;
            this.image= g.Robject["web"];
            //渔网被实例化时的帧数
            this.initFrame= g.f;

            //执行超类构造函数
            this._super();
        },
        render:function(){
            g.ctx.save();
            g.ctx.translate(this.x,this.y);
            g.ctx.scale(this.scale,this.scale);
            g.ctx.drawImage(this.image,this.cutX, this.cutY,this.w,this.h,-this.w/2,-this.h/2,this.w,this.h);
            g.ctx.restore();
        },
        update:function(){
            //渔网生成一段时间之后消失
            if(g.f-this.initFrame>35){
                g.actors= _.without(g.actors,this);
            }

        }

    })
})();