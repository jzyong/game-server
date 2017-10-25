(function(){
    var Score=window.Score=Class.extend({
        init:function(){
            this.cutW=20;
            this.cutH=24;
            this.w=21;
            this.h=21;
            this.image= g.Robject["number_black"];
            //执行超类构造函数
            //this._super();
        },
        render:function(){
            g.ctx.save();
            var score="";
            //分数 字符串格式
            if(g.score!=0){
              score=g.score+"";
            }
            //前面补位0的个数
            var ZeroCount=6-score.length;
            //数字上屏位置信号量
            var idx=0;
            //有分数的位置
            for(var i=score.length-1 ; i>=0 ; i--){
                var number=parseInt(score[i]);
                g.ctx.drawImage(this.image,0, (9-number)*this.cutH,this.cutW,this.cutH,22+(23*(5-idx)),574,this.w,this.h);
                idx++;
            }
            // 补充的0的位置
            for(var i=0;i<ZeroCount;i++){
                g.ctx.drawImage(this.image,0, 9*this.cutH,this.cutW,this.cutH,22+(23*(5-idx)),574,this.w,this.h);
               idx++;
            }
            g.ctx.restore();
        },
        update:function(){
        }
    })
})();