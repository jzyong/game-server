(function(){
	var Background=window.Background=Actor.extend({
		init:function(){
			//背景图片左上角顶点坐标 宽高 运动速度
			this.x=0;
			this.y=0;
			this.w=800;
			this.h=600;
			this.image= g.Robject["game_bg"];
			//执行超类构造函数
			this._super();
		},
		render:function(){
			g.ctx.drawImage(this.image, this.x,this.y,this.w,this.h);
		},
		update:function(){

		}
	})
})();