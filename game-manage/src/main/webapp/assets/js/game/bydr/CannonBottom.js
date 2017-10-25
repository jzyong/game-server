//炮台
(function(){
    var CannonBottom=window.CannonBottom=Class.extend({
        init:function(){
            this.x=0;
            this.y=0;
            this.w=765;
            this.h=70;
            this.image= g.Robject["bottom"];
        },
        render:function(){
            //new出炮台
            g.ctx.drawImage(this.image,0,0,765,70,
                4,522,765,80);
        },
        update:function(){

        }
    })
})();