(function(){
	var Actor=window.Actor=Class.extend({
		init:function(){
			g.actors.push(this);
		},
		render:function(){
			throw Error("实例必须进行渲染");
		},
		update:function(){
			
		}
	});
})();