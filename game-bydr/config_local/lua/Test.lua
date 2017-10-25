local res;
if #(KEYS)>= 1 and #(ARGV) >= 1 then
	redis.call('set',KEYS[1],ARGV[1]);
	res=redis.call('get',KEYS[1]);
end
return res;